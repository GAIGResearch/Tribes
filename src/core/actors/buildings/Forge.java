package core.actors.buildings;

import core.TribesConfig;
import core.Types;
import core.actors.City;
import core.game.GameState;

public class Forge extends Building {


    // Building Purpose -> The production will set up inside the addBuilding in City
    public Forge(int x, int y) {
        super(x, y);
        this.type = Types.BUILDING.FORGE;
    }

    public int computeBonusPopulation(City c, GameState gs) {
        //TODO: Depends on number of adjacent mines
        return -1;
    }

    @Override
    public Building copy() {
        return new Forge(position.x, position.y);
    }
}
