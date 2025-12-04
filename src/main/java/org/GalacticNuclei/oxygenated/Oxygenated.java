package org.GalacticNuclei.oxygenated;

import org.GalacticNuclei.oxygenated.commands.*;
import org.GalacticNuclei.oxygenated.commands.time.Day;
import org.GalacticNuclei.oxygenated.commands.time.Night;
import org.GalacticNuclei.oxygenated.commands.time.Noon;
import org.GalacticNuclei.oxygenated.commands.time.Sunrise;
import org.GalacticNuclei.oxygenated.commands.time.Sunset;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class Oxygenated extends JavaPlugin {

    private static Oxygenated instance;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        instance = this;
//        COMMANDS
        Objects.requireNonNull(getCommand("broadcast")).setExecutor(new Broadcast());
        Objects.requireNonNull(getCommand("heal")).setExecutor(new Heal());
        Objects.requireNonNull(getCommand("day")).setExecutor(new Day());
        Objects.requireNonNull(getCommand("night")).setExecutor(new Night());
        Objects.requireNonNull(getCommand("noon")).setExecutor(new Noon());
        Objects.requireNonNull(getCommand("sunset")).setExecutor(new Sunset());
        Objects.requireNonNull(getCommand("sunrise")).setExecutor(new Sunrise());
        Objects.requireNonNull(getCommand("socialspy")).setExecutor(new SocialSpy());
        Objects.requireNonNull(getCommand("reloadoxygenated")).setExecutor(new Reload());
        Objects.requireNonNull(getCommand("clearchat")).setExecutor(new ClearChat());
        Objects.requireNonNull(getCommand("invsee")).setExecutor(new Invsee());

//        LISTENERS
        getServer().getPluginManager().registerEvents(new org.GalacticNuclei.oxygenated.Listeners.SocialSpyListener(), this);


    }

    @Override
    public void onDisable() {
        instance = null;
        // Plugin shutdown logic
    }

    public static Oxygenated getInstance() {
        return instance;
    }
}
