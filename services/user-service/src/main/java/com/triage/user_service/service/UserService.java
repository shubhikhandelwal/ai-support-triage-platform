package com.triage.user_service.service;


import com.triage.user_service.dto.SignupRequest;
import com.triage.user_service.entity.User;
import com.triage.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User signup(SignupRequest signupRequest) {
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            throw new IllegalArgumentException("Username already taken");
        }

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = User.builder()
                .username(signupRequest.getUsername())
                .email(signupRequest.getEmail())
                .password(passwordEncoder.encode(signupRequest.getPassword()))
                .role(User.Role.CUSTOMER)
                .build();

        return userRepository.save(user);
    }
    public User loadAuthenticatedUser(String username){
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found:" + username));
    }

}
