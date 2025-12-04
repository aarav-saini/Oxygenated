package org.GalacticNuclei.oxygenated;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

public class Msg {
    private static final MiniMessage mm = MiniMessage.miniMessage();
    private static String defaultPrefix = "<gray>[<bold>Oxygenated</bold>]</gray> ";
    public static void send(CommandSender sender, String message) {
        sendWithPrefix(sender, message, defaultPrefix);
    }
    public static void sendWithPrefix(CommandSender sender, String message, String prefix) {
        sender.sendMessage(mm.deserialize(prefix + message));
    }
    public static void sendRaw(CommandSender sender, String message) {
        sender.sendMessage(mm.deserialize(message));
    }
    public static void setDefaultPrefix(String prefix) {
        defaultPrefix = prefix;
    }
    public static String getDefaultPrefix() {
        return defaultPrefix;
    }
    public static @Nullable @NotNull Component deserialize(String message) {
        return mm.deserialize(message);
    }
}