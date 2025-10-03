package com.dev.explainor.conductor.layout;

import com.dev.explainor.conductor.domain.Command;
import com.dev.explainor.conductor.service.SceneState;

public interface LayoutManager {
    Point calculatePosition(Command command, SceneState sceneState);
}
