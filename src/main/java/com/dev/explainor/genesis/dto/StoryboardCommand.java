package com.dev.explainor.genesis.dto;

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
    @JsonSubTypes.Type(value = PauseCommand.class, name = "pause")
})
public sealed interface StoryboardCommand permits 
    CreateEntityCommand, 
    ConnectEntitiesCommand, 
    PauseCommand {
    
    String id();
}

