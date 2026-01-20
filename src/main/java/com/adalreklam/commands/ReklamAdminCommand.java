package com.adalreklam.commands;

import com.adalreklam.AdalReklamPlugin;
import com.adalreklam.gui.MainMenuGUI;
import com.adalreklam.models.AdType;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReklamAdminCommand implements CommandExecutor, TabCompleter {
    private final AdalReklamPlugin plugin;

    public ReklamAdminCommand(AdalReklamPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("adalreklam.admin")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.getConfigManager().getMessage("no-permission")));
            return true;
        }

        if (args.length == 0) {
            sendAdminHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                handleReload(sender);
                break;

            case "stats":
            case "istatistik":
                handleStats(sender);
                break;

            case "logs":
            case "log":
                handleLogs(sender, args);
                break;

            case "setprice":
            case "fiyat":
                handleSetPrice(sender, args);
                break;

            default:
                sender.sendMessage(ChatColor.RED + "Bilinmeyen komut! Yardım için: /reklamadmin");
        }

        return true;
    }

    private void handleReload(CommandSender sender) {
        plugin.getConfigManager().reloadConfig();
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                plugin.getConfigManager().getMessage("reload-success")));
    }

    private void handleStats(CommandSender sender) {
        int totalAds = plugin.getAdvertisementManager().getTotalAds();
        double totalRevenue = plugin.getAdvertisementManager().getTotalRevenue();

        sender.sendMessage("");
        sender.sendMessage(ChatColor.GOLD + "═══════ " + ChatColor.YELLOW + "Reklam İstatistikleri" +
                ChatColor.GOLD + " ═══════");
        sender.sendMessage("");
        sender.sendMessage(ChatColor.YELLOW + "Toplam Reklam: " + ChatColor.WHITE + totalAds);
        sender.sendMessage(ChatColor.YELLOW + "Toplam Gelir: " + ChatColor.WHITE +
                plugin.getEconomyManager().formatMoney(totalRevenue) + " TL");
        sender.sendMessage("");
        sender.sendMessage(ChatColor.GOLD + "═══════════════════════════════════");
        sender.sendMessage("");
    }

    private void handleLogs(CommandSender sender, String[] args) {
        int page = 1;
        if (args.length > 1) {
            try {
                page = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Geçersiz sayfa numarası!");
                return;
            }
        }

        List<String> logs = plugin.getLogManager().getRecentLogs(10 * page);
        int start = (page - 1) * 10;
        int end = Math.min(start + 10, logs.size());

        if (start >= logs.size()) {
            sender.sendMessage(ChatColor.RED + "Bu sayfa boş!");
            return;
        }

        sender.sendMessage("");
        sender.sendMessage(ChatColor.GOLD + "═══════ " + ChatColor.YELLOW +
                "Reklam Logları (Sayfa " + page + ")" + ChatColor.GOLD + " ═══════");
        sender.sendMessage("");

        for (int i = start; i < end; i++) {
            sender.sendMessage(ChatColor.GRAY + logs.get(i));
        }

        sender.sendMessage("");
        sender.sendMessage(ChatColor.YELLOW + "Toplam: " + logs.size() + " log kaydı");
        sender.sendMessage(ChatColor.GRAY + "Sonraki sayfa: /reklamadmin logs " + (page + 1));
        sender.sendMessage("");
    }

    private void handleSetPrice(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Kullanım: /reklamadmin setprice <tip> <fiyat>");
            sender.sendMessage(ChatColor.GRAY + "Tipler: discord, chat, bossbar");
            return;
        }

        String type = args[1].toLowerCase();
        double price;

        try {
            price = Double.parseDouble(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Geçersiz fiyat!");
            return;
        }

        if (price < 0) {
            sender.sendMessage(ChatColor.RED + "Fiyat negatif olamaz!");
            return;
        }

        String configPath;
        switch (type) {
            case "discord":
                configPath = "prices.discord-webhook";
                break;
            case "chat":
                configPath = "prices.chat-reklam";
                break;
            case "bossbar":
                configPath = "prices.bossbar-reklam";
                break;
            default:
                sender.sendMessage(ChatColor.RED + "Geçersiz tip! (discord, chat, bossbar)");
                return;
        }

        plugin.getConfig().set(configPath, price);
        plugin.saveConfig();

        sender.sendMessage(ChatColor.GREEN + "Fiyat güncellendi!");
        sender.sendMessage(ChatColor.YELLOW + type.toUpperCase() + " fiyatı: " +
                ChatColor.WHITE + plugin.getEconomyManager().formatMoney(price) + " TL");
    }

    private void sendAdminHelp(CommandSender sender) {
        sender.sendMessage("");
        sender.sendMessage(ChatColor.GOLD + "═══════ " + ChatColor.RED + "Ada Reklam Admin" +
                ChatColor.GOLD + " ═══════");
        sender.sendMessage("");
        sender.sendMessage(ChatColor.YELLOW + "/reklamadmin reload " +
                ChatColor.GRAY + "- Config'i yeniden yükle");
        sender.sendMessage(ChatColor.YELLOW + "/reklamadmin stats " +
                ChatColor.GRAY + "- İstatistikleri görüntüle");
        sender.sendMessage(ChatColor.YELLOW + "/reklamadmin logs [sayfa] " +
                ChatColor.GRAY + "- Logları görüntüle");
        sender.sendMessage(ChatColor.YELLOW + "/reklamadmin setprice <tip> <fiyat> " +
                ChatColor.GRAY + "- Fiyat değiştir");
        sender.sendMessage("");
        sender.sendMessage(ChatColor.GOLD + "═══════════════════════════════════");
        sender.sendMessage("");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = new ArrayList<>();

        if (!sender.hasPermission("adalreklam.admin")) {
            return completions;
        }

        if (args.length == 1) {
            completions.addAll(Arrays.asList("reload", "stats", "logs", "setprice"));
        } else if (args.length == 2 && args[0].equalsIgnoreCase("setprice")) {
            completions.addAll(Arrays.asList("discord", "chat", "bossbar"));
        }

        return completions;
    }
}