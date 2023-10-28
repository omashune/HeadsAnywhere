package ru.omashune.headsanywhere;

import net.skinsrestorer.api.SkinsRestorerProvider;
import net.skinsrestorer.api.event.SkinApplyEvent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ru.omashune.headsanywhere.hook.HeadsAnywhereExpansion;
import ru.omashune.headsanywhere.listener.ResourcePackListener;
import ru.omashune.headsanywhere.manager.HeadManager;

public final class HeadsAnywhere extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        HeadManager headManager = new HeadManager(getConfig().getInt("cache-time", 300),
                getConfig().getString("heads-provider", "https://mc-heads.net/avatar/%s/8"));
        PluginManager pluginManager = Bukkit.getPluginManager();

        ConfigurationSection resourcePack = getConfig().getConfigurationSection("resource-pack");
        if (resourcePack.getBoolean("enabled", false))
            pluginManager.registerEvents(new ResourcePackListener(resourcePack), this);

        if (pluginManager.getPlugin("SkinsRestorer") != null)
            SkinsRestorerProvider.get().getEventBus()
                    .subscribe(this, SkinApplyEvent.class, e ->
                            Bukkit.getScheduler().runTaskLater(this, () -> headManager.refreshPlayerHead(e.getPlayer(Player.class).getName()), 1));

        if (pluginManager.getPlugin("PlaceholderAPI") != null)
            new HeadsAnywhereExpansion(headManager).register();
    }

}
