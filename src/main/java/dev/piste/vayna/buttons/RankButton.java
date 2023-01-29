package dev.piste.vayna.buttons;

import dev.piste.vayna.Bot;
import dev.piste.vayna.apis.henrik.HenrikAPI;
import dev.piste.vayna.apis.henrik.HenrikApiException;
import dev.piste.vayna.apis.henrik.gson.HenrikAccount;
import dev.piste.vayna.apis.henrik.gson.MMRHistory;
import dev.piste.vayna.apis.riotgames.RiotAPI;
import dev.piste.vayna.apis.riotgames.gson.RiotAccount;
import dev.piste.vayna.apis.valorantapi.ValorantAPI;
import dev.piste.vayna.apis.valorantapi.gson.CompetitiveTier;
import dev.piste.vayna.apis.valorantapi.gson.competitivetier.Tier;
import dev.piste.vayna.config.Configs;
import dev.piste.vayna.embeds.ErrorEmbed;
import dev.piste.vayna.util.Embed;
import dev.piste.vayna.util.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class RankButton {

    public void perform(ButtonInteractionEvent event) {
        event.deferReply().queue();
        RiotAccount oldRiotAccount = Bot.getStatsButtonMap().get(event.getButton().getId().split(";")[1]);

        Bot.getStatsButtonMap().remove(event.getButton().getId().split(";")[1]);

        if(oldRiotAccount == null) {
            event.getHook().editOriginalEmbeds(ErrorEmbed.getButtonTooOld(event.getUser())).setActionRow(
                    Button.link(Configs.getSettings().getSupportGuild().getInviteUri(), "Support").withEmoji(Emoji.getDiscord())
            ).queue();
            return;
        }

        RiotAccount riotAccount = RiotAPI.getAccountByPuuid(oldRiotAccount.getPuuid());

        HenrikAccount henrikAccount;
        try {
            henrikAccount = HenrikAPI.getAccountByRiotId(riotAccount.getGameName(), riotAccount.getTagLine());
        } catch (HenrikApiException e) {
            event.getHook().editOriginalEmbeds(ErrorEmbed.getHenrikApiError(event.getUser())).setActionRow(
                    Button.link(Configs.getSettings().getSupportGuild().getInviteUri(), "Support").withEmoji(Emoji.getDiscord())
            ).queue();
            return;
        }

        Embed embed = new Embed()
                .setAuthor(riotAccount.getRiotId(), Configs.getSettings().getWebsiteUri(), henrikAccount.getCard().getSmall())
                .setTitle("» Rank")
                .setColor(209, 54, 57);

        MMRHistory mmrHistory = henrikAccount.getMmrHistory().get(0);

        CompetitiveTier competitiveTier = ValorantAPI.getLatestCompetitiveTier();
        if(mmrHistory.getCurrentTierPatched() == null) {
            Tier tier = competitiveTier.getTiers().get(0);
            embed.addField("Rank", Emoji.getRankByTierName(tier.getTierName()).getFormatted()  + " " + tier.getTierName(), true)
                    .setThumbnail(tier.getLargeIcon());
            event.getHook().editOriginalEmbeds(embed.build()).queue();
            return;
        }
        for(Tier tier : competitiveTier.getTiers()) {
            if(tier.getTier() == mmrHistory.getCurrentTier()) {
                embed.addField("Rank", Emoji.getRankByTierName(tier.getTierName()).getFormatted() + " " + tier.getTierName(), false)
                        .setThumbnail(tier.getLargeIcon());
                if(mmrHistory.getCurrentTier() > 23) {
                    embed.addField("Rating", "**" + mmrHistory.getRankingInTier() + "** » " +
                            (mmrHistory.getMmrChangeToLastGame()>=0 ? Emoji.getIncrease().getFormatted() + " **+" + mmrHistory.getMmrChangeToLastGame() + "**" : Emoji.getDecrease().getFormatted() + " **" + mmrHistory.getMmrChangeToLastGame() + "**"), false);
                } else {
                    embed.addField("Rating", getProgressBar(mmrHistory.getRankingInTier()) + "\n" +
                            "**" + mmrHistory.getRankingInTier() + "**/**100** » " +
                            (mmrHistory.getMmrChangeToLastGame()>=0 ? Emoji.getIncrease().getFormatted() + " **+" + mmrHistory.getMmrChangeToLastGame() + "**" : Emoji.getDecrease().getFormatted() + " **" + mmrHistory.getMmrChangeToLastGame() + "**"), false);
                }

                event.getHook().editOriginalEmbeds(embed.build()).queue();
                return;
            }
        }

    }

    private String getProgressBar(int ranking) {
        int value = (int) Math.round(ranking/10.0);
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i<value; i++) {
            if(i==0) {
                stringBuilder.append(Emoji.getProgressBarGreen("start").getFormatted());
                continue;
            }
            if(i==9) {
                stringBuilder.append(Emoji.getProgressBarGreen("end").getFormatted());
                continue;
            }
            stringBuilder.append(Emoji.getProgressBarGreen("line").getFormatted());
        }
        for(int i = value; i<10; i++) {
            if(i==0) {
                stringBuilder.append(Emoji.getProgressBarRed("start").getFormatted());
                continue;
            }
            if(i==9) {
                stringBuilder.append(Emoji.getProgressBarRed("end").getFormatted());
                continue;
            }
            stringBuilder.append(Emoji.getProgressBarRed("line").getFormatted());
        }
        return stringBuilder.toString();
    }

}
