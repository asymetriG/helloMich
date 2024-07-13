package com.example.hellomich;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Session {
    private String senderEmail;
    private String  receiverEmail;
    private Timestamp createdAt;
    private boolean isActive;
    private double senderLang;
    private double  receiverLang;
    private double senderLong;
    private double  receiverLong;
    private String docname;

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public Session(String senderEmail, String receiverEmail, Timestamp createdAt, boolean isActive, double senderLang, double receiverLang, double senderLong, double receiverLong) {
        this.senderEmail = senderEmail;
        this.receiverEmail = receiverEmail;
        this.createdAt = createdAt;
        this.isActive = isActive;
        this.senderLang = senderLang;
        this.receiverLang = receiverLang;
        this.senderLong = senderLong;
        this.receiverLong = receiverLong;
        this.docname = senderEmail+"-"+receiverEmail;
    }

    public Map<String, Object> toHashMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("senderEmail", senderEmail);
        map.put("receiverEmail", receiverEmail);
        map.put("createdAt", createdAt);
        map.put("isActive", isActive);
        map.put("senderLang", senderLang);
        map.put("receiverLang", receiverLang);
        map.put("senderLong", senderLong);
        map.put("receiverLong", receiverLong);
        return map;
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

    public double getSenderLang() {
        return senderLang;
    }

    public void setSenderLang(double senderLang) {
        this.senderLang = senderLang;
    }

    public double getReceiverLang() {
        return receiverLang;
    }

    public void setReceiverLang(double receiverLang) {
        this.receiverLang = receiverLang;
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
