// package com.group15.roborally.server.model;

// // import com.example.demo.model.Keys.RegisterId;
// import java.util.Set;
// import jakarta.persistence.*;
// import lombok.AllArgsConstructor;
// import lombok.Getter;
// import lombok.NoArgsConstructor;
// import lombok.Setter;

// @Entity
// @Table(name = "registers")
// @Getter
// @Setter
// @NoArgsConstructor
// @AllArgsConstructor
// // @IdClass(RegisterId.class)
// public class Register {
//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long registerId;

//     // @Id
//     // @ManyToOne
//     // @JoinColumn(name = "playerId", referencedColumnName = "playerId", insertable = false, updatable = false)
//     private Long playerId;

//     @ManyToOne
//     @JoinColumn(name = "gameId", referencedColumnName = "gameId", insertable = false, updatable = false)
//     private Set<Player> players;
    
//     // @Id
//     // @ManyToOne
//     // @JoinColumn(name = "turnId", referencedColumnName = "turnId", insertable = false, updatable = false)
//     // private Long turnId;

//     private String m1;
//     private String m2;
//     private String m3;
//     private String m4;
//     private String m5;
// }
