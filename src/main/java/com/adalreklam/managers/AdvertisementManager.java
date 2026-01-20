package com.adalreklam.managers;

import com.adalreklam.AdalReklamPlugin;
import com.adalreklam.models.AdType;
import com.adalreklam.models.AdRequest;
import com.adalreklam.models.Advertisement;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AdvertisementManager {
    private final AdalReklamPlugin plugin;
    private final Map<UUID, AdRequest> pendingRequests;
    private final Map<UUID, Long> chatCooldowns;
    private final Map<UUID, Long> bossbarCooldowns;
    private final Map<UUID, Long> discordCooldowns;
    private final List<Advertisement> history;

    public AdvertisementManager(AdalReklamPlugin plugin) {
        this.plugin = plugin;
        this.pendingRequests = new ConcurrentHashMap<>();
        this.chatCooldowns = new ConcurrentHashMap<>();
        this.bossbarCooldowns = new ConcurrentHashMap<>();
        this.discordCooldowns = new ConcurrentHashMap<>();
        this.history = new ArrayList<>();
    }

    public void createRequest(UUID playerId, AdType type) {
        AdRequest request = new AdRequest(playerId, type);
        pendingRequests.put(playerId, request);
    }

    public AdRequest getRequest(UUID playerId) {
        return pendingRequests.get(playerId);
    }

    public void updateRequestMessage(UUID playerId, String message) {
        AdRequest request = pendingRequests.get(playerId);
        if (request != null) {
            request.setMessage(message);
        }
    }

    public void removeRequest(UUID playerId) {
        pendingRequests.remove(playerId);
    }

    public boolean hasPendingRequest(UUID playerId) {
        return pendingRequests.containsKey(playerId);
    }

    public void addToHistory(Advertisement ad) {
        history.add(ad);
    }

    public List<Advertisement> getHistory() {
        return new ArrayList<>(history);
    }

    public void setCooldown(UUID playerId, AdType type) {
        long currentTime = System.currentTimeMillis();
        switch (type) {
            case CHAT:
                chatCooldowns.put(playerId, currentTime);
                break;
            case BOSSBAR:
                bossbarCooldowns.put(playerId, currentTime);
                break;
            case DISCORD_WEBHOOK:
                discordCooldowns.put(playerId, currentTime);
                break;
        }
    }

    public boolean isOnCooldown(UUID playerId, AdType type) {
        Map<UUID, Long> cooldownMap = getCooldownMap(type);

        if (!cooldownMap.containsKey(playerId)) {
            return false;
        }

        long cooldownTime = getCooldownTime(type) * 1000;
        long lastUse = cooldownMap.get(playerId);

        return (System.currentTimeMillis() - lastUse) < cooldownTime;
    }

    public long getRemainingCooldown(UUID playerId, AdType type) {
        Map<UUID, Long> cooldownMap = getCooldownMap(type);

        if (!cooldownMap.containsKey(playerId)) {
            return 0;
        }

        long cooldownTime = getCooldownTime(type) * 1000;
        long lastUse = cooldownMap.get(playerId);
        long remaining = cooldownTime - (System.currentTimeMillis() - lastUse);

        return Math.max(0, remaining / 1000);
    }

    private Map<UUID, Long> getCooldownMap(AdType type) {
        switch (type) {
            case CHAT:
                return chatCooldowns;
            case BOSSBAR:
                return bossbarCooldowns;
            case DISCORD_WEBHOOK:
                return discordCooldowns;
            default:
                return new HashMap<>();
        }
    }

    private long getCooldownTime(AdType type) {
        switch (type) {
            case CHAT:
                return plugin.getConfigManager().getConfig()
                        .getLong("advertisements.chat.cooldown", 3600);
            case BOSSBAR:
                return plugin.getConfigManager().getConfig()
                        .getLong("advertisements.bossbar.cooldown", 1800);
            case DISCORD_WEBHOOK:
                return plugin.getConfigManager().getConfig()
                        .getLong("discord.cooldown", 7200);
            default:
                return 3600;
        }
    }

    public void clearAllRequests() {
        pendingRequests.clear();
    }

    public int getTotalAds() {
        return history.size();
    }

    public double getTotalRevenue() {
        return history.stream()
                .filter(Advertisement::isSuccessful)
                .mapToDouble(Advertisement::getPrice)
                .sum();
    }
}