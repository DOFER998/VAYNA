package dev.piste.vayna.translations;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Piste | https://github.com/PisteDev
 */
public class Language {

    private static final Gson GSON = new Gson();

    private final Map<String, String> translations;

    /**
     * Creates a new language object from the given locale.
     *
     * @param locale the locale to load the language from
     */
    protected Language(String locale) {
        Map<String, String> loadedTranslations;
        try {
            String json = Files.readString(Paths.get("translations", locale + ".json"));
            loadedTranslations = GSON.fromJson(json, new TypeToken<HashMap<String, String>>(){}.getType());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.translations = Collections.unmodifiableMap(loadedTranslations);
    }

    /**
     * Retrieves the translation for the given key.
     *
     * @param key the key to retrieve the translation for
     * @return the translation for the given key, or "No translation found" if the key is not found
     */
    public String getTranslation(String key) {
        return translations.getOrDefault(key, "No translation found");
    }

    /**
     * Retrieves the language code for this language.
     *
     * @return the language code
     */
    public String getLocale() {
        return translations.get("locale");
    }

    /**
     * Retrieves the name of this language.
     *
     * @return the language name
     */
    public String getName() {
        return translations.get("name");
    }

    /**
     * Retrieves the emoji for this country.
     *
     * @return the country emoji
     */
    public String getEmoji() {
        return translations.get("emoji");
    }

    /**
     * Retrieves the prefix to use for embed titles in this language.
     *
     * @return the embed title prefix
     */
    public String getEmbedTitlePrefix() {
        return translations.get("embed-title-prefix");
    }

}