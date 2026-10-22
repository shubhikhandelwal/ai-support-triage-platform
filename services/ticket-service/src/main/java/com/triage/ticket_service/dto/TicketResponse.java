package com.triage.ticket_service.dto;

import com.triage.ticket_service.entity.Ticket;
import com.triage.ticket_service.entity.TicketCategory;
import com.triage.ticket_service.entity.TicketPriority;
import com.triage.ticket_service.entity.TicketStatus;

import java.time.Instant;

public record TicketResponse (
    Long id,
    String customerUsername,
    String subject,
    String description,
    TicketStatus status,
    TicketCategory category,
    TicketPriority priority,
    String aiSummary,
    Instant createdAt,
    Instant updatesAt
){
    public static TicketResponse from(Ticket ticket){
        return new TicketResponse(
                ticket.getId(),
                ticket.getCustomerUsername(),
                ticket.getSubject(),
                ticket.getDescription(),
                ticket.getStatus(),
                ticket.getCategory(),
                ticket.getPriority(),
                ticket.getAiSummary(),
                ticket.getCreatedAt(),
                ticket.getUpdatedAt()
        );
    }
}
