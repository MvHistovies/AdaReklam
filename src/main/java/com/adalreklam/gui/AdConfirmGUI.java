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

public class AdConfirmGUI {
    private final AdalReklamPlugin plugin;

    public AdConfirmGUI(AdalReklamPlugin plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        AdRequest request = plugin.getAdvertisementManager().getRequest(player.getUniqueId());
        if (request == null) {
            player.sendMessage(ChatColor.RED + "Hata: Bekleyen reklam isteği bulunamadı!");
            return;
        }

        String title = ChatColor.translateAlternateColorCodes('&',
                plugin.getConfigManager().getConfig().getString("gui.confirm-menu.title", "&a&lReklamı Onayla"));

        Inventory inv = Bukkit.createInventory(null, 27, title);

        double price = plugin.getConfigManager().getPrice(request.getType());

        // Onayla butonu (Slot 11)
        ItemStack confirmItem = new ItemStack(Material.LIME_CONCRETE);
        ItemMeta confirmMeta = confirmItem.getItemMeta();
        confirmMeta.setDisplayName(ChatColor.GREEN + "✔ ONAYLA");
        confirmMeta.setLore(Arrays.asList(
                "",
                ChatColor.GRAY + "Reklamınız gönderilecek ve",
                ChatColor.GOLD + plugin.getEconomyManager().formatMoney(price) + " TL " +
                        ChatColor.GRAY + "çekilecek.",
                "",
                ChatColor.YELLOW + "▶ Onaylamak için tıklayın!"
        ));
        confirmItem.setItemMeta(confirmMeta);
        inv.setItem(11, confirmItem);

        // Düzenle butonu (Slot 13)
        ItemStack editItem = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta editMeta = editItem.getItemMeta();
        editMeta.setDisplayName(ChatColor.YELLOW + "✎ DÜZENLE");
        editMeta.setLore(Arrays.asList(
                "",
                ChatColor.GRAY + "Mesajınızı değiştirmek",
                ChatColor.GRAY + "için tekrar yazabilirsiniz.",
                "",
                ChatColor.YELLOW + "▶ Düzenlemek için tıklayın!"
        ));
        editItem.setItemMeta(editMeta);
        inv.setItem(13, editItem);

        // Reddet/İptal butonu (Slot 15)
        ItemStack cancelItem = new ItemStack(Material.RED_CONCRETE);
        ItemMeta cancelMeta = cancelItem.getItemMeta();
        cancelMeta.setDisplayName(ChatColor.RED + "✗ İPTAL ET");
        cancelMeta.setLore(Arrays.asList(
                "",
                ChatColor.GRAY + "Reklam satın alma işlemini",
                ChatColor.GRAY + "iptal eder.",
                "",
                ChatColor.YELLOW + "▶ İptal için tıklayın!"
        ));
        cancelItem.setItemMeta(cancelMeta);
        inv.setItem(15, cancelItem);

        // Mesaj önizleme (Slot 22)
        ItemStack previewItem = new ItemStack(Material.PAPER);
        ItemMeta previewMeta = previewItem.getItemMeta();
        previewMeta.setDisplayName(ChatColor.GOLD + "📝 Mesaj Önizleme");

        List<String> previewLore = new ArrayList<>();
        previewLore.add("");
        previewLore.add(ChatColor.YELLOW + "Tip: " + ChatColor.WHITE + request.getType().getName());
        previewLore.add(ChatColor.YELLOW + "Fiyat: " + ChatColor.WHITE +
                plugin.getEconomyManager().formatMoney(price) + " TL");
        previewLore.add("");
        previewLore.add(ChatColor.GRAY + "Mesajınız:");

        // Mesajı 40 karakterde böl
        String message = request.getMessage();
        if (message.length() > 40) {
            previewLore.add(ChatColor.WHITE + message.substring(0, 40));
            if (message.length() > 80) {
                previewLore.add(ChatColor.WHITE + message.substring(40, 80));
                if (message.length() > 80) {
                    previewLore.add(ChatColor.WHITE + message.substring(80) + "...");
                }
            } else {
                previewLore.add(ChatColor.WHITE + message.substring(40));
            }
        } else {
            previewLore.add(ChatColor.WHITE + message);
        }

        previewMeta.setLore(previewLore);
        previewItem.setItemMeta(previewMeta);
        inv.setItem(22, previewItem);

        // Dekoratif cam paneller
        ItemStack glassPane = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
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
}