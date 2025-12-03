package org.GalacticNuclei.oxygenated.commands.time;

import org.GalacticNuclei.oxygenated.Msg;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Night implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("oxygenated.time.night")) {
            Msg.send(sender, "<red>You do not have permission.");
            return true;
        }

        World world = Bukkit.getWorlds().get(0);
        if (world == null) {
            Msg.send(sender, "<red>No world is loaded.");
            return true;
        }

        world.setTime(13000L); // night start ~7:00 PM [web:9]
        Msg.send(sender, "<green>Time set to <yellow>night</yellow>.");

        return true;
    }
}
