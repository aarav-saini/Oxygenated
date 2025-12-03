package org.GalacticNuclei.oxygenated.listeners;

import org.GalacticNuclei.oxygenated.Msg;
import org.GalacticNuclei.oxygenated.commands.SocialSpy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class SocialSpyListener implements Listener {

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {

        String raw = event.getMessage(); // e.g. "/msg Steve hello there"
        Player sender = event.getPlayer();

        if (raw.length() < 2 || raw.charAt(0) != '/') {
            return;
        }

        // Strip leading "/"
        String withoutSlash = raw.substring(1);
        String[] split = withoutSlash.split(" ", 2);

        String label = split[0].toLowerCase();          // "msg"
        String argsRaw = split.length > 1 ? split[1] : "";

        // List of commands you treat as private-message commands
        if (!isPrivateMessageCommand(label)) {
            return;
        }

        // Optional: basic sanity check to ensure there are args
        if (argsRaw.isEmpty()) {
            return;
        }

        // Relay to all spies
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.equals(sender)) continue;
            if (!SocialSpy.hasSocialSpy(online)) continue;

            Msg.send(online,
                    "<gray>[Spy]</gray> <yellow>" + sender.getName()
                            + "</yellow> <gray>ran</gray> <white>/" + label + " " + argsRaw + "</white>");
        }
    }

    private boolean isPrivateMessageCommand(String label) {
        switch (label) {
            case "msg":
            case "tell":
            case "w":
            case "whisper":
            case "pm":
            case "r":
            case "m":
                return true;
            // Add plugin-specific commands (e.g. "cmi msg", "mail", etc.) if needed
            default:
                return false;
        }
    }
}
