package core.actors.buildings;

import core.Types;
import core.actors.City;
import core.game.Board;
import core.game.GameState;
import utils.Vector2d;

public abstract class Building {

    public Vector2d position;
    public  Types.BUILDING type;

    public Building(int x, int y) {
        this.position = new Vector2d(x, y);
    }

    public abstract Building copy();

    public int getBonus(){
        return type.getBonus();
    }
    public int getCost(){
        return type.getCost();
    }
    public int getPoints() {return type.getPoints();}

    public int computeProduction(City c, GameState gs) { return 0;}
    public int computeBonusPopulation(City c, GameState gs) { return type.getBonus();}

    public boolean adjacent(Building other)
    {
        return Vector2d.chebychevDistance(position, other.position) == 1;
    }

    public boolean isMatchingBuilding(Building existingBuilding)
    {
        return type.getMatchingBuilding() == existingBuilding.type;
    }
}
