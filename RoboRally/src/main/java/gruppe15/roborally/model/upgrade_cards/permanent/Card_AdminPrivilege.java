package gruppe15.roborally.model.upgrade_cards.permanent;

import gruppe15.roborally.controller.GameController;
import gruppe15.roborally.model.*;
import gruppe15.roborally.model.upgrade_cards.UpgradeCardPermanent;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class Card_AdminPrivilege extends UpgradeCardPermanent {
    public Card_AdminPrivilege() {
        super("Admin Privilege", 3, 0, 1, Phase.PROGRAMMING, Phase.ACTIVATION);
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
        System.out.println("Player: \"" + owner.getName() + "\" used UpgradeCard: \"" + title + "\".");
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
