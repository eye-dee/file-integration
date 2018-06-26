package com.train.fileintegration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ImageBanner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.channel.MessageChannels;
import org.springframework.integration.file.FileHeaders;
import org.springframework.integration.file.dsl.Files;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.util.ReflectionUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;


@Configuration
public class ChannelConfig {

    @Bean
    public IntegrationFlow fileFlow(
            @Value("${input-dir:${HOMEPATH}/example}") File in,
            MessageChannel channel,
            Environment environment) {
        return IntegrationFlows.from(Files.inboundAdapter(in)
                .autoCreateDirectory(true)
                .preventDuplicates(true)
                .patternFilter("*.jpg"), p -> p.poller(pm -> pm.fixedRate(1000)))
                .transform(File.class, (File source) -> {
                    try (var baos = new ByteArrayOutputStream(); var ps = new PrintStream(baos)) {
                        ImageBanner imageBanner = new ImageBanner(new FileSystemResource(source));
                        imageBanner.printBanner(environment, getClass(), ps);
                        return MessageBuilder.withPayload(baos.toByteArray())
                                .setHeader(FileHeaders.FILENAME, source.getAbsoluteFile().getName())
                                .build();
                    } catch (IOException e) {
                        ReflectionUtils.rethrowRuntimeException(e);
                    }
                    return null;
                })
                .channel(channel)
                .get();
    }

    @Bean
    public MessageChannel channel() {
        return MessageChannels.publishSubscribe().get();
    }

}
