package org.GalacticNuclei.oxygenated.commands.utility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.GalacticNuclei.oxygenated.Oxygenated;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.List;
public class Rules implements CommandExecutor {
    private final Oxygenated plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();
    public Rules(Oxygenated plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("oxygenated.rules")) {
            sender.sendMessage("No permission.");
            return true;
        }
        List<String> rules = plugin.getConfig().getStringList("rules.lines");
        int perPage = plugin.getConfig().getInt("rules.per-page", 6);
        int page = 1;
        if (args.length >= 1) {
            try { page = Integer.parseInt(args[0]); } catch (NumberFormatException ignored) {}
        }
        if (page < 1) page = 1;
        int maxPage = Math.max(1, (int) Math.ceil(rules.size() / (double) perPage));
        if (page > maxPage) page = maxPage;
        send(sender, Component.text("Rules (" + page + "/" + maxPage + ")", NamedTextColor.GOLD));
        int start = (page - 1) * perPage;
        int end = Math.min(start + perPage, rules.size());
        for (int i = start; i < end; i++) {
            send(sender, mm.deserialize(rules.get(i)));
        }
        Component prev = (page > 1)
                ? Component.text("« Prev", NamedTextColor.AQUA).clickEvent(ClickEvent.runCommand("/rules " + (page - 1)))
                : Component.text("« Prev", NamedTextColor.DARK_GRAY);
        Component next = (page < maxPage)
                ? Component.text("Next »", NamedTextColor.AQUA).clickEvent(ClickEvent.runCommand("/rules " + (page + 1)))
                : Component.text("Next »", NamedTextColor.DARK_GRAY);
        send(sender, prev.append(Component.text("  |  ", NamedTextColor.GRAY)).append(next));
        return true;
    }
    private void send(CommandSender sender, Component component) {
        if (sender instanceof Player p) {
            p.sendMessage(component);
        } else {
            sender.sendMessage(component.toString());
        }
    }
}