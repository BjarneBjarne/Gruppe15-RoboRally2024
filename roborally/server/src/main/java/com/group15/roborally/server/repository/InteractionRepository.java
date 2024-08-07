package com.group15.roborally.server.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.group15.roborally.common.model.Interaction;
import org.springframework.stereotype.Repository;

@Repository
public interface InteractionRepository extends JpaRepository<Interaction, Long> {
    Interaction findByPlayerIdAndInteractionNo(long playerId, int interactionNo);

    boolean existsByPlayerIdAndInteractionNo(long playerId, int interactionNo);
}
