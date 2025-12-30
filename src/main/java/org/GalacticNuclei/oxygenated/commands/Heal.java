package org.GalacticNuclei.oxygenated.commands;
import org.GalacticNuclei.oxygenated.Msg;
import org.GalacticNuclei.oxygenated.Oxygenated;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.Objects;
public class Heal implements CommandExecutor {
    private final Oxygenated plugin = Oxygenated.getInstance();
    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            String[] args
    ) {
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                Msg.send(sender, msg("self-only-player"));
                return true;
            }
            if (!sender.hasPermission("oxygenated.heal")) {
                Msg.send(sender, msg("no-permission-self"));
                return true;
            }
            heal(player);
            Msg.send(player, msg("self-healed"));
            return true;
        }
        if (!sender.hasPermission("oxygenated.heal.others")) {
            Msg.send(sender, msg("no-permission-others"));
            return true;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            Msg.send(sender,
                    msg("player-not-found")
                            .replace("{player}", args[0]));
            return true;
        }
        heal(target);
        Msg.send(sender,
                msg("healed-other")
                        .replace("{player}", target.getName()));
        Msg.send(target,
                msg("healed-by-other")
                        .replace("{sender}", sender.getName()));
        return true;
    }
    private void heal(Player player) {
        boolean potionEffectsCleared =
                plugin.getConfig().getBoolean("heal.potion-effects-cleared", true);
        boolean hungerRestored =
                plugin.getConfig().getBoolean("heal.hunger-restored", true);
        boolean saturationRestored =
                plugin.getConfig().getBoolean("heal.saturation-restored", true);
        boolean fireExtinguished =
                plugin.getConfig().getBoolean("heal.fire-extinguished", true);
        double maxHealth = Objects.requireNonNull(
                player.getAttribute(Attribute.MAX_HEALTH)).getValue();
        player.setHealth(maxHealth);
        if (hungerRestored) {
            player.setFoodLevel(20);
        }
        if (saturationRestored) {
            player.setSaturation(20f);
        }
        if (fireExtinguished) {
            player.setFireTicks(0);
        }
        if (potionEffectsCleared) {
            player.getActivePotionEffects().forEach(effect ->
                    player.removePotionEffect(effect.getType()));
        }
    }
    private String msg(String key) {
        return plugin.getConfig().getString(
                "heal.messages." + key,
                "<red>Message not configured."
        );
    }
}