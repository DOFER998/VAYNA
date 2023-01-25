package dev.piste.vayna.util;
/*
 * Author: Piste
 * Date: 26.05.2021
 */

import dev.piste.vayna.Bot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.util.ArrayList;

public class Embed {

    private EmbedBuilder embedBuilder;
    private boolean colorExists = false;
    private boolean footerExists = false;

    public Embed() {
        this.embedBuilder = new EmbedBuilder();
    }

    public Embed setDescription(String description) {
        embedBuilder.setDescription(description);
        return this;
    }

    public Embed setThumbnail(String url) {
        embedBuilder.setThumbnail(url);
        return this;
    }

    public Embed addField(String name, String text, boolean inline) {
        embedBuilder.addField(name, text, inline);
        return this;
    }

    public Embed addBlankField(boolean inline) {
        embedBuilder.addField("\u200e", "\u200e", inline);
        return this;
    }

    public Embed setColor(int red, int green, int blue) {
        embedBuilder.setColor(new Color(red, green, blue).getRGB());
        colorExists = true;
        return this;
    }

    public Embed setTitle(String title) {
        embedBuilder.setTitle(title);
        return this;
    }

    public Embed setTitle(String title, String titleUrl) {
        embedBuilder.setTitle(title, titleUrl);
        return this;
    }

    public Embed setAuthor(String name, String url, String iconUrl) {
        embedBuilder.setAuthor(name, url, iconUrl);
        return this;
    }

    public Embed setAuthor(String name, String url) {
        embedBuilder.setAuthor(name, url);
        return this;
    }

    public Embed setAuthor(String name) {
        embedBuilder.setAuthor(name);
        return this;
    }

    public Embed setImage(String url) {
        embedBuilder.setImage(url);
        return this;
    }

    public Embed setFooter(String text, String iconUrl) {
        embedBuilder.setFooter(text, iconUrl);
        footerExists = true;
        return this;
    }

    public MessageEmbed build() {
        if(!colorExists) embedBuilder.setColor(new Color(157, 57, 191).getRGB());
        if(!footerExists) embedBuilder.setFooter("VAYNA • VALORANT BOT", Bot.getJDA().getSelfUser().getAvatarUrl());
        return embedBuilder.build();

    }

}
