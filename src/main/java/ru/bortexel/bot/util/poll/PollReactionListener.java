package ru.bortexel.bot.util.poll;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import static ru.bortexel.bot.BortexelBot.handleException;

public class PollReactionListener extends ListenerAdapter {
    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        try {
            MessageReaction.ReactionEmote reactionEmote = event.getReaction().getReactionEmote();
            // Кастомные эмодзи не поддерживаются
            if (reactionEmote.isEmote()) return;

            Message message = event.getTextChannel().retrieveMessageById(event.getMessageId()).complete();
            SelfUser selfUser = event.getJDA().getSelfUser();
            if (message == null) return;
            // Проверяем, что сообщение было отправлено текущим пользователем
            if (!message.getAuthor().getId().equals(selfUser.getId())) return;

            // Пробуем получить голосование из сообщения
            Poll poll = Poll.getFromMessage(message);
            if (poll == null) return;

            if (!poll.isMultipleChoiceAllowed()) {
                // Проверяем, что участник не проголосовал дважды, если это запрещено
                for (MessageReaction reaction : message.getReactions()) {
                    MessageReaction.ReactionEmote currentReactionEmote = reaction.getReactionEmote();
                    if (currentReactionEmote.isEmote()) continue;
                    // Реакцию, добавленную нашим ботом удалять не следует
                    if (selfUser.getId().equals(event.getUserId())) continue;
                    // Мы удалим все остальные реакции этого пользователя, а эту оставим
                    if (reactionEmote.getEmoji().equals(currentReactionEmote.getEmoji())) continue;
                    // Удаляем эту реакцию
                    reaction.retrieveUsers().forEach(user -> {
                        if (user.getId().equals(event.getUserId())) reaction.removeReaction(user).queue();
                    });
                }
            }

            // Наконец отрисовываем эмбед с голосованием заново
            poll.rerender(message);
        } catch (InsufficientPermissionException ignored) { } catch (Exception e) {
            handleException(e);
        }
    }

    @Override
    public void onGuildMessageReactionRemove(@NotNull GuildMessageReactionRemoveEvent event) {
        try {
            if (event.getReaction().getReactionEmote().isEmote()) return;
            Message message = event.getChannel().retrieveMessageById(event.getMessageId()).complete();
            if (message == null) return;
            Poll poll = Poll.getFromMessage(message);
            if (poll != null) poll.rerender(message);
        } catch (InsufficientPermissionException ignored) { } catch (Exception e) {
            handleException(e);
        }
    }
}
