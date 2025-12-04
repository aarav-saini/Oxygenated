package org.GalacticNuclei.oxygenated.commands;

import org.GalacticNuclei.oxygenated.Msg;
import org.GalacticNuclei.oxygenated.Oxygenated;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Reload implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("oxygenated.reload")) {
            Msg.send(sender, "<red>You do not have permission.");
            return true;
        }

        Oxygenated plugin = Oxygenated.getInstance();

        plugin.reloadConfig();  // Reload configuration from disk

        Msg.send(sender, "<green>Configuration reloaded successfully.");

        return true;
    }
}
