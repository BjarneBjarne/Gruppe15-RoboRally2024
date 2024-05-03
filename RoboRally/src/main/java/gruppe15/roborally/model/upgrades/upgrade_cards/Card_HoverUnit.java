package gruppe15.roborally.model.upgrades.upgrade_cards;

import gruppe15.roborally.model.Board;
import gruppe15.roborally.model.EventHandler;
import gruppe15.roborally.model.Phase;
import gruppe15.roborally.model.Player;
import gruppe15.roborally.model.boardelements.Hole;
import gruppe15.roborally.model.events.PlayerMoveListener;
import gruppe15.roborally.model.upgrades.UpgradeCard;

public class Card_HoverUnit extends UpgradeCard {

    public Card_HoverUnit(String title, int purchaseCost, int useCost, int maxUses, Phase refreshedOn) {
        super(title, purchaseCost, useCost, maxUses, refreshedOn);
    }

    @Override
    public void initialize(Board board, Player owner) {
        super.initialize(board, owner);

        // Defining effects on events

        // OnDamageDealt
        EventHandler.onEvent(PlayerMoveListener.class, (PlayerMoveListener) space -> {
            if (space.getBoardElement().getClass() ) {
                System.out.println("{" + owner.getName() + "} has a Hover Unit!");
            }

            return space;
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
