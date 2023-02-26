package dev.piste.vayna.apis.officer.gson;

/**
 * @author Piste | https://github.com/PisteDev
 */
@SuppressWarnings("ALL")
public class Gamemode {

    private String uuid;
    private String displayName;
    private String duration;
    private String displayIcon;

    public String getUuid() {
        return uuid;
    }

    public String getDisplayIcon() {
        return displayIcon;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDuration() {
        return duration;
    }

}
