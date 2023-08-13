package ru.omashune.headsanywhere.hook;

import lombok.AllArgsConstructor;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.omashune.headsanywhere.manager.HeadManager;

@AllArgsConstructor
public class HeadsAnywhereExpansion extends PlaceholderExpansion {

    private final HeadManager headManager;

    @Override
    public @NotNull String getIdentifier() {
        return "headsanywhere";
    }

    @Override
    public @NotNull String getAuthor() {
        return "omashune";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (player == null) return null;
        if (!identifier.equalsIgnoreCase("head")) return null;

        BaseComponent[] head = headManager.getPlayerHead(player);
        return head == null ? "" : BaseComponent.toLegacyText(head) + ChatColor.RESET;
    }
}
