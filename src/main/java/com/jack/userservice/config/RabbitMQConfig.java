package com.jack.userservice.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;

@Configuration
public class RabbitMQConfig {

    // Inject the custom queue name from the application.yml file
    @Value("${app.wallet-creation}")
    private String walletCreationQueue;

    // Inject RabbitMQ properties from application.yml
    @Value("${spring.rabbitmq.host}")
    private String rabbitHost;

    @Value("${spring.rabbitmq.port}")
    private int rabbitPort;

    @Value("${spring.rabbitmq.username}")
    private String rabbitUsername;

    @Value("${spring.rabbitmq.password}")
    private String rabbitPassword;

    // Define the queue for wallet creation
    @Bean
    public Queue walletCreationQueue() {
        return new Queue(walletCreationQueue, true);  // Durable queue for persistence
    }

    // Configure the ConnectionFactory using values from application.yml
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(rabbitHost);  // Dynamically set the host from application.yml
        connectionFactory.setUsername(rabbitUsername);  // Set the username
        connectionFactory.setPassword(rabbitPassword);  // Set the password
        connectionFactory.setPort(rabbitPort);  // Set the port
        return connectionFactory;
    }

    // Configure RabbitTemplate and pass the connectionFactory
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }
}
