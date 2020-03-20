package core.actions.cityactions;

import core.actions.Action;
import core.actors.City;
import utils.Vector2d;

public abstract class CityAction extends Action {
//TODO choose which format to follow for positions, targetPos or targetX,targetY

    protected City city;
    protected Vector2d targetPos;

    /** Setters and getters */

    public City getCity() { return city; }
    public Vector2d getTargetPos() { return targetPos; }

    public void setCity(City c) {this.city = c; }
    public void setTargetPos(Vector2d pos) { targetPos = pos; }

}
