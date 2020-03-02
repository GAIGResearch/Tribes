package core.actors.buildings;

import core.TribesConfig;
import core.Types;

public class Windmill extends Building {

    private final Types.TERRAIN TERRAIN_CONSTRAINT = Types.TERRAIN.PLAIN;

    // Building Purpose -> The production will set up inside the addBuilding in City
    public Windmill(int x, int y) {
        super(x, y, TribesConfig.WIND_MILL_COST, Types.BUILDING.WINDMILL, 0);
    }

    // Copy Purpose
    public Windmill(int x, int y, int production) {
        super(x, y, TribesConfig.WIND_MILL_COST, Types.BUILDING.WINDMILL, production);
    }

    @Override
    public void setProduction(int production) {
        super.setProduction(production);
    }

    @Override
    public Building copy() {
        return new Windmill(position.x, position.y, getPRODUCTION());
    }


}
