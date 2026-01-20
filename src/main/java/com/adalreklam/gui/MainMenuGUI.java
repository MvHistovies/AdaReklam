package com.adalreklam.gui;

import com.adalreklam.AdalReklamPlugin;
import com.adalreklam.models.AdType;
import com.adalreklam.models.AdRequest;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainMenuGUI {
    private final AdalReklamPlugin plugin;

    public MainMenuGUI(AdalReklamPlugin plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        String title = ChatColor.translateAlternateColorCodes('&',
                plugin.getConfigManager().getConfig().getString("gui.main-menu.title", "&6&lReklam Satın Al"));

        Inventory inv = Bukkit.createInventory(null, 27, title);

        // Discord Webhook Item (Slot 11)
        ItemStack discordItem = createAdItem(
                Material.PAPER,
                AdType.DISCORD_WEBHOOK,
                player
        );
        inv.setItem(11, discordItem);

        // Chat Reklam Item (Slot 13)
        ItemStack chatItem = createAdItem(
                Material.WRITABLE_BOOK,
                AdType.CHAT,
                player
        );
        inv.setItem(13, chatItem);

        // Bossbar Reklam Item (Slot 15)
        ItemStack bossbarItem = createAdItem(
                Material.BEACON,
                AdType.BOSSBAR,
                player
        );
        inv.setItem(15, bossbarItem);

        // Bilgi item (Slot 22)
        ItemStack infoItem = new ItemStack(Material.BOOK);
        ItemMeta infoMeta = infoItem.getItemMeta();
        infoMeta.setDisplayName(ChatColor.YELLOW + "ℹ Bilgi");
        infoMeta.setLore(Arrays.asList(
                ChatColor.GRAY + "Reklam satın almak için",
                ChatColor.GRAY + "yukarıdaki seçeneklerden",
                ChatColor.GRAY + "birini tıklayın.",
                "",
                ChatColor.GOLD + "Bakiyeniz: " + ChatColor.WHITE +
                        plugin.getEconomyManager().formatMoney(
                                plugin.getEconomyManager().getBalance(player)) + " TL"
        ));
        infoItem.setItemMeta(infoMeta);
        inv.setItem(22, infoItem);

        // Dekoratif cam paneller
        ItemStack glassPane = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta glassMeta = glassPane.getItemMeta();
        glassMeta.setDisplayName(" ");
        glassPane.setItemMeta(glassMeta);

        for (int i = 0; i < 27; i++) {
            if (inv.getItem(i) == null) {
                inv.setItem(i, glassPane);
            }
        }

        player.openInventory(inv);
    }

    private ItemStack createAdItem(Material material, AdType type, Player player) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        double price = plugin.getConfigManager().getPrice(type);
        boolean canAfford = plugin.getEconomyManager().hasBalance(player, price);

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', type.getDisplayName()));

        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GOLD + "Fiyat: " + ChatColor.WHITE +
                plugin.getEconomyManager().formatMoney(price) + " TL");
        lore.add("");

        switch (type) {
            case DISCORD_WEBHOOK:
                lore.add(ChatColor.GRAY + "Mesajınız Discord sunucusuna");
                lore.add(ChatColor.GRAY + "webhook ile gönderilir.");
                break;
            case CHAT:
                lore.add(ChatColor.GRAY + "Mesajınız oyundaki tüm");
                lore.add(ChatColor.GRAY + "oyunculara chat'te gösterilir.");
                break;
            case BOSSBAR:
                lore.add(ChatColor.GRAY + "Mesajınız ekranın üstünde");
                lore.add(ChatColor.GRAY + "bossbar olarak gösterilir.");
                break;
        }

        lore.add("");
        if (canAfford) {
            lore.add(ChatColor.GREEN + "✔ Satın alabilirsiniz!");
            lore.add(ChatColor.YELLOW + "▶ Tıklayın!");
        } else {
            lore.add(ChatColor.RED + "✘ Yetersiz bakiye!");
            lore.add(ChatColor.GRAY + "Eksik: " +
                    plugin.getEconomyManager().formatMoney(price -
                            plugin.getEconomyManager().getBalance(player)) + " TL");
        }

        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
}