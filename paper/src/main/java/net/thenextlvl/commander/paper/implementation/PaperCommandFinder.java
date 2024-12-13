package net.thenextlvl.commander.paper.implementation;

import lombok.RequiredArgsConstructor;
import net.thenextlvl.commander.CommandFinder;
import net.thenextlvl.commander.paper.CommanderPlugin;
import org.jspecify.annotations.NullMarked;

import java.util.Set;
import java.util.regex.Pattern;

@NullMarked
@RequiredArgsConstructor
public class PaperCommandFinder implements CommandFinder {
    private final CommanderPlugin plugin;

    @Override
    public Set<String> findCommands(Pattern pattern) {
        return findCommands(plugin.getServer().getCommandMap().getKnownCommands().entrySet()
                .stream().mapMulti((entry, consumer) -> {
                    consumer.accept(entry.getKey());
                    entry.getValue().getAliases().forEach(consumer);
                }), pattern);
    }
}
