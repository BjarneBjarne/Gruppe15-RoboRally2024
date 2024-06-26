# Pseudo Code
## Server (Lobby)
### Receive new game POST
```
ResponseEntity<NewGame> newGame(NewGame newGame){
    Generate Gid;
    Insert values (Gid, 1, 'lobby', 'default') into Games(Gid, NrOfPlayers, GamePhase, Map);

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
ResponseEntity<UpdateClient> updatePlayer(UpdateClient updateClient){
    update object 'updateClient' (with variables as shown in class diagram 'class_diagram_web')

    if(updateClient.hasChange == false){
        return postResponse;
    }

    if(updateClient.isDisconnected == true){
        delete row from 'Players' where Pid == updateClient.pId;
        update slotNr in 'Players';
        NrOfPlayers-- where Gid == updateClient.pId;
        return no repsonse;
    }

    update values(updateClient.robot, updateClient.isReady) in Player(Robot, isReady) where Pid == updateClient.pId;

    return ResponseEntity.ok(updateClient);
}
```
## Client (Lobby)
### Send update POST
```
void updateServer(Player player){
    HttpPostRequest with updateClient as body;
    updateClient = HttpClient.send(postRequest, bodyHandler.respons);
    player.updateInfo(updateClient);
    gameController.updateInfo(updateClient);
    board.updateInfo(updateClient);
}
```