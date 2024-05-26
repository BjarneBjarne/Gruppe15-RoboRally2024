package gruppe15.roborally.model.upgrade_cards.permanent;

import gruppe15.roborally.controller.GameController;
import gruppe15.roborally.model.Command;
import gruppe15.roborally.model.EventHandler;
import gruppe15.roborally.model.Player;
import gruppe15.roborally.model.events.PlayerCommandListener;
import gruppe15.roborally.model.upgrade_cards.UpgradeCardPermanent;

public class Card_CrabLegs extends UpgradeCardPermanent {

    public Card_CrabLegs() {
        super("Crab Legs", 5, 0, 0, null);
    }

    @Override
    public void initialize(Player owner, GameController gameController) {
        super.initialize(owner, gameController);

        // Defining effects on events

        // OnMoveStart
        eventListeners.add(EventHandler.subscribe((PlayerCommandListener) command -> {
            if (command == Command.MOVE_1) {
                System.out.println("Player: \"" + owner.getName() + "\" used UpgradeCard: \"" + title + "\".");
                owner.queueCommand(Command.CRAB_LEGS, gameController);
                owner.queueCommand(Command.MOVE_1, false, gameController);
                return Command.BRAKES;
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
