package gruppe15.roborally.model.upgrade_cards.temporary;

import gruppe15.roborally.controller.GameController;
import gruppe15.roborally.model.Command;
import gruppe15.roborally.model.CommandCard;
import gruppe15.roborally.model.Phase;
import gruppe15.roborally.model.Player;
import gruppe15.roborally.model.upgrade_cards.UpgradeCardTemporary;

public class Card_WeaselRoutine extends UpgradeCardTemporary {

    public Card_WeaselRoutine() {
        super("Weasel Routine", 3, 0, 1, null, Phase.PROGRAMMING);
    }

    @Override
    public void initialize(Player owner, GameController gameController) {
        super.initialize(owner, gameController);
    }

    @Override
    protected void onEnabled() {

    }

    @Override
    protected void onDisabled() {

    }

    @Override
    protected void onActivated() {
        owner.discard(new CommandCard(Command.WEASEL_ROUTINE));
        super.onActivated();
    }
}