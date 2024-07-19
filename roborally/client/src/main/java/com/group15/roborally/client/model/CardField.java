/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package com.group15.roborally.client.model;

import com.group15.roborally.common.observer.Subject;
import com.group15.roborally.client.model.upgrade_cards.UpgradeCard;
import lombok.Getter;

import static com.group15.roborally.client.model.CardField.CardFieldTypes.*;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class CardField extends Subject {
    public enum CardFieldTypes {
        COMMAND_CARD_FIELD,
        UPGRADE_CARD_SHOP_FIELD,
        PERMANENT_UPGRADE_CARD_FIELD,
        TEMPORARY_UPGRADE_CARD_FIELD
    }
    final public CardFieldTypes cardFieldType;
    final public Player player;
    final public UpgradeShop upgradeShop;
    final public int index;
    @Getter
    private Card card;
    @Getter
    private boolean visible;

    /**
     * Method for making an UpgradeShop field.
     */
    public CardField(UpgradeShop upgradeShop) {
        index = 0;
        this.player = null;
        this.upgradeShop = upgradeShop;
        this.card = null;
        this.visible = true;
        this.cardFieldType = UPGRADE_CARD_SHOP_FIELD;
    }

    /**
     * Method for making a card field for at player.
     * @param player The player owning the CardField.
     * @param cardFieldType The type of cards that can go here.
     */
    public CardField(Player player, CardFieldTypes cardFieldType) {
        index = 0;
        this.player = player;
        this.upgradeShop = null;
        this.card = null;
        this.visible = true;
        this.cardFieldType = cardFieldType;
    }

    /**
     * Method for making a command card field.
     * @param player
     * @param i
     */
    public CardField(Player player, int i) {
        index = i;
        this.player = player;
        this.upgradeShop = null;
        this.card = null;
        this.visible = true;
        this.cardFieldType = COMMAND_CARD_FIELD;
    }

    public void setCard(Card card) {
        if (card != this.card) {
            this.card = card;
            notifyChange();
        }
    }

    public void setVisible(boolean visible) {
        if (visible != this.visible) {
            this.visible = visible;
            notifyChange();
        }
    }

    public boolean getHasActivateButton() {
        if (card == null) {
            return false;
        }
        if (card instanceof UpgradeCard upgradeCard) {
            return upgradeCard.getHasActive();
        }
        return false;
    }

    public boolean getCanBeActivated() {
        if (card == null) {
            return false;
        }
        if (card instanceof UpgradeCard upgradeCard) {
            return upgradeCard.canBeActivated();
        }
        return false;
    }

    public void activateCard() {
        if (card == null) {
            return;
        }
        if (card instanceof UpgradeCard upgradeCard) {
            upgradeCard.tryActivate();
        }
    }
}
