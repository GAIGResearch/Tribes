package core.actors;

import core.Types;
import utils.Vector2d;

public class Building {

    public Vector2d position;
    public  Types.BUILDING type;

    public Building(int x, int y, Types.BUILDING type) {
        this.position = new Vector2d(x, y);
        this.type = type;
    }

    public Building copy()
    {
        return new Building(position.x, position.y, type);
    }

    public int getBonus(){
        return type.getBonus();
    }
}
