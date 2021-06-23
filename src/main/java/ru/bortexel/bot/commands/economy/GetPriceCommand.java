package ru.bortexel.bot.commands.economy;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.commands.DefaultBotCommand;
import ru.bortexel.bot.core.AccessLevel;
import ru.bortexel.bot.util.Channels;
import ru.bortexel.bot.util.CommandUtil;
import ru.bortexel.bot.util.EmbedUtil;
import ru.bortexel.bot.util.TextUtil;
import ru.ruscalworld.bortexel4j.exceptions.NotFoundException;
import ru.ruscalworld.bortexel4j.models.economy.Item;

import java.util.function.Consumer;

public class GetPriceCommand extends DefaultBotCommand {
    protected GetPriceCommand(BortexelBot bot) {
        super("price", bot);
        this.addAlias("стоимость");
    }

    @Override
    public void onCommand(Message message) {
        String[] args = TextUtil.getCommandArgs(message);
        getPrice(args[1], response -> message.reply(response).queue());
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event, InteractionHook hook) {
        OptionMapping itemOption = event.getOption("item");
        assert itemOption != null;
        getPrice(itemOption.getAsString(), response -> hook.sendMessageEmbeds(response).queue());
    }

    private void getPrice(String itemName, Consumer<MessageEmbed> callback) {
        Item item;

        try {
            item = Item.getByID(itemName, this.getBot().getApiClient()).execute();
        } catch (NotFoundException e) {
            MessageEmbed messageEmbed = EmbedUtil.makeError("Предмет не найден", "Указанный предмет не найден в базе данных. " +
                    "Проверьте правильность написания названия и повторите попытку.").build();
            callback.accept(messageEmbed);
            return;
        }

        item.getPrices(this.getBot().getApiClient()).executeAsync(prices -> {
            if (prices.getPrices() == null) {
                MessageEmbed messageEmbed = EmbedUtil.makeError("Стоимость не установлена", "Указанный предмет есть в нашей базе данных, " +
                        "однако стоимость на него не была установлена.").build();
                callback.accept(messageEmbed);
                return;
            }

            EmbedBuilder builder = EmbedUtil.makeItemPriceInfo(prices);
            callback.accept(builder.build());
        });
    }

    @Override
    public CommandData getSlashCommandData() {
        return CommandUtil.makeSlashCommand(this)
                .addOption(OptionType.STRING, "item", "Идентификатор предмета", true);
    }

    @Override
    public String getUsage() {
        return "<предмет>";
    }

    @Override
    public String getUsageExample() {
        return "`$price trident` выводит среднюю стоимость трезубца";
    }

    @Override
    public String getDescription() {
        return "Получает среднюю стоимость указанного предмета";
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
        return 1;
    }
}
