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
                    "backgroundColor": "#f0f4f8"
                  },
                  "totalDuration": 8,
                  "timeline": [
                    {
                      "elementId": "client-icon",
                      "type": "icon",
                      "asset": "laptop",
                      "action": "appear",
                      "time": 0,
                      "props": { "x": 200, "y": 360, "fontSize": 100 }
                    },
                    {
                      "elementId": "client-text",
                      "type": "text",
                      "content": "Клиент",
                      "action": "appear",
                      "time": 0.3,
                      "props": { "x": 200, "y": 480, "fontSize": 32 }
                    },
                    {
                      "elementId": "server-icon",
                      "type": "animated-icon",
                      "asset": "https://assets9.lottiefiles.com/packages/lf20_96b_kofh.json",
                      "action": "appear",
                      "time": 1,
                      "props": { "x": 1080, "y": 360 }
                    },
                    {
                      "elementId": "server-text",
                      "type": "text",
                      "content": "Сервер",
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
                      "content": "HTTP запрос",
                      "action": "appear",
                      "time": 2.5,
                      "props": { "x": 640, "y": 310, "fontSize": 24 }
                    },
                    {
                      "elementId": "response-arrow",
                      "type": "arrow",
                      "action": "animate",
                      "time": 4.5,
                      "duration": 1.5,
                      "from": { "x": 1000, "y": 380 },
                      "to": { "x": 280, "y": 380 }
                    },
                    {
                      "elementId": "response-label",
                      "type": "text",
                      "content": "Ответ сервера",
                      "action": "appear",
                      "time": 5,
                      "props": { "x": 640, "y": 430, "fontSize": 24 }
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

