package com.dev.explainor.conductor.layout;

import com.dev.explainor.conductor.domain.Command;
import com.dev.explainor.conductor.service.SceneState;

@Deprecated(since = "Genesis", forRemoval = true)
public interface LayoutManager {
    Point calculatePosition(Command command, SceneState sceneState);
}
