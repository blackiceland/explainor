package com.dev.explainor.genesis.validation;

import com.dev.explainor.genesis.dto.*;

import java.util.HashSet;
import java.util.Set;

public class StoryboardValidator {

    public ValidationResult validate(StoryboardV1 storyboard) {
        Set<String> entityIds = new HashSet<>();
        Set<String> allIds = new HashSet<>();

        for (StoryboardCommand command : storyboard.commands()) {
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

        for (StoryboardCommand command : storyboard.commands()) {
            if (command instanceof ConnectEntitiesCommand connectCmd) {
                String from = connectCmd.params().from();
                String to = connectCmd.params().to();

                if (!entityIds.contains(from)) {
                    return ValidationResult.failure(
                        "ConnectEntitiesCommand '%s' references non-existent entity: from='%s'"
                            .formatted(connectCmd.id(), from)
                    );
                }

                if (!entityIds.contains(to)) {
                    return ValidationResult.failure(
                        "ConnectEntitiesCommand '%s' references non-existent entity: to='%s'"
                            .formatted(connectCmd.id(), to)
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

