package com.jack.userservice.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jack.userservice.message.WalletCreationMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OutboxProcessorService {

    private final OutboxRepository outboxRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public OutboxProcessorService(OutboxRepository outboxRepository, RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.outboxRepository = outboxRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedRate = 5000) // Check the outbox every 5 seconds
    public void processOutbox() {
        List<Outbox> unprocessedMessages = outboxRepository.findByProcessedFalse();

        for (Outbox outbox : unprocessedMessages) {
            try {
                // Deserialize the message payload
                WalletCreationMessage message = objectMapper.readValue(outbox.getPayload(), WalletCreationMessage.class);

                // Send the message to RabbitMQ
                rabbitTemplate.convertAndSend("walletCreationQueue", message);

                // Mark the message as processed
                outbox.setProcessed(true);
                outboxRepository.save(outbox);

            } catch (Exception e) {
                // Log and retry in the next iteration
                System.err.println("Failed to process outbox message: " + e.getMessage());
            }
        }
    }
}
