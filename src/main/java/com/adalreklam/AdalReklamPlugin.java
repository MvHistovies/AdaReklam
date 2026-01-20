package com.adalreklam;

import com.adalreklam.commands.ReklamCommand;
import com.adalreklam.commands.ReklamAdminCommand;
import com.adalreklam.listeners.ChatListener;
import com.adalreklam.listeners.GUIListener;
import com.adalreklam.managers.ConfigManager;
import com.adalreklam.managers.EconomyManager;
import com.adalreklam.managers.AdvertisementManager;
import com.adalreklam.managers.LogManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class AdalReklamPlugin extends JavaPlugin {

    private static AdalReklamPlugin instance;
    private ConfigManager configManager;
    private EconomyManager economyManager;
    private AdvertisementManager advertisementManager;
    private LogManager logManager;
    private Economy economy;

    @Override
    public void onEnable() {
        instance = this;

        // ASCII Art Logo
        getLogger().info("================================");
        getLogger().info("   Ada Reklam Plugin v1.0");
        getLogger().info("   Paper 1.20.x");
        getLogger().info("================================");

        // Config yükleme
        configManager = new ConfigManager(this);
        configManager.loadConfig();

        // Vault kontrolü ve ekonomi sistemi
        if (!setupEconomy()) {
            getLogger().log(Level.SEVERE, "Vault bulunamadı! Plugin devre dışı bırakılıyor.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Manager'ları başlat
        logManager = new LogManager(this);
        economyManager = new EconomyManager(this, economy);
        advertisementManager = new AdvertisementManager(this);

        // Komutları kaydet
        getCommand("reklam").setExecutor(new ReklamCommand(this));
        getCommand("reklamadmin").setExecutor(new ReklamAdminCommand(this));

        // Listener'ları kaydet
        getServer().getPluginManager().registerEvents(new GUIListener(this), this);
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);

        getLogger().info("Plugin başarıyla yüklendi!");
    }

    @Override
    public void onDisable() {
        // Son logları kaydet
        if (logManager != null) {
            logManager.saveAndClose();
        }

        // Bekleyen istekleri temizle
        if (advertisementManager != null) {
            advertisementManager.clearAllRequests();
        }

        getLogger().info("Plugin kapatıldı. Görüşmek üzere!");
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }

        economy = rsp.getProvider();
        return economy != null;
    }

    // Getter metodları
    public static AdalReklamPlugin getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public EconomyManager getEconomyManager() {
        return economyManager;
    }

    public AdvertisementManager getAdvertisementManager() {
        return advertisementManager;
    }

    public LogManager getLogManager() {
        return logManager;
    }

    public Economy getEconomy() {
        return economy;
    }
}