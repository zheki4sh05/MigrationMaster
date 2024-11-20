package org.example.migrations.utils;

public enum State {
    SUCCESS ("SUCCESS"),
    PENDING("PENDING"),
    FAILED ("FAILED");
    private final String state;
    State(String state) {
        this.state = state;
    }
    public String state() {
        return state;
    }

}
