package com.triage.ticket_service.service;

import com.triage.ticket_service.dto.CreateTicketRequest;
import com.triage.ticket_service.dto.TicketResponse;
import com.triage.ticket_service.dto.TriageUpdateRequest;
import com.triage.ticket_service.entity.Ticket;
import com.triage.ticket_service.entity.TicketCategory;
import com.triage.ticket_service.entity.TicketPriority;
import com.triage.ticket_service.entity.TicketStatus;
import com.triage.ticket_service.exception.TicketNotFoundException;
import com.triage.ticket_service.messaging.TicketCreatedEvent;
import com.triage.ticket_service.messaging.TicketEventPublisher;
import com.triage.ticket_service.repository.TicketRepository;
import com.triage.ticket_service.repository.TicketSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketService {
    private final TicketRepository ticketRepository;
    private final TicketEventPublisher eventPublisher;

    public TicketResponse createTicket(CreateTicketRequest request, String username){
        Ticket ticket = Ticket.builder()
                .customerUsername(username)
                .subject(request.subject())
                .description(request.description())
                .build();
        Ticket saved = ticketRepository.save(ticket);
        try{
            eventPublisher.publish(new TicketCreatedEvent(
                    saved.getId(),
                    saved.getCustomerUsername(),
                    saved.getSubject(),
                    saved.getDescription()
            ));
        }catch (Exception ex){
            log.error("Ticket {} saved but event publish failed; it stays OPEN until reconciled", saved.getId(), ex); //The {} is a parameter placeholder. The logging framework inserts the ticket ID into the message.
        }
        return TicketResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public Page<TicketResponse> listTickets(String username, boolean isAgent, TicketStatus status, TicketCategory category, TicketPriority priority, Pageable pageable){
        if(isAgent){
            return ticketRepository
                    .findAll(TicketSpecifications.withFilters(status, category, priority), pageable)
                    .map(TicketResponse::from);
        }
        return ticketRepository.findByCustomerUsername(username, pageable)
                .map(TicketResponse::from);
    }

    @Transactional(readOnly = true)
    public TicketResponse getTicket(Long id, String username, boolean isAgent) {
        Ticket ticket = isAgent
                ? ticketRepository.findById(id).orElseThrow(() -> new TicketNotFoundException(id))
                : ticketRepository.findByIdAndCustomerUsername(id, username)
                .orElseThrow(() -> new TicketNotFoundException(id));
        return TicketResponse.from(ticket);
    }

    @Transactional
    public TicketResponse applyTriage(Long id, TriageUpdateRequest request) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException(id));

        ticket.setCategory(request.category());
        ticket.setPriority(request.priority());
        ticket.setAiSummary(request.aiSummary());

        if (ticket.getStatus() == TicketStatus.OPEN) { //method changes the status to TRIAGED only if the current status is OPEN.
            ticket.setStatus(TicketStatus.TRIAGED);
        }

        return TicketResponse.from(ticketRepository.saveAndFlush(ticket));
    }
}
