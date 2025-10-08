package com.dev.explainor.helios.layout;

import com.dev.explainor.helios.service.SceneState;
import com.dev.explainor.genesis.domain.Command;

@Deprecated(since = "Genesis", forRemoval = true)
public interface LayoutManager {
    Point calculatePosition(Command command, SceneState sceneState);
}
