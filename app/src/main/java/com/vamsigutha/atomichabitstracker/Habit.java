package com.vamsigutha.atomichabitstracker;

import com.google.firebase.Timestamp;

import java.util.List;

public class Habit {
    private String title;
    private String description;
    private Boolean completed;
    private List<String> days;
    private Timestamp remainder;
    private String userId;

    public Habit(){};

    public Habit(String title, String description, Boolean completed, List<String> days, Timestamp remainder, String userId) {
        this.title = title;
        this.description = description;
        this.completed = completed;
        this.days = days;
        this.remainder = remainder;
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

    public List<String> getDays() {
        return days;
    }

    public void setDays(List<String> days) {
        this.days = days;
    }

    public Timestamp getRemainder() {
        return remainder;
    }

    public void setRemainder(Timestamp remainder) {
        this.remainder = remainder;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Habit{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", completed=" + completed +
                ", days=" + days +
                ", remainder=" + remainder +
                ", userId='" + userId + '\'' +
                '}';
    }
}
