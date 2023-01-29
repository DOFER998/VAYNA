package dev.piste.vayna.apis.henrik;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import dev.piste.vayna.apis.HttpRequest;
import dev.piste.vayna.apis.henrik.gson.CurrentBundle;
import dev.piste.vayna.apis.henrik.gson.HenrikAccount;
import dev.piste.vayna.apis.henrik.gson.MMRHistory;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class HenrikAPI {

    public static HenrikAccount getAccountByRiotId(String gameName, String tagLine) throws HenrikApiException {
        JsonObject jsonObject = new HttpRequest().doHenrikApiRequest("https://api.henrikdev.xyz/valorant/v1/account/" + URLEncoder.encode(gameName, StandardCharsets.UTF_8) + "/" + URLEncoder.encode(tagLine, StandardCharsets.UTF_8) + "?force=true");
        JsonObject dataObject = jsonObject.getAsJsonObject("data");
        if(dataObject == null) throw new HenrikApiException();
        return new Gson().fromJson(dataObject, HenrikAccount.class);
    }

    public static List<CurrentBundle> getCurrentBundles() {
        JsonObject jsonObject = new HttpRequest().doHenrikApiRequest("https://api.henrikdev.xyz/valorant/v2/store-featured");
        JsonArray dataArray = jsonObject.getAsJsonArray("data");
        return new Gson().fromJson(dataArray, new TypeToken<List<CurrentBundle>>(){}.getType());
    }

    public static List<MMRHistory> getMmrHistory(String puuid, String region) {
        JsonObject jsonObject = new HttpRequest().doHenrikApiRequest("https://api.henrikdev.xyz/valorant/v1/by-puuid/mmr-history/" + region + "/" + puuid);
        JsonArray dataArray = jsonObject.getAsJsonArray("data");
        return new Gson().fromJson(dataArray, new TypeToken<List<MMRHistory>>(){}.getType());
    }



}
