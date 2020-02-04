package core.actors.buildings;

import core.Types;

public class LumberHut extends Building{

    private final Types.BUILDING TYPE = Types.BUILDING.LUMBER_HUT;
    private final int COST = 2;
    private final int PRODUCTION = 1;
    // TODO: It needs to check if the resource is field tile or not
    private final Types.TERRAIN TERRAIN_CONSTRAINT = Types.TERRAIN.FOREST;


    public LumberHut(int x, int y) {
        super(x, y);
    }

    public Types.BUILDING getTYPE() {
        return TYPE;
    }

    public int getCOST() {
        return COST;
    }

    public int getPRODUCTION() {
        return PRODUCTION;
    }

    public Types.TERRAIN getTERRAIN_CONSTRAINT() {
        return TERRAIN_CONSTRAINT;
    }

    @Override
    public Building copy() {
        return new LumberHut(getX(), getY());
    }
}
