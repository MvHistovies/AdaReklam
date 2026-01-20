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

public class ReklamCommand implements CommandExecutor, TabCompleter {
    private final AdalReklamPlugin plugin;

    public ReklamCommand(AdalReklamPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Bu komut sadece oyuncular tarafından kullanılabilir!");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("adalreklam.use")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.getConfigManager().getMessage("no-permission")));
            return true;
        }

        if (args.length == 0) {
            // Ana menüyü aç
            MainMenuGUI mainMenu = new MainMenuGUI(plugin);
            mainMenu.open(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("yardim")) {
            sendHelpMessage(player);
            return true;
        }

        // Bilinmeyen argüman
        player.sendMessage(ChatColor.RED + "Bilinmeyen komut! Yardım için: /reklam help");
        return true;
    }

    private void sendHelpMessage(Player player) {
        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "═══════ " + ChatColor.YELLOW + "Ada Reklam Yardım" +
                ChatColor.GOLD + " ═══════");
        player.sendMessage("");
        player.sendMessage(ChatColor.YELLOW + "/reklam " + ChatColor.GRAY + "- Reklam satın alma menüsü");
        player.sendMessage(ChatColor.YELLOW + "/reklam help " + ChatColor.GRAY + "- Bu yardım mesajı");
        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "Reklam Tipleri:");
        player.sendMessage(ChatColor.AQUA + "  • Discord Webhook " + ChatColor.GRAY + "- " +
                ChatColor.WHITE + plugin.getEconomyManager()
                .formatMoney(plugin.getConfigManager().getPrice(AdType.DISCORD_WEBHOOK)) + " TL");
        player.sendMessage(ChatColor.AQUA + "  • Chat Reklam " + ChatColor.GRAY + "- " +
                ChatColor.WHITE + plugin.getEconomyManager()
                .formatMoney(plugin.getConfigManager().getPrice(AdType.CHAT)) + " TL");
        player.sendMessage(ChatColor.AQUA + "  • Bossbar Reklam " + ChatColor.GRAY + "- " +
                ChatColor.WHITE + plugin.getEconomyManager()
                .formatMoney(plugin.getConfigManager().getPrice(AdType.BOSSBAR)) + " TL");
        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "═══════════════════════════════════");
        player.sendMessage("");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("help");
            completions.add("yardim");
        }

        return completions;
    }
}