package com.gruppe15.exceptions;

import com.gruppe15.model.Phase;
import com.gruppe15.model.player_interaction.PlayerInteraction;

public class UnhandledPhaseInteractionException extends Exception {
    /**
     * Constructs an {@code UnhandledPhaseInteractionException} with default message.
     */
    public UnhandledPhaseInteractionException(Phase phase, PlayerInteraction interaction) {
        super("Player interaction: \"" + interaction.toString() + "\" happened at unexpected phase: \"" + phase.toString() + "\". This should either be handled, by defining which callback method to call at this phase at GameController.continueActions(), or it should not be made possible for the interaction to happen in that phase.");
    }

    /**
     * Constructs an {@code UnhandledPhaseInteractionException} with a detail message.
     * @param s The detail message.
     */
    public UnhandledPhaseInteractionException(String s) {
        super(s);
    }
}