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
    @Id
    private long gameId;

    private int turn;

    private String m1;
    private String m2;
    private String m3;
    private String m4;
    private String m5;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "players_playerId", insertable = false, updatable = false)
    private Player player;
}
