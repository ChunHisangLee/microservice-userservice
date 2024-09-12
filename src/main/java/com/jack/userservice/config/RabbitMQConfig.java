package com.jack.userservice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQConfig.class);

    // Inject the exchange and routing key from the application.yml file
    @Value("${app.wallet.exchange}")
    private String walletExchange;

    // Define the TopicExchange for wallet creation
    @Bean
    public TopicExchange walletExchange() {
        logger.info("Creating RabbitMQ exchange: {}", walletExchange);
        return new TopicExchange(walletExchange);
    }

    // RabbitTemplate is auto-configured by Spring Boot if you're not customizing it
    @Bean
    public RabbitTemplate rabbitTemplate() {
        return new RabbitTemplate();
    }
}
