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
        Player player = e.getPlayer();

        switch (e.getStatus()) {
            case SUCCESSFULLY_LOADED:
                sendMessageIfEnabled(player, "successfully-loaded", "§aSuccessfully loaded!");
                break;
            case FAILED_DOWNLOAD:
                if (kickPlayerIfEnabled(player, "failed-download", "§cFailed to download the resource pack. Please, contact the staff!")) return;
                sendMessageIfEnabled(player, "failed-download", "§cFailed to download the resource pack. Please, contact the staff!");
                break;
            case DECLINED:
                kickPlayerIfEnabled(player, "declined", "§cYou need to accept the resource pack to play on the server!");
                break;
        }
    }

    private void sendMessageIfEnabled(Player player, String key, String def) {
        if (resourcePack.getBoolean("messaging", false))
            player.sendMessage(resourcePack.getString(key, def));
    }

    private boolean kickPlayerIfEnabled(Player player, String key, String def) {
        if (resourcePack.getBoolean("kick-player", true)) {
            player.kickPlayer(resourcePack.getString(key, def));
            return true;
        }

        return false;
    }

}
