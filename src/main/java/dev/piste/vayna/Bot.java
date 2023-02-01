package dev.piste.vayna;

import dev.piste.vayna.config.Configs;
import dev.piste.vayna.config.tokens.TokensConfig;
import dev.piste.vayna.listener.ButtonInteractionListener;
import dev.piste.vayna.listener.GuildJoinLeaveListener;
import dev.piste.vayna.listener.ModalInteractionListener;
import dev.piste.vayna.listener.SlashCommandListener;
import dev.piste.vayna.manager.ButtonManager;
import dev.piste.vayna.manager.CommandManager;
import dev.piste.vayna.manager.ModalManager;
import dev.piste.vayna.mongodb.Mongo;
import dev.piste.vayna.util.FontColor;
import dev.piste.vayna.util.TranslationManager;
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
        return FontColor.WHITE + "[" + FontColor.PURPLE + name + FontColor.WHITE + "]" + FontColor.RESET + " ";
    }

    public static void main(String[] args) {
        Mongo.connect();

        TokensConfig tokensConfig = Configs.getTokens();

        jda = JDABuilder.createDefault(isDebug() ? tokensConfig.getBot().getDevelopment() : tokensConfig.getBot().getVayna())
                .addEventListeners(new SlashCommandListener())
                .addEventListeners(new GuildJoinLeaveListener())
                .addEventListeners(new ButtonInteractionListener())
                .addEventListeners(new ModalInteractionListener())
                .setActivity(Activity.competing("VALORANT"))
                .setStatus(OnlineStatus.ONLINE)
                .setChunkingFilter(ChunkingFilter.ALL)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .enableIntents(GatewayIntent.GUILD_PRESENCES)
                .build();

        CommandManager.registerCommands();
        ButtonManager.registerButtons();
        ModalManager.registerModals();

        System.out.println(getConsolePrefix("Discord") + FontColor.GREEN + "Connected" + FontColor.RESET);

        new Bot().listenShutdown();
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
                            System.out.println(getConsolePrefix("VAYNA") + FontColor.GREEN + "Stopped" + FontColor.RESET);
                        }
                        reader.close();
                    } else {
                        System.out.println(getConsolePrefix("VAYNA") + FontColor.YELLOW + "Type 'exit' to stop the bot." + FontColor.RESET);
                    }
                }
            } catch (IOException ignored) {
            }
        }).start();
    }

}
