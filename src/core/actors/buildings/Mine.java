package core.actors.buildings;

import core.TribesConfig;
import core.Types;

public class Mine extends Building{

    private final Types.TERRAIN TERRAIN_CONSTRAINT = Types.TERRAIN.MOUNTAIN;
    private final Types.RESOURCE RESOURCE_CONSTRAINT = Types.RESOURCE.ORE;


    public Mine(int x, int y) {
        super(x, y, TribesConfig.MINE_COST, Types.BUILDING.MINE, TribesConfig.MINE_PRODUCTION);
    }

    // This type of building has no setProduction Ability
    @Override
    public void setProduction(int production){}

    @Override
    public Building copy() {
        return new Mine(position.x, position.y);
    }
}
