package com.example.hellomich;

import com.google.firebase.Timestamp;

import java.util.HashMap;
import java.util.Map;

public class Session {
    private String senderEmail;
    private String receiverEmail;
    private Timestamp createdAt;
    private boolean isActive;
    private double senderLat;
    private double receiverLat;
    private double senderLong;
    private double receiverLong;

    public Session(String senderEmail, String receiverEmail, Timestamp createdAt, boolean isActive, double senderLat, double receiverLat, double senderLong, double receiverLong) {
        this.senderEmail = senderEmail;
        this.receiverEmail = receiverEmail;
        this.createdAt = createdAt;
        this.isActive = isActive;
        this.senderLat = senderLat;
        this.receiverLat = receiverLat;
        this.senderLong = senderLong;
        this.receiverLong = receiverLong;
    }

    public Map<String, Object> toHashMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("senderEmail", senderEmail);
        map.put("receiverEmail", receiverEmail);
        map.put("createdAt", createdAt);
        map.put("isActive", isActive);
        map.put("senderLat", senderLat);
        map.put("receiverLat", receiverLat);
        map.put("senderLong", senderLong);
        map.put("receiverLong", receiverLong);
        return map;
    }

    // Getters and Setters
    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public double getSenderLat() {
        return senderLat;
    }

    public void setSenderLat(double senderLat) {
        this.senderLat = senderLat;
    }

    public double getreceiverLat() {
        return receiverLat;
    }

    public void setreceiverLat(double receiverLat) {
        this.receiverLat = receiverLat;
    }

    public double getSenderLong() {
        return senderLong;
    }

    public void setSenderLong(double senderLong) {
        this.senderLong = senderLong;
    }

    public double getReceiverLong() {
        return receiverLong;
    }

    public void setReceiverLong(double receiverLong) {
        this.receiverLong = receiverLong;
    }
}
