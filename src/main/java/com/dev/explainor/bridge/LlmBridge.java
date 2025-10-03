package com.dev.explainor.bridge;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Service
public class LlmBridge {

    public Mono<JsonNode> getAnimationScenario(String prompt) {
        String jsonString = """
                {
                  "canvas": {
                    "width": 1280,
                    "height": 720,
                    "backgroundColor": "#EAEAEA"
                  },
                  "totalDuration": 10,
                  "timeline": [
                    {
                      "elementId": "client-icon",
                      "type": "icon",
                      "asset": "laptop",
                      "action": "appear",
                      "time": 0,
                      "props": { "x": 200, "y": 360 }
                    },
                    {
                      "elementId": "client-text",
                      "type": "text",
                      "content": "Client",
                      "action": "appear",
                      "time": 0.3,
                      "props": { "x": 200, "y": 480, "fontSize": 32 }
                    },
                    {
                      "elementId": "server-icon",
                      "type": "icon",
                      "asset": "server",
                      "action": "appear",
                      "time": 1,
                      "props": { "x": 1080, "y": 360 }
                    },
                    {
                      "elementId": "server-text",
                      "type": "text",
                      "content": "Server",
                      "action": "appear",
                      "time": 1.3,
                      "props": { "x": 1080, "y": 480, "fontSize": 32 }
                    },
                    {
                      "elementId": "request-arrow",
                      "type": "arrow",
                      "action": "animate",
                      "time": 2,
                      "duration": 1.5,
                      "from": { "x": 280, "y": 360 },
                      "to": { "x": 1000, "y": 360 }
                    },
                            {
                              "elementId": "request-label",
                              "type": "text",
                              "content": "HTTP Request",
                              "action": "appear",
                              "time": 2.5,
                              "props": { "x": 640, "y": 310, "fontSize": 24 }
                            },
                            {
                              "elementId": "request-arrow",
                              "action": "disappear",
                              "time": 6.0
                            },
                            {
                              "elementId": "request-label",
                              "action": "disappear",
                              "time": 6.0
                            },
                            {
                              "elementId": "response-arrow",
                              "type": "arrow",
                              "action": "animate",
                              "time": 6.5,
                              "duration": 1.5,
                              "from": { "x": 1000, "y": 360 },
                              "to": { "x": 280, "y": 360 }
                            },
                    {
                      "elementId": "response-label",
                      "type": "text",
                      "content": "Server Response",
                      "action": "appear",
                      "time": 7,
                      "props": { "x": 640, "y": 410, "fontSize": 24 }
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

