package org.GalacticNuclei.oxygenated.commands;

import org.GalacticNuclei.oxygenated.Msg;
import org.GalacticNuclei.oxygenated.Oxygenated;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Heal implements CommandExecutor {

    private final Oxygenated plugin = Oxygenated.getInstance();
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // /heal — self
        if (args.length == 0) {

            if (!(sender instanceof Player)) {
                Msg.send(sender, "<red>Only players may heal themselves.");
                return true;
            }

            if (!sender.hasPermission("oxygenated.heal")) {
                Msg.send(sender, "<red>You do not have permission.");
                return true;
            }

            Player player = (Player) sender;
            heal(player);

            Msg.send(player, "<green>You have been healed.");
            return true;
        }

        // /heal <player> — heal others
        if (!sender.hasPermission("oxygenated.heal.others")) {
            Msg.send(sender, "<red>You do not have permission to heal others.");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            Msg.send(sender, "<red>Player '<white>" + args[0] + "</white>' not found.");
            return true;
        }

        heal(target);

        Msg.send(sender, "<green>You healed <yellow>" + target.getName() + "</yellow>.");
        Msg.send(target, "<green>You have been healed by <yellow>" + sender.getName() + "</yellow>.");

        return true;
    }

    private void heal(Player player) {

        // Read config fresh each time (respects /reloadoxygenated)
        boolean potionEffectsCleared = plugin.getConfig().getBoolean("heal.potion-effects-cleared", true);
        boolean hungerRestored = plugin.getConfig().getBoolean("heal.hunger-restored", true);
        boolean saturationRestored = plugin.getConfig().getBoolean("heal.saturation-restored", true);
        boolean fireExtinguished = plugin.getConfig().getBoolean("heal.fire-extinguished", true);

        // Fully restore health
        double maxHealth = player.getAttribute(Attribute.MAX_HEALTH).getValue();
        player.setHealth(maxHealth);

        // Restore hunger if enabled
        if (hungerRestored) {
            player.setFoodLevel(20);
        }

        // Restore saturation if enabled
        if (saturationRestored) {
            player.setSaturation(20f);
        }

        // Extinguish fire if enabled
        if (fireExtinguished) {
            player.setFireTicks(0);
        }

        // Remove potion effects if enabled
        if (potionEffectsCleared) {
            player.getActivePotionEffects().forEach(effect ->
                    player.removePotionEffect(effect.getType()));
        }
    }

}
