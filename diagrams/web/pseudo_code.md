# Pseudo Code
## Server (Lobby)
### Receive new game POST
```
void newGame(String playerName){
    Generate Gid;
    Insert values (Gid, 1, 'lobby', 'default') into Games(Gid, NrOfPlayers, Phase, Map);

    Generate Pid;
    Insert values (Pid, Gid, playerName, 1, false) into Players(Pid, Gid, PName, SlotNr, isReady);

    return postResponse with Pid;
}
```
### Receive new player POST
```
void newPlayer(Long gameId, String playerName){
    if(gameId not exists){
        return postResponse with error;
    }

    Generate Pid;
    int slotNr = (max(slotNr) where Gid == gameId) + 1;
    Insert values (Pid, gameId, playerName, slotNr, false) into Players(Pid, Gid, PName, SlotNr, isReady);

    NrOfPlayers++ where Gid == gameId;

    postResponse = playerNames[], playerRobots[], playerSlots[], currentMap, gamePhase;

    return postResponse;
}
```
### Receive update POST
```
void updatePlayer(Long playerId, PlayerWebTemplate player){
    postResponse = playerNames[], playerRobots[], playerSlots[], currentMap, gamePhase;

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

    return postResponse;
}
```