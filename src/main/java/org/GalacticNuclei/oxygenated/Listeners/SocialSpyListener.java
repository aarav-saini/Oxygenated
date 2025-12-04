package org.GalacticNuclei.oxygenated.Listeners;
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
        String raw = event.getMessage();
        Player sender = event.getPlayer();
        if (raw.length() < 2 || raw.charAt(0) != '/') {
            return;
        }
        String withoutSlash = raw.substring(1);
        String[] split = withoutSlash.split(" ", 2);
        String label = split[0].toLowerCase();
        String argsRaw = split.length > 1 ? split[1] : "";
        if (!isPrivateMessageCommand(label)) {
            return;
        }
        if (argsRaw.isEmpty()) {
            return;
        }
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.equals(sender)) continue;
            if (!SocialSpy.hasSocialSpy(online)) continue;
            Msg.send(online,
                    "<gray>[Spy]</gray> <yellow>" + sender.getName()
                            + "</yellow> <gray>ran</gray> <white>/" + label + " " + argsRaw + "</white>");
        }
    }
    private boolean isPrivateMessageCommand(String label) {
        return switch (label) {
            case "msg", "tell", "w", "whisper", "pm", "r", "m" -> true;
            default -> false;
        };
    }
}