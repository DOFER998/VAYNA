package dev.piste.vayna.commands;

import com.mongodb.client.MongoCollection;
import dev.piste.vayna.api.riotgames.Account;
import dev.piste.vayna.config.SettingsConfig;
import dev.piste.vayna.manager.CommandManager;
import dev.piste.vayna.mongodb.Mongo;
import dev.piste.vayna.util.Embed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.bson.Document;

import java.util.Random;

import static com.mongodb.client.model.Filters.eq;

public class ConnectionCommand {

    public static void performCommand(SlashCommandInteractionEvent event, String subcommand) {
        MongoCollection<Document> linkedAccountsCollection = Mongo.getLinkedAccountsCollection();
        Document foundAccount = linkedAccountsCollection.find(eq("discordId", event.getUser().getIdLong())).first();

        Embed embed = new Embed();

        switch (subcommand) {
            case "connect" -> {
                if(foundAccount != null) {
                    embed.setColor(255, 0, 0);
                    embed.setTitle("» Connection already existing");
                    embed.setDescription("Your Discord account is already connected with a Riot account. " +
                            "Please disconnect your currently connected Riot account from your Discord account first " +
                            "(</connection disconnect:" + CommandManager.findSubcommand(CommandManager.findCommand("connection"), "disconnect").getId() + ">) and then try again.");
                    event.getHook().editOriginalEmbeds(embed.build()).queue();
                    return;
                }
                MongoCollection<Document> authKeysCollection = Mongo.getAuthKeysCollection();

                Document foundAuthKeyDocument = authKeysCollection.find(eq("discordId", event.getUser().getIdLong())).first();
                String authKey;
                if(foundAuthKeyDocument != null) {
                    authKey = (String) foundAuthKeyDocument.get("authKey");
                } else {
                    authKey = getRandomHexString();
                    Document newAuthKeyDocument = new Document();
                    newAuthKeyDocument.put("discordId", event.getUser().getIdLong());
                    newAuthKeyDocument.put("authKey", authKey);
                    authKeysCollection.insertOne(newAuthKeyDocument);
                }
                embed.setTitle("» Authorization");
                embed.setDescription("Please click on the button below to log in with your Riot account.");
                event.getHook().editOriginalEmbeds(embed.build()).setActionRow(
                        Button.link(new SettingsConfig().getWebsiteUri() + "/RSO/redirect/?authKey=" + authKey, Emoji.fromCustom("RiotGames", 1065717205402124398l, false))
                ).queue();
            }
            case "disconnect" -> {
                if (!accountIsExisting(event, foundAccount, embed)) return;
                linkedAccountsCollection.deleteOne(foundAccount);
                embed.setColor(0, 255, 0);
                embed.setTitle("» Success");
                embed.setDescription("Your Riot account has successfully been disconnected from your Discord account.");
                event.getHook().editOriginalEmbeds(embed.build()).queue();
            }
            case "info" -> {
                if (!accountIsExisting(event, foundAccount, embed)) return;
                Account account = new Account(foundAccount.getString("puuid"));
                embed.setTitle("» Current connection");
                embed.setDescription("This is your currently connected Riot account:");
                embed.addField("Riot-Games Account", Emoji.fromCustom("RiotGames", 1065717205402124398l, false).getAsMention() + " **" + account.getRiotId() + "**", false);
                event.getHook().editOriginalEmbeds(embed.build()).queue();
                return;
            }
        }


    }

    private static boolean accountIsExisting(SlashCommandInteractionEvent event, Document foundAccount, Embed embed) {
        if(foundAccount == null) {
            embed.setColor(255, 0, 0);
            embed.setTitle("» No connection existing");
            embed.setDescription("You haven't connected a Riot account to your Discord account yet. " +
                    "To do this, type " +
                    "</connection connect:" + CommandManager.findSubcommand(CommandManager.findCommand("connection"), "connect").getId() + ">.");
            event.getHook().editOriginalEmbeds(embed.build()).queue();
            return false;
        }
        return true;
    }

    private static String getRandomHexString(){
        Random r = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        while(stringBuilder.length() < 32) {
            stringBuilder.append(Integer.toHexString(r.nextInt()));
        }
        return stringBuilder.substring(0, 32);
    }

}
