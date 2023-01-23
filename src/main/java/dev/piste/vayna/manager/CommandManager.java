package dev.piste.vayna.manager;

import dev.piste.vayna.Bot;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CommandManager {

    public static void createCommands() {

        List<String> commandList = new ArrayList<>();
        commandList.add("help");
        commandList.add("connection");
        commandList.add("stats");

        for(Command command : Bot.getJDA().retrieveCommands().complete()) {
            if(!commandList.contains(command.getName())) {
                command.delete().queue();
            }
        }

        for(String commandName : commandList) {
            createCommand(commandName);
        }

    }

    private static void createCommand(String commandName) {
        switch (commandName) {
            case "help":
                Bot.getJDA().upsertCommand("help", "Get information about the bot and a list of all available commands").queue();
                break;
            case "connection":
                Bot.getJDA().upsertCommand("connection", "Manage the connection to your Riot account").queue();
                break;
            case "stats":
                SubcommandData userSub = new SubcommandData("user", "Get general information about a VALORANT profile from a Discord user")
                        .addOption(OptionType.USER, "user", "The discord user to get the stats from");
                SubcommandData riotIdSub = new SubcommandData("riotid", "Get general information about a VALORANT profile by providing a Riot-ID")
                        .addOption(OptionType.STRING, "name", "The name of the Riot-ID (<name>#<tag>)", true)
                        .addOption(OptionType.STRING, "tag", "The tag of the Riot-ID (<name>#<tag>)", true);
                Bot.getJDA().upsertCommand("stats", "Stats").addSubcommands(userSub, riotIdSub).queue();
        }
    }

    public static Command findCommand(String query) {
        for(Command command : Bot.getJDA().retrieveCommands().complete()) {
            if(command.getName().equalsIgnoreCase(query)) return Objects.requireNonNull(command);
        }
        return null;
    }

    public static Command.Subcommand findSubcommand(Command command, String query) {
        for(Command.Subcommand subcommand : command.getSubcommands()) {
            if(subcommand.getName().equalsIgnoreCase(query)) return Objects.requireNonNull(subcommand);
        }
        return null;
    }



}
