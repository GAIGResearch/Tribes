package core.actors.buildings;

import core.TribesConfig;
import core.Types;
import core.actors.City;
import core.game.Board;
import core.game.GameState;

public class Monument extends Building{

    public Monument(int x, int y, Types.BUILDING monumentType) {
        super(x, y);
        this.type = monumentType;
    }

    public int computeBonusPopulation(City c, GameState gs) { return type.getBonus();}

    @Override
    public Building copy() {
        return new Monument(position.x, position.y, type);
    }
}
