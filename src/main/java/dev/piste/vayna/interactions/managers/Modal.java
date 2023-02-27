package dev.piste.vayna.interactions.managers;

import dev.piste.vayna.apis.HttpErrorException;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;

/**
 * @author Piste | https://github.com/PisteDev
 */
public interface Modal {

    void perform(ModalInteractionEvent event) throws HttpErrorException;

    String getName();

}
