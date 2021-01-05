package ru.bortexel.bot.commands.economy;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.core.AccessLevel;
import ru.bortexel.bot.core.Command;
import ru.bortexel.bot.util.*;
import ru.ruscalworld.bortexel4j.exceptions.NotFoundException;
import ru.ruscalworld.bortexel4j.models.economy.Item;

import java.util.List;

public class GetPriceCommand implements Command {
    private final BortexelBot bot;

    public GetPriceCommand(BortexelBot bot) {
        this.bot = bot;
    }

    @Override
    public void onCommand(Message message) {
        try {
            String[] args = TextUtil.getCommandArgs(message);
            MessageChannel channel = message.getChannel();

            String id = args[1];
            Item item;

            try {
                item = Item.getByID(id, bot.getApiClient()).execute();
            } catch (NotFoundException e) {
                MessageEmbed messageEmbed = EmbedUtil.makeError("Предмет не найден", "Указанный предмет не найден в базе данных. " +
                        "Проверьте правильность написания названия и повторите попытку.").build();
                channel.sendMessage(messageEmbed).queue();
                return;
            }

            item.getPrices(bot.getApiClient()).executeAsync(prices -> {
                if (prices.getPrices() == null) {
                    MessageEmbed messageEmbed = EmbedUtil.makeError("Стоимость не установлена", "Указанный предмет есть в нашей базе данных, " +
                            "однако стоимость на него не была установлена.").build();
                    channel.sendMessage(messageEmbed).queue();
                    return;
                }

                EmbedBuilder builder = EmbedUtil.makeItemPriceInfo(prices);
                channel.sendMessage(builder.build()).queue();
            });
        } catch (Exception e) {
            BortexelBot.handleException(e);
        }
    }

    @Override
    public String getName() {
        return "price";
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
    public String[] getAliases() {
        return new String[] { "стоимость" };
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
