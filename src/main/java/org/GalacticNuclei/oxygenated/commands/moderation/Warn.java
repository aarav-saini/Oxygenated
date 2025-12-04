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
public class Warn implements CommandExecutor {
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 0) {
            Msg.send(sender, "<red>Usage: /warn <player> <reason> | /warn delete <id> | /warn show <player>");
            return true;
        }
        String sub = args[0].toLowerCase();
        try {
            return switch (sub) {
                case "delete", "del" -> handleDelete(sender, args);
                case "show" -> handleShow(sender, args);
                default -> handleWarn(sender, args);
            };
        } catch (SQLException e) {
            Msg.send(sender, "<red>Database error occurred. Contact an administrator.");
            sender.getServer().getConsoleSender().sendMessage("SQL Error in warn command: " + e.getMessage());
            e.printStackTrace();
            return true;
        }
    }
    private boolean handleDelete(CommandSender sender, String[] args) throws SQLException {
        if (!sender.hasPermission("oxygenated.warn.delete")) {
            Msg.send(sender, "<red>You do not have permission to delete warnings.");
            return true;
        }
        if (args.length < 2) {
            Msg.send(sender, "<red>Usage: /warn delete <id>");
            return true;
        }
        int id;
        try {
            id = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            Msg.send(sender, "<red>Invalid warning ID. Must be a number.");
            return true;
        }
        SQL.deleteWarning(id);
        Msg.send(sender, "<green>Warning with ID <yellow>" + id + "</yellow> deleted.");
        return true;
    }
    private boolean handleShow(CommandSender sender, String[] args) throws SQLException {
        if (!sender.hasPermission("oxygenated.warn.show")) {
            Msg.send(sender, "<red>You do not have permission to view warnings.");
            return true;
        }
        if (args.length < 2) {
            Msg.send(sender, "<red>Usage: /warn show <player>");
            return true;
        }
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            Msg.send(sender, "<red>Player '<white>" + args[1] + "</white>' not found.");
            return true;
        }
        List<SQL.Warning> warnings = SQL.getPlayerWarnings(target.getUniqueId());
        if (warnings.isEmpty()) {
            Msg.send(sender, "<green>Player <yellow>" + target.getName() + "</yellow> has no warnings.");
            return true;
        }
        Msg.send(sender, "<gold>Warnings for <yellow>" + target.getName() + "</yellow>:");
        for (SQL.Warning w : warnings) {
            String time = dateFormat.format(new Date(w.timestamp()));
            Msg.send(sender, String.format("<gray>ID %d <white>[<yellow>%s</yellow>]: <white>%s <dark_gray>- warned by <yellow>%s</yellow> at %s",
                    w.id(), w.playerName(), w.reason(), w.warnedBy(), time));
        }
        return true;
    }
    private boolean handleWarn(CommandSender sender, String[] args) throws SQLException {
        if (!sender.hasPermission("oxygenated.warn")) {
            Msg.send(sender, "<red>You do not have permission.");
            return true;
        }
        if (args.length < 2) {
            Msg.send(sender, "<red>Usage: /warn <player> <reason>");
            return true;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            Msg.send(sender, "<red>Player '<white>" + args[0] + "</white>' not found.");
            return true;
        }
        String reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        SQL.addWarning(target, reason, sender.getName());
        int warningCount = SQL.getWarningCount(target.getUniqueId());
        Msg.send(sender, "<green>Warned <yellow>" + target.getName() + "</yellow> (" + warningCount + " total warnings)");
        Msg.send(sender, "<gray>Reason: <white>" + reason);
        Msg.send(target, "<red>[WARN] You have been warned by <yellow>" + sender.getName() + "</yellow>");
        Msg.send(target, "<gray>Reason: <white>" + reason);
        Msg.send(target, "<yellow>You now have <bold>" + warningCount + "</bold> total warnings.");
        if (warningCount >= 3) {
            Bukkit.broadcast(
                    Msg.deserialize("<red>[STAFF] <yellow>" + target.getName() + " has reached " + warningCount + " warnings!"),
                    "oxygenated.staff"
            );
        }
        return true;
    }
}