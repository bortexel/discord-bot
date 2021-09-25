package ru.bortexel.bot.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import ru.bortexel.bot.core.AccessLevel;
import ru.bortexel.bot.core.Command;
import ru.bortexel.bot.util.Channels;

import java.util.ArrayList;
import java.util.List;

public abstract class DefaultCommand implements Command {
    private final String name;
    private final List<String> aliases = new ArrayList<>();
    private final List<String> slashAliases = new ArrayList<>();
    private boolean ephemeral = false;
    private boolean global = false;
    private boolean legacy = true;

    protected DefaultCommand(String name) {
        this.name = name;
    }

    @Override
    public final String getName() {
        return this.name;
    }

    @Override
    public String getUsage() {
        return null;
    }

    @Override
    public String getUsageExample() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public final String[] getAliases() {
        return this.aliases.toArray(new String[aliases.size()]);
    }

    @Override
    public String[] getSlashAliases() {
        return this.slashAliases.toArray(new String[slashAliases.size()]);
    }

    public final void addAlias(String alias, boolean slash) {
        this.aliases.add(alias);
        if (slash) this.slashAliases.add(alias);
    }

    public final void addAlias(String alias) {
        this.addAlias(alias, false);
    }

    @Override
    public String[] getAllowedChannelIds() {
        return new String[] { Channels.BOTS_CHANNEL };
    }

    @Override
    public AccessLevel getAccessLevel() {
        return null;
    }

    @Override
    public int getMinArgumentCount() {
        return 0;
    }

    @Override
    public CommandData getSlashCommandData() {
        return null;
    }

    @Override
    public final boolean isEphemeral() {
        return ephemeral;
    }

    public void setEphemeral(boolean ephemeral) {
        this.ephemeral = ephemeral;
    }

    @Override
    public boolean isGlobal() {
        return global;
    }

    public void setGlobal(boolean global) {
        this.global = global;
    }

    public void setLegacySupported(boolean legacy) {
        this.legacy = legacy;
    }

    @Override
    public boolean isLegacySupported() {
        return this.legacy;
    }
}
