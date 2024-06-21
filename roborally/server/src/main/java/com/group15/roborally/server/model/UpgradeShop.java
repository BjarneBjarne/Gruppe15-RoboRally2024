package com.group15.roborally.server.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    private long gameId;

    private String[] cards;

    private int turn;
}
