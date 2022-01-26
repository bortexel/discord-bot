package ru.bortexel.bot.listeners.bortexel;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.resources.ExternalResource;
import ru.bortexel.bot.resources.ResourceType;
import ru.bortexel.bot.util.Channels;
import ru.bortexel.bot.util.EmbedUtil;
import ru.bortexel.bot.util.Roles;
import ru.ruscalworld.bortexel4j.listening.events.forms.GenericRequestEvent;
import ru.ruscalworld.bortexel4j.models.account.Account;
import ru.ruscalworld.bortexel4j.models.forms.WhitelistForm;

import java.util.Optional;

public class WhitelistFormListener extends BotListener {
    public WhitelistFormListener(BortexelBot bot) {
        super(bot);
    }

    @Override
    public void onWhitelistFormSubmitted(GenericRequestEvent event) {
        WhitelistForm request = event.getRequest();
        Account.getByID(request.getAccountID(), this.getBot().getApiClient()).executeAsync(account -> {
            TextChannel channel = this.getBot().getJDA().getTextChannelById(Channels.WHITELIST_FORMS_CHANNEL);
            if (channel == null) return;

            Role moderator = Roles.moderator().getRole();
            assert moderator != null;
            channel.sendMessage(moderator.getAsMention())
                    .setEmbeds(EmbedUtil.makeWhitelistFormInfo(request, account).build())
                    .queue(message ->
                            ExternalResource.register(ResourceType.WHITELIST_FORM, request.getID(), message, this.getBot())
                    );
        }, BortexelBot::handleException);
    }

    @Override
    public void onWhitelistFormReviewed(GenericRequestEvent event) {
        WhitelistForm request = event.getRequest();
        Optional<ExternalResource> resource = ExternalResource.getByExternalID(request.getID(), ResourceType.WHITELIST_FORM, this.getBot());
        if (resource.isEmpty()) return;

        Account.getByID(request.getAccountID(), this.getBot().getApiClient()).executeAsync(account ->
                resource.get().retrieveMessage().queue(message ->
                        message.editMessageEmbeds(EmbedUtil.makeWhitelistFormInfo(request, account).build()).queue()
                ), BortexelBot::handleException
        );
    }
}
