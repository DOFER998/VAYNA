package dev.piste.vayna.api.henrik;

import com.fasterxml.jackson.databind.JsonNode;
import dev.piste.vayna.api.HttpRequest;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class HenrikAccount {

    private final String puuid;
    private final int level;
    private final String playerCardSmall;
    private final String playerCardLarge;
    private final String playerCardWide;

    public HenrikAccount(String gameName, String tagLine) {
        JsonNode accountNode = HttpRequest.doHenrikApiRequest("https://api.henrikdev.xyz/valorant/v1/account/" + URLEncoder.encode(gameName, StandardCharsets.UTF_8) + "/" + URLEncoder.encode(tagLine, StandardCharsets.UTF_8) + "?force=true");
        JsonNode dataNode = accountNode.path("data");
        JsonNode cardNode = dataNode.path("card");

        puuid = dataNode.get("puuid").asText();
        level = dataNode.get("account_level").asInt();
        playerCardSmall = cardNode.get("small").asText();
        playerCardLarge = cardNode.get("large").asText();
        playerCardWide = cardNode.get("wide").asText();
    }

    public String getPuuid() {
        return puuid;
    }

    public long getLevel() {
        return level;
    }

    public String getPlayerCardSmall() {
        return playerCardSmall;
    }

    public String getPlayerCardLarge() {
        return playerCardLarge;
    }

    public String getPlayerCardWide() {
        return playerCardWide;
    }

}
