package dev.piste.vayna.mongodb;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import dev.piste.vayna.Bot;
import dev.piste.vayna.config.ConfigManager;
import dev.piste.vayna.config.tokens.MongoDbConfig;
import dev.piste.vayna.util.ConsoleColor;
import org.bson.Document;

import java.util.Collections;

public class Mongo {

    private static MongoDatabase mongoDatabase;

    public static void connect() {

        MongoDbConfig mongoDbConfig = ConfigManager.getTokensConfig().getMongodb();

        MongoCredential credential = MongoCredential.createCredential(mongoDbConfig.getUsername(), mongoDbConfig.getAuthDb(), mongoDbConfig.getPassword().toCharArray());
        MongoClientSettings settings =
                MongoClientSettings.builder().credential(credential).applyToClusterSettings(builder ->
                        builder.hosts(Collections.singletonList(new ServerAddress(mongoDbConfig.getHost(), mongoDbConfig.getPort())))).build();

        MongoClient mongoClient = MongoClients.create(settings);
        mongoDatabase = mongoClient.getDatabase("VAYNA");

        System.out.println(Bot.getConsolePrefix("MongoDB") + ConsoleColor.GREEN + "Connected" + ConsoleColor.RESET);
    }

    public static MongoDatabase getMongoDatabase() {
        return mongoDatabase;
    }

    public static MongoCollection<Document> getLinkedAccountCollection() {
        return getMongoDatabase().getCollection("linked-accounts");
    }

    public static MongoCollection<Document> getAuthKeyCollection() {
        return getMongoDatabase().getCollection("auth-keys");
    }

    public static MongoCollection<Document> getGuildSettingsCollection() {
        return getMongoDatabase().getCollection("guild-settings");
    }


}
