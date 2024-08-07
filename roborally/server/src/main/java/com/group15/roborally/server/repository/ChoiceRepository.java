package com.group15.roborally.server.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.group15.roborally.common.model.Choice;
import org.springframework.stereotype.Repository;

@Repository
public interface ChoiceRepository extends JpaRepository<Choice, Long> {
    List<Choice> findAllByGameIdAndWaitCount(String gameId, int waitCount, Sort sort);
}
