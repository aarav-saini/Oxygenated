package org.GalacticNuclei.oxygenated;

import org.GalacticNuclei.oxygenated.commands.*;
import org.GalacticNuclei.oxygenated.commands.moderation.Ban;
import org.GalacticNuclei.oxygenated.commands.moderation.ModLogs;
import org.GalacticNuclei.oxygenated.commands.moderation.Mute;
import org.GalacticNuclei.oxygenated.commands.moderation.Warn;
import org.GalacticNuclei.oxygenated.commands.time.*;
import org.GalacticNuclei.oxygenated.database.SQL;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class Oxygenated extends JavaPlugin {
    private static Oxygenated instance;
    @Override
    public void onEnable() {
        saveDefaultConfig();
        instance = this;
        SQL.initDatabase(this);
        Objects.requireNonNull(getCommand("broadcast")).setExecutor(new Broadcast());
        Objects.requireNonNull(getCommand("heal")).setExecutor(new Heal());
        Objects.requireNonNull(getCommand("day")).setExecutor(new Day());
        Objects.requireNonNull(getCommand("night")).setExecutor(new Night());
        Objects.requireNonNull(getCommand("noon")).setExecutor(new Noon());
        Objects.requireNonNull(getCommand("sunset")).setExecutor(new Sunset());
        Objects.requireNonNull(getCommand("sunrise")).setExecutor(new Sunrise());
        Objects.requireNonNull(getCommand("socialspy")).setExecutor(new SocialSpy());
        Objects.requireNonNull(getCommand("reloadoxygenated")).setExecutor(new Reload());
        Objects.requireNonNull(getCommand("warn")).setExecutor(new Warn());
        Objects.requireNonNull(getCommand("ban")).setExecutor(new Ban());
        Objects.requireNonNull(getCommand("mute")).setExecutor(new Mute());
        Objects.requireNonNull(getCommand("modlogs")).setExecutor(new ModLogs());
        Objects.requireNonNull(getCommand("clearchat")).setExecutor(new ClearChat());
        Objects.requireNonNull(getCommand("invsee")).setExecutor(new Invsee());
        getServer().getPluginManager().registerEvents(new org.GalacticNuclei.oxygenated.Listeners.SocialSpyListener(), this);
        getServer().getPluginManager().registerEvents(new org.GalacticNuclei.oxygenated.Listeners.MuteListener(), this);
        getServer().getPluginManager().registerEvents(new org.GalacticNuclei.oxygenated.Listeners.BanListener(), this);
        getLogger().info("Oxygenated enabled successfully!");
    }
    @Override
    public void onDisable() {
        instance = null;
        getLogger().info("Oxygenated disabled.");
    }
    public static Oxygenated getInstance() {
        return instance;
    }
}