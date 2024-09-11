package com.jack.userservice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;

@Configuration
public class RabbitMQConfig {
    private static final Logger logger = LoggerFactory.getLogger(RabbitMQConfig.class);

    // Inject the custom queue name from the application.yml file
    @Value("${app.wallet-creation}")
    private String walletCreationQueue;

    // Inject RabbitMQ properties from application.yml
    @Value("${spring.rabbitmq.host:docker}")
    private String rabbitmqHost;

    @Value("${spring.rabbitmq.port}")
    private int rabbitmqPort;

    @Value("${spring.rabbitmq.username}")
    private String rabbitmqUsername;

    @Value("${spring.rabbitmq.password}")
    private String rabbitmqPassword;

    // Define the queue for wallet creation
    @Bean
    public Queue walletCreationQueue() {
        logger.info("Creating RabbitMQ queue: {}", walletCreationQueue);
        return new Queue(walletCreationQueue, true);  // Durable queue for persistence
    }

    // Configure the ConnectionFactory using values from application.yml
    @Bean
    public ConnectionFactory connectionFactory() {
        logger.info("Initializing RabbitMQ ConnectionFactory with the following details:");
        logger.info("Host: {}", rabbitmqHost);
        logger.info("Port: {}", rabbitmqPort);
        logger.info("Username: {}", rabbitmqUsername);

        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(rabbitmqHost);  // Dynamically set the host from application.yml
        connectionFactory.setUsername(rabbitmqUsername);  // Set the username
        connectionFactory.setPassword(rabbitmqPassword);  // Set the password
        connectionFactory.setPort(rabbitmqPort);  // Set the port
        logger.info("RabbitMQ ConnectionFactory initialized successfully.");
        return connectionFactory;
    }

    // Configure RabbitTemplate and pass the connectionFactory
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }
}
