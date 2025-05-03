package com.example.zenioapp;

public class ChatRequest {
    private String inputs;
    private ChatParameters parameters;

    public ChatRequest(String inputs, ChatParameters parameters) {
        this.inputs = inputs;
        this.parameters = parameters;
    }

    public String getInputs() {
        return inputs;
    }

    public ChatParameters getParameters() {
        return parameters;
    }
}