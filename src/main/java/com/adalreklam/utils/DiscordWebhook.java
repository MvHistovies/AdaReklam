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
import java.util.UUID;

public class DiscordWebhook {
    private final String url;
    private String content;
    private String username;
    private String avatarUrl;
    private final java.util.List<EmbedObject> embeds = new java.util.ArrayList<>();

    public DiscordWebhook(String url) {
        this.url = url;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public void addEmbed(EmbedObject embed) {
        this.embeds.add(embed);
    }

    public void execute() throws Exception {
        if (this.content == null && this.embeds.isEmpty()) {
            throw new IllegalArgumentException("İçerik veya embed gerekli!");
        }

        StringBuilder json = new StringBuilder("{");

        if (content != null) {
            json.append("\"content\":\"").append(content).append("\",");
        }

        if (username != null) {
            json.append("\"username\":\"").append(username).append("\",");
        }

        if (avatarUrl != null) {
            json.append("\"avatar_url\":\"").append(avatarUrl).append("\",");
        }

        if (!embeds.isEmpty()) {
            json.append("\"embeds\":[");
            for (int i = 0; i < embeds.size(); i++) {
                json.append(embeds.get(i).toJson());
                if (i < embeds.size() - 1) json.append(",");
            }
            json.append("]");
        }

        json.append("}");

        URL url = new URL(this.url);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("User-Agent", "AdalReklam-Webhook");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = json.toString().getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int responseCode = connection.getResponseCode();
        if (responseCode < 200 || responseCode >= 300) {
            throw new Exception("Webhook yanıt kodu: " + responseCode);
        }
    }

    public static class EmbedObject {
        private String title;
        private String description;
        private String url;
        private Integer color;
        private Footer footer;
        private Instant timestamp;

        public EmbedObject setTitle(String title) {
            this.title = title;
            return this;
        }

        public EmbedObject setDescription(String description) {
            this.description = description;
            return this;
        }

        public EmbedObject setUrl(String url) {
            this.url = url;
            return this;
        }

        public EmbedObject setColor(int color) {
            this.color = color;
            return this;
        }

        public EmbedObject setFooter(String text, String icon) {
            this.footer = new Footer(text, icon);
            return this;
        }

        public EmbedObject setTimestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        private String toJson() {
            StringBuilder json = new StringBuilder("{");

            if (title != null) {
                json.append("\"title\":\"").append(escapeJson(title)).append("\",");
            }

            if (description != null) {
                json.append("\"description\":\"").append(escapeJson(description)).append("\",");
            }

            if (url != null) {
                json.append("\"url\":\"").append(url).append("\",");
            }

            if (color != null) {
                json.append("\"color\":").append(color).append(",");
            }

            if (footer != null) {
                json.append("\"footer\":{\"text\":\"").append(escapeJson(footer.text)).append("\"");
                if (footer.iconUrl != null) {
                    json.append(",\"icon_url\":\"").append(footer.iconUrl).append("\"");
                }
                json.append("},");
            }

            if (timestamp != null) {
                json.append("\"timestamp\":\"").append(timestamp.toString()).append("\",");
            }

            // Son virgülü kaldır
            if (json.charAt(json.length() - 1) == ',') {
                json.setLength(json.length() - 1);
            }

            json.append("}");
            return json.toString();
        }

        private String escapeJson(String text) {
            return text.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
        }

        private static class Footer {
            private final String text;
            private final String iconUrl;

            public Footer(String text, String iconUrl) {
                this.text = text;
                this.iconUrl = iconUrl;
            }
        }
    }
}