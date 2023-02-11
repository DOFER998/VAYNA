package dev.piste.vayna.commands.selectmenu;

import dev.piste.vayna.util.Embed;
import dev.piste.vayna.util.translations.Language;
import dev.piste.vayna.util.translations.LanguageManager;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

/**
 * @author Piste | https://github.com/zPiste
 */
public class SettingsSelectMenu implements dev.piste.vayna.manager.StringSelectMenu {

    @Override
    public void perform(StringSelectInteractionEvent event) {
        Language language = LanguageManager.getLanguage(event.getGuild());

        // Language settings
        if (event.getSelectedOptions().get(0).getValue().equals("language")) {
            Embed embed = new Embed()
                    .setAuthor(event.getGuild().getName(), event.getGuild().getIconUrl())
                    .setTitle(language.getEmbedTitlePrefix() + language.getTranslation("stringselect-settings-language-embed-title"))
                    .setDescription(language.getTranslation("stringselect-settings-language-embed-description"));
            StringSelectMenu.Builder stringSelectMenuBuilder = StringSelectMenu.create("language")
                    .setPlaceholder(language.getTranslation("stringselect-settings-language-selectmenu-placeholder"));
            for(Language foundLanguage : LanguageManager.getLanguages()) {
                stringSelectMenuBuilder.addOption(foundLanguage.getTranslation("language-name"), foundLanguage.getLanguageCode(), Emoji.fromUnicode(foundLanguage.getTranslation("language-emoji")));
            }
            // Reply
            event.editMessageEmbeds(embed.build()).setActionRow(
                    stringSelectMenuBuilder.build()
            ).queue();
        }
    }

    @Override
    public String getName() {
        return "settings";
    }
}