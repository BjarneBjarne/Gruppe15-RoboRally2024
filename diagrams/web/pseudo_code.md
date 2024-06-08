# Pseudo Code
## Server (Lobby)
### Receive new game POST
```
ResponseEntity<NewGame> newGame(NewGame newGame){
    Generate Gid;
    Insert values (Gid, 1, 'lobby', 'default') into Games(Gid, NrOfPlayers, Phase, Map);

    Generate Pid;
    Insert values (Pid, Gid, newGame.pName, 1, false) into Players(Pid, Gid, PName, SlotNr, isReady);
    newGame.gId = Gid;
    newGame.pId = Pid;

    return ResponseEntity.ok(NewGame);
}
```
### Receive new player POST
```
ResponseEntity<NewPlayer> newPlayer(NewPlayer newPlayer){
    if(newPlayer.gId not exists){
        return postResponse with error;
    }

    Generate Pid;
    int slotNr = (max(slotNr) where Gid == gameId) + 1;
    Insert values (Pid, newPlayer.gId, newPlayer.pName, slotNr, false) into Players(Pid, Gid, PName, SlotNr, isReady);

    NrOfPlayers++ where Gid == gameId;

    newPlayer.pId = pId;
    newPlayer.slotNr = slotNr;
    ... (as shown in class diagram 'class_diagram_web')

    return ResponseEntity.ok(NewPlayer);
}
```
### Receive update POST
```
ResponseEntity<updateClient> updatePlayer(UpdateClient updateClient){
    update object 'updateClient' as shown in class diagram 'class_diagram_web'

    if(player.hasChange == false){
        return postResponse;
    }

    if(player.isDisconnected == true){
        delete row from 'Players' where Pid == playerId;
        update slotNr in 'Players';
        NrOfPlayers-- where Gid == gameId;
        return no repsonse;
    }

    update values(player.robot, player.isReady) in Player(Robot, isReady) where playerId == Pid;

    return ResponseEntity.ok(updateClient);
}
```