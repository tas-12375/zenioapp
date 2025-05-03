package com.example.zenioapp;

public class ChatParameters {
    private double temperature;
    private int max_tokens;

    public ChatParameters(double temperature, int max_tokens) {
        this.temperature = temperature;
        this.max_tokens = max_tokens;
    }

    public double getTemperature() {
        return temperature;
    }

    public int getMaxTokens() {
        return max_tokens;
    }
}