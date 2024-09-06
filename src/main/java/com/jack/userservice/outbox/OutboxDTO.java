package com.jack.userservice.outbox;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutboxDTO {
    private String eventType;
    private String payload;
    private boolean processed;
}
