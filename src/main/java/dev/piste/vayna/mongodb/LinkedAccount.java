package dev.piste.vayna.mongodb;

import com.mongodb.client.MongoCollection;
import org.bson.Document;

import static com.mongodb.client.model.Filters.eq;

public class LinkedAccount {

    private long discordUserId;
    private String riotPuuid;
    private boolean visibleToPublic;
    private static final MongoCollection<Document> linkedAccountCollection = Mongo.getLinkedAccountCollection();
    private final Document linkedAccount;
    private final boolean isExisting;

    public LinkedAccount(long discordUserId) {
        this.discordUserId = discordUserId;
        linkedAccount = linkedAccountCollection.find(eq("discordUserId", discordUserId)).first();
        if(linkedAccount == null) {
            isExisting = false;
        } else {
            isExisting = true;
            this.riotPuuid = (String) linkedAccount.get("riotPuuid");
            this.visibleToPublic = (boolean) linkedAccount.get("public");
        }
    }

    public LinkedAccount(String riotPuuid) {
        this.riotPuuid = riotPuuid;
        linkedAccount = linkedAccountCollection.find(eq("riotPuuid", riotPuuid)).first();
        if(linkedAccount == null) {
            isExisting = false;
        } else {
            isExisting = true;
            this.discordUserId = (long) linkedAccount.get("discordUserId");
            this.visibleToPublic = (boolean) linkedAccount.get("public");
        }
    }

    public boolean isExisting() {
        return isExisting;
    }

    public long getDiscordUserId() {
        if(isExisting) {
            return discordUserId;
        } else {
            return 0;
        }
    }

    public String getRiotPuuid() {
        if(isExisting) {
            return riotPuuid;
        } else {
            return null;
        }
    }

    public boolean isVisibleToPublic() {
        if(isExisting) {
            return visibleToPublic;
        } else {
            return true;
        }
    }

    public void delete() {
        if(isExisting) {
            linkedAccountCollection.deleteOne(linkedAccount);
        }
    }

    public static void insert(long discordUserId, String riotPuuid, boolean visibleToPublic) {
        Document newLinkedAccountDocument = new Document();
        newLinkedAccountDocument.put("discordUserId", discordUserId);
        newLinkedAccountDocument.put("riotPuuid", riotPuuid);
        newLinkedAccountDocument.put("public", visibleToPublic);
        linkedAccountCollection.insertOne(newLinkedAccountDocument);
    }

}
