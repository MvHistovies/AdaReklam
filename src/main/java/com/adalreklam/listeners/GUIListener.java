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

public class GUIListener implements Listener {
    private final AdalReklamPlugin plugin;

    public GUIListener(AdalReklamPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) return;

        Player player = (Player) e.getWhoClicked();
        String title = ChatColor.stripColor(e.getView().getTitle());

        // Ana menü kontrolü
        if (title.contains("Reklam Satın Al")) {
            e.setCancelled(true);
            handleMainMenuClick(player, e.getCurrentItem());
            return;
        }

        // Onay menüsü kontrolü
        if (title.contains("Reklamı Onayla")) {
            e.setCancelled(true);
            handleConfirmMenuClick(player, e.getCurrentItem());
        }
    }

    private void handleMainMenuClick(Player player, ItemStack clickedItem) {
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        AdType selectedType = null;

        switch (clickedItem.getType()) {
            case PAPER:
                selectedType = AdType.DISCORD_WEBHOOK;
                break;
            case WRITABLE_BOOK:
                selectedType = AdType.CHAT;
                break;
            case BEACON:
                selectedType = AdType.BOSSBAR;
                break;
            default:
                return;
        }

        double price = plugin.getConfigManager().getPrice(selectedType);

        // Bakiye kontrolü
        if (!plugin.getEconomyManager().hasBalance(player, price)) {
            player.closeInventory();
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.getConfigManager().getMessage("insufficient-balance")
                            .replace("{price}", plugin.getEconomyManager().formatMoney(price))));
            return;
        }

        // Cooldown kontrolü (TÜM TİPLER İÇİN)
        if (plugin.getAdvertisementManager().isOnCooldown(player.getUniqueId(), selectedType)) {
            if (!player.hasPermission("adalreklam.bypass.cooldown")) {
                long remaining = plugin.getAdvertisementManager()
                        .getRemainingCooldown(player.getUniqueId(), selectedType);
                player.closeInventory();
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.getConfigManager().getMessage("cooldown-active")
                                .replace("{time}", formatTime(remaining))));
                return;
            }
        }

        // Request oluştur
        plugin.getAdvertisementManager().createRequest(player.getUniqueId(), selectedType);
        player.closeInventory();

        player.sendMessage("");
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                plugin.getConfigManager().getMessage("enter-message")));
        player.sendMessage(ChatColor.GRAY + "İptal için 'iptal' yazın.");
        player.sendMessage("");
    }

    private void handleConfirmMenuClick(Player player, ItemStack clickedItem) {
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        AdRequest request = plugin.getAdvertisementManager().getRequest(player.getUniqueId());
        if (request == null) return;

        switch (clickedItem.getType()) {
            case LIME_CONCRETE: // Onayla
                handleConfirm(player, request);
                break;

            case WRITABLE_BOOK: // Düzenle
                player.closeInventory();
                player.sendMessage("");
                player.sendMessage(ChatColor.YELLOW + "✎ Mesajınızı tekrar yazın:");
                player.sendMessage(ChatColor.GRAY + "İptal için 'iptal' yazın.");
                player.sendMessage("");
                break;

            case RED_CONCRETE: // İptal
                player.closeInventory();
                plugin.getAdvertisementManager().removeRequest(player.getUniqueId());
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.getConfigManager().getMessage("purchase-cancelled")));
                break;
        }
    }

    private void handleConfirm(Player player, AdRequest request) {
        double price = plugin.getConfigManager().getPrice(request.getType());

        // Son bakiye kontrolü
        if (!plugin.getEconomyManager().hasBalance(player, price)) {
            player.closeInventory();
            plugin.getAdvertisementManager().removeRequest(player.getUniqueId());
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.getConfigManager().getMessage("insufficient-balance")
                            .replace("{price}", plugin.getEconomyManager().formatMoney(price))));
            return;
        }

        // Para çek
        if (!plugin.getEconomyManager().withdrawMoney(player, price)) {
            player.closeInventory();
            plugin.getAdvertisementManager().removeRequest(player.getUniqueId());
            player.sendMessage(ChatColor.RED + "Para çekme hatası!");
            return;
        }

        // Cooldown ayarla (AdType parametresi ile)
        plugin.getAdvertisementManager().setCooldown(player.getUniqueId(), request.getType());

        // Reklamı gönder
        AdvertisementSender sender = new AdvertisementSender(plugin);
        sender.sendAdvertisement(player, request.getType(), request.getMessage(), price);

        // Request'i temizle
        plugin.getAdvertisementManager().removeRequest(player.getUniqueId());
        player.closeInventory();
    }

    private String formatTime(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;

        if (hours > 0) {
            return String.format("%d saat %d dakika", hours, minutes);
        } else if (minutes > 0) {
            return String.format("%d dakika %d saniye", minutes, secs);
        } else {
            return String.format("%d saniye", secs);
        }
    }
}
