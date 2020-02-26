package core.actors.buildings;

import core.TribesConfig;
import core.Types;

public class LumberHut extends Building{

    private final Types.TERRAIN TERRAIN_CONSTRAINT = Types.TERRAIN.FOREST;

    public LumberHut(int x, int y) {
        super(x, y, TribesConfig.LUMBER_HUT_COST, Types.BUILDING.LUMBER_HUT, TribesConfig.LUMBER_HUT_PRODUCTION);
    }

    @Override
    public Types.TERRAIN getTERRAIN_CONSTRAINT() {
        return TERRAIN_CONSTRAINT;
    }

    // This type of building has no setProduction Ability
    @Override
    public void setProduction(int production) {

    }

    @Override
    public Building copy() {
        return new LumberHut(position.x, position.y);
    }
}
