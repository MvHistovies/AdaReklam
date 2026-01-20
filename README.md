# 🎮 Ada Reklam Plugin

[![GitHub release](https://img.shields.io/github/v/release/YOURUSERNAME/AdalReklam)](https://github.com/YOURUSERNAME/AdalReklam/releases)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![Paper](https://img.shields.io/badge/Paper-1.20+-green.svg)](https://papermc.io/)

Discord Webhook destekli profesyonel Minecraft reklam sistemi.

![Banner][Imgur](https://imgur.com/0z8EcWo)

## ✨ Özellikler

- 💬 **Discord Webhook Reklam** - Mesajları Discord sunucusuna gönder
- 💭 **Chat Reklam** - Tıklanabilir oyuncu isimleri ile chat reklamı
- 📊 **Bossbar Reklam** - Ekran üstünde bossbar gösterimi
- 💰 **Vault Ekonomi** - Otomatik para çekme sistemi
- 🎨 **GUI Menü** - Modern kullanıcı arayüzü
- ⏱️ **Cooldown Sistemi** - Her reklam tipi için ayrı cooldown
- 📝 **Log Sistemi** - Detaylı reklam kaydı
- 👑 **Admin Komutları** - İstatistik ve yönetim araçları

## 📦 Kurulum

### Gereksinimler
- Paper 1.20.1 veya üzeri
- Java 17 veya üzeri
- Vault
- Bir ekonomi plugini (EssentialsX, CMI, vb.)

### Adımlar
1. [Releases](https://github.com/YOURUSERNAME/AdalReklam/releases) sayfasından en son versiyonu indirin
2. `AdalReklam-x.x.x.jar` dosyasını `plugins/` klasörüne atın
3. Sunucuyu başlatın
4. `plugins/AdalReklam/config.yml` dosyasını düzenleyin
5. Discord webhook URL'inizi ekleyin
6. `/reklamadmin reload` komutu ile yeniden yükleyin

## ⚙️ Yapılandırma

### config.yml
```yaml
prices:
  discord-webhook: 300000
  chat-reklam: 150000
  bossbar-reklam: 200000

discord:
  webhook-url: "https://discord.com/api/webhooks/..."
  cooldown: 7200

advertisements:
  chat:
    cooldown: 3600
  bossbar:
    cooldown: 1800
```

## 🎮 Komutlar

### Oyuncu Komutları
| Komut | Açıklama | İzin |
|-------|----------|------|
| `/reklam` | Reklam menüsünü açar | `adalreklam.use` |
| `/reklam help` | Yardım mesajı | `adalreklam.use` |

### Admin Komutları
| Komut | Açıklama | İzin |
|-------|----------|------|
| `/reklamadmin reload` | Config'i yenile | `adalreklam.admin` |
| `/reklamadmin stats` | İstatistikler | `adalreklam.admin` |
| `/reklamadmin logs [sayfa]` | Logları görüntüle | `adalreklam.admin` |
| `/reklamadmin setprice <tip> <fiyat>` | Fiyat değiştir | `adalreklam.admin` |

## 🔐 İzinler
```yaml
adalreklam.use - Reklam satın alabilir (default: true)
adalreklam.admin - Admin komutlarını kullanabilir (default: op)
adalreklam.bypass.cooldown - Cooldown'ları atlar (default: op)
```

## 🏗️ Build (Geliştiriciler İçin)
```bash
# Clone
git clone https://github.com/YOURUSERNAME/AdalReklam.git
cd AdalReklam

# Build
mvn clean package

# Jar dosyası
target/AdalReklam-1.0.0.jar
```

## 📸 Ekran Görüntüleri

### Ana Menü
![Ana Menü](https://i.imgur.com/screenshot1.png)

### Chat Reklam
![Chat Reklam](https://i.imgur.com/screenshot2.png)

### Discord Webhook
![Discord](https://i.imgur.com/screenshot3.png)

## 🤝 Katkıda Bulunma

1. Fork edin
2. Feature branch oluşturun (`git checkout -b feature/AmazingFeature`)
3. Commit edin (`git commit -m 'Add some AmazingFeature'`)
4. Push edin (`git push origin feature/AmazingFeature`)
5. Pull Request açın

## 📝 Changelog

### v1.0.0 (20 Ocak 2025)
- ✨ İlk sürüm
- ✅ Discord Webhook desteği
- ✅ Tıklanabilir chat mesajları
- ✅ Tip bazlı cooldown sistemi
- ✅ GUI menü sistemi
- ✅ Log sistemi

## 📄 Lisans

Bu proje [MIT Lisansı](LICENSE) altında lisanslanmıştır.

## 💖 Teşekkürler

- [Paper](https://papermc.io/) - Server API
- [Vault](https://github.com/MilkBowl/VaultAPI) - Ekonomi API
- [Discord](https://discord.com/) - Webhook API

## 📞 Destek

- **Discord:** [Sunucuya Katıl](https://discord.gg/yourserver)
- **Issues:** [GitHub Issues](https://github.com/YOURUSERNAME/AdalReklam/issues)
- **Wiki:** [Documentation](https://github.com/YOURUSERNAME/AdalReklam/wiki)

---

**Made with ❤️ by [Your Name](https://github.com/YOURUSERNAME)**
