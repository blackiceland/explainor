package com.dev.explainor.genesis.validation;

import com.dev.explainor.genesis.dto.*;
import com.dev.explainor.genesis.domain.Command;
import com.dev.explainor.genesis.domain.ConnectEntitiesCommand;
import com.dev.explainor.genesis.domain.ConnectEntitiesParams;
import com.dev.explainor.genesis.domain.CreateEntityCommand;

import java.util.HashSet;
import java.util.Set;

public class StoryboardValidator {

    public ValidationResult validate(StoryboardV1 storyboard) {
        Set<String> entityIds = new HashSet<>();
        Set<String> allIds = new HashSet<>();

        for (Command command : storyboard.commands()) {
            if (allIds.contains(command.id())) {
                return ValidationResult.failure(
                    "Duplicate command ID: " + command.id()
                );
            }
            allIds.add(command.id());

            if (command instanceof CreateEntityCommand createCmd) {
                entityIds.add(createCmd.id());
            }
        }

        for (Command command : storyboard.commands()) {
            if (command instanceof ConnectEntitiesCommand(String id, ConnectEntitiesParams params)) {
                String from = params.from();
                String to = params.to();

                if (!entityIds.contains(from)) {
                    return ValidationResult.failure(
                        "ConnectEntitiesCommand '%s' references non-existent entity: from='%s'"
                            .formatted(id, from)
                    );
                }

                if (!entityIds.contains(to)) {
                    return ValidationResult.failure(
                        "ConnectEntitiesCommand '%s' references non-existent entity: to='%s'"
                            .formatted(id, to)
                    );
                }
            }
        }

        return ValidationResult.success();
    }

    public record ValidationResult(boolean valid, String errorMessage) {
        public static ValidationResult success() {
            return new ValidationResult(true, null);
        }

        public static ValidationResult failure(String message) {
            return new ValidationResult(false, message);
        }
    }
}

