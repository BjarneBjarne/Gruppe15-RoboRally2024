package com.group15.roborally.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@Entity
@Table(name = "choices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Choice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long choiceId;
    private String gameId;
    private long playerId;
    private String code;
    private int waitCount;
    private String resolveStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "playerId", referencedColumnName = "playerId", insertable = false, updatable = false)
    @JsonIgnore
    private Player player;

    public Choice(String gameId, long playerId, String code, int waitCount, String resolveStatus) {
        this.gameId = gameId;
        this.playerId = playerId;
        this.code = code;
        this.waitCount = waitCount;
        this.resolveStatus = resolveStatus;
    }

    public static final String READY_CHOICE = "READY_CHOICE";

    public enum ResolveStatus {
        NONE,
        UNRESOLVED
    }

    public boolean isResolved() {
        return !(resolveStatus.equals(ResolveStatus.NONE.name()) || resolveStatus.equals(ResolveStatus.UNRESOLVED.name()));
    }

    /**
     * Method for comparing two lists of this class. Two lists are equal, if they contain the same objects, defined
     * by their id, regardless of the order of the objects.
     * @return Whether the two lists contain the same objects.
     */
    public static boolean areListsEqual(List<Choice> list1, List<Choice> list2) {
        if (list1 == null || list2 == null) return list1 == list2;
        if (list1.size() != list2.size()) return false;
        Set<Choice> set1 = new HashSet<>(list1);
        Set<Choice> set2 = new HashSet<>(list2);
        return set1.equals(set2);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Choice choice = (Choice) o;
        return choiceId == choice.choiceId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(choiceId);
    }
}
