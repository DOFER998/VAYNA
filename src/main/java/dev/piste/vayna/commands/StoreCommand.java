package dev.piste.vayna.commands;

import dev.piste.vayna.Bot;
import dev.piste.vayna.apis.StatusCodeException;
import dev.piste.vayna.apis.henrik.HenrikAPI;
import dev.piste.vayna.apis.valorantapi.ValorantAPI;
import dev.piste.vayna.apis.valorantapi.gson.Buddy;
import dev.piste.vayna.apis.valorantapi.gson.Bundle;
import dev.piste.vayna.apis.valorantapi.gson.Playercard;
import dev.piste.vayna.apis.valorantapi.gson.Spray;
import dev.piste.vayna.manager.Command;
import dev.piste.vayna.apis.henrik.gson.CurrentBundle;
import dev.piste.vayna.apis.henrik.gson.store.Item;
import dev.piste.vayna.config.Configs;
import dev.piste.vayna.config.settings.SettingsConfig;
import dev.piste.vayna.util.Embed;
import dev.piste.vayna.util.Emoji;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class StoreCommand implements Command {


    @Override
    public void perform(SlashCommandInteractionEvent event) throws StatusCodeException {
        event.deferReply().queue();

        SettingsConfig settingsConfig = Configs.getSettings();

        List<MessageEmbed> embedList = new ArrayList<>();

        for(CurrentBundle currentBundle : HenrikAPI.getCurrentBundles()) {
            Bundle bundle = ValorantAPI.getBundle(currentBundle.getBundleUuid());
            Embed bundleEmbed = new Embed()
                    .setAuthor(event.getUser().getName(), settingsConfig.getWebsiteUri(), event.getUser().getAvatarUrl())
                    .setTitle("» " + bundle.getDisplayName())
                    .addField("Price", currentBundle.getPrice() + " " + Emoji.getVP().getFormatted(), true)
                    .addField("Duration", "This bundle will be removed from the store <t:" + (Instant.now().getEpochSecond()+currentBundle.getSecondsRemaining()) + ":R>", true)
                    .setImage(bundle.getDisplayIcon());
            embedList.add(bundleEmbed.build());

            for(Item item : currentBundle.getItems()) {
                if(item.getType().equalsIgnoreCase("buddy")) {
                    Buddy buddy = ValorantAPI.getBuddy(item.getUuid());
                    Embed itemEmbed = new Embed()
                            .removeFooter()
                            .setTitle("» " + buddy.getDisplayName())
                            .addField("Amount", item.getAmount() + "x", true)
                            .addField("Price", item.getBasePrice() + " " + Emoji.getVP().getFormatted(), true)
                            .setThumbnail(buddy.getDisplayIcon());
                    embedList.add(itemEmbed.build());
                } else if(item.getType().equalsIgnoreCase("player_card")) {
                    Playercard playercard = ValorantAPI.getPlayercard(item.getUuid());
                    Embed itemEmbed = new Embed()
                            .removeFooter()
                            .setTitle("» " + playercard.getDisplayName())
                            .addField("Amount", item.getAmount() + "x", true)
                            .addField("Price", item.getBasePrice() + " " + Emoji.getVP().getFormatted(), true)
                            .setThumbnail(playercard.getLargeArt())
                            .setImage(playercard.getWideArt());
                    embedList.add(itemEmbed.build());
                } else if(item.getType().equalsIgnoreCase("spray")) {
                    Spray spray = ValorantAPI.getSpray(item.getUuid());
                    Embed itemEmbed = new Embed()
                            .removeFooter()
                            .setTitle("» " + spray.getDisplayName())
                            .addField("Amount", item.getAmount() + "x", true)
                            .addField("Price", item.getBasePrice() + " " + Emoji.getVP().getFormatted(), true);
                    if(spray.getAnimationGif() != null) {
                        itemEmbed.setThumbnail(spray.getAnimationGif());
                    } else {
                        itemEmbed.setThumbnail(spray.getFullTransparentIcon());
                    }
                    embedList.add(itemEmbed.build());
                } else {
                    Embed itemEmbed = new Embed()
                            .removeFooter()
                            .setTitle("» " + item.getName())
                            .addField("Amount", item.getAmount() + "x", true)
                            .addField("Price", item.getBasePrice() + " " + Emoji.getVP().getFormatted(), true)
                            .setImage(item.getImage());
                    embedList.add(itemEmbed.build());
                }

            }

        }

        event.getHook().editOriginalEmbeds(embedList).queue();

    }

    @Override
    public void register() {
        Bot.getJDA().upsertCommand(getName(), getDescription()).queue();
    }

    @Override
    public String getName() {
        return "store";
    }

    @Override
    public String getDescription() {
        return "Get information about the current bundle in the VALORANT store";
    }
}
