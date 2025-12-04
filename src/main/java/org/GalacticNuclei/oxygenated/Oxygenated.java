package org.GalacticNuclei.oxygenated;

import org.GalacticNuclei.oxygenated.commands.*;
import org.GalacticNuclei.oxygenated.commands.time.Day;
import org.GalacticNuclei.oxygenated.commands.time.Night;
import org.GalacticNuclei.oxygenated.commands.time.Noon;
import org.GalacticNuclei.oxygenated.commands.time.Sunrise;
import org.GalacticNuclei.oxygenated.commands.time.Sunset;
import org.bukkit.plugin.java.JavaPlugin;
public final class Oxygenated extends JavaPlugin {

    private static Oxygenated instance;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        instance = this;
//        COMMANDS
        getCommand("broadcast").setExecutor(new Broadcast());
        getCommand("heal").setExecutor(new Heal());
        getCommand("day").setExecutor(new Day());
        getCommand("night").setExecutor(new Night());
        getCommand("noon").setExecutor(new Noon());
        getCommand("sunset").setExecutor(new Sunset());
        getCommand("sunrise").setExecutor(new Sunrise());
        getCommand("socialspy").setExecutor(new SocialSpy());
        getCommand("reloadoxygenated").setExecutor(new Reload());

//        LISTENERS
        getServer().getPluginManager().registerEvents(new org.GalacticNuclei.oxygenated.listeners.SocialSpyListener(), this);


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
