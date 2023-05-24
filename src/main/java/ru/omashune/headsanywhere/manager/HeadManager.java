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
import java.util.concurrent.TimeUnit;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HeadManager {

    char[] characters = new char[]{'ϧ', 'Ϩ', 'ϩ', 'Ϫ', 'ϫ', 'Ϭ', 'ϭ', 'Ϯ'};
    Gson gson = new Gson();

    String headsProvider;
    LoadingCache<Player, BaseComponent[]> cache;

    public HeadManager(int cacheTime, String headProvider) {
        cache = CacheBuilder.newBuilder()
                .expireAfterWrite(cacheTime, TimeUnit.SECONDS)
                .build(CacheLoader.from(this::loadHead));
        this.headsProvider = headProvider;
    }

    @SneakyThrows
    public BaseComponent[] getPlayerHead(Player player) {
        return cache.get(player);
    }

    public void refreshPlayerHead(Player player) {
        cache.refresh(player);
    }

    private BaseComponent[] loadHead(Player player) {
        try {
            String skinUrl = getSkinUrl(player);
            URL url = new URL(skinUrl == null ? String.format(headsProvider, player.getName()) : skinUrl);

            BufferedImage headImage = skinUrl == null ? ImageIO.read(url) : HeadUtil.getHead(url);
            ComponentBuilder builder = new ComponentBuilder();

            for (int x = 0; x < headImage.getWidth(); x++) {
                for (int y = 0; y < headImage.getHeight(); y++) {
                    builder.append(String.valueOf(characters[y]))
                            .color(ChatColor.of(new Color(headImage.getRGB(x, y))));
                }
            }

            return builder.create();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getSkinUrl(Player player) {
        try {
            Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
            GameProfile profile = getGameProfile(entityPlayer);
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

    private GameProfile getGameProfile(Object entityPlayer) throws IllegalAccessException, InvocationTargetException {
        for (Method method : entityPlayer.getClass().getSuperclass().getDeclaredMethods()) {
            if (method.getReturnType() == GameProfile.class) return (GameProfile) method.invoke(entityPlayer);
        }

        return null;
    }

}
