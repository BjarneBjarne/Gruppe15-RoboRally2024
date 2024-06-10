package gruppe15.roborally.model.lobby;

public class Lobby {
    private Long pId;
    private Long gId;
    private String pName;
    private int isReady;    // Boolean
    private int hasChanged; // Boolean
    private String[] pNames;
    private String[] robots;
    private int[] areReady; // Boolean
    private String map;

    public Lobby(Long pId, Long gId, String pName, int isReady, int hasChanged, String[] pNames, String[] robots, int[] areReady, String map) {
        this.pId = pId;
        this.gId = gId;
        this.pName = pName;
        this.isReady = isReady;
        this.hasChanged = hasChanged;
        this.pNames = pNames;
        this.robots = robots;
        this.areReady = areReady;
        this.map = map;
    }

    public Long getpId() {
        return pId;
    }

    public Long getgId() {
        return gId;
    }

    public String getpName() {
        return pName;
    }

    public int getIsReady() {
        return isReady;
    }

    public int getHasChanged() {
        return hasChanged;
    }

    public String[] getpNames() {
        return pNames;
    }

    public String[] getRobots() {
        return robots;
    }

    public int[] getAreReady() {
        return areReady;
    }

    public String getMap() {
        return map;
    }
}
