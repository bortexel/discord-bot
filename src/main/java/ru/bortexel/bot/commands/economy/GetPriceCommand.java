package ru.bortexel.bot.commands.economy;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.core.AccessLevel;
import ru.bortexel.bot.core.Command;
import ru.bortexel.bot.core.Executable;
import ru.bortexel.bot.util.*;
import ru.ruscalworld.bortexel4j.economy.ItemPrices;

import java.sql.Timestamp;
import java.util.LinkedHashMap;
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

            String item = args[1];
            ItemPrices prices;

            try {
                prices = ItemPrices.getPrices(item);
            } catch (Exception e) {
                if (e.getMessage().contains("wasn't found in database")) {
                    MessageEmbed messageEmbed = EmbedUtil.makeError("Предмет не найден", "Указанный предмет не найден в базе данных. " +
                            "Проверьте правильность написания названия и повторите попытку.").build();
                    channel.sendMessage(messageEmbed).queue();
                } else BortexelBot.handleException(e);
                return;
            }

            List<LinkedHashMap<String, Object>> itemPrices = prices.prices;
            if (prices.prices.size() == 0) {
                MessageEmbed messageEmbed = EmbedUtil.makeError("Стоимость не установлена", "Указанный предмет есть в нашей базе данных, " +
                        "однако стоимость на него не была установлена.").build();
                channel.sendMessage(messageEmbed).queue();
                return;
            }

            LinkedHashMap<String, Object> priceMap = itemPrices.get(itemPrices.size() - 1);
            double price = priceMap.get("price") instanceof Double ? (double) priceMap.get("price") : (int) priceMap.get("price");
            Timestamp time = new Timestamp((int) priceMap.get("time") * 1000L);

            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(BortexelBot.EMBED_COLOR);
            builder.setTitle("Средняя цена на " + PriceUtil.formatName(prices.item.name, prices.item.category));
            builder.setThumbnail(BortexelCDN.getItemIconUrl(prices.item.id));
            builder.addField("За 1", PriceUtil.formatPrice(price) + " " + Emojis.GOLD_ORE, true);
            builder.addField("За 32", PriceUtil.formatPrice(price * 32) + " " + Emojis.GOLD_ORE, true);
            builder.addField("За 64", PriceUtil.formatPrice(price * 64) + " " + Emojis.GOLD_ORE, true);
            builder.setTimestamp(time.toLocalDateTime());
            builder.setFooter("Последнее обновление");
            MessageEmbed messageEmbed = builder.build();
            channel.sendMessage(messageEmbed).queue();
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
