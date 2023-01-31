package dev.piste.vayna.commands;

import dev.piste.vayna.Bot;
import dev.piste.vayna.apis.StatusCodeException;
import dev.piste.vayna.apis.henrik.HenrikAPI;
import dev.piste.vayna.apis.henrik.gson.HenrikAccount;
import dev.piste.vayna.apis.henrik.gson.MMR;
import dev.piste.vayna.apis.henrik.gson.mmr.Rank;
import dev.piste.vayna.apis.riotgames.RiotAPI;
import dev.piste.vayna.apis.riotgames.gson.RiotAccount;
import dev.piste.vayna.apis.valorantapi.ValorantAPI;
import dev.piste.vayna.apis.valorantapi.gson.competitivetier.Tier;
import dev.piste.vayna.config.Configs;
import dev.piste.vayna.config.translations.Language;
import dev.piste.vayna.manager.Command;
import dev.piste.vayna.mongodb.LinkedAccount;
import dev.piste.vayna.util.Embed;
import dev.piste.vayna.util.Emoji;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.*;

/**
 * @author Piste | https://github.com/zPiste
 */
public class LeaderboardCommand implements Command {


    @Override
    public void perform(SlashCommandInteractionEvent event) throws StatusCodeException {
        event.deferReply().queue();

        Language language = Language.getLanguage(event.getGuild());

        if(event.getChannelType() == ChannelType.PRIVATE) {
            event.getHook().editOriginalEmbeds(language.getCommands().getLeaderboard().getErrors().getPrivateChannel().getMessageEmbed(event.getUser())).setActionRow(
                    language.getErrors().getSupportButton()
            ).queue();
            return;
        }

        // Put all linked accounts in this guild in the eloMap
        HashMap<User, MMR> eloMap = new HashMap<>();

        for(Member member : event.getGuild().getMembers()) {
            LinkedAccount linkedAccount = new LinkedAccount(member.getUser().getIdLong());

            if(linkedAccount.isExisting() && linkedAccount.isVisibleToPublic()) {
                try {
                    RiotAccount riotAccount = RiotAPI.getAccountByPuuid(linkedAccount.getRiotPuuid());
                    HenrikAccount henrikAccount = HenrikAPI.getAccountByRiotId(riotAccount.getGameName(), riotAccount.getTagLine());
                    eloMap.put(member.getUser(), henrikAccount.getMmr());
                } catch (StatusCodeException ignored) {
                }
            }
        }

        if(eloMap.size() == 0) {
            event.getHook().editOriginalEmbeds(language.getCommands().getLeaderboard().getErrors().getNoPlayersDisplayable().getMessageEmbed(event.getUser())).setActionRow(
                    language.getErrors().getSupportButton()
            ).queue();
            return;
        }

        // Create a list of the elos and sort it // Calculate the average elo of this guild
        ArrayList<Integer> eloList = new ArrayList<>();
        int memberElos = 0;
        for(MMR mmr : eloMap.values()) {
            memberElos += mmr.getRank().getElo();
            eloList.add(mmr.getRank().getElo());
        }
        eloList.sort(Collections.reverseOrder());
        int guildElo = memberElos / eloMap.size();

        Embed embed = new Embed().setAuthor(event.getGuild().getName(), Configs.getSettings().getWebsiteUri(), event.getGuild().getIconUrl())
                .setDescription(language.getCommands().getLeaderboard().getDescription());

        // Create an embed field for the best 20 players in this guild
        for(int i = 0; i<20; i++) {
            if(eloList.size() == i) break;
            for(HashMap.Entry<User, MMR> entry : eloMap.entrySet()) {
                User user = entry.getKey();
                MMR mmr = entry.getValue();
                Rank rank = mmr.getRank();
                Tier tier = null;
                for(Tier forTier : ValorantAPI.getLatestCompetitiveTier(language.getLanguageCode()).getTiers()) {
                    if(forTier.getTier() == rank.getCurrentTier()) {
                        tier = forTier;
                        break;
                    }
                }
                if(entry.getValue().getRank().getElo() == eloList.get(i)) {
                    if(rank.getElo() >= 2100) {
                        embed.addField((i+1) + ". " + user.getAsTag() + " (" + Emoji.getRiotGames().getFormatted() + " " + mmr.getGameName() + "#" + mmr.getTagLine() + ")",
                                Emoji.getRankByTierName(rank.getCurrentTierPatched()).getFormatted() + " " + tier.getTierName() +
                                        " (**" + rank.getRankingInTier() + "RR**)", false);
                    } else {
                        embed.addField((i+1) + ". " + user.getAsTag() + " (" + Emoji.getRiotGames().getFormatted() + " " + mmr.getGameName() + "#" + mmr.getTagLine() + ")",
                                Emoji.getRankByTierName(rank.getCurrentTierPatched()).getFormatted() + " " + tier.getTierName() +
                                        " (**" + rank.getRankingInTier() + "**/**100**)", false);
                    }

                }
            }
        }

        // Put the average guild elo in the embed
        int guildRatingInTier;
        Tier guildTier;
        if(guildElo < 2100) {
            guildRatingInTier = guildElo % 100;
            guildTier = ValorantAPI.getLatestCompetitiveTier(language.getLanguageCode()).getTiers().get((int) ((guildElo / 100.0) + 3.0));
        } else {
            guildRatingInTier = guildElo - 2100;
            if(guildRatingInTier < 90) {
                guildTier = ValorantAPI.getLatestCompetitiveTier(language.getLanguageCode()).getTiers().get(24);
            } else if(guildRatingInTier < 200) {
                guildTier = ValorantAPI.getLatestCompetitiveTier(language.getLanguageCode()).getTiers().get(25);
            } else if(guildRatingInTier < 450) {
                guildTier = ValorantAPI.getLatestCompetitiveTier(language.getLanguageCode()).getTiers().get(26);
            } else {
                guildTier = ValorantAPI.getLatestCompetitiveTier(language.getLanguageCode()).getTiers().get(27);
            }
        }
        embed.setTitle(Configs.getTranslations().getTitlePrefix() + language.getCommands().getLeaderboard().getTitle().replaceAll("%rank:name%", guildTier.getTierName()).replaceAll("%rank:rating%", String.valueOf(guildRatingInTier)));
        embed.setThumbnail(guildTier.getLargeIcon());

        event.getHook().editOriginalEmbeds(embed.build()).queue();
    }

    @Override
    public void register() {
        Bot.getJDA().upsertCommand(getName(), getDescription()).queue();
    }

    @Override
    public String getName() {
        return "leaderboard";
    }

    @Override
    public String getDescription() {
        return "Get a list of the best 20 players in this server";
    }
}
