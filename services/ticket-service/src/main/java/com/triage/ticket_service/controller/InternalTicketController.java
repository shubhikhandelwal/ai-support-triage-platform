package com.triage.ticket_service.controller;

import com.triage.ticket_service.dto.TicketResponse;
import com.triage.ticket_service.dto.TriageUpdateRequest;
import com.triage.ticket_service.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("internal/tickets")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SERVICE')")
public class InternalTicketController {
    private final TicketService ticketService;

    @PatchMapping("/{id}/triage")
    public TicketResponse applyTriage(@PathVariable Long id,
                                      @Valid @RequestBody TriageUpdateRequest request) {
        return ticketService.applyTriage(id, request);
    }
}
