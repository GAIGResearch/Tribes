package core.actors.buildings;

import core.TribesConfig;
import core.Types;
import core.actors.City;
import core.game.GameState;

public class Port extends Building {

    public Port(int x, int y) {
        super(x, y);
        this.type = Types.BUILDING.PORT;
    }

    @Override
    public Port copy(){
        return new Port(position.x, position.y);
    }

}
