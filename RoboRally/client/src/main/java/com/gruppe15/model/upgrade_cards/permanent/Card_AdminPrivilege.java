package com.gruppe15.model.upgrade_cards.permanent;

import com.gruppe15.controller.GameController;
import com.gruppe15.model.*;
import com.gruppe15.model.upgrade_cards.UpgradeCardPermanent;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class Card_AdminPrivilege extends UpgradeCardPermanent {
    public Card_AdminPrivilege() {
        super("Admin Privilege", 3, 0, 1, Phase.PROGRAMMING, Phase.PLAYER_ACTIVATION);
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
        printUsage();
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
