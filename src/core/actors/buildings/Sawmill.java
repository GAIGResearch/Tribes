package core.actors.buildings;

import core.TribesConfig;
import core.Types;
import core.actors.City;
import core.game.GameState;

public class Sawmill extends Building {

    public Sawmill(int x, int y) {
        super(x, y);
        this.type = Types.BUILDING.SAWMILL;
    }

    public int computeBonusPopulation(City c, GameState gs) {
        //TODO: Depends on number of adjacent lumber huts
        return -1;
    }

    @Override
    public Building copy() {
        return new Sawmill(position.x, position.y);
    }


}
