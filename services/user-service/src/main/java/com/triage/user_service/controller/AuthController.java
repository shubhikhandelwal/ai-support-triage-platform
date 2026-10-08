package com.triage.user_service.controller;

import com.triage.user_service.dto.AuthResponse;
import com.triage.user_service.dto.LoginRequest;
import com.triage.user_service.dto.SignupRequest;
import com.triage.user_service.entity.User;
import com.triage.user_service.security.JWTService;
import com.triage.user_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;

    private AuthResponse buildAuthResponse(User user){ //Generate JWT + Build response
        String token = jwtService.generateToken(user.getUsername(),
                user.getRole().name());
        return AuthResponse.builder()
                .accessToken(token)
                .userId(user.getId())
                .username(user.getUsername())
                .role(user.getRole().name())
                .build();
    }
    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignupRequest request){
        User user = userService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(buildAuthResponse(user));
    }
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        User user = userService.loadAuthenticatedUser(request.getUsername());
        return ResponseEntity.ok(buildAuthResponse(user));
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResponse> me(Authentication authentication) {
        User user = userService.loadAuthenticatedUser(authentication.getName());
        return ResponseEntity.ok(AuthResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .role(user.getRole().name())
                .build());
    }
}
