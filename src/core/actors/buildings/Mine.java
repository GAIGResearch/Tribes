package core.actors.buildings;

import core.TribesConfig;
import core.Types;
import core.actors.City;
import core.game.GameState;

public class Mine extends Building{

    private final Types.RESOURCE RESOURCE_CONSTRAINT = Types.RESOURCE.ORE;

    public Mine(int x, int y) {
        super(x, y);
        this.type = Types.BUILDING.MINE;
    }

    public int computeBonusPopulation(City c, GameState gs) { return type.getBonus();}

    @Override
    public Building copy() {
        return new Mine(position.x, position.y);
    }
}
