package com.train.fileintegration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@Configuration
public class PostgresIntegration {

    @Bean
    public IntegrationFlow postgres(MessageChannel channel, JdbcTemplate jdbcTemplate) {
        return IntegrationFlows.from(channel)
                .handle(m -> jdbcTemplate.update("INSERT INTO messages(message) VALUES(?)", m.getPayload().toString()))
                .get();
    }

}
