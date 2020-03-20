package core.actors.buildings;

import core.TribesConfig;
import core.Types;
import core.actors.City;
import core.game.GameState;

public class Farm extends Building{

    public Farm(int x, int y) {
        super(x, y);
        this.type = Types.BUILDING.FARM;
    }

    @Override
    public Building copy() {
        return new Farm(position.x, position.y);
    }
}
