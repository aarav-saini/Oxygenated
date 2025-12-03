package org.GalacticNuclei.oxygenated.commands;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Broadcast implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("oxygenated.broadcast")) {
            sender.sendMessage("§cYou do not have permission.");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage("§cUsage: /broadcast <message>");
            return true;
        }

        String message = String.join(" ", args);

        Bukkit.broadcastMessage("§6[BROADCAST] §f" + message);

        // Sound for all players
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1f, 1f);
        }

        return true;
    }
}
