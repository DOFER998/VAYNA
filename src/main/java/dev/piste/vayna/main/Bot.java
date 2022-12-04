package dev.piste.vayna.main;

import dev.piste.vayna.json.JSONTokens;
import dev.piste.vayna.listener.GuildJoinLeaveListener;
import dev.piste.vayna.listener.MessageReceivedListener;
import dev.piste.vayna.listener.SlashCommandListener;
import dev.piste.vayna.mongodb.Mongo;
import dev.piste.vayna.util.TokenType;
import dev.piste.vayna.util.FontColor;
import dev.piste.vayna.util.Resources;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import javax.security.auth.login.LoginException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Bot {

    private static JDA jda;

    public static void main(String[] args) {
        try {
            new Bot();
        } catch (LoginException | IllegalArgumentException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Bot() throws LoginException, IllegalArgumentException, InterruptedException {

        Mongo.connect();
        TokenType tokenType;
        if(System.getProperty("os.name").equalsIgnoreCase("Windows 10")) {
            tokenType = TokenType.DEVELOPMENT;
        } else {
            tokenType = TokenType.PUBLIC;
        }
        String token = (String) JSONTokens.readToken(tokenType);


        jda = JDABuilder.createDefault(token)
                .addEventListeners(new SlashCommandListener())
                .addEventListeners(new MessageReceivedListener())
                .addEventListeners(new GuildJoinLeaveListener())
                .setActivity(Activity.competing("VALORANT"))
                .setStatus(OnlineStatus.ONLINE)
                .setChunkingFilter(ChunkingFilter.ALL)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .enableIntents(GatewayIntent.GUILD_PRESENCES)
                .build();

        createCommands();

        System.out.println(Resources.SYSTEM_PRINT_PREFIX + FontColor.GREEN + "Connected to " + FontColor.YELLOW + tokenType.toString() + FontColor.GREEN + " instance." + FontColor.RESET);

        shutdownListener();


    }

    public static JDA getJDA() {
        return jda;
    }

    public static String getVersion() {
        return "0.0.2";
    }

    public static String getWebsiteUrl() {
        return "https://piste.dev/VAYNA/";
    }

    private void shutdownListener() {
        new Thread(() -> {
            String line = "";
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            try {
                while((line = reader.readLine()) != null) {
                    if(line.equalsIgnoreCase("exit")) {
                        if(jda != null) {
                            jda.shutdown();
                            System.out.println(Resources.SYSTEM_PRINT_PREFIX + FontColor.GREEN + "The bot has successfully been stopped." + FontColor.RESET);
                        }
                        reader.close();
                    } else {
                        System.out.println(Resources.SYSTEM_PRINT_PREFIX + FontColor.YELLOW + "Type 'exit' to stop the bot." + FontColor.RESET);
                    }
                }
            } catch (IOException ignored) {}
        }).start();
    }

    private void createCommands() {

        jda.upsertCommand("help", "Get informations about the bot and a list of all commands").queue();

    }

}
