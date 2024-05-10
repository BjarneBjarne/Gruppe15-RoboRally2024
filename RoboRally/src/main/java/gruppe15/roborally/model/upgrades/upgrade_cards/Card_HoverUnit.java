package gruppe15.roborally.model.upgrades.upgrade_cards;

import gruppe15.roborally.model.*;
import gruppe15.roborally.model.boardelements.BE_Hole;
import gruppe15.roborally.model.events.PlayerMoveListener;
import gruppe15.roborally.model.upgrades.UpgradeCard;
import javafx.util.Pair;

public class Card_HoverUnit extends UpgradeCard {

    public Card_HoverUnit(String title, int purchaseCost, int useCost, int maxUses, Phase refreshedOn) {
        super(title, purchaseCost, useCost, maxUses, refreshedOn);
    }

    @Override
    public void initialize(Board board, Player owner) {
        super.initialize(board, owner);
        // Defining effects on events
        // OnDamageDealt
        EventHandler.onEvent((PlayerMoveListener) (space, shouldReboot) -> {
            if (space.getBoardElement() != null && space.getBoardElement() instanceof BE_Hole) {
                Velocity playerVel = owner.getVelocity();
                if ((Math.abs(playerVel.forward) + Math.abs(playerVel.right)) > 0) {
                    System.out.println("{" + owner.getName() + "} has a Hover Unit!");
                    shouldReboot = false;
                }
            }
            return new Pair<>(space, shouldReboot);
        }, owner);
    }

    @Override
    protected void onEnabled() {

    }

    @Override
    protected void onDisabled() {

    }

    @Override
    protected void onActivated() {

    }
}
