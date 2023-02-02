package dev.piste.vayna.commands;

import dev.piste.vayna.Bot;
import dev.piste.vayna.manager.Command;
import dev.piste.vayna.modals.FeedbackModal;
import dev.piste.vayna.util.TranslationManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

/**
 * @author Piste | https://github.com/zPiste
 */
public class FeedbackCommand implements Command {

    @Override
    public void perform(SlashCommandInteractionEvent event) {
        TranslationManager translation = TranslationManager.getTranslation(event.getGuild());
        TextInput feedbackInput = TextInput.create("feedback", translation.getTranslation("command-feedback-modal-text-name"), TextInputStyle.PARAGRAPH)
                .setPlaceholder(translation.getTranslation("command-feedback-modal-text-placeholder"))
                .setMaxLength(4000)
                .build();
        Modal modal = Modal.create(new FeedbackModal().getName(), translation.getTranslation("command-feedback-modal-title"))
                .addActionRows(ActionRow.of(feedbackInput))
                .build();
        event.replyModal(modal).queue();
    }

    @Override
    public void register() {
        Bot.getJDA().upsertCommand(getName(), getDescription()).queue();
    }

    @Override
    public String getName() {
        return "feedback";
    }

    @Override
    public String getDescription() {
        return "Share feedback to the developer of VAYNA";
    }
}
