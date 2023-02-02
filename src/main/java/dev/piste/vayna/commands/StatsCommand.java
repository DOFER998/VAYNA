package dev.piste.vayna.commands;

import dev.piste.vayna.Bot;
import dev.piste.vayna.apis.StatusCodeException;
import dev.piste.vayna.apis.henrik.HenrikAPI;
import dev.piste.vayna.apis.riotgames.InvalidRegionException;
import dev.piste.vayna.apis.riotgames.RiotAPI;
import dev.piste.vayna.config.Configs;
import dev.piste.vayna.manager.Command;
import dev.piste.vayna.apis.henrik.gson.HenrikAccount;
import dev.piste.vayna.apis.riotgames.gson.ActiveShard;
import dev.piste.vayna.apis.riotgames.gson.RiotAccount;
import dev.piste.vayna.apis.riotgames.InvalidRiotIdException;
import dev.piste.vayna.manager.CommandManager;
import dev.piste.vayna.mongodb.LinkedAccount;
import dev.piste.vayna.util.Embed;
import dev.piste.vayna.util.Emoji;
import dev.piste.vayna.util.TranslationManager;
import dev.piste.vayna.util.buttons.Buttons;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class StatsCommand implements Command {

    @Override
    public void perform(SlashCommandInteractionEvent event) throws StatusCodeException {
        event.deferReply().queue();

        TranslationManager translation = TranslationManager.getTranslation(event.getGuild());

        LinkedAccount linkedAccount = null;
        RiotAccount riotAccount = null;
        long discordUserId = 0;
        if(event.getSubcommandName() == null) return;
        switch (event.getSubcommandName()) {
            // /stats me
            case "me" -> {
                discordUserId = event.getUser().getIdLong();
                linkedAccount = new LinkedAccount(event.getUser().getIdLong());
                riotAccount = RiotAPI.getAccountByPuuid(linkedAccount.getRiotPuuid());
            }
            // /stats user <@user>
            case "user" -> {
                discordUserId = event.getOption("user").getAsLong();
                linkedAccount = new LinkedAccount(discordUserId);
                riotAccount = RiotAPI.getAccountByPuuid(linkedAccount.getRiotPuuid());
            }
            // /stats riot-id <name> <tag>
            case "riot-id" -> {
                String gameName = event.getOption("name").getAsString();
                String tagLine = event.getOption("tag").getAsString();
                try {
                    riotAccount = RiotAPI.getAccountByRiotId(gameName, tagLine);

                    linkedAccount = new LinkedAccount(riotAccount.getPuuid());
                } catch (InvalidRiotIdException e) {
                    Embed embed = new Embed().setAuthor(event.getUser().getName(), Configs.getSettings().getWebsiteUri(), event.getUser().getAvatarUrl())
                            .setColor(255, 0, 0)
                            .setTitle(translation.getTranslation("embed-title-prefix") + translation.getTranslation("command-stats-error-riotid-embed-title"))
                            .setDescription(translation.getTranslation("command-stats-error-riotid-embed-description")
                                    .replaceAll("%emoji:riotgames%", Emoji.getRiotGames().getFormatted())
                                    .replaceAll("%riotid%", gameName + "#" + tagLine));
                    event.getHook().editOriginalEmbeds(embed.build()).setActionRow(
                            Buttons.getSupportButton(event.getGuild())
                    ).queue();
                    return;
                }
            }
        }

        if (linkedAccount.isExisting()) {
            if(!linkedAccount.isVisibleToPublic() && (linkedAccount.getDiscordUserId() != event.getUser().getIdLong())) {
                Embed embed = new Embed().setAuthor(event.getUser().getName(), Configs.getSettings().getWebsiteUri(), event.getUser().getAvatarUrl())
                        .setColor(255, 0, 0)
                        .setTitle(translation.getTranslation("embed-title-prefix") + translation.getTranslation("command-stats-error-private-embed-title"))
                        .setDescription(translation.getTranslation("command-stats-error-private-embed-description"));
                event.getHook().editOriginalEmbeds(embed.build()).setActionRow(
                        Buttons.getSupportButton(event.getGuild())
                ).queue();
                return;
            }
        } else {
            if(discordUserId != 0) {
                if (discordUserId == event.getUser().getIdLong()) {
                    Embed embed = new Embed().setAuthor(event.getUser().getName(), Configs.getSettings().getWebsiteUri(), event.getUser().getAvatarUrl())
                            .setColor(255, 0, 0)
                            .setTitle(translation.getTranslation("embed-title-prefix") + translation.getTranslation("command-stats-error-noconnectionself-embed-title"))
                            .setDescription(translation.getTranslation("command-stats-error-noconnectionself-embed-description")
                                    .replaceAll("%command:connection%", CommandManager.getAsJdaCommand(new ConnectionCommand()).getAsMention()));
                    event.getHook().editOriginalEmbeds(embed.build()).setActionRow(
                            Buttons.getSupportButton(event.getGuild())
                    ).queue();
                } else {
                        Embed embed = new Embed().setAuthor(event.getUser().getName(), Configs.getSettings().getWebsiteUri(), event.getUser().getAvatarUrl())
                                .setColor(255, 0, 0)
                                .setTitle(translation.getTranslation("embed-title-prefix") + translation.getTranslation("command-stats-error-noconnection-embed-title"))
                                .setDescription(translation.getTranslation("command-stats-error-noconnection-embed-description")
                                        .replaceAll("%user:target%", event.getJDA().getUserById(discordUserId).getAsMention()));
                    event.getHook().editOriginalEmbeds(embed.build()).setActionRow(
                            Buttons.getSupportButton(event.getGuild())
                    ).queue();
                }
                return;
            }
        }

        ActiveShard activeShard;
        try {
            activeShard = RiotAPI.getActiveShard(riotAccount.getPuuid());
        } catch (InvalidRegionException e) {
            Embed embed = new Embed().setAuthor(event.getUser().getName(), Configs.getSettings().getWebsiteUri(), event.getUser().getAvatarUrl())
                    .setColor(255, 0, 0)
                    .setTitle(translation.getTranslation("embed-title-prefix") + translation.getTranslation("command-stats-error-region-embed-title"))
                    .setDescription(translation.getTranslation("command-stats-error-region-embed-description")
                            .replaceAll("%emoji:riotgames%", Emoji.getRiotGames().getFormatted())
                            .replaceAll("%riotid%", riotAccount.getRiotId()));
            event.getHook().editOriginalEmbeds(embed.build()).setActionRow(
                    Buttons.getSupportButton(event.getGuild())
            ).queue();
            return;
        }

        String regionEmoji = switch (activeShard.getActiveShard()) {
            case "eu" -> "\uD83C\uDDEA\uD83C\uDDFA";
            case "na" -> "\uD83C\uDDFA\uD83C\uDDF8";
            case "br", "latam" -> "\uD83C\uDDE7\uD83C\uDDF7";
            case "kr" -> "\uD83C\uDDF0\uD83C\uDDF7";
            case "ap" -> "\uD83C\uDDE6\uD83C\uDDFA";
            default -> "none";
        };
        String regionName = RiotAPI.getPlatformData(activeShard.getActiveShard()).getName();

        HenrikAccount henrikAccount = HenrikAPI.getAccountByRiotId(riotAccount.getGameName(), riotAccount.getTagLine());

            Embed embed = new Embed();
            embed.setAuthor(riotAccount.getRiotId(), Configs.getSettings().getWebsiteUri(), henrikAccount.getCard().getSmall());
            embed.setColor(209, 54, 57);
            embed.setTitle(translation.getTranslation("embed-title-prefix") + translation.getTranslation("command-stats-embed-title"));
            embed.setDescription(translation.getTranslation("command-stats-embed-description"));
            embed.addField(translation.getTranslation("command-stats-embed-field-1-name"), Emoji.getLevel().getFormatted() + " " + henrikAccount.getAccountLevel(), true);
            embed.addField(translation.getTranslation("command-stats-embed-field-2-name"), regionEmoji + " " + regionName, true);
            if(linkedAccount.isExisting()) {
                embed.addField(translation.getTranslation("command-stats-embed-field-3-name"),
                        Emoji.getDiscord().getFormatted() + " " + Bot.getJDA().getUserById(linkedAccount.getDiscordUserId()).getAsMention() + " (`" + Bot.getJDA().getUserById(linkedAccount.getDiscordUserId()).getAsTag() + "`)", true);
            }
        event.getHook().editOriginalEmbeds(embed.build()).setActionRow(
            Buttons.getRankButton(event.getGuild(), riotAccount)
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
