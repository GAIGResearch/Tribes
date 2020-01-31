package core.units;

import core.Types;

public abstract class Building {


    private int x;
    private int y;


    public Building(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Building copy(){return null;}

    // TODO: Linked to BOARD to see if it make requirements to build (return boolean) -> Implement this method to all buildings
}
