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
}
