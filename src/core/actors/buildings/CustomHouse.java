package core.actors.buildings;

import core.TribesConfig;
import core.Types;

public class CustomHouse extends Building {

    private final Types.TERRAIN TERRAIN_CONSTRAINT = Types.TERRAIN.PLAIN;

    // Building Purpose -> The production will set up inside the addBuilding in City
    public CustomHouse(int x, int y) {
        super(x, y, TribesConfig.CUSTOM_COST, Types.BUILDING.CUSTOM_HOUSE, 0);
    }

    // Copy Purpose
    public CustomHouse(int x, int y, int production) {
        super(x, y, TribesConfig.CUSTOM_COST, Types.BUILDING.CUSTOM_HOUSE, production);
    }

    @Override
    public void setProduction(int production) {
        super.setProduction(production);
    }

    @Override
    public Building copy() {
        return new CustomHouse(getX(), getY(), getPRODUCTION());
    }

    @Override
    public Types.TERRAIN getTERRAIN_CONSTRAINT() {
        return TERRAIN_CONSTRAINT;
    }
}
