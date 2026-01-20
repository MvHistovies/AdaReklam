package com.adalreklam.managers;

import com.adalreklam.AdalReklamPlugin;
import com.adalreklam.models.Advertisement;
import org.bukkit.Bukkit;

import java.io.*;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class LogManager {
    private final AdalReklamPlugin plugin;
    private final File logFile;
    private BufferedWriter writer;
    private final DateTimeFormatter formatter;

    public LogManager(AdalReklamPlugin plugin) {
        this.plugin = plugin;
        this.formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        File logsDir = new File(plugin.getDataFolder(), "logs");
        if (!logsDir.exists()) {
            logsDir.mkdirs();
        }

        String logFileName = plugin.getConfigManager().getConfig()
                .getString("logging.file", "advertisements.log");
        this.logFile = new File(logsDir, logFileName);

        try {
            if (!logFile.exists()) {
                logFile.createNewFile();
            }
            writer = new BufferedWriter(new FileWriter(logFile, true));
        } catch (IOException e) {
            plugin.getLogger().severe("Log dosyası oluşturulamadı: " + e.getMessage());
        }
    }

    public void log(Advertisement ad) {
        if (!plugin.getConfigManager().getConfig().getBoolean("logging.enabled", true)) {
            return;
        }

        String logEntry = String.format("[%s] [%s] %s: \"%s\" | Fiyat: %.0f | %s%n",
                LocalDateTime.now().format(formatter),
                ad.getType().getName(),
                ad.getPlayerName(),
                ad.getMessage(),
                ad.getPrice(),
                ad.isSuccessful() ? "BAŞARILI" : "BAŞARISIZ");

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                writer.write(logEntry);
                writer.flush();
            } catch (IOException e) {
                plugin.getLogger().warning("Log yazma hatası: " + e.getMessage());
            }
        });
    }

    public List<String> getRecentLogs(int count) {
        List<String> logs = new ArrayList<>();
        try {
            List<String> allLines = Files.readAllLines(logFile.toPath());
            int start = Math.max(0, allLines.size() - count);
            logs = allLines.subList(start, allLines.size());
            Collections.reverse(logs);
        } catch (IOException e) {
            plugin.getLogger().warning("Log okuma hatası: " + e.getMessage());
        }
        return logs;
    }

    public void saveAndClose() {
        try {
            if (writer != null) {
                writer.flush();
                writer.close();
            }
        } catch (IOException e) {
            plugin.getLogger().warning("Log dosyası kapatma hatası: " + e.getMessage());
        }
    }
}