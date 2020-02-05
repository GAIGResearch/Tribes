package core.actors.buildings;

import core.TribesConfig;
import core.Types;

public class Sawmill extends Building {

    private final Types.TERRAIN TERRAIN_CONSTRAINT = Types.TERRAIN.PLAIN;

    public Sawmill(int x, int y) {
        super(x, y, TribesConfig.SAW_MILL_COST, Types.BUILDING.SAWMILL, 0);
    }

    public Sawmill(int x, int y, int production) {
        super(x, y, TribesConfig.SAW_MILL_COST, Types.BUILDING.SAWMILL, production);
    }

    @Override
    public void setProduction(int production) {
        super.setProduction(production);
    }

    @Override
    public Types.TERRAIN getTERRAIN_CONSTRAINT() {
        return TERRAIN_CONSTRAINT;
    }

    @Override
    public Building copy() {
        return new Sawmill(getX(), getY(), getPRODUCTION());
    }


}
