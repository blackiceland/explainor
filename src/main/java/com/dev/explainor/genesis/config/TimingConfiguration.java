package com.dev.explainor.genesis.config;

import com.dev.explainor.genesis.timing.DefaultTimingProvider;
import com.dev.explainor.genesis.timing.TimingProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TimingConfiguration {

    @Bean
    public TimingProvider timingProvider() {
        return new DefaultTimingProvider();
    }
}

