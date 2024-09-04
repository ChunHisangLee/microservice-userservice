package com.jack.userservice.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletCreationMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long userId;
    private Double initialBalance;
}
