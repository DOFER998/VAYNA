package dev.piste.vayna.util.messages;

import dev.piste.vayna.apis.StatusCodeException;
import dev.piste.vayna.util.Embed;
import dev.piste.vayna.util.translations.Language;
import dev.piste.vayna.util.translations.LanguageManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

/**
 * @author Piste | https://github.com/zPiste
 */
public class ErrorMessages {

    public static Embed getStatusCodeErrorEmbed(Guild guild, User user, StatusCodeException exception) {
        String[] message = exception.getMessage().split(" ");
        Language language = LanguageManager.getLanguage(guild);
        int statusCode = Integer.parseInt(message[0]);

        Embed embed = new Embed()
                .setColor(255, 0, 0)
                .setTitle(language.getEmbedTitlePrefix() + language.getTranslation("error-api-embed-title"))
                .setDescription(language.getTranslation("error-api-embed-description"))
                .setAuthor(user.getName(), user.getAvatarUrl());
        switch (statusCode) {
            case 400 -> embed.addField(language.getTranslation("error-api-embed-field-1-name"), "`400: Bad request`", false);
            case 401 -> embed.addField(language.getTranslation("error-api-embed-field-1-name"), "`401: Unauthorized`", false);
            case 403 -> embed.addField(language.getTranslation("error-api-embed-field-1-name"), "`403: Forbidden`", false);
            case 404 -> embed.addField(language.getTranslation("error-api-embed-field-1-name"), "`404: Not found`", false);
            case 405 -> embed.addField(language.getTranslation("error-api-embed-field-1-name"), "`405: Method not allowed`", false);
            case 408 -> embed.addField(language.getTranslation("error-api-embed-field-1-name"), "`408: Timeout`", false);
            case 500 -> embed.addField(language.getTranslation("error-api-embed-field-1-name"), "`500: Internal server error`", false);
            case 502 -> embed.addField(language.getTranslation("error-api-embed-field-1-name"), "`502: Bad gateway`", false);
            case 503 -> embed.addField(language.getTranslation("error-api-embed-field-1-name"), "`503: Service unavailable`", false);
            case 504 -> embed.addField(language.getTranslation("error-api-embed-field-1-name"), "`504: Gateway timeout`", false);
            case 429 -> {
                embed.setTitle(language.getEmbedTitlePrefix() + language.getTranslation("error-ratelimit-embed-title"));
                embed.setDescription(language.getTranslation("error-ratelimit-embed-description"));
                embed.addField(language.getTranslation("error-api-embed-field-1-name"), "`429: Too many requests`", false);
            }
        }
        return embed;
    }

}
