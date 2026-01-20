package com.adalreklam.models;

import org.bukkit.entity.Player;
import java.time.LocalDateTime;
import java.util.UUID;

public enum AdType {
    DISCORD_WEBHOOK("Discord Webhook", "§6💬 Discord Reklam", 300000),
    CHAT("Chat Reklam", "§b💭 Chat Reklam", 150000),
    BOSSBAR("Bossbar Reklam", "§e📊 Bossbar Reklam", 200000);

    private final String name;
    private final String displayName;
    private final double defaultPrice;

    AdType(String name, String displayName, double defaultPrice) {
        this.name = name;
        this.displayName = displayName;
        this.defaultPrice = defaultPrice;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getDefaultPrice() {
        return defaultPrice;
    }
}