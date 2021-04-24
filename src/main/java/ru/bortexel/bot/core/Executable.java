package ru.bortexel.bot.core;

import net.dv8tion.jda.api.commands.CommandHook;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.requests.restaction.CommandUpdateAction;

public interface Executable {
    void onCommand(Message message);
    void onSlashCommand(SlashCommandEvent event, CommandHook hook);
    CommandUpdateAction.CommandData getSlashCommandData();
    boolean isEphemeral();
    boolean isGlobal();
}
