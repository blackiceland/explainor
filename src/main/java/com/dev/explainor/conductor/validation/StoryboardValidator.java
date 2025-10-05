package com.dev.explainor.conductor.validation;

import com.dev.explainor.conductor.domain.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class StoryboardValidator {

    public ValidationResult validate(Storyboard storyboard) {
        if (storyboard == null) {
            throw new NullPointerException("Storyboard cannot be null");
        }

        List<String> errors = new ArrayList<>();
        Set<String> declaredEntities = new HashSet<>();
        Set<String> seenIds = new HashSet<>();

        if (storyboard.commands() == null) {
            throw new IllegalArgumentException("Commands list cannot be null");
        }

        // Empty storyboard is valid
        if (storyboard.commands().isEmpty()) {
            return new ValidationResult(true, errors);
        }

        for (int i = 0; i < storyboard.commands().size(); i++) {
            Command command = storyboard.commands().get(i);
            String commandContext = "Command #" + (i + 1) + " (id: " + command.id() + ")";

            if (command.id() == null || command.id().isBlank()) {
                errors.add(commandContext + ": id is null or blank");
                continue;
            }

            // Check for duplicate IDs
            if (!seenIds.add(command.id())) {
                errors.add(commandContext + ": duplicate id '" + command.id() + "' already exists");
            }

            if (command instanceof CreateEntityCommand createCmd) {
                validateCreateEntity(createCmd, declaredEntities, errors, commandContext);
            } else if (command instanceof ConnectEntitiesCommand connectCmd) {
                validateConnectEntities(connectCmd, declaredEntities, errors, commandContext);
            } else if (command instanceof PauseCommand pauseCmd) {
                validatePause(pauseCmd, errors, commandContext);
            } else if (command instanceof FocusOnCommand focusCmd) {
                validateFocusOn(focusCmd, declaredEntities, errors, commandContext);
            }
        }

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join("; ", errors));
        }

        return new ValidationResult(true, errors);
    }

    private void validateCreateEntity(CreateEntityCommand command, Set<String> declaredEntities,
                                      List<String> errors, String context) {
        if (command.params() == null) {
            errors.add(context + ": params is null");
            return;
        }

        if (command.params().label() == null || command.params().label().isBlank()) {
            errors.add(context + ": label is null or blank");
        }

        declaredEntities.add(command.id());
    }

    private void validateConnectEntities(ConnectEntitiesCommand command, Set<String> declaredEntities,
                                         List<String> errors, String context) {
        if (command.params() == null) {
            errors.add(context + ": params is null");
            return;
        }

        String from = command.params().from();
        String to = command.params().to();

        if (from == null || from.isBlank()) {
            errors.add(context + ": 'from' parameter is null or blank");
        } else if (!declaredEntities.contains(from)) {
            errors.add(context + ": 'from' entity '" + from + "' not found");
        }

        if (to == null || to.isBlank()) {
            errors.add(context + ": 'to' parameter is null or blank");
        } else if (!declaredEntities.contains(to)) {
            errors.add(context + ": 'to' entity '" + to + "' not found");
        }
    }

    private void validatePause(PauseCommand command, List<String> errors, String context) {
        if (command.params() == null) {
            errors.add(context + ": params is null");
            return;
        }

        if (command.params().duration() <= 0) {
            errors.add(context + ": duration must be positive");
        }
    }

    private void validateFocusOn(FocusOnCommand command, Set<String> declaredEntities,
                                  List<String> errors, String context) {
        if (command.params() == null) {
            errors.add(context + ": params is null");
            return;
        }

        String target = command.params().target();
        String area = command.params().area();
        Double scale = command.params().scale();

        // Must have at least one parameter
        if (target == null && area == null && scale == null) {
            errors.add(context + ": must specify at least one of: target, area, or scale");
        }

        if (target != null && !target.equals("center") && !declaredEntities.contains(target)) {
            errors.add(context + ": target entity '" + target + "' not found");
        }

        if (scale != null && scale <= 0) {
            errors.add(context + ": scale must be positive");
        }
    }

    public record ValidationResult(boolean isValid, List<String> errors) {
        public String getErrorMessage() {
            return String.join("; ", errors);
        }
    }
}

