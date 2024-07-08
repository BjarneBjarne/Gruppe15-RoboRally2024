package com.group15.roborally.client.model.upgrade_cards.temporary;

import com.group15.roborally.client.controller.GameController;
import com.group15.roborally.client.model.Player;
import com.group15.roborally.client.model.upgrade_cards.UpgradeCardTemporary;
import com.group15.roborally.common.model.GamePhase;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class Card_ManualSort extends UpgradeCardTemporary {

    public Card_ManualSort() {
        super("Manual Sort", 1, 0, 1, null, GamePhase.PLAYER_ACTIVATION);
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
    public void onActivated() {
        // TODO: Make this override Card_AdminPrivilege
        Queue<Player> oldPriorityList = owner.board.getPriorityList();
        List<Player> newPriorityList = new ArrayList<>();

        for (Player p : oldPriorityList) {
            if (p == owner) {
                newPriorityList.addFirst(p);
            } else {
                newPriorityList.addLast(p);
            }
        }

        owner.board.setPriorityList(newPriorityList);

        super.onActivated();
    }
}
