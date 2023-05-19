package ru.omashune.headsanywhere.manager;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.entity.Player;
import ru.omashune.headsanywhere.util.HeadUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.concurrent.TimeUnit;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HeadManager {

    char[] characters = new char[]{'ϧ', 'Ϩ', 'ϩ', 'Ϫ', 'ϫ', 'Ϭ', 'ϭ', 'Ϯ'};
    LoadingCache<Player, BaseComponent[]> cache;

    public HeadManager(int cacheTime) {
        cache = CacheBuilder.newBuilder()
                .expireAfterWrite(cacheTime, TimeUnit.SECONDS)
                .build(CacheLoader.from(this::loadHead));
    }

    @SneakyThrows
    public BaseComponent[] getPlayerHead(Player player) {
        return cache.get(player);
    }

    public void refreshPlayerHead(Player player) {
        cache.refresh(player);
    }

    @SneakyThrows
    private BaseComponent[] loadHead(Player player) {
        URL skinUrl = player.getPlayerProfile().getTextures().getSkin();

        BufferedImage headImage = skinUrl == null ?
                ImageIO.read(new URL("https://minotar.net/helm/" + player.getName() + "/8.png").openStream()) :
                HeadUtil.getHead(skinUrl);
        ComponentBuilder builder = new ComponentBuilder();

        for (int x = 0; x < headImage.getWidth(); x++) {
            for (int y = 0; y < headImage.getHeight(); y++) {
                builder.append(String.valueOf(characters[y]))
                        .color(ChatColor.of(new Color(headImage.getRGB(x, y))));
            }
        }

        return builder.create();
    }

}
