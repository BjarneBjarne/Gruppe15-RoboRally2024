package com.group15.model.upgrade_cards.temporary;

import com.group15.controller.GameController;
import com.group15.model.Command;
import com.group15.model.CommandCard;
import com.group15.model.Phase;
import com.group15.model.Player;
import com.group15.model.upgrade_cards.UpgradeCardTemporary;

public class Card_SpamFolderRoutine extends UpgradeCardTemporary {

    public Card_SpamFolderRoutine() {
        super("Spam Folder Routine", 2, 0, 1, null, Phase.PROGRAMMING);
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
        owner.discard(new CommandCard(Command.SPAM_FOLDER));
        super.onActivated();
    }
}