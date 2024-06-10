package gruppe15.roborally.communication;

public class ServerCommunication {
    String playerName;
    String lobbyID;

public void startLobby(String PlayerName){
this.playerName = playerName;

//Todo Insert generation of lobby id and communicate with server


}

public void playerReady(){

}

public void joinLobby(String PlayerName, String lobbyID){
    this.lobbyID=lobbyID;
    this.playerName= playerName;


}
public void leaveLobby(){
    //todo tell the server that the player has left


}

public void changeSelectionHost(String map,String robot){
    //todo change map and robot from


}

public void changeSelectionPlayer(String robot){

    //todo send new robot to server

}


public LobbyStatus requestLobbyStatus(){

    LobbyStatus lobbyStatus = new LobbyStatus();

    //todo fetch lobby status

    return lobbyStatus ;

}


}
