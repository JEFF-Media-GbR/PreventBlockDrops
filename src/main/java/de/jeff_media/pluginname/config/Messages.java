package de.jeff_media.pluginname.config;

import de.jeff_media.pluginname.PluginName;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Messages {

    public static String TEST1;
    public static String TEST2;
    public static String CONFIG_RELOADED;

    private final PluginName main;

    public Messages() {
        this.main = PluginName.getInstance();

        TEST1 = load("test1","&aThis is a test message.");
        TEST2 = load("test2", "&bThis is another text message.");

        CONFIG_RELOADED = color(String.format("&a%s has been reloaded.",main.getName()));
    }

    public static void showActionBarMessage(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
    }

    private String load(String path, String defaultMessage) {
        String messagePrefix = "message-";
        return color(main.getConfig().getString(messagePrefix + path,defaultMessage));
    }

    private String color(String message) {
        return ChatColor.translateAlternateColorCodes('&',message);
    }

}
