package gruppe15.roborally.model.upgrade_cards.temporary;

import gruppe15.roborally.controller.GameController;
import gruppe15.roborally.model.*;
import gruppe15.roborally.model.upgrade_cards.UpgradeCardTemporary;

public class Card_SpamBlocker extends UpgradeCardTemporary {

    public Card_SpamBlocker() {
        super("Spam Blocker", 3, 0, 1, null, Phase.PROGRAMMING);
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
        CardField[] playerCardFields = owner.getCardFields();
        for (CardField playerCardField : playerCardFields) {
            if (playerCardField.getCard() instanceof CommandCard card) {
                if (card.command == Command.SPAM) {
                    playerCardField.setCard(owner.drawFromDeck());
                }
            }
        }
        super.onActivated();
    }
}
