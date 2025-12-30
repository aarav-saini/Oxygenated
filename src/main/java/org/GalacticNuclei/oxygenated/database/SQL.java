package org.GalacticNuclei.oxygenated.database;

import org.GalacticNuclei.oxygenated.Oxygenated;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SQL {

    private static final String DB_URL = "jdbc:sqlite:plugins/Oxygenated/Oxygenated.db";

    private static final String WARNINGS_TABLE = "warnings";
    private static final String BANS_TABLE = "bans";
    private static final String MUTES_TABLE = "mutes";
    private static final String EVENT_WARPS_TABLE = "event_warps";

    /* ===============================
       DATABASE INITIALIZATION
    =============================== */

    public static void initDatabase(Oxygenated plugin) {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            // WARNINGS
            stmt.execute("CREATE TABLE IF NOT EXISTS " + WARNINGS_TABLE + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "uuid VARCHAR(36) NOT NULL," +
                    "player_name VARCHAR(16) NOT NULL," +
                    "reason TEXT NOT NULL," +
                    "warned_by VARCHAR(16) NOT NULL," +
                    "timestamp BIGINT NOT NULL)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_warnings_uuid ON " + WARNINGS_TABLE + "(uuid)");

            // BANS
            stmt.execute("CREATE TABLE IF NOT EXISTS " + BANS_TABLE + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "uuid VARCHAR(36) NOT NULL," +
                    "player_name VARCHAR(16) NOT NULL," +
                    "reason TEXT NOT NULL," +
                    "banned_by VARCHAR(16) NOT NULL," +
                    "timestamp BIGINT NOT NULL," +
                    "ban_expires BIGINT NOT NULL)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_bans_uuid ON " + BANS_TABLE + "(uuid)");

            // MUTES
            stmt.execute("CREATE TABLE IF NOT EXISTS " + MUTES_TABLE + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "uuid VARCHAR(36) NOT NULL," +
                    "player_name VARCHAR(16) NOT NULL," +
                    "reason TEXT NOT NULL," +
                    "muted_by VARCHAR(16) NOT NULL," +
                    "timestamp BIGINT NOT NULL," +
                    "mute_expires BIGINT NOT NULL)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_mutes_uuid ON " + MUTES_TABLE + "(uuid)");

            // EVENT WARPS (DISABLED BY DEFAULT)
            stmt.execute("CREATE TABLE IF NOT EXISTS " + EVENT_WARPS_TABLE + " (" +
                    "name TEXT PRIMARY KEY," +
                    "world TEXT NOT NULL," +
                    "x REAL NOT NULL," +
                    "y REAL NOT NULL," +
                    "z REAL NOT NULL," +
                    "enabled INTEGER NOT NULL)");

            plugin.getLogger().info("Database initialized successfully.");

        } catch (SQLException e) {
            plugin.getLogger().severe("Database initialization failed: " + e.getMessage());
        }
    }

    /* ===============================
       EVENT WARPS
    =============================== */

    public record EventWarp(
            String name,
            String world,
            double x,
            double y,
            double z,
            boolean enabled
    ) {}

    public static void addEventWarp(String name, String world, double x, double y, double z) throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO " + EVENT_WARPS_TABLE +
                             " (name, world, x, y, z, enabled) VALUES (?, ?, ?, ?, ?, 0)")) {
            stmt.setString(1, name.toLowerCase());
            stmt.setString(2, world);
            stmt.setDouble(3, x);
            stmt.setDouble(4, y);
            stmt.setDouble(5, z);
            stmt.executeUpdate();
        }
    }

    public static boolean eventWarpExists(String name) throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT 1 FROM " + EVENT_WARPS_TABLE + " WHERE name = ?")) {
            stmt.setString(1, name.toLowerCase());
            return stmt.executeQuery().next();
        }
    }

    public static void setEventWarpEnabled(String name, boolean enabled) throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE " + EVENT_WARPS_TABLE + " SET enabled = ? WHERE name = ?")) {
            stmt.setInt(1, enabled ? 1 : 0);
            stmt.setString(2, name.toLowerCase());
            stmt.executeUpdate();
        }
    }

    public static void deleteEventWarp(String name) throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(
                     "DELETE FROM " + EVENT_WARPS_TABLE + " WHERE name = ?")) {
            stmt.setString(1, name.toLowerCase());
            stmt.executeUpdate();
        }
    }

    public static EventWarp getEventWarp(String name) throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM " + EVENT_WARPS_TABLE + " WHERE name = ?")) {
            stmt.setString(1, name.toLowerCase());
            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) return null;

            return new EventWarp(
                    rs.getString("name"),
                    rs.getString("world"),
                    rs.getDouble("x"),
                    rs.getDouble("y"),
                    rs.getDouble("z"),
                    rs.getInt("enabled") == 1
            );
        }
    }

    public static List<EventWarp> getAllEventWarps() throws SQLException {
        List<EventWarp> warps = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM " + EVENT_WARPS_TABLE)) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                warps.add(new EventWarp(
                        rs.getString("name"),
                        rs.getString("world"),
                        rs.getDouble("x"),
                        rs.getDouble("y"),
                        rs.getDouble("z"),
                        rs.getInt("enabled") == 1
                ));
            }
        }
        return warps;
    }
    public static void addWarning(Player player, String reason, String warnedBy) throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO " + WARNINGS_TABLE + " (uuid, player_name, reason, warned_by, timestamp) VALUES (?, ?, ?, ?, ?)")) {
            stmt.setString(1, player.getUniqueId().toString());
            stmt.setString(2, player.getName());
            stmt.setString(3, reason);
            stmt.setString(4, warnedBy);
            stmt.setLong(5, System.currentTimeMillis());
            stmt.executeUpdate();
        }
    }
    public static int getWarningCount(UUID uuid) throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT COUNT(*) FROM " + WARNINGS_TABLE + " WHERE uuid = ?")) {
            stmt.setString(1, uuid.toString());
            ResultSet rs = stmt.executeQuery();
            return rs.getInt(1);
        }
    }
    public static List<Warning> getPlayerWarnings(UUID uuid) throws SQLException {
        List<Warning> warnings = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM " + WARNINGS_TABLE + " WHERE uuid = ? ORDER BY timestamp DESC")) {
            stmt.setString(1, uuid.toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                warnings.add(new Warning(
                        rs.getInt("id"),
                        UUID.fromString(rs.getString("uuid")),
                        rs.getString("player_name"),
                        rs.getString("reason"),
                        rs.getString("warned_by"),
                        rs.getLong("timestamp")
                ));
            }
        }
        return warnings;
    }
    public static void deleteWarning(int warningId) throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(
                     "DELETE FROM " + WARNINGS_TABLE + " WHERE id = ?")) {
            stmt.setInt(1, warningId);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("No warning found with ID " + warningId);
            }
        }
    }
    public record Warning(int id, UUID uuid, String playerName, String reason, String warnedBy, long timestamp) {
    }
    public static void addBan(Player player, String reason, String bannedBy, long banExpires) throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO " + BANS_TABLE + " (uuid, player_name, reason, banned_by, timestamp, ban_expires) VALUES (?, ?, ?, ?, ?, ?)")) {
            stmt.setString(1, player.getUniqueId().toString());
            stmt.setString(2, player.getName());
            stmt.setString(3, reason);
            stmt.setString(4, bannedBy);
            stmt.setLong(5, System.currentTimeMillis());
            stmt.setLong(6, banExpires);
            stmt.executeUpdate();
        }
    }
    public static List<Ban> getPlayerBans(UUID uuid) throws SQLException {
        List<Ban> bans = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM " + BANS_TABLE + " WHERE uuid = ? ORDER BY timestamp DESC")) {
            stmt.setString(1, uuid.toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                bans.add(new Ban(
                        rs.getInt("id"),
                        UUID.fromString(rs.getString("uuid")),
                        rs.getString("player_name"),
                        rs.getString("reason"),
                        rs.getString("banned_by"),
                        rs.getLong("timestamp"),
                        rs.getLong("ban_expires")
                ));
            }
        }
        return bans;
    }
    public static void deleteBan(int banId) throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(
                     "DELETE FROM " + BANS_TABLE + " WHERE id = ?")) {
            stmt.setInt(1, banId);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("No ban found with ID " + banId);
            }
        }
    }
    public static boolean isBanned(UUID uuid) throws SQLException {
        long now = System.currentTimeMillis();
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT ban_expires FROM " + BANS_TABLE + " WHERE uuid = ? ORDER BY timestamp DESC LIMIT 1")) {
            stmt.setString(1, uuid.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                long expires = rs.getLong("ban_expires");
                return expires == 0 || expires > now;
            }
            return false;
        }
    }
    public record Ban(int id, UUID uuid, String playerName, String reason, String bannedBy, long timestamp,
                      long banExpires) {
    }
    public static void addMute(Player player, String reason, String mutedBy, long muteExpires) throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO " + MUTES_TABLE + " (uuid, player_name, reason, muted_by, timestamp, mute_expires) VALUES (?, ?, ?, ?, ?, ?)")) {
            stmt.setString(1, player.getUniqueId().toString());
            stmt.setString(2, player.getName());
            stmt.setString(3, reason);
            stmt.setString(4, mutedBy);
            stmt.setLong(5, System.currentTimeMillis());
            stmt.setLong(6, muteExpires);
            stmt.executeUpdate();
        }
    }
    public static List<Mute> getPlayerMutes(UUID uuid) throws SQLException {
        List<Mute> mutes = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM " + MUTES_TABLE + " WHERE uuid = ? ORDER BY timestamp DESC")) {
            stmt.setString(1, uuid.toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                mutes.add(new Mute(
                        rs.getInt("id"),
                        UUID.fromString(rs.getString("uuid")),
                        rs.getString("player_name"),
                        rs.getString("reason"),
                        rs.getString("muted_by"),
                        rs.getLong("timestamp"),
                        rs.getLong("mute_expires")
                ));
            }
        }
        return mutes;
    }
    public static void deleteMute(int muteId) throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(
                     "DELETE FROM " + MUTES_TABLE + " WHERE id = ?")) {
            stmt.setInt(1, muteId);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("No mute found with ID " + muteId);
            }
        }
    }
    public static boolean isMuted(UUID uuid) throws SQLException {
        long now = System.currentTimeMillis();
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT mute_expires FROM " + MUTES_TABLE + " WHERE uuid = ? ORDER BY timestamp DESC LIMIT 1")) {
            stmt.setString(1, uuid.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                long expires = rs.getLong("mute_expires");
                return expires == 0 || expires > now;
            }
            return false;
        }
    }
    public record Mute(int id, UUID uuid, String playerName, String reason, String mutedBy, long timestamp,
                       long muteExpires) {
    }
}