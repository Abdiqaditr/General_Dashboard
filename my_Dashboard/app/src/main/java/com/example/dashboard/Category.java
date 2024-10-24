package com.example.dashboard;

import java.util.Date;

public class Category {
    private String name;
    private int iconResId;
    private int totalTasks;
    private int completedTasks;
    private Date deadline;

    public Category(String name, int iconResId, int totalTasks) {
        this.name = name;
        this.iconResId = iconResId;
        this.totalTasks = totalTasks;
        this.completedTasks = 0;
        this.deadline = null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIconResId() {
        return iconResId;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }

    public int getTotalTasks() {
        return totalTasks;
    }

    public void setTotalTasks(int totalTasks) {
        this.totalTasks = totalTasks;
    }

    public int getCompletedTasks() {
        return completedTasks;
    }

    public void setCompletedTasks(int completedTasks) {
        this.completedTasks = completedTasks;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public void incrementCompletedTasks() {
        if (completedTasks < totalTasks) {
            completedTasks++;
        }
    }

    public int getProgress() {
        return totalTasks > 0 ? (completedTasks * 100) / totalTasks : 0;
    }

    public int getTaskCount() {
        return totalTasks;
    }
}