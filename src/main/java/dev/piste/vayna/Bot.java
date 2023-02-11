package dev.piste.vayna;

import dev.piste.vayna.config.ConfigManager;
import dev.piste.vayna.listener.*;
import dev.piste.vayna.manager.*;
import dev.piste.vayna.mongodb.Mongo;
import dev.piste.vayna.util.ConsoleColor;
import dev.piste.vayna.util.translations.LanguageManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Bot {

    private static JDA jda;

    public static boolean isDebug() {
        return System.getProperty("os.name").startsWith("Windows");
    }

    public static String getConsolePrefix(String name) {
        return ConsoleColor.WHITE + "[" + ConsoleColor.PURPLE + name + ConsoleColor.WHITE + "]" + ConsoleColor.RESET + " ";
    }

    public static void main(String[] args) {
        ConfigManager.loadConfigs();
        Mongo.connect();
        LanguageManager.loadLanguages();

        jda = JDABuilder.createDefault(isDebug() ? ConfigManager.getTokensConfig().getBot().getDevelopment() : ConfigManager.getTokensConfig().getBot().getVayna())
                .addEventListeners(new CommandInteractionListeners())
                .addEventListeners(new GuildJoinLeaveListener())
                .setActivity(Activity.competing("VALORANT"))
                .setStatus(OnlineStatus.ONLINE)
                .setChunkingFilter(ChunkingFilter.ALL)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .build();

        CommandManager.registerCommands();
        ButtonManager.registerButtons();
        ModalManager.registerModals();
        StringSelectMenuManager.registerStringSelectMenus();
        UserContextCommandManager.registerStringSelectMenus();

        new Bot().listenShutdown();

        try {
            jda.awaitReady();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(getConsolePrefix("Discord") + ConsoleColor.GREEN + "Connected" + ConsoleColor.RESET);
    }

    public static JDA getJDA() {
        return jda;
    }

    private void listenShutdown() {
        new Thread(() -> {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            try {
                while (reader.readLine() != null) {
                    if (reader.readLine().equalsIgnoreCase("exit")) {
                        if (jda != null) {
                            jda.shutdown();
                            System.out.println(getConsolePrefix("VAYNA") + ConsoleColor.GREEN + "Stopped" + ConsoleColor.RESET);
                        }
                        reader.close();
                    } else {
                        System.out.println(getConsolePrefix("VAYNA") + ConsoleColor.YELLOW + "Type 'exit' to stop the bot." + ConsoleColor.RESET);
                    }
                }
            } catch (IOException ignored) {
            }
        }).start();
    }

}
