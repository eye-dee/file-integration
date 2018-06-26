package com.train.fileintegration;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.channel.MessageChannels;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@Configuration
@RequiredArgsConstructor
public class RabbitIntegration {


    private static final String NAME = "EXAMPLE";

    @Bean
    public Exchange exchange() {
        return ExchangeBuilder.directExchange(NAME).build();
    }

    @Bean
    public Queue queue() {
        return QueueBuilder.durable(NAME).build();
    }

    @Bean
    public Binding binding(Exchange exchange, Queue queue) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(NAME)
                .noargs();
    }

    @Bean
    public IntegrationFlow amqp(
            MessageChannel channel,
            AmqpTemplate amqpTemplate) {
        return IntegrationFlows.from(channel)
                .handle((MessageHandler) amqpTemplate::convertAndSend)
                .get();
    }
}

