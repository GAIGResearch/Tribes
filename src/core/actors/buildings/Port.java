package core.actors.buildings;

import core.Types;

public class Port extends Building {

    private final Types.BUILDING TYPE = Types.BUILDING.PORT;
    private final int COST = 10;
    private final int PRODUCTION = 2;
    private final Types.TERRAIN TERRAIN_CONSTRAINT = Types.TERRAIN.SHALLOW_WATER;

    public Port(int x, int y) {
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

    @Override
    public Types.TERRAIN getTERRAIN_CONSTRAINT() {
        return TERRAIN_CONSTRAINT;
    }

    public Port copy(){
        return new Port(getX(), getY());
    }

}
