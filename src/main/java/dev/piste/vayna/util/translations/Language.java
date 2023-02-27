package dev.piste.vayna.util.translations;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dev.piste.vayna.Bot;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;

/**
 * @author Piste | https://github.com/PisteDev
 */
public class Language {

    private final HashMap<String, String> translations;

    public Language(String languageCode) {
        try {
            translations = new Gson().fromJson(new FileReader(Bot.isDebug() ? "C:/Users/nilsr/Development/VAYNA/DiscordBot/translations/" + languageCode + ".json" : "translations/" + languageCode + ".json"), new TypeToken<HashMap<String, String>>(){}.getType());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public String getTranslation(String key) {
        if(!translations.containsKey(key)) {
            return "Translation not found";
        }
        return translations.get(key);
    }

    public String getLanguageCode() {
        return translations.get("language-code");
    }

    public String getEmbedTitlePrefix() {
        return translations.get("embed-title-prefix");
    }

}
