package gruppe15.roborally.model;

public class Velocity {
    private int forward;
    private int right;
    public Velocity (int forward, int right) {
        this.forward = forward;
        this.right = right;
    }
    public int getForward() {
        return this.forward;
    }
    public int getRight() {
        return this.right;
    }
    public void setForward(int newForward) {
        this.forward = newForward;
    }
    public void setRight(int newRight) {
        this.right = newRight;
    }
}
