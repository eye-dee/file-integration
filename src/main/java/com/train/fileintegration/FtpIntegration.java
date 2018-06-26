package com.train.fileintegration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.file.FileNameGenerator;
import org.springframework.integration.ftp.outbound.FtpMessageHandler;
import org.springframework.integration.ftp.session.DefaultFtpSessionFactory;
import org.springframework.integration.ftp.session.FtpRemoteFileTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import java.io.File;
import java.util.UUID;

@Configuration
public class FtpIntegration {

    @Bean
    public DefaultFtpSessionFactory sessionFactory() {
        var ftp = new DefaultFtpSessionFactory();
        ftp.setUsername("file");
        ftp.setPassword("file");
        ftp.setHost("localhost");
        ftp.setPort(21);

        return ftp;
    }

    @Bean
    public MessageHandler ftpHandler() {
        FtpMessageHandler handler = new FtpMessageHandler(sessionFactory());
        handler.setRemoteDirectoryExpressionString("headers['remote-target-dir']");
        handler.setFileNameGenerator(message -> UUID.randomUUID().toString() + ".txt");
        return handler;
    }


    @Bean
    public IntegrationFlow ftpOut(MessageChannel channel, MessageHandler ftpHandler) {
        return IntegrationFlows.from(channel)
                .handle(ftpHandler)
                .get();
    }

}
