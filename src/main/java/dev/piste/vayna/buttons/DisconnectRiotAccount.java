package dev.piste.vayna.buttons;

import com.mongodb.client.MongoCollection;
import dev.piste.vayna.commands.ConnectionCommand;
import dev.piste.vayna.config.SettingsConfig;
import dev.piste.vayna.embeds.ConnectionEmbed;
import dev.piste.vayna.mongodb.AuthKey;
import dev.piste.vayna.mongodb.LinkedAccount;
import dev.piste.vayna.mongodb.Mongo;
import dev.piste.vayna.util.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.bson.Document;

import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;

public class DisconnectRiotAccount {

    public static void performButton(ButtonInteractionEvent event) {
        MongoCollection<Document> linkedAccountsCollection = Mongo.getLinkedAccountsCollection();

        ConnectionCommand.countConnections();

        if(LinkedAccount.isExisting(event.getUser().getIdLong())) {
            // The account has been found in the database
            linkedAccountsCollection.deleteOne(eq("discordId", event.getUser().getIdLong()));
        }

        String authKey = AuthKey.get(event.getUser());

        event.replyEmbeds(ConnectionEmbed.getNoConnectionPresent(event.getUser().getAsTag())).setActionRow(
                Button.link(SettingsConfig.getWebsiteUri() + "/RSO/redirect/?authKey=" + authKey, "Connect").withEmoji(Emoji.getRiotGames())
        ).setEphemeral(true).queue();
    }

}
