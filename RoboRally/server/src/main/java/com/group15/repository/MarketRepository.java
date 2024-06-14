package com.group15.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.group15.model.Market;

public interface MarketRepository extends JpaRepository<Market, Long>{
    
}
