package dev.piste.vayna.interactions.commands.slash;

import dev.piste.vayna.apis.HenrikAPI;
import dev.piste.vayna.apis.HttpErrorException;
import dev.piste.vayna.apis.OfficerAPI;
import dev.piste.vayna.apis.entities.henrik.StoreBundle;
import dev.piste.vayna.apis.entities.officer.Bundle;
import dev.piste.vayna.interactions.selectmenus.string.BundleSelectMenu;
import dev.piste.vayna.translations.Language;
import dev.piste.vayna.translations.LanguageManager;
import dev.piste.vayna.util.Embed;
import dev.piste.vayna.util.templates.MessageEmbeds;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Piste | https://github.com/PisteDev
 */
public class StoreSlashCommand implements ISlashCommand {


    @Override
    public void perform(SlashCommandInteractionEvent event, Language language) throws HttpErrorException, IOException, InterruptedException {
        event.deferReply().setEphemeral(true).queue();

        OfficerAPI officerAPI = new OfficerAPI();
        HenrikAPI henrikAPI = new HenrikAPI();
        List<StoreBundle> storeBundles = henrikAPI.getStoreBundles();

        if(storeBundles.size() <= 1) {
            event.getHook().editOriginalEmbeds(MessageEmbeds.getBundleEmbeds(language, storeBundles.get(0))).queue();
        } else {
            ArrayList<SelectOption> selectOptions = new ArrayList<>();
            for(StoreBundle storeBundle : storeBundles) {
                Bundle bundle = officerAPI.getBundle(storeBundle.getId(), language.getLanguageCode());
                selectOptions.add(SelectOption.of(bundle.getDisplayName(), bundle.getId()));
            }
            StringSelectMenu stringSelectMenu = StringSelectMenu.create(new BundleSelectMenu().getName())
                    .setPlaceholder(language.getTranslation("command-store-selectmenu-placeholder"))
                    .addOptions(selectOptions)
                    .build();
            Embed embed = new Embed()
                    .setTitle(language.getTranslation("command-store-embed-select-title"))
                    .setDescription(language.getTranslation("command-store-embed-select-description"));
            event.getHook().editOriginalEmbeds(embed.build()).setActionRow(stringSelectMenu).queue();
        }

    }

    @Override
    public CommandData getCommandData() {
        return Commands.slash(getName(), getDescription());
    }

    @Override
    public String getName() {
        return "store";
    }

    @Override
    public String getDescription() {
        return LanguageManager.getLanguage().getTranslation("command-store-description");
    }
}
