package com.dev.explainor.bridge;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Service
public class LlmBridge {

    // private final WebClient webClient;
    // @Value("${llm.api.key}")
    // private String apiKey;
    // @Value("${llm.api.url}")
    // private String apiUrl;

    // public LlmBridge(WebClient webClient) {
    //     this.webClient = webClient;
    // }

    public Mono<JsonNode> getAnimationScenario(String prompt) {
        // TODO: Implement actual API call to an LLM
        // For now, return a hardcoded JSON for testing purposes

        String jsonString = """
                {
                  "canvas": {
                    "width": 1280,
                    "height": 720,
                    "backgroundColor": "#ffffff"
                  },
                  "totalDuration": 6,
                  "timeline": [
                    {
                      "elementId": "client-icon",
                      "type": "icon",
                      "asset": "laptop",
                      "action": "appear",
                      "time": 0,
                      "props": { "x": 150, "y": 360, "fontSize": 200 }
                    },
                    {
                      "elementId": "server-icon",
                      "type": "icon",
                      "asset": "server",
                      "action": "appear",
                      "time": 0.5,
                      "props": { "x": 1130, "y": 360, "fontSize": 200 }
                    },
                    {
                      "elementId": "client-text",
                      "type": "text",
                      "content": "Клиент",
                      "action": "appear",
                      "time": 0,
                      "props": { "x": 150, "y": 500, "fontSize": 30 }
                    },
                    {
                      "elementId": "server-text",
                      "type": "text",
                      "content": "Сервер",
                      "action": "appear",
                      "time": 0.5,
                      "props": { "x": 1130, "y": 500, "fontSize": 30 }
                    }
                  ]
                }
                """;

        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode jsonNode = mapper.readTree(jsonString);
            return Mono.just(jsonNode);
        } catch (IOException e) {
            return Mono.error(e);
        }
    }
}

