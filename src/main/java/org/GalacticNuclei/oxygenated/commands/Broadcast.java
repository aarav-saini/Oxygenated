package org.GalacticNuclei.oxygenated.commands;

import org.GalacticNuclei.oxygenated.Msg;
import org.GalacticNuclei.oxygenated.Oxygenated;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Broadcast implements CommandExecutor {
    private final Oxygenated plugin = Oxygenated.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String prefix = plugin.getConfig().getString("broadcast.prefix");
        int fadeIn = plugin.getConfig().getInt("broadcast.fade-in-ticks");
        int stay = plugin.getConfig().getInt("broadcast.stay-ticks");
        int fadeOut = plugin.getConfig().getInt("broadcast.fade-out-ticks");
        int soundVolume = plugin.getConfig().getInt("broadcast.sound-volume");
        int soundPitch = plugin.getConfig().getInt("broadcast.sound-pitch");
        boolean titleEnabled = plugin.getConfig().getBoolean("broadcast.title-enabled");
        String titleText = plugin.getConfig().getString("broadcast.title-text");
        if (!sender.hasPermission("oxygenated.broadcast")) {
            Msg.send(sender, "<red>You do not have permission.");
            return true;
        }

        if (args.length == 0) {
            Msg.send(sender, "<red>Usage: /broadcast <message>");
            return true;
        }

        String message = String.join(" ", args);

        // Sending title, chat message, and sound to each player
        for (Player player : Bukkit.getOnlinePlayers()) {
            // Legacy title (Bukkit 1.8-1.20)
            if (titleEnabled) {
                player.sendTitle(ChatColor.GOLD + titleText, ChatColor.WHITE + message, fadeIn, stay, fadeOut);
            }

            // Chat message
            Msg.sendRaw(player, prefix + " " + message);

            // Sound
            player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, soundVolume, soundPitch);
        }

        return true;
    }
}
