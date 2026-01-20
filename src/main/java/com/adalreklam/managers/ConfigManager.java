package com.adalreklam.managers;

import com.adalreklam.AdalReklamPlugin;
import com.adalreklam.models.AdType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class ConfigManager {
    private final AdalReklamPlugin plugin;
    private FileConfiguration config;
    private FileConfiguration messages;

    public ConfigManager(AdalReklamPlugin plugin) {
        this.plugin = plugin;
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();
        config = plugin.getConfig();

        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        messages = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public void reloadConfig() {
        plugin.reloadConfig();
        config = plugin.getConfig();
        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        messages = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public double getPrice(AdType type) {
        switch (type) {
            case DISCORD_WEBHOOK:
                return config.getDouble("prices.discord-webhook", 300000);
            case CHAT:
                return config.getDouble("prices.chat-reklam", 150000);
            case BOSSBAR:
                return config.getDouble("prices.bossbar-reklam", 200000);
            default:
                return 0;
        }
    }

    public String getMessage(String path) {
        String msg = messages.getString("messages." + path, "&cMesaj bulunamadı: " + path);
        String prefix = messages.getString("prefix", "&6[Ada Reklam] &r");
        return msg.replace("{prefix}", prefix);
    }

    public FileConfiguration getConfig() {
        return config;
    }
}