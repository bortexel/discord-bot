package ru.bortexel.bot.core;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public interface Executable {
    default void onCommand(Message message) {}
    default void onButtonClick(ButtonClickEvent event, String[] args) {}
    default void onSlashCommand(SlashCommandEvent event, InteractionHook hook) {}
    CommandData getSlashCommandData();
    boolean isEphemeral();
    boolean isGlobal();
}
