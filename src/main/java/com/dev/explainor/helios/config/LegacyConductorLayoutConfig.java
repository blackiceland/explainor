package com.dev.explainor.helios.config;

import com.dev.explainor.helios.layout.GraphBasedLayoutManager;
import com.dev.explainor.helios.layout.LayoutManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration("legacyConductorConfig")
@ConditionalOnProperty(prefix = "legacy", name = "enabled", havingValue = "true")
public class LegacyConductorLayoutConfig {

    @Bean(name = "legacyConductorLayoutManager")
    public LayoutManager legacyLayoutManager(GraphBasedLayoutManager graphBasedLayoutManager) {
        return graphBasedLayoutManager;
    }
}


