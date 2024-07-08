package com.example.demo.controller;

public class HourlyForecast {
    private long timestamp;
    private double temperature;
    private String condition;
    private String description;
    private String icon;
    private String formattedTime;

    // Constructor
    public HourlyForecast(long timestamp, double temperature, String condition, String description, String icon, String formattedTime) {
        this.timestamp = timestamp;
        this.temperature = temperature;
        this.condition = condition;
        this.description = description;
        this.icon = icon;
        this.formattedTime = formattedTime;
    }

    // Getters and setters
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getFormattedTime() {
        return formattedTime;
    }

    public void setFormattedTime(String formattedTime) {
        this.formattedTime = formattedTime;
    }

    @Override
    public String toString() {
        return "HourlyForecast{" +
                "timestamp=" + timestamp +
                ", temperature=" + temperature +
                ", condition='" + condition + '\'' +
                ", description='" + description + '\'' +
                ", icon='" + icon + '\'' +
                ", formattedTime='" + formattedTime + '\'' +
                '}';
    }
}
