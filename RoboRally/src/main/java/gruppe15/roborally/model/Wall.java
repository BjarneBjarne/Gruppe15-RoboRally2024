package gruppe15.roborally.model;

import gruppe15.roborally.model.Heading;

public class Wall {
    private Heading direction;
    public Wall(Heading direction) {
        this.direction = direction;
    }

    public Heading getDirection() {
        return direction;
    }
}
