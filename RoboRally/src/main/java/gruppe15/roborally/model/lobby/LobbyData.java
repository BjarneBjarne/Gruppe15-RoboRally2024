package gruppe15.roborally.model.lobby;

/**
 * To be serialized/deserialized and synchronized with the server.
 */
public class LobbyData {
    long pId;
    long gId;
    String pName;
    int isReady;    // Boolean
    int hasChanged; // Boolean
    String[] pNames;
    String[] robots;
    int[] areReady; // Boolean
    String map;
    String hName;

    public LobbyData() {
        this(-1, -1, "", -1, -1, new String[]{""}, new String[]{""}, new int[]{-1}, "", "");
    }

    public LobbyData(long pId, long gId, String pName, int isReady, int hasChanged, String[] pNames, String[] robots, int[] areReady, String map, String hName) {
        this.pId = pId;
        this.gId = gId;
        this.pName = pName;
        this.isReady = isReady;
        this.hasChanged = hasChanged;
        this.pNames = pNames;
        this.robots = robots;
        this.areReady = areReady;
        this.map = map;
        this.hName = hName;
    }

    public Long getPId() {
        return pId;
    }

    public Long getGId() {
        return gId;
    }

    public String getPName() {
        return pName;
    }

    public int getIsReady() {
        return isReady;
    }

    public int getHasChanged() {
        return hasChanged;
    }

    public String[] getPNames() {
        return pNames;
    }

    public String[] getRobots() {
        return robots;
    }

    public int[] getAreReady() {
        return areReady;
    }

    public String getCourse() {
        return map;
    }

    public String getHName() {
        return hName;
    }

    public void setPId(long pId) {
        this.pId = pId;
    }

    public void setGId(long gId) {
        this.gId = gId;
    }

    public void setPName(String pName) {
        this.pName = pName;
    }

    public void setIsReady(int isReady) {
        this.isReady = isReady;
    }

    public void setHasChanged(int hasChanged) {
        this.hasChanged = hasChanged;
    }

    public void setPNames(String[] pNames) {
        this.pNames = pNames;
    }

    public void setRobots(String[] robots) {
        this.robots = robots;
    }

    public void setAreReady(int[] areReady) {
        this.areReady = areReady;
    }

    public void setMap(String map) {
        this.map = map;
    }

    public void setHName(String hName) {
        this.hName = hName;
    }
}
