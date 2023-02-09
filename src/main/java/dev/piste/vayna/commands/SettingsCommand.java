package dev.piste.vayna.commands;

import dev.piste.vayna.Bot;
import dev.piste.vayna.apis.StatusCodeException;
import dev.piste.vayna.config.ConfigManager;
import dev.piste.vayna.manager.Command;
import dev.piste.vayna.util.Embed;
import dev.piste.vayna.util.Language;
import dev.piste.vayna.util.LanguageManager;
import dev.piste.vayna.util.buttons.Buttons;
import dev.piste.vayna.util.messages.ReplyMessages;
import dev.piste.vayna.util.selectmenus.SelectMenus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;

/**
 * @author Piste | https://github.com/zPiste
 */
public class SettingsCommand implements Command {


    @Override
    public void perform(SlashCommandInteractionEvent event) throws StatusCodeException {
        event.deferReply(true).queue();

        if(!event.getMember().hasPermission(Permission.MANAGE_SERVER)) {
            Language language = LanguageManager.getLanguage(event.getGuild());

            Embed embed = new Embed()
                    .setAuthor(event.getUser().getName(), ConfigManager.getSettingsConfig().getWebsiteUri(), event.getUser().getAvatarUrl())
                    .setTitle(language.getEmbedTitlePrefix() + language.getTranslation("command-settings-error-perms-embed-title"))
                    .setDescription(language.getTranslation("command-settings-error-perms-embed-description"))
                    .setColor(255, 0, 0);
            event.getHook().editOriginalEmbeds(embed.build()).setActionRow(
                    Buttons.getSupportButton(event.getGuild())
            ).queue();
            return;
        }

        event.getHook().editOriginalEmbeds(ReplyMessages.getSettings(event.getGuild())).setActionRow(
                SelectMenus.getSettingsSelectMenu(event.getGuild())
        ).queue();

    }

    @Override
    public void register() throws StatusCodeException {
        Bot.getJDA().upsertCommand(getName(), getDescription()).setGuildOnly(true).setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_SERVER)).queue();
    }

    @Override
    public String getName() {
        return "settings";
    }

    @Override
    public String getDescription() {
        return "Manage the bot settings of this server";
    }
}
