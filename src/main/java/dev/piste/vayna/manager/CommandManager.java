package dev.piste.vayna.manager;

import dev.piste.vayna.Bot;
import dev.piste.vayna.apis.StatusCodeException;
import dev.piste.vayna.commands.*;
import dev.piste.vayna.config.Configs;
import dev.piste.vayna.util.Embed;
import dev.piste.vayna.util.Emoji;
import dev.piste.vayna.util.messages.ErrorMessages;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CommandManager {

    private static final Map<String, Command> commands = new HashMap<>();

    public static void registerCommands() {
        addCommand(new AgentCommand());
        addCommand(new ConnectionCommand());
        addCommand(new GamemodeCommand());
        addCommand(new HelpCommand());
        addCommand(new MapCommand());
        addCommand(new StatsCommand());
        addCommand(new WeaponCommand());
        addCommand(new StoreCommand());
        addCommand(new LeaderboardCommand());
        addCommand(new FeedbackCommand());

        for(net.dv8tion.jda.api.interactions.commands.Command command : Bot.getJDA().retrieveCommands().complete()) {
            if(!commands.containsKey(command.getName().toLowerCase())) {
                command.delete().queue();
            }
        }
    }

    private static void addCommand(Command command) {
        commands.put(command.getName().toLowerCase(), command);
        try {
            command.register();
        } catch (StatusCodeException e) {
            if(Bot.isDebug()) {
                System.out.println("Error registering command: " + e.getMessage());
                return;
            }
            TextChannel logChannel = Bot.getJDA().getGuildById(Configs.getSettings().getSupportGuild().getId()).getTextChannelById(Configs.getSettings().getLogChannels().getError());
            Embed embed = new Embed().setTitle("Register command HTTP error")
                    .setDescription(e.getMessage());
            logChannel.sendMessageEmbeds(embed.build()).queue();
        }
    }

    public static Collection<Command> getCommands() {
        return commands.values();
    }

    public static void perform(SlashCommandInteractionEvent event) {
        final String commandName = event.getName().toLowerCase();
        Thread thread = new Thread(() -> {
            try {
                commands.get(commandName).perform(event);
            } catch (StatusCodeException e) {
                Embed embed = ErrorMessages.getStatusCodeErrorMessage(event.getUser(), event.getGuild(), e);
                event.getHook().editOriginalEmbeds(embed.build()).setActionRow(
                        Button.link(Configs.getSettings().getSupportGuild().getInviteUri(), "Support").withEmoji(Emoji.getDiscord())
                ).queue();
                if(Bot.isDebug()) return;
                TextChannel logChannel = Bot.getJDA().getGuildById(Configs.getSettings().getSupportGuild().getId()).getTextChannelById(Configs.getSettings().getLogChannels().getError());
                embed.addField("URL", e.getMessage().split(" ")[1], false)
                        .setAuthor(event.getUser().getAsTag(), Configs.getSettings().getWebsiteUri(), event.getUser().getAvatarUrl())
                        .setDescription(" ");
                logChannel.sendMessageEmbeds(embed.build()).queue();
            }
        });
        thread.start();
    }

    public static net.dv8tion.jda.api.interactions.commands.Command getAsJdaCommand(Command command) {
        for(net.dv8tion.jda.api.interactions.commands.Command jdaCommand : Bot.getJDA().retrieveCommands().complete()) {
            if(jdaCommand.getName().equalsIgnoreCase(command.getName())) return jdaCommand;
        }
        return null;
    }


}
