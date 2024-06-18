package com.group15.roborally.server.model;

// import com.example.demo.model.Keys.RegisterId;
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
// @IdClass(RegisterId.class)
public class Register {
    // @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    // private long registerId;

    @Id
    private long playerId;

    private int turn;

    private String m1;
    private String m2;
    private String m3;
    private String m4;
    private String m5;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "players_playerId", insertable = false, updatable = false)
    private Player player;

    @Override
    public boolean equals(Object o) {
        Register other = (Register) o;
        return playerId == other.playerId
                && turn == other.turn
                && m1.equals(other.m1)
                && m2.equals(other.m2)
                && m3.equals(other.m3)
                && m4.equals(other.m4)
                && m5.equals(other.m5);
    }
}
