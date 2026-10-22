package com.triage.ticket_service.messaging;

public record TicketCreatedEvent(
        Long ticketId,
        String customerUsername,
        String subject,
        String description
) {
}