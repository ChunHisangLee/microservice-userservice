package com.jack.userservice.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletResponseDTO {
    private Long userId;
    private double usdBalance;
    private double btcBalance;
}
