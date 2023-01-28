package dev.piste.vayna.commands;

import dev.piste.vayna.Bot;
import dev.piste.vayna.apis.henrik.HenrikAPI;
import dev.piste.vayna.apis.riotgames.RiotAPI;
import dev.piste.vayna.config.Configs;
import dev.piste.vayna.manager.Command;
import dev.piste.vayna.apis.henrik.gson.HenrikAccount;
import dev.piste.vayna.apis.riotgames.gson.ActiveShard;
import dev.piste.vayna.apis.riotgames.gson.RiotAccount;
import dev.piste.vayna.embeds.ErrorEmbed;
import dev.piste.vayna.apis.henrik.HenrikApiException;
import dev.piste.vayna.apis.riotgames.RiotApiException;
import dev.piste.vayna.mongodb.LinkedAccount;
import dev.piste.vayna.util.Embed;
import dev.piste.vayna.util.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.UUID;

public class StatsCommand implements Command {

    @Override
    public void perform(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        LinkedAccount linkedAccount = null;
        RiotAccount riotAccount = null;

        if(event.getSubcommandName() == null) return;
        switch (event.getSubcommandName()) {
            // /stats me
            case "me" -> {
                linkedAccount = new LinkedAccount(event.getUser().getIdLong());

                if (!linkedAccount.isExisting()) {
                    event.getHook().editOriginalEmbeds(ErrorEmbed.getSelfRiotAccountNotConnected(event.getUser())).setActionRow(
                            Button.link(Configs.getSettings().getSupportGuild().getInviteUri(), "Support").withEmoji(Emoji.getDiscord())
                    ).queue();
                    return;
                }
                riotAccount = RiotAPI.getAccountByPuuid(linkedAccount.getRiotPuuid());
            }
            // /stats user <@user>
            case "user" -> {
                long discordUserId = event.getOption("user").getAsLong();
                linkedAccount = new LinkedAccount(discordUserId);

                if (!linkedAccount.isExisting()) {
                    if (discordUserId == event.getUser().getIdLong()) {
                        event.getHook().editOriginalEmbeds(ErrorEmbed.getSelfRiotAccountNotConnected(event.getUser())).setActionRow(
                                Button.link(Configs.getSettings().getSupportGuild().getInviteUri(), "Support").withEmoji(Emoji.getDiscord())
                        ).queue();
                    } else {
                        event.getHook().editOriginalEmbeds(ErrorEmbed.getRiotAccountNotConnected(event.getUser(), Bot.getJDA().getUserById(discordUserId))).setActionRow(
                                Button.link(Configs.getSettings().getSupportGuild().getInviteUri(), "Support").withEmoji(Emoji.getDiscord())
                        ).queue();
                    }
                    return;
                }
                if(!linkedAccount.isVisibleToPublic() && (discordUserId != event.getUser().getIdLong())) {
                    event.getHook().editOriginalEmbeds(ErrorEmbed.getLinkedAccountPrivate(event.getUser())).setActionRow(
                            Button.link(Configs.getSettings().getSupportGuild().getInviteUri(), "Support").withEmoji(Emoji.getDiscord())
                    ).queue();
                    return;
                }
                riotAccount = RiotAPI.getAccountByPuuid(linkedAccount.getRiotPuuid());

            }
            // /stats riot-id <name> <tag>
            case "riot-id" -> {
                String gameName = event.getOption("name").getAsString();
                String tagLine = event.getOption("tag").getAsString();
                try {
                    riotAccount = RiotAPI.getAccountByRiotId(gameName, tagLine);

                    linkedAccount = new LinkedAccount(riotAccount.getPuuid());
                    if (linkedAccount.isExisting()) {
                        if(!linkedAccount.isVisibleToPublic() && (linkedAccount.getDiscordUserId() != event.getUser().getIdLong())) {
                            event.getHook().editOriginalEmbeds(ErrorEmbed.getLinkedAccountPrivate(event.getUser())).setActionRow(
                                    Button.link(Configs.getSettings().getSupportGuild().getInviteUri(), "Support").withEmoji(Emoji.getDiscord())
                            ).queue();
                            return;
                        }
                    }
                } catch (RiotApiException e) {
                    event.getHook().editOriginalEmbeds(ErrorEmbed.getRiotIdNotFound(event.getUser(), gameName + "#" + tagLine)).setActionRow(
                            Button.link(Configs.getSettings().getSupportGuild().getInviteUri(), "Support").withEmoji(Emoji.getDiscord())
                    ).queue();
                    return;
                }
            }
        }

        String regionEmoji;
        String regionName;
        try {
            ActiveShard activeShard = RiotAPI.getActiveShard(riotAccount.getPuuid());
            regionEmoji = switch (activeShard.getActiveShard()) {
                case "eu" -> "\uD83C\uDDEA\uD83C\uDDFA";
                case "na" -> "\uD83C\uDDFA\uD83C\uDDF8";
                case "br", "latam" -> "\uD83C\uDDE7\uD83C\uDDF7";
                case "kr" -> "\uD83C\uDDF0\uD83C\uDDF7";
                case "ap" -> "\uD83C\uDDE6\uD83C\uDDFA";
                default -> "none";
            };
            regionName = RiotAPI.getPlatformData(activeShard.getActiveShard()).getName();
        } catch (RiotApiException e) {
            event.getHook().editOriginalEmbeds(ErrorEmbed.getNoRegion(event.getUser())).setActionRow(
                    Button.link(Configs.getSettings().getSupportGuild().getInviteUri(), "Support").withEmoji(Emoji.getDiscord())
            ).queue();
            return;
        }

        HenrikAccount henrikAccount;
        try {
            henrikAccount = HenrikAPI.getAccountByRiotId(riotAccount.getGameName(), riotAccount.getTagLine());
        } catch (HenrikApiException e) {
            event.getHook().editOriginalEmbeds(ErrorEmbed.getHenrikApiError(event.getUser())).setActionRow(
                    Button.link(Configs.getSettings().getSupportGuild().getInviteUri(), "Support").withEmoji(Emoji.getDiscord())
            ).queue();
            return;
        }

        String uuid = UUID.randomUUID().toString();

        Bot.getStatsButtonMap().put(uuid, riotAccount);

        Embed embed = new Embed();
        embed.setAuthor(riotAccount.getRiotId(), Configs.getSettings().getWebsiteUri(), henrikAccount.getCard().getSmall());
        embed.setColor(209, 54, 57);
        embed.setTitle("» Statistics");
        embed.setDescription("Click on one of the buttons below to see more information.");
        embed.addField("Level", Emoji.getLevel().getFormatted() + " " + henrikAccount.getAccountLevel(), true);
        embed.addField("Region", regionEmoji + " " + regionName, true);
        if(linkedAccount.isExisting()) {
            embed.addField("Connection", Emoji.getDiscord().getFormatted() + " " + Bot.getJDA().getUserById(linkedAccount.getDiscordUserId()).getAsMention() + " (`" + Bot.getJDA().getUserById(linkedAccount.getDiscordUserId()).getAsTag() + "`)", true);
        }
        event.getHook().editOriginalEmbeds(embed.build()).setActionRow(
                Button.secondary("rank;" + uuid, "Rank").withEmoji(Emoji.getRankByTierName("Unranked"))
        ).queue();
    }

    @Override
    public void register() {
        SubcommandData userSub = new SubcommandData("user", "Get general information about a VALORANT profile from a Discord user")
                .addOption(OptionType.USER, "user", "The discord user to get the stats from", true);
        SubcommandData riotIdSub = new SubcommandData("riot-id", "Get general information about a VALORANT profile by providing a Riot-ID")
                .addOption(OptionType.STRING, "name", "The name of the Riot-ID (<name>#<tag>)", true)
                .addOption(OptionType.STRING, "tag", "The tag of the Riot-ID (<name>#<tag>)", true);
        SubcommandData meSub = new SubcommandData("me", "Get general information about your VALORANT profile");
        Bot.getJDA().upsertCommand(getName(), getDescription()).addSubcommands(userSub, riotIdSub, meSub).queue();
    }

    @Override
    public String getName() {
        return "stats";
    }

    @Override
    public String getDescription() {
        return "Get general information about a VALORANT profile";
    }
}
