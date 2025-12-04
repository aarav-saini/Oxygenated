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
public class Ban implements CommandExecutor {
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 0) {
            Msg.send(sender, "<red>Usage: /ban <player> <reason> [duration] | /ban delete <id> | /ban show <player>");
            return true;
        }
        String sub = args[0].toLowerCase();
        try {
            return switch (sub) {
                case "delete", "del" -> handleDelete(sender, args);
                case "show" -> handleShow(sender, args);
                default -> handleBan(sender, args);
            };
        } catch (SQLException e) {
            Msg.send(sender, "<red>Database error occurred. Contact an administrator.");
            sender.getServer().getConsoleSender().sendMessage("SQL Error in ban command: " + e.getMessage());
            e.printStackTrace();
            return true;
        }
    }
    private boolean handleBan(CommandSender sender, String[] args) throws SQLException {
        if (!sender.hasPermission("oxygenated.ban")) {
            Msg.send(sender, "<red>You do not have permission.");
            return true;
        }
        if (args.length < 2) {
            Msg.send(sender, "<red>Usage: /ban <player> <reason> [duration]");
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
        SQL.addBan(target, reason, sender.getName(), expireTimestamp);
        String expiryText = expireTimestamp == 0 ? "Permanent" : dateFormat.format(new Date(expireTimestamp));
        target.kick(Msg.deserialize(
                "<red>[BAN] You have been banned!\n" +
                        "<white>Reason: <gray>" + reason + "\n" +
                        "<white>Expires: <yellow>" + expiryText + "\n" +
                        "<gray>Banned by: <yellow>" + sender.getName()
        ));
        Msg.send(sender, "<red>[BAN] <yellow>" + target.getName() + "</yellow> banned for <white>" + reason + "</white>");
        Msg.send(sender, "<gray>Duration: <yellow>" + expiryText + "</yellow>");
        return true;
    }
    private boolean handleDelete(CommandSender sender, String[] args) throws SQLException {
        if (!sender.hasPermission("oxygenated.ban.delete")) {
            Msg.send(sender, "<red>You do not have permission to delete bans.");
            return true;
        }
        if (args.length < 2) {
            Msg.send(sender, "<red>Usage: /ban delete <id>");
            return true;
        }
        int id;
        try {
            id = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            Msg.send(sender, "<red>Invalid ban ID. Must be a number.");
            return true;
        }
        SQL.deleteBan(id);
        Msg.send(sender, "<green>Ban with ID <yellow>" + id + "</yellow> deleted successfully.");
        return true;
    }
    private boolean handleShow(CommandSender sender, String[] args) throws SQLException {
        if (!sender.hasPermission("oxygenated.ban.show")) {
            Msg.send(sender, "<red>You do not have permission to view bans.");
            return true;
        }
        if (args.length < 2) {
            Msg.send(sender, "<red>Usage: /ban show <player>");
            return true;
        }
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            Msg.send(sender, "<red>Player '<white>" + args[1] + "</white>' not found.");
            return true;
        }
        List<SQL.Ban> bans = SQL.getPlayerBans(target.getUniqueId());
        if (bans.isEmpty()) {
            Msg.send(sender, "<green><yellow>" + target.getName() + "</yellow> has no bans.");
            return true;
        }
        Msg.send(sender, "<red>=== Bans for <yellow>" + target.getName() + "</yellow> ===");
        for (SQL.Ban b : bans) {
            String time = dateFormat.format(new Date(b.timestamp()));
            String expires = b.banExpires() == 0 ? "Permanent" : dateFormat.format(new Date(b.banExpires()));
            Msg.send(sender, String.format("<red>ID %d | <white>%s <dark_gray>by <yellow>%s</yellow> <gray>at %s | Expires: %s",
                    b.id(), b.reason(), b.bannedBy(), time, expires));
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