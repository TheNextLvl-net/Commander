package net.thenextlvl.commander.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.commander.CommanderPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
@SuppressWarnings("UnstableApiUsage")
class RevealCommand {
    private final CommanderPlugin plugin;

    public ArgumentBuilder<CommandSourceStack, ?> create() {
        return Commands.literal("reveal")
                .then(Commands.argument("command", StringArgumentType.string())
                        .suggests((context, suggestions) -> {
                            plugin.commandRegistry().hiddenCommands().stream()
                                    .map(StringArgumentType::escapeIfRequired)
                                    .filter(s -> s.contains(suggestions.getRemaining()))
                                    .forEach(suggestions::suggest);
                            return suggestions.buildFuture();
                        })
                        .executes(this::reveal));
    }

    private int reveal(CommandContext<CommandSourceStack> context) {
        var sender = context.getSource().getSender();
        var command = context.getArgument("command", String.class);
        var success = plugin.commandRegistry().reveal(command);
        var message = success ? "command.revealed" : "nothing.changed";
        plugin.bundle().sendMessage(sender, message, Placeholder.parsed("command", command));
        if (success) Bukkit.getOnlinePlayers().forEach(Player::updateCommands);
        return Command.SINGLE_SUCCESS;
    }
}