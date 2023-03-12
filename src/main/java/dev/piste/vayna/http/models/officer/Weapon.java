package dev.piste.vayna.http.models.officer;

import com.google.gson.annotations.SerializedName;
import dev.piste.vayna.http.HttpErrorException;
import dev.piste.vayna.http.apis.OfficerAPI;
import dev.piste.vayna.translations.Language;

import java.io.IOException;
import java.util.List;

/**
 * @author Piste | https://github.com/PisteDev
 */
@SuppressWarnings("unused")
public class Weapon {

    @SerializedName("uuid")
    private String id;
    @SerializedName("displayName")
    private String displayName;
    @SerializedName("category")
    private String category;
    @SerializedName("defaultSkinUuid")
    private String defaultSkinId;
    @SerializedName("displayIcon")
    private String displayIcon;
    @SerializedName("killStreamIcon")
    private String killStreamIcon;
    @SerializedName("assetPath")
    private String assetPath;
    @SerializedName("weaponStats")
    private Stats stats;
    @SerializedName("shopData")
    private ShopData shopData;
    @SerializedName("skins")
    private List<Skin> skins;

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getCategory() {
        return category;
    }

    public Skin getDefaultSkin() {
        for(Skin skin : getSkins()) {
            if(skin.getId().equals(defaultSkinId)) {
                return skin;
            }
        }
        return null;
    }

    public String getDisplayIcon() {
        return displayIcon;
    }

    public String getKillStreamIcon() {
        return killStreamIcon;
    }

    public String getAssetPath() {
        return assetPath;
    }

    public Stats getStats() {
        return stats;
    }

    public ShopData getShopData() {
        return shopData;
    }

    public List<Skin> getSkins() {
        return skins;
    }

    public static class Stats {

        @SerializedName("fireRate")
        private double fireRate;
        @SerializedName("magazineSize")
        private int magazineSize;
        @SerializedName("runSpeedMultiplier")
        private double runSpeedMultiplier;
        @SerializedName("equipTimeSeconds")
        private double equipTimeSeconds;
        @SerializedName("reloadTimeSeconds")
        private double reloadTimeSeconds;
        @SerializedName("firstBulletAccuracy")
        private double firstBulletAccuracy;
        @SerializedName("shotgunPelletCount")
        private int shotgunPelletCount;
        @SerializedName("wallPenetration")
        private String wallPenetration;
        @SerializedName("feature")
        private String feature;
        @SerializedName("fireMode")
        private String fireMode;
        @SerializedName("altFireType")
        private String altFireType;
        @SerializedName("adsStats")
        private ADSStats adsStats;
        @SerializedName("altShotgunStats")
        private AltShotgunStats altShotgunStats;
        @SerializedName("airBurstStats")
        private AirBurstStats airBurstStats;
        @SerializedName("damageRanges")
        private List<DamageRange> damageRanges;

        public double getFireRate() {
            return fireRate;
        }

        public int getMagazineSize() {
            return magazineSize;
        }

        public double getRunSpeedMultiplier() {
            return runSpeedMultiplier;
        }

        public double getEquipTimeSeconds() {
            return equipTimeSeconds;
        }

        public double getReloadTimeSeconds() {
            return reloadTimeSeconds;
        }

        public double getFirstBulletAccuracy() {
            return firstBulletAccuracy;
        }

        public int getShotgunPelletCount() {
            return shotgunPelletCount;
        }

        public String getWallPenetration() {
            return wallPenetration;
        }

        public String getFeature() {
            return feature;
        }

        public String getFireMode() {
            return fireMode;
        }

        public String getAltFireType() {
            return altFireType;
        }

        public ADSStats getAdsStats() {
            return adsStats;
        }

        public AltShotgunStats getAltShotgunStats() {
            return altShotgunStats;
        }

        public AirBurstStats getAirBurstStats() {
            return airBurstStats;
        }

        public List<DamageRange> getDamageRanges() {
            return damageRanges;
        }

        public static class ADSStats {

            @SerializedName("zoomMultiplier")
            private double zoomMultiplier;
            @SerializedName("fireRate")
            private double fireRate;
            @SerializedName("runSpeedMultiplier")
            private double runSpeedMultiplier;
            @SerializedName("burstCount")
            private int burstCount;
            @SerializedName("firstBulletAccuracy")
            private double firstBulletAccuracy;

            public double getZoomMultiplier() {
                return zoomMultiplier;
            }

            public double getFireRate() {
                return fireRate;
            }

            public double getRunSpeedMultiplier() {
                return runSpeedMultiplier;
            }

            public int getBurstCount() {
                return burstCount;
            }

            public double getFirstBulletAccuracy() {
                return firstBulletAccuracy;
            }

        }

        public static class AltShotgunStats {

            @SerializedName("shotgunPelletCount")
            private int shotgunPelletCount;
            @SerializedName("burstRate")
            private double burstRate;

            public int getShotgunPelletCount() {
                return shotgunPelletCount;
            }

            public double getBurstRate() {
                return burstRate;
            }

        }

        public static class AirBurstStats {

            @SerializedName("shotgunPelletCount")
            private int shotgunPelletCount;
            @SerializedName("burstDistance")
            private double burstDistance;

            public int getShotgunPelletCount() {
                return shotgunPelletCount;
            }

            public double getBurstDistance() {
                return burstDistance;
            }

        }

        public static class DamageRange {

            @SerializedName("rangeStartMeters")
            private int rangeStartMeters;
            @SerializedName("rangeEndMeters")
            private int rangeEndMeters;
            @SerializedName("headDamage")
            private int headDamageCount;
            @SerializedName("bodyDamage")
            private int bodyDamageCount;
            @SerializedName("legDamage")
            private int legDamageCount;

            public int getRangeStartMeters() {
                return rangeStartMeters;
            }

            public int getRangeEndMeters() {
                return rangeEndMeters;
            }

            public int getHeadDamageCount() {
                return headDamageCount;
            }

            public int getBodyDamageCount() {
                return bodyDamageCount;
            }

            public int getLegDamageCount() {
                return legDamageCount;
            }

        }

    }

    public static class Skin {

        @SerializedName("uuid")
        private String id;
        @SerializedName("displayName")
        private String displayName;
        @SerializedName("themeUuid")
        private String themeId;
        @SerializedName("contentTierUuid")
        private String contentTierId;
        @SerializedName("displayIcon")
        private String displayIcon;
        @SerializedName("wallpaper")
        private String wallpaper;
        @SerializedName("assetPath")
        private String assetPath;
        @SerializedName("chromas")
        private List<Chroma> chromas;
        @SerializedName("levels")
        private List<Level> levels;

        public String getId() {
            return id;
        }

        public String getDisplayName() {
            return displayName;
        }

        public Theme getTheme(Language language) throws IOException, HttpErrorException, InterruptedException {
            return new OfficerAPI().getTheme(themeId.toLowerCase(), language);
        }

        public String getContentTierId() {
            return contentTierId;
        }

        public String getDisplayIcon() {
            return displayIcon;
        }

        public String getWallpaper() {
            return wallpaper;
        }

        public String getAssetPath() {
            return assetPath;
        }

        public List<Chroma> getChromas() {
            return chromas;
        }

        public List<Level> getLevels() {
            return levels;
        }

        public static class Chroma {

            @SerializedName("uuid")
            private String id;
            @SerializedName("displayName")
            private String displayName;
            @SerializedName("displayIcon")
            private String displayIcon;
            @SerializedName("fullRender")
            private String fullRender;
            @SerializedName("swatch")
            private String swatch;
            @SerializedName("streamedVideo")
            private String video;
            @SerializedName("assetPath")
            private String assetPath;

            public String getId() {
                return id;
            }

            public String getDisplayName() {
                return displayName;
            }

            public String getDisplayIcon() {
                return displayIcon;
            }

            public String getFullRender() {
                return fullRender;
            }

            public String getSwatch() {
                return swatch;
            }

            public String getVideo() {
                return video;
            }

            public String getAssetPath() {
                return assetPath;
            }

        }

        public static class Level {

            @SerializedName("uuid")
            private String id;
            @SerializedName("displayName")
            private String displayName;
            @SerializedName("levelItem")
            private String levelItem;
            @SerializedName("displayIcon")
            private String displayIcon;
            @SerializedName("streamedVideo")
            private String video;
            @SerializedName("assetPath")
            private String assetPath;

            public String getId() {
                return id;
            }

            public String getDisplayName() {
                return displayName;
            }

            public String getLevelItem() {
                return levelItem;
            }

            public String getDisplayIcon() {
                return displayIcon;
            }

            public String getVideo() {
                return video;
            }

            public String getAssetPath() {
                return assetPath;
            }

        }

    }

}