package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "registers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Register {

    @Id
    private long playerId;

    private String m1;
    private String m2;
    private String m3;
    private String m4;
    private String m5;

    @Id
    private int turnId;
}
