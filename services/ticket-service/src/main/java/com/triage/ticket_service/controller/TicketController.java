package com.triage.ticket_service.controller;

import com.triage.ticket_service.dto.CreateTicketRequest;
import com.triage.ticket_service.dto.PageResponse;
import com.triage.ticket_service.dto.TicketResponse;
import com.triage.ticket_service.entity.TicketCategory;
import com.triage.ticket_service.entity.TicketPriority;
import com.triage.ticket_service.entity.TicketStatus;
import com.triage.ticket_service.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class TicketController {
    private static final int MAX_PAGE_SIZE = 100;
    private final TicketService ticketService;

    @PostMapping
    public ResponseEntity<TicketResponse> create(
            @Valid @RequestBody CreateTicketRequest request,
            Authentication authentication
            ){
        TicketResponse created = ticketService.createTicket(request, authentication.getName());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public PageResponse<TicketResponse> list(@RequestParam(required = false) TicketStatus status,
                                             @RequestParam(required = false)TicketCategory category,
                                             @RequestParam(required = false)TicketPriority priority,
                                             @RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "20") int size,
                                             Authentication authentication
                                             ){
        Pageable pageable = PageRequest.of(
                Math.max(page, 0), //Negative page numbers become 0.
                Math.min(Math.max(size, 1), MAX_PAGE_SIZE), //Page sizes below 1 become 1.
                Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id")));
        Page<TicketResponse> result = ticketService.listTickets(
                authentication.getName(), isAgent(authentication),
                status, category, priority, pageable);
        return PageResponse.from(result);
    }

    @GetMapping("{id}")
    public TicketResponse get(@PathVariable Long id, Authentication authentication){
        return ticketService.getTicket(id, authentication.getName(), isAgent(authentication));
    }

    private boolean isAgent(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_AGENT"));
    }
}
