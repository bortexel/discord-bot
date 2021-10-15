package ru.bortexel.bot.listeners.bortexel;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import ru.bortexel.bot.BortexelBot;
import ru.bortexel.bot.resources.ExternalResource;
import ru.bortexel.bot.resources.ResourceType;
import ru.bortexel.bot.util.Channels;
import ru.bortexel.bot.util.EmbedUtil;
import ru.bortexel.bot.util.Roles;
import ru.ruscalworld.bortexel4j.listening.events.city.GenericCityEvent;
import ru.ruscalworld.bortexel4j.listening.events.shop.GenericShopEvent;
import ru.ruscalworld.bortexel4j.models.account.Account;
import ru.ruscalworld.bortexel4j.models.city.City;
import ru.ruscalworld.bortexel4j.models.shop.Shop;
import ru.ruscalworld.bortexel4j.models.user.User;

public class ProjectListener extends BotListener {
    public ProjectListener(BortexelBot bot) {
        super(bot);
    }

    @Override
    public void onShopCreated(GenericShopEvent event) {
        Shop shop = event.getShop();
        if (!shop.isActive()) return;
        BortexelBot bot = this.getBot();
        Account.getByID(shop.getOwnerID(), bot.getApiClient())
                .executeAsync(account -> User.getByID(account.getUserID(), bot.getApiClient())
                        .executeAsync(user -> {
                                    MessageEmbed embed = EmbedUtil.makeShopInfo(shop, account, user).build();
                                    TextChannel channel = bot.getJDA().getTextChannelById(Channels.SHOPS_CHANNEL);
                                    if (channel != null) channel.sendMessageEmbeds(embed)
                                            .queue(message -> ExternalResource.register(ResourceType.SHOP, shop.getID(), message, bot));
                                    else return;

                                    if (account.getDiscordID() != null)
                                        Roles.shopOwner().grant(account.getDiscordID());
                                }
                        )
                );
    }

    @Override
    public void onCityCreated(GenericCityEvent event) {
        City city = event.getCity();
        if (!city.isActive()) return;
        BortexelBot bot = this.getBot();
        Account.getByID(city.getOwnerID(), bot.getApiClient())
                .executeAsync(account -> User.getByID(account.getUserID(), bot.getApiClient())
                        .executeAsync(user -> {
                                    MessageEmbed embed = EmbedUtil.makeCityInfo(city, account, user).build();
                                    TextChannel channel = bot.getJDA().getTextChannelById(Channels.CITIES_CHANNEL);
                                    if (channel != null) channel.sendMessageEmbeds(embed)
                                            .queue(message -> ExternalResource.register(ResourceType.CITY, city.getID(), message, bot));
                                    else return;

                                    if (account.getDiscordID() != null)
                                        Roles.cityRepresentative().grant(account.getDiscordID());
                                }
                        )
                );
    }
}
