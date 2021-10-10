package ru.bortexel.bot.core;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.Component;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class InteractionContext {
    private final CompletableFuture<Message> supplier;
    private final Timestamp registeredAt = new Timestamp(System.currentTimeMillis());

    public InteractionContext(CompletableFuture<Message> supplier) {
        this.supplier = supplier;
    }

    public void onButtonClick(ButtonClickEvent event) {
        if (event.getButton() == null || event.getButton().getId() == null) return;

        this.getSupplier().thenAccept(message -> {
            // Send message from CompletableFuture and update original message by adding link to the new one
            event.getHook().sendMessage(message).queue(reply -> {
                Message oldMessage = event.getMessage();
                List<ActionRow> rows = new ArrayList<>();

                for (ActionRow row : oldMessage.getActionRows()) {
                    List<Component> newComponents = new ArrayList<>();

                    for (Component component : row.getComponents()) {
                        if (component instanceof Button) {
                            Component newComponent;
                            if (component.getId() == null || !component.getId().equals(event.getButton().getId()))
                                newComponent = component;
                            else newComponent = ((Button) component).withUrl(reply.getJumpUrl());
                            newComponents.add(newComponent);
                        } else newComponents.add(component);
                    }

                    rows.add(ActionRow.of(newComponents));
                }

                oldMessage.editMessageComponents(rows).queue();
            });
        });
    }

    public boolean isActual() {
        long toDeleteAt = this.getRegisteredAt().getTime() + 3600 * 1000;
        return toDeleteAt > System.currentTimeMillis();
    }

    public CompletableFuture<Message> getSupplier() {
        return supplier;
    }

    public Timestamp getRegisteredAt() {
        return registeredAt;
    }
}
