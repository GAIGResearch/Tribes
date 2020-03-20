package core.actors.buildings;

import core.TribesConfig;
import core.Types;
import core.actors.City;
import core.game.GameState;

public class Mine extends Building{


    public Mine(int x, int y) {
        super(x, y);
        this.type = Types.BUILDING.MINE;
    }


    @Override
    public Building copy() {
        return new Mine(position.x, position.y);
    }
}
