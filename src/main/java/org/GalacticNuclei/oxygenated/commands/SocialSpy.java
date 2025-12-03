package org.GalacticNuclei.oxygenated.commands;

import org.GalacticNuclei.oxygenated.Msg;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SocialSpy implements CommandExecutor {

    private static final Set<UUID> SPY_ENABLED = new HashSet<>();

    public static boolean hasSocialSpy(Player player) {
        return SPY_ENABLED.contains(player.getUniqueId());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            Msg.send(sender, "<red>Only players may use this command.");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("oxygenated.socialspy")) {
            Msg.send(player, "<red>You do not have permission.");
            return true;
        }

        UUID id = player.getUniqueId();

        if (SPY_ENABLED.contains(id)) {
            SPY_ENABLED.remove(id);
            Msg.send(player, "<yellow>SocialSpy <red>disabled</red>.");
        } else {
            SPY_ENABLED.add(id);
            Msg.send(player, "<yellow>SocialSpy <green>enabled</green>.");
        }

        return true;
    }
}
