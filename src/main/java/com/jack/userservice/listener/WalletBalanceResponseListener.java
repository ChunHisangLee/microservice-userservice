package com.jack.userservice.listener;

import com.jack.userservice.dto.WalletBalanceDTO;
import com.jack.userservice.dto.WalletResponseDTO;
import com.jack.userservice.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class WalletBalanceResponseListener {

    private static final Logger logger = LoggerFactory.getLogger(WalletBalanceResponseListener.class);
    private final UserService userService;

    public WalletBalanceResponseListener(UserService userService) {
        this.userService = userService;
    }

    // Listen for the response from wallet-service on the reply-to queue
    @RabbitListener(queues = "${app.wallet.reply-to-queue}")
    public void handleWalletBalanceResponse(WalletResponseDTO walletResponseDTO) {
        logger.info("Received wallet balance response for user ID: {}", walletResponseDTO.getUserId());

        // Cache the wallet balance
        WalletBalanceDTO balanceDTO = new WalletBalanceDTO(walletResponseDTO.getUserId(), walletResponseDTO.getUsdBalance(), walletResponseDTO.getBtcBalance());
        userService.cacheWalletBalance(balanceDTO);

        logger.info("Cached wallet balance for user ID: {}", walletResponseDTO.getUserId());
    }

}
