package core.actions.cityactions;

import core.game.GameState;
import core.units.City;

public class Destroy extends CityAction
{
    private int targetX;
    private int targetY;

    public Destroy(City c, int targetX, int targetY)
    {
        super.city = c;
        this.targetX = targetX;
        this.targetY = targetY;
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
