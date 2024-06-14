package com.group15.roborally.server.repository;

import com.group15.roborally.server.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import com.group15.roborally.server.model.Market;

import java.util.List;

public interface MarketRepository extends JpaRepository<Market, Long>{
}
