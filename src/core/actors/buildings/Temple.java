package core.actors.buildings;

import core.Types;

public class Temple extends Building {

    private final Types.BUILDING TYPE = Types.BUILDING.TEMPLE;
    private final int COST = 20;
    private final int PRODUCTION = 1;
    private final int points = 100;
    // TODO: It needs to check if the resource is field tile or not
    private final Types.TERRAIN TERRAIN_CONSTRAINT = Types.TERRAIN.PLAIN;


    public Temple(int x, int y) {
        super(x, y);
    }

    @Override
    public Building copy() {
        return new Temple(getX(), getY());
    }

    @Override
    public Types.BUILDING getTYPE() {
        return TYPE;
    }

    @Override
    public int getCOST() {
        return COST;
    }

    @Override
    public int getPRODUCTION() {
        return PRODUCTION;
    }

    @Override
    public int getPoints() {
        return points;
    }

    @Override
    public Types.TERRAIN getTERRAIN_CONSTRAINT() {
        return TERRAIN_CONSTRAINT;
    }
}
