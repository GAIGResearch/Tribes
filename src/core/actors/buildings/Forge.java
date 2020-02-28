package core.actors.buildings;

import core.TribesConfig;
import core.Types;

public class Forge extends Building {

    private final Types.TERRAIN TERRAIN_CONSTRAINT = Types.TERRAIN.PLAIN;

    // Building Purpose -> The production will set up inside the addBuilding in City
    public Forge(int x, int y) {
        super(x, y, TribesConfig.FORGE_COST, Types.BUILDING.FORGE, 0);
    }

    // Copy Purpose
    public Forge(int x, int y, int production) {
        super(x, y, TribesConfig.FORGE_COST, Types.BUILDING.FORGE, production);
    }

    public void setProduction(int production) {
        super.setProduction(production*2);
    }

    @Override
    public Building copy() {
        return new Forge(getX(), getY(), getPRODUCTION());
    }
}
