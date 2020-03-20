package core.actions.cityactions;

import core.actions.Action;
import core.actors.City;
import utils.Vector2d;

public abstract class CityAction implements Action {


    protected int cityId;
    protected Vector2d targetPos;

    /** Setters and getters */

    public int getCityId() { return cityId; }
    public Vector2d getTargetPos() { return targetPos; }

    public void setCityId(int cityId) {this.cityId = cityId; }
    public void setTargetPos(Vector2d pos) { targetPos = pos; }

}
