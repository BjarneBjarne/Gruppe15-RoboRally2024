package gruppe15.roborally.model.upgrades.upgrade_cards;

import gruppe15.roborally.model.*;
import gruppe15.roborally.model.events.PlayerCommandListener;
import gruppe15.roborally.model.upgrades.UpgradeCardPermanent;

public class Card_Brakes extends UpgradeCardPermanent {

    public Card_Brakes() {
        super("Brakes", 3, 0, 0, null);
    }

    @Override
    public void initialize(Board board, Player owner) {
        super.initialize(board, owner);

        // Defining effects on events

        // OnMoveStart
        eventListeners.add(EventHandler.subscribe((PlayerCommandListener) command -> {
            System.out.println("Player {" + owner.getName() + "} moved!");
            if (command == Command.MOVE_1) {
                return Command.DO_NOTHING;
            } else {
                return command;
            }
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

    }
}
