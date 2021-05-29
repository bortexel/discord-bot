package ru.bortexel.bot.commands.roles;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.commands.DefaultBotCommand;
import ru.bortexel.bot.core.AccessLevel;
import ru.bortexel.bot.util.CommandUtil;
import ru.bortexel.bot.util.EmbedUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class RoleCommand extends DefaultBotCommand {
    public RoleCommand(String name, BortexelBot bot) {
        super(name, bot);
    }

    @Override
    public void onCommand(Message message) {
        MessageEmbed response = manageRoles(message.getGuild(), message.getMentionedMembers());
        message.reply(response).queue();
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event, InteractionHook hook) {
        OptionMapping userOption = event.getOption("user");
        assert userOption != null;
        MessageEmbed response = manageRoles(event.getGuild(), Collections.singletonList(userOption.getAsMember()));
        hook.sendMessageEmbeds(response).queue();
    }

    private MessageEmbed manageRoles(Guild guild, List<Member> members) {
        Role role = this.getRole();
        assert role != null;

        if (members.size() == 0) {
            EmbedBuilder builder = EmbedUtil.makeError("Укажите участников", "Вам необходимо упомянуть участников, " +
                    "которым Вы хотите выдать роль " + role.getAsMention() + " или забрать её.");
            return builder.build();
        }

        List<Member> granted = new ArrayList<>();
        List<Member> revoked = new ArrayList<>();

        for (Member member : members) {
            if (member.getRoles().contains(role)) {
                guild.removeRoleFromMember(member, role).queue();
                revoked.add(member);
            } else {
                guild.addRoleToMember(member, role).queue();
                granted.add(member);
            }
        }

        List<String> revokedMentions = new ArrayList<>();
        revoked.forEach(member -> revokedMentions.add(member.getAsMention()));
        String revokedText = "Роль " + role.getAsMention() + " была отобрана у участник" +
                (revoked.size() == 1 ? "а " : "ов ") + String.join(", ", revokedMentions);

        List<String> grantedMentions = new ArrayList<>();
        granted.forEach(member -> grantedMentions.add(member.getAsMention()));
        String grantedText = "Роль " + role.getAsMention() + " была выдана участник" +
                (granted.size() == 1 ? "у " : "ам ") + String.join(", ", grantedMentions);

        EmbedBuilder builder = EmbedUtil.makeDefaultEmbed();
        builder.setTitle("Выдача ролей");
        builder.setDescription((granted.size() > 0 ? grantedText + "\n" : "") + (revoked.size() > 0 ? revokedText + "\n" : ""));

        return builder.build();
    }

    @Override
    public CommandData getSlashCommandData() {
        return CommandUtil.makeSlashCommand(this)
                .addOption(OptionType.USER, "user", "Участник", true);
    }

    @Override
    public String getUsage() {
        return "<@участник>";
    }

    @Override
    public String getUsageExample() {
        assert this.getRole() != null;
        return "`" + BortexelBot.COMMAND_PREFIX + this.getName() + " @RuscalWorld" + "` выдаст роль \"" +
                this.getRole().getName() + "\" участнику <@496297262952218638>, если у него её нет.";
    }

    @Override
    public String getDescription() {
        assert this.getRole() != null;
        return "Выдаёт роль \"" + this.getRole().getName() + "\" указанному участнику, либо забирает её, если она уже есть у него.";
    }

    @Override
    public AccessLevel getAccessLevel() {
        return null;
    }

    @Override
    public int getMinArgumentCount() {
        return 1;
    }

    protected abstract Role getRole();
}
