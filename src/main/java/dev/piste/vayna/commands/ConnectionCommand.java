package dev.piste.vayna.commands;

import dev.piste.vayna.Bot;
import dev.piste.vayna.apis.StatusCodeException;
import dev.piste.vayna.apis.riotgames.RiotAPI;
import dev.piste.vayna.config.translations.Language;
import dev.piste.vayna.manager.Command;
import dev.piste.vayna.apis.riotgames.gson.RiotAccount;
import dev.piste.vayna.config.Configs;
import dev.piste.vayna.counter.StatsCounter;
import dev.piste.vayna.mongodb.AuthKey;
import dev.piste.vayna.mongodb.LinkedAccount;
import dev.piste.vayna.util.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class ConnectionCommand implements Command {

    @Override
    public void perform(SlashCommandInteractionEvent event) throws StatusCodeException {
        event.deferReply().setEphemeral(true).queue();

        Language language = Language.getLanguage(event.getGuild());

        StatsCounter.countConnections();

        LinkedAccount linkedAccount = new LinkedAccount(event.getUser().getIdLong());

        if(!linkedAccount.isExisting()) {
            event.getHook().editOriginalEmbeds(language.getCommands().getConnection().getNone().getMessageEmbed(event.getUser())).setActionRow(
                    language.getCommands().getConnection().getConnectButton(new AuthKey(event.getUser().getIdLong()).getAuthKey())
            ).queue();
        } else {
            RiotAccount riotAccount = RiotAPI.getAccountByPuuid(linkedAccount.getRiotPuuid());

            event.getHook().setEphemeral(true).editOriginalEmbeds(language.getCommands().getConnection().getPresent().getMessageEmbed(event.getUser(), riotAccount.getRiotId(), linkedAccount.isVisibleToPublic())).setActionRow(
                    language.getCommands().getConnection().getDisconnectButton(),
                    language.getCommands().getConnection().getVisibilityButton(linkedAccount.isVisibleToPublic())
            ).queue();
        }
    }

    @Override
    public void register() {
        Bot.getJDA().upsertCommand(getName(), getDescription()).queue();
    }

    @Override
    public String getName() {
        return "connection";
    }

    @Override
    public String getDescription() {
        return "Manage the connection to your Riot-Games account and its visibility";
    }
}
