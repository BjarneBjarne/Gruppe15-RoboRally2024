package com.group15.roborally.server.repository;

import com.group15.roborally.common.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;

import com.group15.roborally.common.model.UpgradeShop;

public interface UpgradeShopRepository extends JpaRepository<UpgradeShop, Long> {
    UpgradeShop findByGameId(String gameId);
}
