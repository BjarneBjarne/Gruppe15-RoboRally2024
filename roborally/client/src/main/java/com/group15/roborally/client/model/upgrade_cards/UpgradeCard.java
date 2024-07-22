package com.group15.roborally.client.model.upgrade_cards;

import com.group15.roborally.client.ApplicationSettings;
import com.group15.roborally.client.controller.GameController;
import com.group15.roborally.client.model.*;
import com.group15.roborally.client.model.Player;
import com.group15.roborally.client.model.upgrade_cards.permanent.*;
import com.group15.roborally.client.model.upgrade_cards.temporary.*;
import com.group15.roborally.common.model.GamePhase;
import com.group15.roborally.common.observer.Observer;
import com.group15.roborally.common.observer.Subject;
import lombok.Getter;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * This is the superclass for any upgrade card.
 * Players will initialize the upgrade cards with themselves as owner, whenever they purchase a card.
 * This way, any player will invoke events, but the card will only trigger, if the player invoking the event, is also the initializing owner of the card.
 */
public abstract class UpgradeCard extends Card implements Observer {
    protected final String title;
    @Getter
    protected final int purchaseCost;

    // Uses handling
    protected final int useCost;
    protected final int maxUses;
    protected int currentUses;
    protected final GamePhase refreshedOn;

    // Activating
    @Getter
    protected final List<GamePhase> activatableOn;
    protected final boolean onlyActivatableOnPlayerTurn;
    private boolean enabled = false;
    private boolean queuedForActivation = false;

    protected Player owner;
    transient protected GameController gameController;

    /**
     * Constructor for any Upgrade Card.
     * @param title The name of the card.
     * @param purchaseCost The amount of energy cubes it costs to purchase the card.
     * @param useCost The amount of energy cubes it costs to use the card.
     * @param maxUses The uses the upgrade card has, before it needs to be refreshed. Temporary cards don't refresh.
     * @param refreshedOn The GamePhase that the card is refreshed.
     * @param activatableOn Leave as NULL if the card can't be activated. If it can, it will have a "Use"-button that can be clicked to activate the card on the activatableOn phase(s).
     */
    public UpgradeCard(String title, int purchaseCost, int useCost, int maxUses, GamePhase refreshedOn, boolean onlyActivatableOnPlayerTurn, GamePhase... activatableOn) {
        this.title = title;
        this.purchaseCost = purchaseCost;
        this.useCost = useCost;
        this.maxUses = maxUses;
        this.refreshedOn = refreshedOn;
        this.onlyActivatableOnPlayerTurn = onlyActivatableOnPlayerTurn;
        this.activatableOn = Collections.unmodifiableList(Arrays.asList(activatableOn));
    }

    private GamePhase lastPhase = null;
    @Override
    public void update(Subject subject) {
        if (subject.equals(owner.board)) {
            GamePhase currentPhase = owner.board.getCurrentPhase();
            if (currentPhase != lastPhase) {
                lastPhase = currentPhase;
                if (currentPhase.equals(refreshedOn)) {
                    refresh();
                }
                // Enables and disables on corresponding phases
                setEnabled(activatableOn.contains(currentPhase));
                //System.out.println("GamePhase: " + owner.board.getCurrentPhase() + ". Handling pre phase?: " + gameController.isHandlingPrePhase() + ". Enabled?: " + enabled + ". Activatable?: " + canBeActivated() + ". gameController.canUseUpgradeCards?:" + gameController.canUseUpgradeCards());
            }
        }
    }

    public enum Types {
        // Permanent upgrade cards
        ADMIN_PRIVILEGE(Card_AdminPrivilege.class),
        BLUE_SCREEN_OF_DEATH(Card_BlueScreenOfDeath.class),
        BRAKES(Card_Brakes.class),
        CRAB_LEGS(Card_CrabLegs.class),
        DEFLECTOR_SHIELD(Card_DeflectorShield.class),
        DOUBLE_BARREL_LASER(Card_DoubleBarrelLaser.class),
        FIREWALL(Card_Firewall.class),
        HOVER_UNIT(Card_HoverUnit.class),
        MEMORY_STICK(Card_MemoryStick.class),
        MINI_HOWITZER(Card_MiniHowitzer.class),
        PRESSOR_BEAM(Card_PressorBeam.class),
        RAIL_GUN(Card_RailGun.class),
        RAMMING_GEAR(Card_RammingGear.class),
        REAR_LASER(Card_RearLaser.class),
        //SCRAMBLER(Card_Scrambler.class),
        TRACTOR_BEAM(Card_TractorBeam.class),
        TROJAN_NEEDLER(Card_TrojanNeedler.class),
        VIRUS_MODULE(Card_VirusModule.class),

        // Temporary upgrade cards
        ENERGY_ROUTINE(Card_EnergyRoutine.class),
        HACK(Card_Hack.class),
        MANUAL_SORT(Card_ManualSort.class),
        REBOOT(Card_Reboot.class),
        RECHARGE(Card_Recharge.class),
        RECOMPILE(Card_Recompile.class),
        REPEAT_ROUTINE(Card_RepeatRoutine.class),
        SANDBOX_ROUTINE(Card_SandboxRoutine.class),
        SPAM_BLOCKER(Card_SpamBlocker.class),
        SPAM_FOLDER_ROUTINE(Card_SpamFolderRoutine.class),
        SPEED_ROUTINE(Card_SpeedRoutine.class),
        WEASEL_ROUTINE(Card_WeaselRoutine.class);

        public final Class<? extends UpgradeCard> upgradeCardClass;
        Types(Class<? extends UpgradeCard> upgradeCardClass) {
            this.upgradeCardClass = upgradeCardClass;
        }
    }

    protected abstract void onEnabled();
    protected abstract void onDisabled();
    protected abstract void onActivated();

    public void activate() {
        if (!getHasActive()) {
            System.err.println("Trying to activate upgrade card + \"" + title + "\" which is not activatable");
            return;
        }

        queuedForActivation = false;
        currentUses--;
        owner.setEnergyCubes(owner.getEnergyCubes() - useCost);
        printUsage();
        onActivated();
    }

    /**
     * Cards must override this method.
     * Initializes the card to respond to actions performed by the owner.
     */
    public void initialize(Player owner, GameController gameController) {
        this.owner = owner;
        this.gameController = gameController;
        owner.board.attach(this);
        refresh();
    }

    public void unInitialize() {
        this.owner.board.detach(this);
        this.owner = null;
    }

    protected void refresh() {
        currentUses = maxUses;
    }

    @Override
    public String getDisplayName() {
        return title;
    }

    public void setEnabled(boolean enable) {
        this.enabled = enable;
        if (this.enabled)
            this.onEnabled();
        else
            this.onDisabled();
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
            queuedForActivation = true;
            gameController.tryUseUpgradeCard(this);
        }
    }
    public boolean canBeActivated() {
        return enabled && !isOnCooldown() && owner.getEnergyCubes() >= useCost && gameController.canUseUpgradeCards() && !queuedForActivation && owner.equals(owner.board.getCurrentPlayer());
    }

    public boolean getHasActive() {
        return activatableOn != null && !activatableOn.isEmpty();
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
        }
        return newUpgradeCard;
    }

    public Types getEnum() {
        String enumName = title.toUpperCase().replace(' ', '_');;
        return Types.valueOf(enumName);
    }
}
