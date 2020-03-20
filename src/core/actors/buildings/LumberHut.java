package core.actors.buildings;

import core.TribesConfig;
import core.Types;
import core.actors.City;
import core.game.GameState;

public class LumberHut extends Building{

    public LumberHut(int x, int y) {
        super(x, y);
        this.type = Types.BUILDING.LUMBER_HUT;
    }

    @Override
    public Building copy() {
        return new LumberHut(position.x, position.y);
    }
}
