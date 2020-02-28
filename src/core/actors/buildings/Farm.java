package core.actors.buildings;

import core.TribesConfig;
import core.Types;

public class Farm extends Building{

    private final Types.TERRAIN TERRAIN_CONSTRAINT = Types.TERRAIN.PLAIN;
    private final Types.RESOURCE RESOURCE_CONSTRAINT = Types.RESOURCE.CROPS;


    public Farm(int x, int y) {
        super(x, y, TribesConfig.FARM_COST, Types.BUILDING.FARM, TribesConfig.FARM_PRODUCTION);
    }

    // This type of building has no setProduction Ability
    @Override
    public void setProduction(int production){}

    @Override
    public Building copy() {
        return new Farm(getX(), getY());
    }
}
