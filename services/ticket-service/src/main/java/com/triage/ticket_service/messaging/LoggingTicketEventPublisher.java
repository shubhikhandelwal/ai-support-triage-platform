package com.triage.ticket_service.messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "app.messaging", name = "mode", havingValue = "log", matchIfMissing = true)
public class LoggingTicketEventPublisher implements TicketEventPublisher{
    @Override
    public void publish(TicketCreatedEvent event){
        log.info("[LOCAL MODE] Would publish TicketCreated: ticketId={},customer={}",
                event.ticketId(), event.customerUsername());
    }
}
