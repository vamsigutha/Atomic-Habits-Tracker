package com.vamsigutha.atomichabitstracker;

import com.google.firebase.Timestamp;

public class Task {
    private String title;
    private String description;
    private Boolean completed;
    private Timestamp timeRemainder;
    private String userId;

    public Task(){};

    public Task(String title, String description, Boolean completed, Timestamp timeRemainder, String userId) {
        this.title = title;
        this.description = description;
        this.completed = completed;
        this.timeRemainder = timeRemainder;
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public Timestamp getTimeRemainder() {
        return timeRemainder;
    }

    public void setTimeRemainder(Timestamp timeRemainder) {
        this.timeRemainder = timeRemainder;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Task{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", completed=" + completed +
                ", timeRemainder=" + timeRemainder +
                ", userId='" + userId + '\'' +
                '}';
    }
}
