package com.dev.explainor.genesis.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "layout.graph")
public class LayoutProperties {

    private double layerSpacing = 160.0;
    private double nodeSpacing = 220.0;
    private double gridStep = 40.0;

    public double getLayerSpacing() {
        return layerSpacing;
    }

    public void setLayerSpacing(double layerSpacing) {
        this.layerSpacing = layerSpacing;
    }

    public double getNodeSpacing() {
        return nodeSpacing;
    }

    public void setNodeSpacing(double nodeSpacing) {
        this.nodeSpacing = nodeSpacing;
    }

    public double getGridStep() {
        return gridStep;
    }

    public void setGridStep(double gridStep) {
        this.gridStep = gridStep;
    }
}



