package com.jack.userservice.outbox;

public class OutboxMapper {

    // Convert DTO to Entity
    public static Outbox mapToEntity(OutboxDTO dto) {
        Outbox outbox = new Outbox();
        outbox.setEventType(dto.getEventType());
        outbox.setPayload(dto.getPayload());
        outbox.setProcessed(dto.isProcessed());
        // 'createdAt' is automatically set to current time by the entity itself
        return outbox;
    }

    // Convert Entity to DTO
    public static OutboxDTO mapToDTO(Outbox entity) {
        return OutboxDTO.builder()
                .eventType(entity.getEventType())
                .payload(entity.getPayload())
                .processed(entity.isProcessed())
                .build();
    }
}
