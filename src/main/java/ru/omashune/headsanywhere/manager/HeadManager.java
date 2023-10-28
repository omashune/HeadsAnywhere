package ru.omashune.headsanywhere.manager;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Iterables;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import ru.omashune.headsanywhere.util.HeadUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Base64;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class HeadManager {

    final char[] characters = new char[]{'ϧ', 'Ϩ', 'ϩ', 'Ϫ', 'ϫ', 'Ϭ', 'ϭ', 'Ϯ'};
    final Gson gson = new Gson();

    final String headsProvider;
    final LoadingCache<String, Optional<BaseComponent[]>> cache;

    boolean hasPlayerProfile;
    String getProfileMethodName;

    public HeadManager(int cacheTime, String headProvider) {
        this.cache = CacheBuilder.newBuilder().expireAfterWrite(cacheTime, TimeUnit.SECONDS).build(CacheLoader.from(this::loadHead));
        this.headsProvider = headProvider;

        try {
            hasPlayerProfile = OfflinePlayer.class.getMethod("getPlayerProfile") != null;
        } catch (NoSuchMethodException ignored) {
            hasPlayerProfile = false;

            String version = Bukkit.getServer().getClass().getName();
            try {
                Class<?> clazz = Class.forName(version.substring(0, version.length() - "CraftServer".length()) + "entity.CraftPlayer");
                for (Method method : clazz.getMethods()) {
                    getProfileMethodName = method.getName();
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @SneakyThrows
    public BaseComponent[] getPlayerHead(String player) {
        return cache.get(player).orElse(null);
    }

    public void refreshPlayerHead(String player) {
        cache.refresh(player);
    }

    private Optional<BaseComponent[]> loadHead(String playerName) {
        try {
            Player player = Bukkit.getPlayer(playerName);
            String skinUrl = getSkinUrl(player);
            boolean isSpigotSkinUrl = skinUrl != null;

            URL url = new URL(isSpigotSkinUrl ? skinUrl : String.format(headsProvider, playerName));

            ComponentBuilder builder = new ComponentBuilder();
            BufferedImage headImage = isSpigotSkinUrl ? HeadUtil.getHead(url) : ImageIO.read(url);

            for (int x = 0; x < headImage.getWidth(); x++) {
                for (int y = 0; y < headImage.getHeight(); y++) {
                    builder.append(String.valueOf(characters[y])).color(ChatColor.of(new Color(headImage.getRGB(x, y))));
                }
            }

            return Optional.of(builder.create());
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    private String getSkinUrl(Player player) {
        if (player == null) return null;

        if (hasPlayerProfile) {
            URL skinURL = player.getPlayerProfile().getTextures().getSkin();
            return skinURL == null ? null : skinURL.toString();
        }

        try {
            Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
            GameProfile profile = (GameProfile) entityPlayer.getClass().getMethod(getProfileMethodName).invoke(entityPlayer);
            if (profile == null) return null;

            Property textureProperty = Iterables.getFirst(profile.getProperties().get("textures"), null);
            if (textureProperty == null) return null;

            return gson.fromJson(new String(
                            Base64.getDecoder().decode(textureProperty.getValue())
                    ), JsonObject.class).getAsJsonObject("textures")
                    .getAsJsonObject("SKIN")
                    .get("url").getAsString();
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            return null;
        }
    }

}