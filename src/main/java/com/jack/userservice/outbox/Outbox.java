package com.jack.userservice.outbox;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "outbox", indexes = {
        @Index(name = "idx_event_type", columnList = "eventType"),
        @Index(name = "idx_processed", columnList = "processed")
})
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Outbox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Event type cannot be blank")
    @Column(nullable = false)
    private String eventType;

    @NotBlank(message = "Payload cannot be blank")
    @Lob
    @Column(nullable = false)
    private String payload; // The message payload (usually serialized JSON)

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private boolean processed = false; // Whether the message has been processed

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Outbox outbox)) return false;
        return Objects.equals(id, outbox.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
