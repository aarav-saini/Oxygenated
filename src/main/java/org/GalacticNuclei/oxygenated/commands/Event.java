package org.GalacticNuclei.oxygenated.commands;

import org.GalacticNuclei.oxygenated.Msg;
import org.GalacticNuclei.oxygenated.database.SQL;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Stream;

public class Event implements CommandExecutor, TabCompleter {

    /* ===============================
       COMMAND
    =============================== */

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {

        if (args.length == 0) {
            Msg.send(sender, "<red>Usage: /eventwarp <add|delete|enable|disable|list|tp>");
            return true;
        }

        String sub = args[0].toLowerCase(Locale.ROOT);

        try {
            switch (sub) {

                /* ===============================
                   ADD
                =============================== */
                case "add" -> {
                    if (!sender.hasPermission("oxygenated.eventwarp.add")) {
                        Msg.send(sender, "<red>You do not have permission to add event warps.");
                        return true;
                    }

                    if (!(sender instanceof Player player)) {
                        Msg.send(sender, "<red>Only players can add event warps.");
                        return true;
                    }

                    if (args.length < 2) {
                        Msg.send(sender, "<red>Usage: /eventwarp add <name>");
                        return true;
                    }

                    String name = args[1].toLowerCase(Locale.ROOT);

                    if (SQL.eventWarpExists(name)) {
                        Msg.send(sender, "<red>That warp already exists.");
                        return true;
                    }

                    SQL.addEventWarp(
                            name,
                            player.getWorld().getName(),
                            player.getLocation().getX(),
                            player.getLocation().getY(),
                            player.getLocation().getZ()
                    );

                    Msg.send(sender, "<green>Event warp '<yellow>" + name + "</yellow>' created (disabled).");
                }

                /* ===============================
                   DELETE
                =============================== */
                case "delete" -> {
                    if (!sender.hasPermission("oxygenated.eventwarp.delete")) {
                        Msg.send(sender, "<red>You do not have permission to delete event warps.");
                        return true;
                    }

                    if (args.length < 2) {
                        Msg.send(sender, "<red>Usage: /eventwarp delete <name>");
                        return true;
                    }

                    String name = args[1].toLowerCase(Locale.ROOT);

                    if (!SQL.eventWarpExists(name)) {
                        Msg.send(sender, "<red>That warp does not exist.");
                        return true;
                    }

                    SQL.deleteEventWarp(name);
                    Msg.send(sender, "<green>Event warp '<yellow>" + name + "</yellow>' deleted.");
                }

                /* ===============================
                   ENABLE / DISABLE
                =============================== */
                case "enable", "disable" -> {
                    boolean enable = sub.equals("enable");

                    if (!sender.hasPermission("oxygenated.eventwarp." + sub)) {
                        Msg.send(sender, "<red>You do not have permission to " + sub + " event warps.");
                        return true;
                    }

                    if (args.length < 2) {
                        Msg.send(sender, "<red>Usage: /eventwarp " + sub + " <name>");
                        return true;
                    }

                    String name = args[1].toLowerCase(Locale.ROOT);

                    if (!SQL.eventWarpExists(name)) {
                        Msg.send(sender, "<red>That warp does not exist.");
                        return true;
                    }

                    SQL.setEventWarpEnabled(name, enable);
                    Msg.send(sender, "<green>Event warp '<yellow>" + name + "</yellow>' " +
                            (enable ? "enabled" : "disabled") + ".");
                }

                /* ===============================
                   LIST
                =============================== */
                case "list" -> {
                    if (!sender.hasPermission("oxygenated.eventwarp.list")) {
                        Msg.send(sender, "<red>You do not have permission to list event warps.");
                        return true;
                    }

                    List<SQL.EventWarp> warps = SQL.getAllEventWarps();

                    if (warps.isEmpty()) {
                        Msg.send(sender, "<yellow>No event warps exist.");
                        return true;
                    }

                    StringBuilder sb = new StringBuilder("<green>Event Warps: ");

                    for (SQL.EventWarp warp : warps) {
                        sb.append("<yellow>")
                                .append(warp.name())
                                .append("</yellow>")
                                .append(warp.enabled() ? "<green> (enabled)" : "<red> (disabled)")
                                .append("<gray>, ");
                    }

                    String msg = sb.substring(0, sb.length() - 2);
                    Msg.send(sender, msg);
                }

                /* ===============================
                   TP
                =============================== */
                case "tp" -> {
                    if (!sender.hasPermission("oxygenated.eventwarp.tp")) {
                        Msg.send(sender, "<red>You do not have permission to teleport to event warps.");
                        return true;
                    }

                    if (args.length < 2) {
                        Msg.send(sender, "<red>Usage: /eventwarp tp [player] <warp>");
                        return true;
                    }

                    Player target;
                    String warpName;

                    if (args.length == 2) {
                        if (!(sender instanceof Player player)) {
                            Msg.send(sender, "<red>Console must specify a player.");
                            return true;
                        }
                        target = player;
                        warpName = args[1];
                    } else {
                        target = Bukkit.getPlayer(args[1]);
                        if (target == null) {
                            Msg.send(sender, "<red>Player not found.");
                            return true;
                        }
                        warpName = args[2];
                    }

                    SQL.EventWarp warp = SQL.getEventWarp(warpName.toLowerCase(Locale.ROOT));

                    if (warp == null) {
                        Msg.send(sender, "<red>That warp does not exist.");
                        return true;
                    }

                    if (!warp.enabled()) {
                        Msg.send(sender, "<red>That warp is currently disabled.");
                        return true;
                    }

                    World world = Bukkit.getWorld(warp.world());
                    if (world == null) {
                        Msg.send(sender, "<red>Warp world is not loaded.");
                        return true;
                    }

                    target.teleport(world.getBlockAt(
                            (int) warp.x(),
                            (int) warp.y(),
                            (int) warp.z()
                    ).getLocation().add(0.5, 0, 0.5));

                    Msg.send(sender, "<green>Teleported <yellow>" + target.getName() +
                            "</yellow> to event warp '<yellow>" + warp.name() + "</yellow>'.");
                }

                default -> Msg.send(sender,
                        "<red>Usage: /eventwarp <add|delete|enable|disable|list|tp>");
            }
        } catch (SQLException e) {
            Msg.send(sender, "<red>Database error occurred.");
            e.printStackTrace();
        }

        return true;
    }

    /* ===============================
       TAB COMPLETION
    =============================== */

    @Override
    public @Nullable List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String alias,
            @NotNull String[] args
    ) {

        try {
            if (args.length == 1) {
                return Stream.of("add", "delete", "enable", "disable", "list", "tp")
                        .filter(s -> s.startsWith(args[0].toLowerCase()))
                        .toList();
            }

            if (args.length == 2 && args[0].equalsIgnoreCase("tp")) {
                return Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .filter(n -> n.toLowerCase().startsWith(args[1].toLowerCase()))
                        .toList();
            }

            if (args.length == 2 &&
                    List.of("delete", "enable", "disable").contains(args[0].toLowerCase())) {
                return SQL.getAllEventWarps().stream()
                        .map(SQL.EventWarp::name)
                        .toList();
            }

            if (args.length == 3 && args[0].equalsIgnoreCase("tp")) {
                return SQL.getAllEventWarps().stream()
                        .map(SQL.EventWarp::name)
                        .toList();
            }

        } catch (SQLException ignored) {}

        return Collections.emptyList();
    }
}
