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

    // Listen for the response from wallet-service on the reply-to queue
    @RabbitListener(queues = "${app.wallet.reply-to-queue}")
    public void receiveWalletBalance(WalletBalanceDTO walletBalance) {
        String cacheKey = cachePrefix + walletBalance.getUserId();
        redisTemplate.opsForValue().set(cacheKey, walletBalance);
        logger.info("Updated cache for user ID: {} with balance: USD {}, BTC {}",
                walletBalance.getUserId(), walletBalance.getUsdBalance(), walletBalance.getBtcBalance());
    }
}
