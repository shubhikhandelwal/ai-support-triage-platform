package com.triage.ticket_service.dto;

import com.triage.ticket_service.entity.TicketCategory;
import com.triage.ticket_service.entity.TicketPriority;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TriageUpdateRequest(

        @NotNull(message = "Category is required")
        TicketCategory category,

        @NotNull(message = "Priority is required")
        TicketPriority priority,

        @Size(max = 2000, message = "Summary must be at most 2000 characters")
        String aiSummary
) {
}