package com.adalreklam.models;

import org.bukkit.entity.Player;
import java.time.LocalDateTime;
import java.util.UUID;

public class Advertisement {
    private final UUID playerId;
    private final String playerName;
    private final AdType type;
    private final String message;
    private final LocalDateTime timestamp;
    private final double price;
    private final boolean successful;

    public Advertisement(UUID playerId, String playerName, AdType type,
                         String message, double price, boolean successful) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.type = type;
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.price = price;
        this.successful = successful;
    }

    // Getters
    public UUID getPlayerId() { return playerId; }
    public String getPlayerName() { return playerName; }
    public AdType getType() { return type; }
    public String getMessage() { return message; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public double getPrice() { return price; }
    public boolean isSuccessful() { return successful; }

    public String toLogFormat() {
        String status = successful ? "BAŞARILI" : "BAŞARISIZ";
        return String.format("[%s] [%s] %s: \"%s\" | Fiyat: %.0f | %s",
                timestamp.toString(),
                type.getName(),
                playerName,
                message,
                price,
                status);
    }
}