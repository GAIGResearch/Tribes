package core.actions.cityactions;

import core.actions.Action;
import core.actors.City;
import core.game.GameState;
import utils.Vector2d;
import core.Types;

public class CityAction extends Action {

    public CityAction(Types.ACTION aType)
    {
        this.actionType = aType;
    }

    protected int cityId;
    protected Vector2d targetPos;

    /** Setters and getters */

    public int getCityId() { return cityId; }
    public Vector2d getTargetPos() { return targetPos; }

    public void setCityId(int cityId) {this.cityId = cityId; }
    public void setTargetPos(Vector2d pos) { targetPos = pos; }

    @Override
    public boolean isFeasible(GameState gs) {
        return false;
    }

    @Override
    public Action copy() {
        return null;
    }
}
