package gruppe15.roborally.model.upgrade_cards.temporary;

import gruppe15.roborally.controller.GameController;
import gruppe15.roborally.model.*;
import gruppe15.roborally.model.upgrade_cards.UpgradeCard;
import gruppe15.roborally.model.upgrade_cards.UpgradeCardTemporary;

public class Card_EnergyRoutine extends UpgradeCardTemporary {

    public Card_EnergyRoutine() {
        super("Energy Routine", 3, 0, 1, null, Phase.PROGRAMMING);
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
        owner.discard(new CommandCard(Command.ENERGY_ROUTINE));
        super.onActivated();
    }
}
