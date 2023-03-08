package dev.piste.vayna.interactions.commands.slash;

import dev.piste.vayna.apis.HttpErrorException;
import dev.piste.vayna.apis.RiotGamesAPI;
import dev.piste.vayna.apis.entities.riotgames.PlatformData;
import dev.piste.vayna.translations.Language;
import dev.piste.vayna.translations.LanguageManager;
import dev.piste.vayna.util.Embed;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Piste | https://github.com/PisteDev
 */
public class StatusSlashCommand implements ISlashCommand {

    @Override
    public void perform(SlashCommandInteractionEvent event, Language language) throws HttpErrorException, IOException, InterruptedException {
        event.deferReply(true).queue();

        PlatformData platformData = new RiotGamesAPI().getPlatformData(event.getOption("region").getAsString());

        List<MessageEmbed> embedList = new ArrayList<>();

        for(PlatformData.Status status : platformData.getMaintenances()) {
            embedList.add(getStatusEmbed(status, language));
        }
        for(PlatformData.Status status : platformData.getIncidents()) {
            embedList.add(getStatusEmbed(status, language));
        }

        if(embedList.isEmpty()) {
            Embed embed = new Embed()
                    .setColor(0, 255, 0)
                    .setTitle(language.getEmbedTitlePrefix() + language.getTranslation("command-status-good-embed-title"))
                    .setDescription(language.getTranslation("command-status-good-embed-description"));
            embedList.add(embed.build());
        }

        event.getHook().editOriginalEmbeds(embedList).queue();
    }

    private MessageEmbed getStatusEmbed(PlatformData.Status status, Language language) {
        String riotLanguageCode = language.getLanguageCode().replaceAll("-", "_");

        String title = null;
        for(PlatformData.Translation translation : status.getTitles()) {
            if(translation.getLocale().equals(riotLanguageCode)) {
                title = translation.getContent();
            }
        }
        if(title == null) throw new NullPointerException("Error while getting title of maintenance (ID: " + status.getId() + ")!");
        List<String> updates = new ArrayList<>();
        for(PlatformData.Update update : status.getUpdates()) {
            for(PlatformData.Translation translation : update.getTranslations()) {
                if(translation.getLocale().equals(riotLanguageCode)) {
                    updates.add(translation.getContent());
                }
            }
        }
        Embed embed = new Embed()
                .setTitle(language.getEmbedTitlePrefix() + title);
        if(updates.size() > 1) {
            for(int i = 0; i < updates.size(); i++) {
                embed.addField(language.getTranslation("command-status-embed-field-1-name").replaceAll("%updateIndex%", String.valueOf(i)), ">>> " + updates.get(i), false);
            }
        } else {
            embed.setDescription(">>> " + updates.get(0));
        }
        embed.addField(language.getTranslation("command-status-embed-field-2-name"), "<t:" + status.getCreationDate().getTime()/1000 + ":F>", true);
        return embed.build();
    }

    @Override
    public CommandData getCommandData() throws HttpErrorException, IOException, InterruptedException {
        OptionData optionData = new OptionData(OptionType.STRING, "region", "Region", true)
                .addChoice("Europe", "eu")
                .addChoice("North America", "na")
                .addChoice("Korea", "kr")
                .addChoice("Asia Pacific", "ap")
                .addChoice("Brazil", "br")
                .addChoice("Latin America", "latam");
        return Commands.slash(getName(), getDescription()).addOptions(optionData);
    }

    @Override
    public String getName() {
        return "status";
    }

    @Override
    public String getDescription() {
        return LanguageManager.getLanguage().getTranslation("command-status-description");
    }
}