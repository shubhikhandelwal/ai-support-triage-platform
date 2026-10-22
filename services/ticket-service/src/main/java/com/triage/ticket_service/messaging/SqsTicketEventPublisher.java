package com.triage.ticket_service.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "app.messaging", name = "mode", havingValue = "sqs")
public class SqsTicketEventPublisher implements TicketEventPublisher {

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;
    private final String queueUrl;

    public SqsTicketEventPublisher(SqsClient sqsClient,
                                   ObjectMapper objectMapper,
                                   @Value("${aws.sqs.queue-url}") String queueUrl) {
        if (queueUrl == null || queueUrl.isBlank()) {
            throw new IllegalStateException(
                    "app.messaging.mode=sqs requires SQS_QUEUE_URL / aws.sqs.queue-url to be set");
        }
        this.sqsClient = sqsClient;
        this.objectMapper = objectMapper;
        this.queueUrl = queueUrl;
    }

    @Override
    public void publish(TicketCreatedEvent event) {
        String body;
        try {
            body = objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Could not serialize TicketCreatedEvent", ex);
        }

        SendMessageResponse response = sqsClient.sendMessage(
                SendMessageRequest.builder()
                        .queueUrl(queueUrl)
                        .messageBody(body)
                        .build());

        log.info("Published TicketCreated: ticketId={}, sqsMessageId={}",
                event.ticketId(), response.messageId());
    }
}