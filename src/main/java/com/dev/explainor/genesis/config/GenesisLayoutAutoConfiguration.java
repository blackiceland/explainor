package com.dev.explainor.genesis.config;

import com.dev.explainor.genesis.layout.DummyLayoutManager;
import com.dev.explainor.genesis.layout.GraphBasedLayoutManager;
import com.dev.explainor.genesis.layout.LayoutManager;
import com.dev.explainor.genesis.layout.PathFinder;
import com.dev.explainor.genesis.layout.OrthogonalPathFinder;
import com.dev.explainor.genesis.validation.StoryboardValidator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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

    @Bean
    @ConditionalOnMissingBean(PathFinder.class)
    public PathFinder orthogonalPathFinder(LayoutProperties properties) {
        return new OrthogonalPathFinder(properties);
    }

    @Bean
    @ConditionalOnProperty(name = "layout.graph.manager", havingValue = "graph", matchIfMissing = true)
    @Qualifier("genesisLayoutManager")
    public LayoutManager graphBasedLayoutManager(LayoutProperties properties) {
        return new GraphBasedLayoutManager(properties);
    }

    @Configuration
    @ConditionalOnProperty(prefix = "layout", name = "algorithm", havingValue = "dummy", matchIfMissing = true)
    static class DummyLayoutConfiguration {
        @Bean(name = "genesisLayoutManager")
        @ConditionalOnMissingBean(LayoutManager.class)
        LayoutManager layoutManager(LayoutProperties props) {
            return new DummyLayoutManager();
        }
    }
}


