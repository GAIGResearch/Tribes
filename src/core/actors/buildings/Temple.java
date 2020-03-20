package core.actors.buildings;

import core.TribesConfig;
import core.Types;
import core.actors.City;
import core.game.GameState;

public class Temple extends Building {

    public Temple(int x, int y, Types.BUILDING templeType) {
        super(x, y);
        this.type = templeType;
    }

    @Override
    public Building copy() {
        return new Temple(position.x, position.y, type);
    }
}
