package com.dev.explainor.genesis.api;

import com.dev.explainor.genesis.dto.FinalTimelineV1;
import com.dev.explainor.genesis.dto.StoryboardV1;
import com.dev.explainor.genesis.service.GenesisConductorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/genesis")
public class GenesisController {

    private static final Logger log = LoggerFactory.getLogger(GenesisController.class);

    private final GenesisConductorService conductorService;

    public GenesisController(GenesisConductorService conductorService) {
        this.conductorService = conductorService;
    }

    @PostMapping("/choreograph")
    public ResponseEntity<?> choreograph(@RequestBody @jakarta.validation.Valid StoryboardV1 storyboard) {
        log.info("Received choreograph request for storyboard v{}", storyboard.version());
        
        try {
            FinalTimelineV1 timeline = conductorService.choreograph(storyboard);
            return ResponseEntity.ok(timeline);
        } catch (IllegalArgumentException e) {
            log.error("Invalid request: {}", e.getMessage());
            ErrorResponse error = ErrorResponse.of("VALIDATION_ERROR", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            log.error("Internal error during choreography", e);
            ErrorResponse error = ErrorResponse.of("INTERNAL_ERROR", "An unexpected error occurred");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<HealthResponse> health() {
        return ResponseEntity.ok(new HealthResponse(
            "ok",
            StoryboardV1.CURRENT_VERSION,
            FinalTimelineV1.CURRENT_VERSION
        ));
    }

    public record HealthResponse(
        String status,
        String storyboardVersion,
        String timelineVersion
    ) {}
}

