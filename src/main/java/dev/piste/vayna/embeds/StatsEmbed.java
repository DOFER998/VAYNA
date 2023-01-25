package dev.piste.vayna.embeds;

import dev.piste.vayna.config.SettingsConfig;
import dev.piste.vayna.manager.CommandManager;
import dev.piste.vayna.util.Embed;
import dev.piste.vayna.util.Emoji;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

public class StatsEmbed {

    public static MessageEmbed getStats(String riotId, String playerCardSmall, int level, String regionName, String regionEmoji) {
        Embed embed = new Embed();
        embed.setAuthor(riotId, SettingsConfig.getWebsiteUri(), playerCardSmall);
        embed.setColor(209, 54, 57);
        embed.setTitle("» Statistics");
        embed.setDescription("Click on one of the buttons below to see more information.");
        embed.addField("Level", Emoji.getLevel().getFormatted() + " " + level, true);
        embed.addField("Region", regionEmoji + " " + regionName, true);
        embed.addField("Rank", "`Coming soon`", true);
        return embed.build();
    }

}
