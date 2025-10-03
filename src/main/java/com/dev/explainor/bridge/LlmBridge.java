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
                            "backgroundColor": "#DDDDDD"
                          },
                          "totalDuration": 6.5,
                           "camera": [
                            { "type": "zoom", "time": 0, "duration": 0, "to": { "scale": 1.5, "x": 440, "y": 0 } },
                            { "type": "zoom", "time": 0.1, "duration": 1.5, "to": { "scale": 1, "x": 0, "y": 0 } },
                            { "type": "pan", "time": 2, "duration": 1.5, "to": { "x": -50 } },
                            { "type": "pan", "time": 4, "duration": 1.5, "to": { "x": 50 } },
                            { "type": "pan", "time": 5.5, "duration": 0.5, "to": { "x": 0 } }
                          ],
                          "timeline": [
                            {
                              "elementId": "client-group",
                              "type": "group",
                              "action": "appear",
                              "time": 0,
                              "props": { "x": 200, "y": 360, "width": 180, "height": 100 },
                              "children": ["client-box", "client-text"]
                            },
                            {
                              "elementId": "client-box",
                              "type": "shape",
                              "action": "appear",
                              "time": 0,
                              "props": {
                                "shapeType": "rectangle",
                                "x": 0, "y": 0,
                                "width": 180, "height": 100,
                                "fillColor": "#FFFFFF",
                                "strokeColor": "#E0E0E0",
                                "strokeWidth": 1
                              }
                            },
                            {
                              "elementId": "client-text",
                              "type": "text",
                              "content": "Client",
                              "action": "appear",
                              "time": 0,
                              "props": { "x": 0, "y": 0, "fontSize": 32 }
                            },
                            {
                              "elementId": "server-group",
                              "type": "group",
                              "action": "appear",
                              "time": 1,
                              "props": { "x": 1080, "y": 360, "width": 180, "height": 100 },
                              "children": ["server-box", "server-text"]
                            },
                            {
                              "elementId": "server-box",
                              "type": "shape",
                              "action": "appear",
                              "time": 1,
                              "props": {
                                "shapeType": "rectangle",
                                "x": 0, "y": 0,
                                "width": 180, "height": 100,
                                "fillColor": "#FFFFFF",
                                "strokeColor": "#E0E0E0",
                                "strokeWidth": 1
                              }
                            },
                            {
                              "elementId": "server-text",
                              "type": "text",
                              "content": "Server",
                              "action": "appear",
                              "time": 1,
                              "props": { "x": 0, "y": 0, "fontSize": 32 }
                            },
                            {
                              "elementId": "request-arrow",
                              "type": "arrow",
                              "action": "animate",
                              "time": 2,
                              "duration": 1.5,
                              "from": { "x": 290, "y": 360 },
                              "to": { "x": 990, "y": 360 }
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
                              "elementId": "response-arrow",
                              "type": "arrow",
                              "action": "animate",
                              "time": 4,
                              "duration": 1.5,
                              "from": { "x": 990, "y": 360 },
                              "to": { "x": 290, "y": 360 }
                            },
                            {
                              "elementId": "response-label",
                              "type": "text",
                              "content": "HTTP Response",
                              "action": "appear",
                              "time": 4.5,
                              "props": { "x": 640, "y": 410, "fontSize": 24 }
                            },
                            {
                              "elementId": "request-arrow",
                              "type": "arrow",
                              "action": "disappear",
                              "time": 4
                            },
                            {
                              "elementId": "request-label",
                              "type": "text",
                              "action": "disappear",
                              "time": 4
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

