package com.adalreklam.utils;

import com.adalreklam.AdalReklamPlugin;
import com.adalreklam.models.AdType;
import com.adalreklam.models.Advertisement;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import javax.net.ssl.HttpsURLConnection;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

// ============================================
// AdvertisementSender.java - Reklamları Gönderir
// ============================================
public class AdvertisementSender {
    private final AdalReklamPlugin plugin;

    public AdvertisementSender(AdalReklamPlugin plugin) {
        this.plugin = plugin;
    }

    public void sendAdvertisement(Player player, AdType type, String message, double price) {
        boolean success = false;

        switch (type) {
            case DISCORD_WEBHOOK:
                success = sendDiscordWebhook(player, message);
                break;
            case CHAT:
                success = sendChatBroadcast(player, message);
                break;
            case BOSSBAR:
                success = sendBossbar(player, message);
                break;
        }

        // Log kaydet
        Advertisement ad = new Advertisement(
                player.getUniqueId(),
                player.getName(),
                type,
                message,
                price,
                success
        );
        plugin.getAdvertisementManager().addToHistory(ad);
        plugin.getLogManager().log(ad);

        // Oyuncuya bildir
        if (success) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.getConfigManager().getMessage("purchase-success")));
        }
    }

    private boolean sendDiscordWebhook(Player player, String message) {
        if (!plugin.getConfigManager().getConfig().getBoolean("discord.enabled", true)) {
            return false;
        }

        String webhookUrl = plugin.getConfigManager().getConfig()
                .getString("discord.webhook-url", "");

        if (webhookUrl.isEmpty() || webhookUrl.contains("YOUR_WEBHOOK")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.getConfigManager().getMessage("discord-error")));
            return false;
        }

        // Async gönder
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                DiscordWebhook webhook = new DiscordWebhook(webhookUrl);

                int embedColor = plugin.getConfigManager().getConfig()
                        .getInt("discord.embed-color", 5814783);
                String footerText = plugin.getConfigManager().getConfig()
                        .getString("discord.footer-text", "Ada Reklam Sistemi");

                webhook.setContent(null);
                webhook.addEmbed(new DiscordWebhook.EmbedObject()
                        .setTitle("🎮 Yeni Reklam!")
                        .setDescription(message)
                        .setColor(embedColor)
                        .setFooter(footerText + " • Gönderen: " + player.getName(), null)
                        .setTimestamp(Instant.now())
                );

                webhook.execute();

                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.getConfigManager().getMessage("discord-sent")));

            } catch (Exception e) {
                plugin.getLogger().warning("Discord webhook hatası: " + e.getMessage());
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.getConfigManager().getMessage("discord-error")));
            }
        });

        return true;
    }

    private boolean sendChatBroadcast(Player player, String message) {
        String format = plugin.getConfigManager().getConfig()
                .getString("advertisements.chat.broadcast-format",
                        "&6&l[REKLAM] &r&f{message} &8- &7{player}");

        String broadcastMessage = ChatColor.translateAlternateColorCodes('&',
                format.replace("{message}", message)
                        .replace("{player}", player.getName()));

        // Adventure API ile tıklanabilir mesaj
        try {
            net.kyori.adventure.text.Component component = createClickableMessage(
                    broadcastMessage,
                    player.getName()
            );

            // Tüm oyunculara gönder
            for (Player online : Bukkit.getOnlinePlayers()) {
                online.sendMessage(component);
            }
        } catch (Exception e) {
            // Adventure API yoksa veya hata varsa, düz mesaj gönder
            plugin.getLogger().warning("Adventure API hatası, düz mesaj gönderiliyor: " + e.getMessage());
            Bukkit.broadcastMessage(broadcastMessage);
        }

        return true;
    }

    private net.kyori.adventure.text.Component createClickableMessage(String fullMessage, String playerName) {
        // Mesajı parçalara ayır
        int playerIndex = fullMessage.lastIndexOf(playerName);

        if (playerIndex == -1) {
            // Legacy text deserializer kullan - renk kodları için
            return net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
                    .legacySection().deserialize(fullMessage);
        }

        String beforePlayer = fullMessage.substring(0, playerIndex);
        String afterPlayer = fullMessage.substring(playerIndex + playerName.length());

        // Component'leri oluştur - Legacy deserializer ile renk kodları çalışır
        net.kyori.adventure.text.Component before =
                net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
                        .legacySection().deserialize(beforePlayer);

        net.kyori.adventure.text.Component clickableName =
                net.kyori.adventure.text.Component.text(playerName)
                        .color(net.kyori.adventure.text.format.NamedTextColor.YELLOW)
                        .decorate(net.kyori.adventure.text.format.TextDecoration.UNDERLINED)
                        .hoverEvent(net.kyori.adventure.text.event.HoverEvent.showText(
                                net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
                                        .legacySection().deserialize("§aAdaya gitmek için tıkla!\n§7/is warp " + playerName)
                        ))
                        .clickEvent(net.kyori.adventure.text.event.ClickEvent.suggestCommand(
                                "/is warp " + playerName
                        ));

        net.kyori.adventure.text.Component after =
                net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
                        .legacySection().deserialize(afterPlayer);

        return before.append(clickableName).append(after);
    }

    private boolean sendBossbar(Player player, String message) {
        String format = plugin.getConfigManager().getConfig()
                .getString("advertisements.bossbar.format",
                        "&6&lREKLAM: &r&f{message} &8(&7{player}&8)");

        String bossbarText = ChatColor.translateAlternateColorCodes('&',
                format.replace("{message}", message)
                        .replace("{player}", player.getName()));

        String colorName = plugin.getConfigManager().getConfig()
                .getString("advertisements.bossbar.color", "YELLOW");
        String styleName = plugin.getConfigManager().getConfig()
                .getString("advertisements.bossbar.style", "SOLID");
        int duration = plugin.getConfigManager().getConfig()
                .getInt("advertisements.bossbar.duration", 30);

        BarColor color = BarColor.YELLOW;
        BarStyle style = BarStyle.SOLID;

        try {
            color = BarColor.valueOf(colorName.toUpperCase());
            style = BarStyle.valueOf(styleName.toUpperCase());
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Geçersiz bossbar ayarları, varsayılan kullanılıyor");
        }

        BossBar bossBar = Bukkit.createBossBar(bossbarText, color, style);
        bossBar.setProgress(1.0);

        // Tüm oyunculara göster
        for (Player online : Bukkit.getOnlinePlayers()) {
            bossBar.addPlayer(online);
        }

        // Süre sonunda kaldır
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            bossBar.removeAll();
        }, duration * 20L);

        return true;
    }
}
