package gruppe15.roborally.model.upgrade_cards.permanent;

import gruppe15.roborally.GameVariables;
import gruppe15.roborally.controller.GameController;
import gruppe15.roborally.model.*;
import gruppe15.roborally.model.damage.Damage;
import gruppe15.roborally.model.events.PlayerLaserHitListener;
import gruppe15.roborally.model.upgrade_cards.UpgradeCardPermanent;

import java.util.ArrayList;
import java.util.List;

public class Card_MiniHowitzer extends UpgradeCardPermanent {
    private boolean activated = true;

    public Card_MiniHowitzer() {
        super("Mini Howitzer", 2, 1, 1, Phase.PLAYER_ACTIVATION, Phase.BOARD_ACTIVATION);
    }

    @Override
    public void initialize(Player owner, GameController gameController) {
        super.initialize(owner, gameController);

        // Defining effects on events

        // OnDamageDealt
        eventListeners.add(EventHandler.subscribe((PlayerLaserHitListener) (damage, playerTakingDamage) -> {
            if (owner != playerTakingDamage) {
                if (activated) {
                    activated = false;
                    if (GameVariables.SHOW_DEBUG_UPGRADE_CARD_USAGE) {
                        System.out.println("Player: \"" + owner.getName() + "\" used UpgradeCard: \"" + title + "\" to push and deal two more SPAM damage to player: \"" + playerTakingDamage + "\".");
                    }
                    // pushDirection calculated for compatibility with e.g. "REAR LASER" UpgradeCardPermanent.
                    List<Player> playerToPush = new ArrayList<>();
                    playerToPush.add(playerTakingDamage);
                    Heading pushDirection;
                    Space ownerSpace = owner.getSpace();
                    Space targetSpace = playerTakingDamage.getSpace();
                    if (ownerSpace.y > targetSpace.y) {
                        pushDirection = Heading.NORTH;
                    } else if (ownerSpace.y < targetSpace.y) {
                        pushDirection = Heading.SOUTH;
                    } else if (ownerSpace.x < targetSpace.x) {
                        pushDirection = Heading.EAST;
                    } else {
                        pushDirection = Heading.WEST;
                    }

                    EventHandler.event_PlayerPush(owner.board.getSpaces(), owner, playerToPush, pushDirection, gameController);
                    // Extra damage already added as owner temporaryBonusDamage
                }
            }
            return damage;
        }, owner));
    }

    @Override
    protected void onEnabled() {

    }

    @Override
    protected void onDisabled() {

    }

    @Override
    protected void onActivated() {
        super.onActivated();
        owner.addTemporaryBonusDamage(new Damage(2, 0, 0, 0));
        activated = true;
    }
}
