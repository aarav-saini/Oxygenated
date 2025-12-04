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
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
public class Mute implements CommandExecutor {
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 0) {
            Msg.send(sender, "<red>Usage: /mute <player> <reason> [duration] | /mute delete <id> | /mute show <player>");
            return true;
        }
        String sub = args[0].toLowerCase();
        try {
            return switch (sub) {
                case "delete", "del" -> handleDelete(sender, args);
                case "show" -> handleShow(sender, args);
                default -> handleMute(sender, args);
            };
        } catch (SQLException e) {
            Msg.send(sender, "<red>Database error occurred. Contact an administrator.");
            sender.getServer().getConsoleSender().sendMessage("SQL Error in mute command: " + e.getMessage());
            e.printStackTrace();
            return true;
        }
    }
    private boolean handleMute(CommandSender sender, String[] args) throws SQLException {
        if (!sender.hasPermission("oxygenated.mute")) {
            Msg.send(sender, "<red>You do not have permission.");
            return true;
        }
        if (args.length < 2) {
            Msg.send(sender, "<red>Usage: /mute <player> <reason> [duration]");
            Msg.send(sender, "<gray>Duration examples: 1h, 2d, 1w, 30m, 1y, permanent");
            return true;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            Msg.send(sender, "<red>Player '<white>" + args[0] + "</white>' not found.");
            return true;
        }
        long expireTimestamp = 0;
        String reason;
        String lastArg = args[args.length - 1].toLowerCase();
        if (args.length >= 3 && (lastArg.equals("permanent") || lastArg.matches("\\d+[hdwmy]"))) {
            long duration = parseDuration(lastArg);
            expireTimestamp = duration > 0 ? System.currentTimeMillis() + duration : 0;
            reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length - 1));
        } else {
            reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        }
        SQL.addMute(target, reason, sender.getName(), expireTimestamp);
        String expiryText = expireTimestamp == 0 ? "Permanent" : dateFormat.format(new Date(expireTimestamp));
        Msg.send(sender, "<gray>[MUTE] <yellow>" + target.getName() + "</yellow> muted for <white>" + reason + "</white>");
        Msg.send(sender, "<gray>Duration: <yellow>" + expiryText + "</yellow>");
        Msg.send(target, "<red>[MUTE] You have been muted by <yellow>" + sender.getName() + "</yellow>");
        Msg.send(target, "<gray>Reason: <white>" + reason + "</gray>");
        Msg.send(target, "<gray>Expires: <yellow>" + expiryText + "</yellow>");
        return true;
    }
    private boolean handleDelete(CommandSender sender, String[] args) throws SQLException {
        if (!sender.hasPermission("oxygenated.mute.delete")) {
            Msg.send(sender, "<red>You do not have permission to delete mutes.");
            return true;
        }
        if (args.length < 2) {
            Msg.send(sender, "<red>Usage: /mute delete <id>");
            return true;
        }
        int id;
        try {
            id = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            Msg.send(sender, "<red>Invalid mute ID. Must be a number.");
            return true;
        }
        SQL.deleteMute(id);
        Msg.send(sender, "<green>Mute with ID <yellow>" + id + "</yellow> deleted successfully.");
        return true;
    }
    private boolean handleShow(CommandSender sender, String[] args) throws SQLException {
        if (!sender.hasPermission("oxygenated.mute.show")) {
            Msg.send(sender, "<red>You do not have permission to view mutes.");
            return true;
        }
        if (args.length < 2) {
            Msg.send(sender, "<red>Usage: /mute show <player>");
            return true;
        }
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            Msg.send(sender, "<red>Player '<white>" + args[1] + "</white>' not found.");
            return true;
        }
        List<SQL.Mute> mutes = SQL.getPlayerMutes(target.getUniqueId());
        if (mutes.isEmpty()) {
            Msg.send(sender, "<green><yellow>" + target.getName() + "</yellow> has no mutes.");
            return true;
        }
        Msg.send(sender, "<gray>=== Mutes for <yellow>" + target.getName() + "</yellow> ===");
        for (SQL.Mute m : mutes) {
            String time = dateFormat.format(new Date(m.timestamp()));
            String expires = m.muteExpires() == 0 ? "Permanent" : dateFormat.format(new Date(m.muteExpires()));
            Msg.send(sender, String.format("<gray>ID %d | <white>%s <dark_gray>by <yellow>%s</yellow> <gray>at %s | Expires: %s",
                    m.id(), m.reason(), m.mutedBy(), time, expires));
        }
        return true;
    }
    private long parseDuration(String durationStr) {
        if (durationStr.equalsIgnoreCase("permanent")) {
            return 0;
        }
        if (durationStr.matches("\\d+[hdwmy]")) {
            String numStr = durationStr.substring(0, durationStr.length() - 1);
            char unit = durationStr.charAt(durationStr.length() - 1);
            long amount = Long.parseLong(numStr);
            return switch (unit) {
                case 'm' -> TimeUnit.MINUTES.toMillis(amount);
                case 'h' -> TimeUnit.HOURS.toMillis(amount);
                case 'd' -> TimeUnit.DAYS.toMillis(amount);
                case 'w' -> TimeUnit.DAYS.toMillis(amount * 7);
                case 'y' -> TimeUnit.DAYS.toMillis(amount * 365);
                default -> 0;
            };
        }
        return 0;
    }
}