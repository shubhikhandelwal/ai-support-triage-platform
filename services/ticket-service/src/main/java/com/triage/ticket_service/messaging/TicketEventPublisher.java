package com.triage.ticket_service.messaging;

public interface TicketEventPublisher {

    void publish(TicketCreatedEvent event);
}