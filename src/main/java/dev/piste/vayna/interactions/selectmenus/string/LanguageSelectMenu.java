package dev.piste.vayna.interactions.selectmenus.string;

import dev.piste.vayna.mongodb.GuildSetting;
import dev.piste.vayna.util.templates.MessageEmbeds;
import dev.piste.vayna.util.templates.SelectMenus;
import dev.piste.vayna.translations.Language;
import dev.piste.vayna.translations.LanguageManager;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;

/**
 * @author Piste | https://github.com/PisteDev
 */
public class LanguageSelectMenu implements IStringSelectMenu {

    @Override
    public void perform(StringSelectInteractionEvent event, Language language) {
        GuildSetting guildSetting = new GuildSetting(event.getGuild().getIdLong());
        guildSetting.setLanguage(event.getSelectedOptions().get(0).getValue());
        guildSetting.update();

        // Reply
        event.editMessageEmbeds(MessageEmbeds.getSettingsEmbed(LanguageManager.getLanguage(event.getGuild()), event.getGuild())).setActionRow(
                SelectMenus.getSettingsSelectMenu(event.getGuild())
        ).queue();
    }

    @Override
    public String getName() {
        return "language";
    }
}
