package com.example.demo.controller;

import java.util.Map;

public class AirQualityData {
    private int aqi;
    private Coordinates coord;
    private Components components;
    private Map<String, Map<String, Object>> particles;
    private Map<String, Object> suggestions;

    // Getters and setters
    public int getAqi() {
        return aqi;
    }

    public void setAqi(int aqi) {
        this.aqi = aqi;
    }

    public Coordinates getCoord() {
        return coord;
    }

    public void setCoord(Coordinates coord) {
        this.coord = coord;
    }

    public Components getComponents() {
        return components;
    }

    public void setComponents(Components components) {
        this.components = components;
    }

    public Map<String, Map<String, Object>> getParticles() {
        return particles;
    }

    public void setParticles(Map<String, Map<String, Object>> particles) {
        this.particles = particles;
    }

    public Map<String, Object> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(Map<String, Object> suggestions) {
        this.suggestions = suggestions;
    }
}
