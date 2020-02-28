package core.actors.buildings;

import core.TribesConfig;
import core.Types;

public class Port extends Building {

    private final Types.TERRAIN TERRAIN_CONSTRAINT = Types.TERRAIN.SHALLOW_WATER;

    public Port(int x, int y) {
        super(x, y, TribesConfig.PORT_COST, Types.BUILDING.PORT, TribesConfig.PORT_PRODUCTION);
    }

    // This type of building has no setProduction Ability
    @Override
    public void setProduction(int production){}

    @Override
    public Port copy(){
        return new Port(getX(), getY());
    }

}
