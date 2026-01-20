package com.adalreklam.listeners;

import com.adalreklam.AdalReklamPlugin;
import com.adalreklam.gui.AdConfirmGUI;
import com.adalreklam.gui.MainMenuGUI;
import com.adalreklam.models.AdRequest;
import com.adalreklam.models.AdType;
import com.adalreklam.utils.AdvertisementSender;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class ChatListener implements Listener {
    private final AdalReklamPlugin plugin;

    public ChatListener(AdalReklamPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();

        // Bekleyen request var mı kontrol et
        if (!plugin.getAdvertisementManager().hasPendingRequest(player.getUniqueId())) {
            return;
        }

        e.setCancelled(true);

        String message = e.getMessage();

        // İptal kontrolü
        if (message.equalsIgnoreCase("iptal") || message.equalsIgnoreCase("cancel")) {
            plugin.getAdvertisementManager().removeRequest(player.getUniqueId());
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.getConfigManager().getMessage("purchase-cancelled")));
            return;
        }

        // Mesaj uzunluğu kontrolü
        int minLength = plugin.getConfigManager().getConfig().getInt("limits.min-message-length", 5);
        int maxLength = plugin.getConfigManager().getConfig().getInt("limits.max-message-length", 100);

        if (message.length() < minLength) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.getConfigManager().getMessage("message-too-short")
                            .replace("{min}", String.valueOf(minLength))));
            return;
        }

        if (message.length() > maxLength) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.getConfigManager().getMessage("message-too-long")
                            .replace("{max}", String.valueOf(maxLength))));
            return;
        }

        // Mesajı request'e kaydet
        plugin.getAdvertisementManager().updateRequestMessage(player.getUniqueId(), message);

        // Onay GUI'sini aç (sync olarak)
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            AdConfirmGUI confirmGUI = new AdConfirmGUI(plugin);
            confirmGUI.open(player);
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        // Oyuncu çıkınca bekleyen request'i temizle
        plugin.getAdvertisementManager().removeRequest(e.getPlayer().getUniqueId());
    }
}