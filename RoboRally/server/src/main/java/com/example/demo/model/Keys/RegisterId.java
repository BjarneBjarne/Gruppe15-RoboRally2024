// package com.example.demo.model.Keys;

// import java.io.Serializable;
// import java.util.Objects;

// public class RegisterId implements Serializable {
//     private long playerId;
//     private int turnId;

//     // Default constructor
//     public RegisterId() {}

//     public RegisterId(long playerId, int turnId) {
//         this.playerId = playerId;
//         this.turnId = turnId;
//     }

//     // Getters and setters, equals() and hashCode() methods

//     @Override
//     public boolean equals(Object o) {
//         if (this == o) return true;
//         if (o == null || getClass() != o.getClass()) return false;
//         RegisterId that = (RegisterId) o;
//         return playerId == that.playerId && turnId == that.turnId;
//     }

//     @Override
//     public int hashCode() {
//         return Objects.hash(playerId, turnId);
//     }
// }
