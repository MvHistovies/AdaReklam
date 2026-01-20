package com.adalreklam.managers;

import com.adalreklam.AdalReklamPlugin;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;

public class EconomyManager {
    private final AdalReklamPlugin plugin;
    private final Economy economy;

    public EconomyManager(AdalReklamPlugin plugin, Economy economy) {
        this.plugin = plugin;
        this.economy = economy;
    }

    public boolean hasBalance(Player player, double amount) {
        return economy.has(player, amount);
    }

    public boolean withdrawMoney(Player player, double amount) {
        if (!hasBalance(player, amount)) {
            return false;
        }
        return economy.withdrawPlayer(player, amount).transactionSuccess();
    }

    public double getBalance(Player player) {
        return economy.getBalance(player);
    }

    public String formatMoney(double amount) {
        return String.format("%,.0f", amount);
    }
}