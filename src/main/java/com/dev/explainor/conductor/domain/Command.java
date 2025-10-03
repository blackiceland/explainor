package com.dev.explainor.conductor.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "command"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = CreateEntityCommand.class, name = "create_entity"),
    @JsonSubTypes.Type(value = ConnectEntitiesCommand.class, name = "connect_entities"),
    @JsonSubTypes.Type(value = PauseCommand.class, name = "pause"),
    @JsonSubTypes.Type(value = FocusOnCommand.class, name = "focus_on")
})
public interface Command {
    String id();
}
