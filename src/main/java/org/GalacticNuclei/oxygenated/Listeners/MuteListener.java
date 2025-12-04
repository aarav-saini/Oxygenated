package org.GalacticNuclei.oxygenated.Listeners;

import org.GalacticNuclei.oxygenated.Msg;
import org.GalacticNuclei.oxygenated.database.SQL;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.sql.SQLException;

public class MuteListener implements Listener {

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        try {
            if (SQL.isMuted(player.getUniqueId())) {
                event.setCancelled(true);
                Msg.send(player, "<red>You are muted and cannot chat.");
                return;
            }
        } catch (SQLException e) {
            event.getPlayer().sendMessage("§cDatabase error - contact admin");
            e.printStackTrace();
        }
    }
}
