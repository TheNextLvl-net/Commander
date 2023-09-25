package net.thenextlvl.commander.listener;

import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.commander.implementation.CraftCommander;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.command.UnknownCommandEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.server.ServerCommandEvent;

@RequiredArgsConstructor
public class CommandListener implements Listener {
    private final CraftCommander commander;

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCommandSend(PlayerCommandSendEvent event) {
        event.getCommands().removeIf(literal -> commander.commandRegistry().isCommandRemoved(literal));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCommand(ServerCommandEvent event) {
        var literal = event.getCommand().split(" ")[0];
        if (commander.platform().commandRegistry().isCommandRegistered(literal)
                && !commander.commandRegistry().isCommandRemoved(literal)) return;
        commander.bundle().sendMessage(event.getSender(), "command.unknown",
                Placeholder.parsed("command", literal));
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onUnknownCommand(UnknownCommandEvent event) {
        var literal = event.getCommandLine().split(" ")[0];
        if (!commander.commandRegistry().isCommandRemoved(literal)) return;
        event.message(commander.bundle().component(event.getSender(), "command.unknown",
                Placeholder.parsed("command", literal)));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        var player = event.getPlayer();
        var literal = event.getMessage().substring(1).split(" ")[0];
        var command = commander.platform().commandRegistry().getCommand(literal).orElse(null);
        if (command == null || commander.commandRegistry().isCommandRemoved(literal)) {
            event.setCancelled(true);
            if (literal.isBlank()) return;
            commander.bundle().sendMessage(player, "command.unknown",
                    Placeholder.parsed("command", literal));
        } else if (!command.testPermissionSilent(player)) {
            var permission = command.getPermission() != null ? command.getPermission() : null;
            if (permission != null) commander.bundle().sendMessage(player, "command.permission",
                    Placeholder.parsed("permission", permission));
            else commander.bundle().sendMessage(player, "command.permission.unknown");
            event.setCancelled(true);
        }
    }
}
