package ru.omashune.headsanywhere;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ru.omashune.headsanywhere.hook.HeadsAnywhereExpansion;
import ru.omashune.headsanywhere.listener.ResourcePackListener;
import ru.omashune.headsanywhere.listener.SkinApplyListener;
import ru.omashune.headsanywhere.manager.HeadManager;

public final class HeadsAnywhere extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        HeadManager headManager = new HeadManager(getConfig().getInt("cache-time", 300),
                getConfig().getString("heads-provider", "https://minotar.net/helm/%s/8.png"));
        PluginManager pluginManager = Bukkit.getPluginManager();

        ConfigurationSection resourcePack = getConfig().getConfigurationSection("resource-pack");
        if (resourcePack.getBoolean("enabled", false))
            pluginManager.registerEvents(new ResourcePackListener(resourcePack), this);

        if (pluginManager.getPlugin("SkinsRestorer") != null)
            pluginManager.registerEvents(new SkinApplyListener(this, headManager), this);

        if (pluginManager.getPlugin("PlaceholderAPI") != null)
            new HeadsAnywhereExpansion(headManager).register();
    }

}
