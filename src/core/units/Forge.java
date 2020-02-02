package core.units;

import core.Types;

public class Forge extends Building {

    private final Types.BUILDING TYPE = Types.BUILDING.FORGE;
    private final int COST = 5;
    private int production;
    private final Types.TERRAIN TERRAIN_CONSTRAINT = Types.TERRAIN.PLAIN;

    public Forge(int x, int y) {
        super(x, y);
    }

    public Forge(int x, int y, int production) {
        super(x, y);
        this.production = production;
    }

    public void setProduction(int production) {
        this.production = production*2;
    }

    @Override
    public Building copy() {
        return new Forge(getX(), getY(), this.production);
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
