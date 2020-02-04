package core.actions.cityactions;

import core.actors.City;

public class BurnForest extends CityAction
{
    private int targetX;
    private int targetY;

    public BurnForest(City c, int targetX, int targetY)
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
}
