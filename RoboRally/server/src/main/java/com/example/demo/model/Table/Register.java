package com.example.demo.model.Table;

import com.example.demo.model.Keys.RegisterId;
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
@IdClass(RegisterId.class)
public class Register {
    @Id
    private long playerId;
    @Id
    private int turnId;

    private String m1;
    private String m2;
    private String m3;
    private String m4;
    private String m5;
}
