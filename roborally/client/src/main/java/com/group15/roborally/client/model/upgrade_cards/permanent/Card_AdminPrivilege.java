package com.group15.roborally.client.model.upgrade_cards.permanent;

import com.group15.roborally.client.controller.GameController;
import com.group15.roborally.client.model.*;
import com.group15.roborally.client.model.upgrade_cards.UpgradeCardPermanent;
import com.group15.roborally.client.model.Player;
import com.group15.roborally.common.model.GamePhase;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class Card_AdminPrivilege extends UpgradeCardPermanent {
    public Card_AdminPrivilege() {
        super("Admin Privilege", 3, 0, 1, GamePhase.PROGRAMMING, GamePhase.PLAYER_ACTIVATION);
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
    }
}
