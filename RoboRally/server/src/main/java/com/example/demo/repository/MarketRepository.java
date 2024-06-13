package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Market;

public interface MarketRepository extends JpaRepository<Market, Long>{
    
}
