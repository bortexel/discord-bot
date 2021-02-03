package ru.bortexel.bot.commands.roles;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.core.AccessLevel;
import ru.bortexel.bot.core.Command;
import ru.bortexel.bot.util.EmbedUtil;

import java.util.ArrayList;
import java.util.List;

public abstract class RoleCommand implements Command {
    private final String name;

    public RoleCommand(String name) {
        this.name = name;
    }

    @Override
    public void onCommand(Message message) {
        Role role = this.getRole();
        assert role != null;

        List<Member> members = message.getMentionedMembers();
        Guild guild = message.getGuild();

        if (members.size() == 0) {
            EmbedBuilder builder = EmbedUtil.makeError("Укажите участников", "Вам необходимо упомянуть участников, " +
                    "которым Вы хотите выдать роль " + role.getAsMention() + " или забрать её.");
            message.getChannel().sendMessage(builder.build()).queue();
            return;
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

        message.getChannel().sendMessage(builder.build()).queue();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getUsage() {
        return "<@участник>";
    }

    @Override
    public String getUsageExample() {
        assert this.getRole() != null;
        return "`" + BortexelBot.COMMAND_PREFIX + this.getName() + " @RuscalWorld" + "` выдаст роль " +
                this.getRole().getAsMention() + " участнику <@496297262952218638>, если у него её нет.";
    }

    @Override
    public String getDescription() {
        assert this.getRole() != null;
        return "Выдаёт роль " + this.getRole().getAsMention() + " указанному участнику, либо забирает её, если она уже есть у него.";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
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
