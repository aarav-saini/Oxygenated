package org.GalacticNuclei.oxygenated;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
public class Msg {
    private static final MiniMessage mm = MiniMessage.miniMessage();
    private static String defaultPrefix = "<gray>[Server]</gray> ";
    public static void loadPrefix() {
        String cfgPrefix = Oxygenated.getInstance().getConfig().getString("default-prefix");
        if (cfgPrefix != null) {
            defaultPrefix = cfgPrefix;
        }
    }
    public static void send(CommandSender sender, String message) {
        sendWithPrefix(sender, message, defaultPrefix);
    }
    public static void sendWithPrefix(CommandSender sender, String message, String prefix) {
        sender.sendMessage(mm.deserialize(prefix + " " + message));
    }
    public static void sendRaw(CommandSender sender, String message) {
        sender.sendMessage(mm.deserialize(message));
    }
    public static @NotNull Component deserialize(@NotNull String message) {
        return mm.deserialize(message);
    }
}