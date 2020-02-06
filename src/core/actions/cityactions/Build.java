package core.actions.cityactions;

import core.Types;
import core.game.GameState;
import core.actors.City;

public class Build extends CityAction
{
    private Types.BUILDING building;
    private int targetX;
    private int targetY;

    public Build(City c, int x, int y, Types.BUILDING building)
    {
        super.city = c;
        this.building = building;
        this.targetX = x;
        this.targetY = y;
    }

    public Types.BUILDING getBuilding() {
        return building;
    }

    public int getTargetX() {
        return this.targetX;
    }

    public int getTargetY() {
        return this.targetY;
    }

    @Override
    public boolean isFeasible(GameState gs) {
        return false;
    }

    @Override
    public void execute(GameState gs) {

    }
}
