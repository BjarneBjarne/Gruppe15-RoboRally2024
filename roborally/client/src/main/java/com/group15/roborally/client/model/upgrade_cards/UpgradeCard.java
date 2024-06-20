package com.group15.roborally.client.model.upgrade_cards;

import com.group15.roborally.client.ApplicationSettings;
import com.group15.roborally.client.controller.GameController;
import com.group15.roborally.client.model.*;
import com.group15.roborally.client.model.Player;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * This is the superclass for any upgrade card.
 * Players will initialize the upgrade cards with themselves as owner, whenever they purchase a card.
 * This way, any player will invoke events, but the card will only trigger, if the player invoking the event, is also the initializing owner of the card.
 */
public abstract class UpgradeCard extends Card {
    protected final String title;
    protected final int purchaseCost;

    // Uses handling
    protected final int useCost;
    protected final int maxUses;
    protected int currentUses;
    protected final Phase refreshedOn;

    // Activating
    protected final List<Phase> activatableOn;
    private boolean enabled = false;

    protected Player owner;
    transient protected GameController gameController;

    /**
     * Constructor for any Upgrade Card.
     * @param title The name of the card.
     * @param purchaseCost The amount of energy cubes it costs to purchase the card.
     * @param useCost The amount of energy cubes it costs to use the card.
     * @param maxUses The uses the upgrade card has, before it needs to be refreshed. Temporary cards don't refresh.
     * @param refreshedOn The Phase that the card is refreshed.
     * @param activatableOn Leave as NULL if the card can't be activated. If it can, it will have a "Use"-button that can be clicked to activate the card on the activatableOn phase(s).
     */
    public UpgradeCard(String title, int purchaseCost, int useCost, int maxUses, Phase refreshedOn, Phase... activatableOn) {
        this.title = title;
        this.purchaseCost = purchaseCost;
        this.useCost = useCost;
        this.maxUses = maxUses;
        this.refreshedOn = refreshedOn;
        this.activatableOn = Collections.unmodifiableList(Arrays.asList(activatableOn));
    }

    protected abstract void onEnabled();
    protected abstract void onDisabled();
    protected abstract void onActivated();

    /**
     * Cards must override this method.
     * Initializes the card to respond to actions performed by the owner.
     */
    public void initialize(Player owner, GameController gameController) {
        this.owner = owner;
        this.gameController = gameController;
        owner.board.setOnPhaseChange(phase -> {
            if (phase == refreshedOn) {
                refresh();
            }
            // Enables and disables on corresponding phases
            setEnabled(activatableOn.contains(phase));
        });
        refresh();
    }

    public void unInitialize() {
        this.owner = null;
    }

    protected void refresh() {
        currentUses = maxUses;
    }

    @Override
    public String getName() {
        return title;
    }

    public void setEnabled(boolean enable) {
        this.enabled = enable;
        if (this.enabled)
            this.onEnabled();
        else
            this.onDisabled();
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public boolean isOnCooldown() {
        return currentUses == 0;
    }

    /**
     * Defines the behavior that should happen when manually activated by the player.
     * <br>
     * For permanent cards, this may include behavior that the player can choose when to use. (Maybe because activation has a cost or the card only can be used once per round, register, etc.)
     * <br>
     * For temporary cards, this typically includes most of their behavior.
     */
    public void tryActivate() {
        if (canBeActivated()) {
            this.currentUses--;
            this.onActivated();
            owner.setEnergyCubes(owner.getEnergyCubes() - useCost);
        }
    }

    public boolean canBeActivated() {
        return enabled && !isOnCooldown() && owner.getEnergyCubes() >= useCost;
    }

    public int getPurchaseCost() {
        return purchaseCost;
    }

    public boolean getHasActivateButton() {
        return activatableOn != null;
    }

    public void printUsage() {
        if (ApplicationSettings.DEBUG_SHOW_UPGRADE_CARD_USAGE) {
            System.out.println("Player: \"" + owner.getName() + "\" used UpgradeCard: \"" + title + "\".");
        }
    }

    /**
     * Method for creating and adding an instance of an UpgradeCard subclass to an UpgradeCard list.
     * @param upgradeCardClass The UpgradeCard subclass to make an instance of.
     * @return Returns a new instance on an UpgradeCard of argument type upgradeCardClass.
     */
    public static UpgradeCard getUpgradeCardFromClass(Class<? extends UpgradeCard> upgradeCardClass) {
        UpgradeCard newUpgradeCard = null;
        Constructor<? extends UpgradeCard> constructor;
        try {
            constructor = upgradeCardClass.getConstructor();
            newUpgradeCard = constructor.newInstance();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return newUpgradeCard;
    }
}
