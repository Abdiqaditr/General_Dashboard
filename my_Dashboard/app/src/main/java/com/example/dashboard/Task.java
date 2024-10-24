package com.example.dashboard;

public class Task {
    private String name;
    private String completionTime;

    public Task(String name, String completionTime) {
        this.name = name;
        this.completionTime = completionTime;
    }

    public String getName() {
        return name;
    }

    public String getCompletionTime() {
        return completionTime;
    }
}
