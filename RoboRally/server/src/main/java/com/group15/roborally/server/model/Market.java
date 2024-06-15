package com.group15.roborally.server.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "market")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Market {

    @Id
    private long gameId;
    private String[] cards;
    private int turn;
    
}
