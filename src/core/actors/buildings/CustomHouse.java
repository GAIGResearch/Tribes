package core.actors.buildings;

import core.TribesConfig;
import core.Types;
import core.actors.City;
import core.game.GameState;

public class CustomHouse extends Building {

    // Building Purpose -> The production will set up inside the addBuilding in City
    public CustomHouse(int x, int y) {
        super(x, y);
        this.type = Types.BUILDING.CUSTOM_HOUSE;
    }

    public int computeBonusPopulation(City c, GameState gs) { return 0;}

    public int computeProduction(City c, GameState gs)
    {
        //TODO: Computes and returns the production of this custom house: 2 * num_adjacent_ports
        return -1;
    }

    @Override
    public Building copy() {
        return new CustomHouse(position.x, position.y);
    }
}
