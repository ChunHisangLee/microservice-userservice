package com.jack.userservice.listener;

import com.jack.userservice.dto.WalletBalanceDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class WalletBalanceResponseListener {
    private static final Logger logger = LoggerFactory.getLogger(WalletBalanceResponseListener.class);

    private final RedisTemplate<String, WalletBalanceDTO> redisTemplate;

    @Value("${app.wallet.cache-prefix}")
    private String cachePrefix;

    public WalletBalanceResponseListener(RedisTemplate<String, WalletBalanceDTO> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @RabbitListener(queues = "${app.wallet.reply-to-queue}")
    public void receiveWalletBalance(WalletBalanceDTO walletBalance) {
        if (walletBalance != null && walletBalance.getUserId() != null) {
            String cacheKey = cachePrefix + walletBalance.getUserId();
            WalletBalanceDTO updatedBalance = redisTemplate.opsForValue().get(cacheKey);

            if (updatedBalance != null) {
                logger.info("Fetched updated balance from Redis for user ID: {} - USD: {}, BTC: {}",
                        walletBalance.getUserId(), updatedBalance.getUsdBalance(), updatedBalance.getBtcBalance());
            } else {
                logger.warn("No balance found in Redis for user ID: {}", walletBalance.getUserId());
            }
        } else {
            logger.error("Received invalid wallet balance response: {}", walletBalance);
        }
    }
}
