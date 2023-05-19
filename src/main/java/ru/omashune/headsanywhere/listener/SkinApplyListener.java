package ru.omashune.headsanywhere.listener;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.skinsrestorer.api.bukkit.events.SkinApplyBukkitEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import ru.omashune.headsanywhere.HeadsAnywhere;
import ru.omashune.headsanywhere.manager.HeadManager;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SkinApplyListener implements Listener {

    HeadsAnywhere plugin;
    HeadManager headManager;

    @EventHandler
    public void onSkinApply(SkinApplyBukkitEvent e) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> headManager.refreshPlayerHead(e.getWho()), 1);
    }

}
