package com.adalreklam.models;

import java.time.LocalDateTime;
import java.util.UUID;

public class AdRequest {
    private final UUID playerId;
    private final AdType type;
    private String message;
    private final LocalDateTime createdAt;
    private boolean confirmed;

    public AdRequest(UUID playerId, AdType type) {
        this.playerId = playerId;
        this.type = type;
        this.message = "";
        this.createdAt = LocalDateTime.now();
        this.confirmed = false;
    }

    // Getters & Setters
    public UUID getPlayerId() { return playerId; }
    public AdType getType() { return type; }
    public String getMessage() { return message; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public boolean isConfirmed() { return confirmed; }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public boolean isExpired() {
        // 5 dakika sonra expire
        return LocalDateTime.now().minusMinutes(5).isAfter(createdAt);
    }
}