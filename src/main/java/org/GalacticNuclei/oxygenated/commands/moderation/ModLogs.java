package org.GalacticNuclei.oxygenated.commands.moderation;
import org.GalacticNuclei.oxygenated.Msg;
import org.GalacticNuclei.oxygenated.database.SQL;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
public class ModLogs implements CommandExecutor {
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!sender.hasPermission("oxygenated.modlogs")) {
            Msg.send(sender, "<red>You do not have permission.");
            return true;
        }
        if (args.length < 1) {
            Msg.send(sender, "<red>Usage: /modlogs <player>");
            return true;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            Msg.send(sender, "<red>Player '<white>" + args[0] + "</white>' not found.");
            return true;
        }
        try {
            List<ModLogEntry> logs = getAllModLogs(target.getUniqueId());
            if (logs.isEmpty()) {
                Msg.send(sender, "<green><yellow>" + target.getName() + "</yellow> has no moderation history.");
                return true;
            }
            Msg.send(sender, "<gold>=== <yellow>" + target.getName() + "</yellow>'s Moderation History <gold>===");
            Msg.send(sender, "<gray>Total: <yellow>" + logs.size() + "</yellow> actions");
            for (ModLogEntry log : logs) {
                String time = dateFormat.format(new Date(log.timestamp));
                String expires = log.expires == 0 ? "Permanent" : dateFormat.format(new Date(log.expires));
                Msg.send(sender, String.format("<gray>[ID:%d] <white>%s <dark_gray>by <yellow>%s</yellow> <gray>at %s %s",
                        log.id, log.actionType, log.staffName, time, getDurationText(log, expires)));
            }
        } catch (SQLException e) {
            Msg.send(sender, "<red>Database error occurred.");
            sender.getServer().getConsoleSender().sendMessage("SQL Error in modlogs: " + e.getMessage());
            e.printStackTrace();
        }
        return true;
    }
    private List<ModLogEntry> getAllModLogs(java.util.UUID uuid) throws SQLException {
        List<ModLogEntry> allLogs = new ArrayList<>();
        for (SQL.Warning w : SQL.getPlayerWarnings(uuid)) {
            allLogs.add(new ModLogEntry(
                    w.id(), "WARN", w.reason(), w.warnedBy(), w.timestamp(), 0
            ));
        }
        for (SQL.Ban b : SQL.getPlayerBans(uuid)) {
            allLogs.add(new ModLogEntry(
                    b.id(), "BAN", b.reason(), b.bannedBy(), b.timestamp(), b.banExpires()
            ));
        }
        for (SQL.Mute m : SQL.getPlayerMutes(uuid)) {
            allLogs.add(new ModLogEntry(
                    m.id(), "MUTE", m.reason(), m.mutedBy(), m.timestamp(), m.muteExpires()
            ));
        }
        allLogs.sort((a, b) -> Long.compare(b.timestamp, a.timestamp));
        return allLogs;
    }
    private String getDurationText(ModLogEntry log, String expires) {
        if (log.actionType.equals("WARN")) {
            return "<gray>- Reason: <white>" + log.reason;
        }
        return String.format("<gray>- Expires: <yellow>%s</yellow> - <white>%s",
                expires, log.reason.length() > 50 ? log.reason.substring(0, 47) + "..." : log.reason);
    }

    private record ModLogEntry(int id, String actionType, String reason, String staffName, long timestamp,
                               long expires) {
    }
}