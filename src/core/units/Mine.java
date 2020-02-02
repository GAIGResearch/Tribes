package core.units;

import core.Types;

public class Mine extends Building{

    private final Types.BUILDING TYPE = Types.BUILDING.MINE;
    private final int COST = 5;
    private final int PRODUCTION = 2;
    // TODO: It needs to check if the resource is field tile or not
    private final Types.TERRAIN TERRAIN_CONSTRAINT = Types.TERRAIN.MOUNTAIN;
    private final Types.RESOURCE RESOURCE_CONSTRAINT = Types.RESOURCE.ORE;


    public Mine(int x, int y) {
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
    public Types.RESOURCE getRESOURCE_CONSTRAINT() {
        return RESOURCE_CONSTRAINT;
    }

    @Override
    public Building copy() {
        return new Mine(getX(), getY());
    }
}
