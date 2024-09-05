package com.jack.userservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue walletCreationQueue() {
        return new Queue("walletCreationQueue", true);
    }

    @Bean
    public DirectExchange walletExchange() {
        return new DirectExchange("walletExchange");
    }

    @Bean
    public Binding binding(Queue walletCreationQueue, DirectExchange walletExchange) {
        return BindingBuilder.bind(walletCreationQueue).to(walletExchange).with("walletRoutingKey");
    }
}
