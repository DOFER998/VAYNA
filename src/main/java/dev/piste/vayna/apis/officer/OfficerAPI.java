package dev.piste.vayna.apis.officer;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import dev.piste.vayna.apis.ApiHttpRequest;
import dev.piste.vayna.apis.StatusCodeException;
import dev.piste.vayna.apis.officer.gson.*;

import java.net.URI;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.util.ArrayList;

public class OfficerAPI {

    // Agents
    public static Agent getAgent(String uuid, String languageCode) throws StatusCodeException {
        JsonObject jsonObject = performHttpRequest("https://valorant-api.com/v1/agents/" + uuid + "?language=" + languageCode);
        JsonObject dataObject = jsonObject.getAsJsonObject("data");
        return new Gson().fromJson(dataObject, Agent.class);
    }

    public static ArrayList<Agent> getAgents(String languageCode) throws StatusCodeException {
        JsonObject jsonObject = performHttpRequest("https://valorant-api.com/v1/agents?isPlayableCharacter=true&language=" + languageCode);
        JsonArray dataArray = jsonObject.getAsJsonArray("data");
        return new Gson().fromJson(dataArray, new TypeToken<ArrayList<Agent>>(){}.getType());
    }

    // Gamemodes
    public static Gamemode getGamemode(String uuid, String languageCode) throws StatusCodeException {
        JsonObject jsonObject = performHttpRequest("https://valorant-api.com/v1/gamemodes/" + uuid + "?language=" + languageCode);
        JsonObject dataObject = jsonObject.getAsJsonObject("data");
        return new Gson().fromJson(dataObject, Gamemode.class);
    }

    // Queues
    public static Queue getQueue(String uuid, String languageCode) throws StatusCodeException {
        JsonObject jsonObject = performHttpRequest("https://valorant-api.com/v1/gamemodes/queues/" + uuid + "?language=" + languageCode);
        JsonObject dataObject = jsonObject.getAsJsonObject("data");
        return new Gson().fromJson(dataObject, Queue.class);
    }

    public static ArrayList<Queue> getQueues(String languageCode) throws StatusCodeException {
        JsonObject jsonObject = performHttpRequest("https://valorant-api.com/v1/gamemodes/queues?language=" + languageCode);
        JsonArray dataArray = jsonObject.getAsJsonArray("data");
        return new Gson().fromJson(dataArray, new TypeToken<ArrayList<Queue>>(){}.getType());
    }

    // Maps
    public static Map getMap(String uuid, String languageCode) throws StatusCodeException {
        JsonObject jsonObject = performHttpRequest("https://valorant-api.com/v1/maps/" + uuid + "?language=" + languageCode);
        JsonObject dataObject = jsonObject.getAsJsonObject("data");
        return new Gson().fromJson(dataObject, Map.class);
    }

    public static ArrayList<Map> getMaps(String languageCode) throws StatusCodeException {
        JsonObject jsonObject = performHttpRequest("https://valorant-api.com/v1/maps?language=" + languageCode);
        JsonArray dataArray = jsonObject.getAsJsonArray("data");
        return new Gson().fromJson(dataArray, new TypeToken<ArrayList<Map>>(){}.getType());
    }

    // Weapons
    public static Weapon getWeapon(String uuid, String languageCode) throws StatusCodeException {
        JsonObject jsonObject = performHttpRequest("https://valorant-api.com/v1/weapons/" + uuid + "?language=" + languageCode);
        JsonObject dataObject = jsonObject.getAsJsonObject("data");
        return new Gson().fromJson(dataObject, Weapon.class);
    }

    public static ArrayList<Weapon> getWeapons(String languageCode) throws StatusCodeException {
        JsonObject jsonObject = performHttpRequest("https://valorant-api.com/v1/weapons?language=" + languageCode);
        JsonArray dataArray = jsonObject.getAsJsonArray("data");
        return new Gson().fromJson(dataArray, new TypeToken<ArrayList<Weapon>>(){}.getType());
    }

    public static Buddy getBuddy(String uuid, String languageCode) throws StatusCodeException {
        JsonObject jsonObject = performHttpRequest("https://valorant-api.com/v1/buddies/" + uuid + "?language=" + languageCode);
        JsonObject dataObject = jsonObject.getAsJsonObject("data");
        return new Gson().fromJson(dataObject, Buddy.class);
    }

    public static Bundle getBundle(String uuid, String languageCode) throws StatusCodeException {
        JsonObject jsonObject = performHttpRequest("https://valorant-api.com/v1/bundles/" + uuid + "?language=" + languageCode);
        JsonObject dataObject = jsonObject.getAsJsonObject("data");
        return new Gson().fromJson(dataObject, Bundle.class);
    }

    public static Playercard getPlayercard(String uuid, String languageCode) throws StatusCodeException {
        JsonObject jsonObject = performHttpRequest("https://valorant-api.com/v1/playercards/" + uuid + "?language=" + languageCode);
        JsonObject dataObject = jsonObject.getAsJsonObject("data");
        return new Gson().fromJson(dataObject, Playercard.class);
    }

    public static Spray getSpray(String uuid, String languageCode) throws StatusCodeException {
        JsonObject jsonObject = performHttpRequest("https://valorant-api.com/v1/sprays/" + uuid + "?language=" + languageCode);
        JsonObject dataObject = jsonObject.getAsJsonObject("data");
        return new Gson().fromJson(dataObject, Spray.class);
    }

    public static CompetitiveTier getCompetitiveTier(String languageCode) throws StatusCodeException {
        JsonObject jsonObject = performHttpRequest("https://valorant-api.com/v1/competitivetiers/03621f52-342b-cf4e-4f86-9350a49c6d04?language=" + languageCode);
        JsonObject dataObject = jsonObject.getAsJsonObject("data");
        return new Gson().fromJson(dataObject, CompetitiveTier.class);
    }

    public static Skin getSkin(String uuid, String languageCode) throws StatusCodeException {
        JsonObject jsonObject = performHttpRequest("https://valorant-api.com/v1/weapons/skins/" + uuid + "?language=" + languageCode);
        JsonObject dataObject = jsonObject.getAsJsonObject("data");
        return new Gson().fromJson(dataObject, Skin.class);
    }

    private static JsonObject performHttpRequest(String uri) throws StatusCodeException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .timeout(Duration.ofSeconds(30))
                .header("Content-Type", "application/json")
                .GET()
                .build();
        return new ApiHttpRequest().performHttpRequest(request);
    }

}
