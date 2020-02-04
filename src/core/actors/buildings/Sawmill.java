package core.actors.buildings;

import core.Types;

public class Sawmill extends Building {

    private final Types.BUILDING TYPE = Types.BUILDING.SAWMILL;
    private final int COST = 5;
    private int production;
    private final Types.TERRAIN TERRAIN_CONSTRAINT = Types.TERRAIN.PLAIN;

    public Sawmill(int x, int y) {
        super(x, y);
    }

    public Sawmill(int x, int y, int production) {
        super(x, y);
        this.production = production;
    }

    public void setProduction(int production) {
        this.production = production;
    }

    @Override
    public Building copy() {
        return new Sawmill(getX(), getY(), this.production);
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
        return production;
    }

    @Override
    public Types.TERRAIN getTERRAIN_CONSTRAINT() {
        return TERRAIN_CONSTRAINT;
    }
}
