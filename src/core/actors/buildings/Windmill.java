package core.actors.buildings;

import core.TribesConfig;
import core.Types;
import core.actors.City;
import core.game.GameState;

public class Windmill extends Building {

    // Building Purpose -> The production will set up inside the addBuilding in City
    public Windmill(int x, int y) {
        super(x, y);
        this.type = Types.BUILDING.WINDMILL;
    }

    public int computeBonusPopulation(City c, GameState gs) {
        //TODO: Depends on number of adjacent farms
        return -1;
    }

    @Override
    public Building copy() {
        return new Windmill(position.x, position.y);
    }
}
