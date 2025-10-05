package com.dev.explainor.renderer.domain;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TimelineEvent(
    String elementId,
    String type,
    String action,
    double time,
    Double duration,
    Map<String, Object> props,
    List<String> children,
    String content,
    AssetReference asset,
    Coordinate from,
    Coordinate to,
    List<Coordinate> path
) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String elementId;
        private String type;
        private String action;
        private double time;
        private Double duration;
        private Map<String, Object> props;
        private List<String> children;
        private String content;
        private AssetReference asset;
        private Coordinate from;
        private Coordinate to;
        private List<Coordinate> path;

        public Builder elementId(String elementId) {
            this.elementId = elementId;
            return this;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder action(String action) {
            this.action = action;
            return this;
        }

        public Builder time(double time) {
            this.time = time;
            return this;
        }

        public Builder duration(Double duration) {
            this.duration = duration;
            return this;
        }

        public Builder props(Map<String, Object> props) {
            this.props = props;
            return this;
        }

        public Builder children(List<String> children) {
            this.children = children;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder asset(AssetReference asset) {
            this.asset = asset;
            return this;
        }

        public Builder from(Coordinate from) {
            this.from = from;
            return this;
        }

        public Builder to(Coordinate to) {
            this.to = to;
            return this;
        }

        public Builder path(List<Coordinate> path) {
            this.path = path;
            return this;
        }

        public TimelineEvent build() {
            return new TimelineEvent(
                elementId, type, action, time, duration,
                props, children, content, asset, from, to, path
            );
        }
    }
}
