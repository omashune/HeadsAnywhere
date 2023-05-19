package ru.omashune.headsanywhere.listener;

import lombok.AllArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

@AllArgsConstructor
public class ResourcePackListener implements Listener {

    private final ConfigurationSection resourcePack;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        e.getPlayer().setResourcePack(resourcePack.getString("url", "https://www.dropbox.com/s/uc2epfgztrw8mvz/HeadsAnywhere.zip?dl=1"));
    }

    @EventHandler
    public void onResourcePackStatus(PlayerResourcePackStatusEvent e) {
        if (!resourcePack.getBoolean("messaging")) return;
        Player player = e.getPlayer();

        switch (e.getStatus()) {
            case SUCCESSFULLY_LOADED:
                player.sendMessage(resourcePack.getString("successfully-loaded", "§aSuccessfully loaded"));
                break;
            case FAILED_DOWNLOAD:
                player.sendMessage(resourcePack.getString("failed-download", "§cFailed to download the resource pack. Please, contact the staff!"));
                break;
        }
    }

}
