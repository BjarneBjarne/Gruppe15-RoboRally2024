package com.group15.roborally.client.model.upgrade_cards.permanent;

import com.group15.roborally.client.controller.GameController;
import com.group15.roborally.client.model.*;
import com.group15.roborally.client.model.upgrade_cards.UpgradeCardPermanent;
import com.group15.roborally.client.model.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class Card_AdminPrivilege extends UpgradeCardPermanent {
    public Card_AdminPrivilege() {
        super("Admin Privilege", 3, 0, 1, Phase.PROGRAMMING, Phase.PLAYER_ACTIVATION);
    }

    @Override
    public void initialize(com.group15.roborally.client.model.Player owner, GameController gameController) {
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
        printUsage();
        Queue<com.group15.roborally.client.model.Player> oldPriorityList = owner.board.getPriorityList();
        List<com.group15.roborally.client.model.Player> newPriorityList = new ArrayList<>();

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
