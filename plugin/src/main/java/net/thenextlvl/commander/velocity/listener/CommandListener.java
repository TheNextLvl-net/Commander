package net.thenextlvl.commander.velocity.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.event.command.PlayerAvailableCommandsEvent;
import lombok.RequiredArgsConstructor;
import net.thenextlvl.commander.velocity.implementation.ProxyCommander;

@RequiredArgsConstructor
public class CommandListener {
    private final ProxyCommander commander;

    @Subscribe
    @SuppressWarnings("UnstableApiUsage")
    public void onCommandSend(PlayerAvailableCommandsEvent event) {
        event.getRootNode().getChildren().removeIf(commandNode ->
                commander.commandRegistry().hasStatus(commandNode.getName()));
    }

    @Subscribe
    public void onCommand(CommandExecuteEvent event) {
        var literal = event.getCommand().split(" ")[0];
        if (!commander.commandRegistry().isRemoved(literal)) return;
        event.setResult(CommandExecuteEvent.CommandResult.forwardToServer());
    }
}