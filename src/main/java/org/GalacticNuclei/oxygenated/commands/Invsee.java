package org.GalacticNuclei.oxygenated.commands;

import org.GalacticNuclei.oxygenated.Msg;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Invsee implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player viewer)) {
            Msg.send(sender, "<red>Only players may use this command.");
            return true;
        }

        if (!viewer.hasPermission("oxygenated.invsee")) {
            Msg.send(viewer, "<red>You do not have permission.");
            return true;
        }

        if (args.length != 1) {
            Msg.send(viewer, "<yellow>Usage: /invsee <player>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            Msg.send(viewer, "<red>Player '<white>" + args[0] + "</white>' not found.");
            return true;
        }

        openInventory(viewer, target);

        Msg.send(viewer, "<green>Opening inventory of <yellow>" + target.getName() + "</yellow>.");
        return true;
    }

    private void openInventory(Player viewer, Player target) {
        viewer.openInventory(target.getInventory());
    }
}
