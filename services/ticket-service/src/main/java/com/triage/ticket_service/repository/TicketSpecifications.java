package com.triage.ticket_service.repository;

import com.triage.ticket_service.entity.Ticket;
import com.triage.ticket_service.entity.TicketCategory;
import com.triage.ticket_service.entity.TicketPriority;
import com.triage.ticket_service.entity.TicketStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class TicketSpecifications {
    private TicketSpecifications(){}

    public static Specification<Ticket> withFilters(TicketStatus status,
                                                    TicketCategory category,
                                                    TicketPriority priority){
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if(status != null){
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (category != null) {
                predicates.add(cb.equal(root.get("category"), category));
            }
            if (priority != null) {
                predicates.add(cb.equal(root.get("priority"), priority));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
