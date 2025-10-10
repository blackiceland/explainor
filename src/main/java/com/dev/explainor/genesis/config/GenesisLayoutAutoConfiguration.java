package com.dev.explainor.genesis.config;

import com.dev.explainor.genesis.validation.StoryboardValidator;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(LayoutProperties.class)
public class GenesisLayoutAutoConfiguration {

    @Bean
    public StoryboardValidator storyboardValidator() {
        return new StoryboardValidator();
    }
}


