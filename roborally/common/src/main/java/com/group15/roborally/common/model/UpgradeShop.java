package com.group15.roborally.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;
import java.util.Objects;

@Entity
@Table(name = "upgradeShops")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpgradeShop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long upgradeShopId;
    private String gameId;
    private String[] cards = null;
    private int turn;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "games_gameId", insertable = false, updatable = false)
    @JsonIgnore
    private Game game;

    @JsonIgnore
    public boolean hasChanges(UpgradeShop otherUpgradeShopState) throws IllegalArgumentException {
        if (otherUpgradeShopState == null) {
            return true;
        }
        if (!this.gameId.equals(otherUpgradeShopState.gameId)) {
            throw new IllegalArgumentException("This gameId is: \"" + this.gameId + "\". Argument upgrade shop has gameId: \"" + otherUpgradeShopState.gameId + "\". Can't compare upgrade shops from two different games.");
        }
        if (this.upgradeShopId != otherUpgradeShopState.upgradeShopId) {
            throw new IllegalArgumentException("This upgradeShopId is: \"" + this.upgradeShopId + "\". Argument upgrade shop has gameId: \"" + otherUpgradeShopState.upgradeShopId + "\". Can't compare two different upgrade shops.");
        }
        return  !Arrays.equals(this.cards, otherUpgradeShopState.cards) ||
                this.turn != otherUpgradeShopState.turn;
    }
}
