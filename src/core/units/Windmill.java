package core.units;

import core.Types;

public class Windmill extends Building {

    private final Types.BUILDING TYPE = Types.BUILDING.WINDMILL;
    private final int COST = 5;
    private int production;
    private final Types.TERRAIN TERRAIN_CONSTRAINT = Types.TERRAIN.PLAIN;

    public Windmill(int x, int y) {
        super(x, y);
    }

    public Windmill(int x, int y, int production) {
        super(x, y);
        this.production = production;
    }

    public void setProduction(int production) {
        this.production = production;
    }

    @Override
    public Building copy() {
        return new Windmill(getX(), getY(), this.production);
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
