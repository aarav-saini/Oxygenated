package org.GalacticNuclei.oxygenated.Listeners;

import org.GalacticNuclei.oxygenated.Msg;
import org.GalacticNuclei.oxygenated.database.SQL;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BanListener implements Listener {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        try {
            if (SQL.isBanned(event.getPlayer().getUniqueId())) {
                // Get latest ban reason and expiry
                var bans = SQL.getPlayerBans(event.getPlayer().getUniqueId());
                if (!bans.isEmpty()) {
                    var latestBan = bans.get(0);
                    String expires = latestBan.banExpires() == 0 ? "Permanent" : dateFormat.format(new Date(latestBan.banExpires()));
                    String kickMessage = Msg.deserialize(
                            "<red>[BAN] You are banned!\nReason: <white>" + latestBan.reason() +
                                    "\nExpires: <yellow>" + expires + "</yellow>"
                    ).toString();
                    event.disallow(PlayerLoginEvent.Result.KICK_BANNED, kickMessage);
                } else {
                    event.disallow(PlayerLoginEvent.Result.KICK_BANNED, "You are banned from this server.");
                }
            }
        } catch (SQLException e) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Database error. Contact an admin.");
            e.printStackTrace();
        }
    }
}
