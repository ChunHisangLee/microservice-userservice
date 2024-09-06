package com.jack.userservice.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String USER_CREATED_QUEUE = "user-created-queue";

    @Bean
    public Queue userCreatedQueue() {
        return new Queue(USER_CREATED_QUEUE, false);
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        // Autowired ConnectionFactory is automatically configured via Spring Boot AMQP starter
        return new RabbitTemplate();
    }
}
