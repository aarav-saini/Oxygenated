package org.GalacticNuclei.oxygenated.commands;

import org.GalacticNuclei.oxygenated.Msg;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ClearChat implements CommandExecutor {

    private static final int CLEAR_LINES = 200;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if (args.length == 0) {

            clearChatForAll();
            Msg.send(sender, "<green>Chat has been cleared for all players.");
            return true;
        }
        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            Msg.send(sender, "<red>Player '<white>" + args[0] + "</white>' not found.");
            return true;
        }

        clearChat(target);

        Msg.send(sender, "<green>You cleared chat for <yellow>" + target.getName() + "</yellow>.");
        Msg.send(target, "<green>Your chat has been cleared by <yellow>" + sender.getName() + "</yellow>.");

        return true;
    }

    private void clearChat(Player player) {
        for (int i = 0; i < CLEAR_LINES; i++) {
            player.sendMessage("");
        }
    }

    private void clearChatForAll() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            clearChat(p);
        }
    }
}
