package ru.bortexel.bot.commands.economy;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.commands.CommandHook;
import net.dv8tion.jda.api.entities.Command;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.requests.restaction.CommandUpdateAction;
import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.commands.DefaultBotCommand;
import ru.bortexel.bot.core.AccessLevel;
import ru.bortexel.bot.util.*;
import ru.ruscalworld.bortexel4j.core.Callback;
import ru.ruscalworld.bortexel4j.exceptions.NotFoundException;
import ru.ruscalworld.bortexel4j.models.economy.Item;

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
    public void onSlashCommand(SlashCommandEvent event, CommandHook hook) {
        SlashCommandEvent.OptionData itemOption = event.getOption("item");
        assert itemOption != null;
        getPrice(itemOption.getAsString(), response -> hook.sendMessage(response).queue());
    }

    private void getPrice(String itemName, Callback<MessageEmbed> callback) {
        Item item;

        try {
            item = Item.getByID(itemName, this.getBot().getApiClient()).execute();
        } catch (NotFoundException e) {
            MessageEmbed messageEmbed = EmbedUtil.makeError("Предмет не найден", "Указанный предмет не найден в базе данных. " +
                    "Проверьте правильность написания названия и повторите попытку.").build();
            callback.handle(messageEmbed);
            return;
        }

        item.getPrices(this.getBot().getApiClient()).executeAsync(prices -> {
            if (prices.getPrices() == null) {
                MessageEmbed messageEmbed = EmbedUtil.makeError("Стоимость не установлена", "Указанный предмет есть в нашей базе данных, " +
                        "однако стоимость на него не была установлена.").build();
                callback.handle(messageEmbed);
                return;
            }

            EmbedBuilder builder = EmbedUtil.makeItemPriceInfo(prices);
            callback.handle(builder.build());
        });
    }

    @Override
    public CommandUpdateAction.CommandData getSlashCommandData() {
        return CommandUtil.makeSlashCommand(this)
                .addOption(new CommandUpdateAction.OptionData(Command.OptionType.STRING, "item", "Идентификатор предмета")
                        .setRequired(true));
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
