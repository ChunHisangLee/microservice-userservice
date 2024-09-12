package com.jack.userservice.client;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class WalletBalanceRequestSender {

    private final RabbitTemplate rabbitTemplate;

    @Value("${app.wallet.exchange}")
    private String walletExchange;

    @Value("${app.wallet.routing-key.balance}")
    private String walletBalanceRoutingKey;

    @Value("${app.wallet.reply-to-queue}")
    private String replyToQueue;

    public WalletBalanceRequestSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendBalanceRequest(Long userId) {
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setReplyTo(replyToQueue);
        Message message = rabbitTemplate.getMessageConverter().toMessage(userId, messageProperties);
        rabbitTemplate.convertAndSend(walletExchange, walletBalanceRoutingKey, message);
    }
}
