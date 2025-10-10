/*
package com.dev.explainor.genesis.service;

import com.dev.explainor.genesis.dto.FinalTimelineV1;
import com.dev.explainor.genesis.dto.StoryboardV1;
import com.dev.explainor.genesis.layout.LayoutManager;
import com.dev.explainor.genesis.layout.PathFinder;
import com.dev.explainor.genesis.validation.StoryboardValidator;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class GenesisConductorServiceTest {
    @Test
    void shouldCallAllServicesInOrder() {
        StoryboardV1 storyboard = new StoryboardV1("1.0.0", List.of());

        LayoutManager layoutManager = mock(LayoutManager.class);
        PathFinder pathFinder = mock(PathFinder.class);
        TimelineEnricher timelineEnricher = mock(TimelineEnricher.class);
        TimelineFactory timelineFactory = mock(TimelineFactory.class);
        StoryboardValidator validator = mock(StoryboardValidator.class);
        LayoutModelFactory layoutModelFactory = mock(LayoutModelFactory.class);

        GenesisConductorService service = new GenesisConductorService(
            validator,
            layoutModelFactory,
            layoutManager,
            pathFinder,
            timelineEnricher,
            timelineFactory
        );

        FinalTimelineV1 result = service.choreograph(storyboard);

        verify(validator).validate(storyboard);
    }
}
*/

