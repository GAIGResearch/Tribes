package core.actions.cityactions;

import core.actions.Action;
import core.actors.City;
import utils.Vector2d;

public abstract class CityAction extends Action {
//TODO choose which format to follow for positions, targetPos or targetX,targetY

    protected City city;
    protected Vector2d targetPos;
    protected int targetX = -1;
    protected int targetY = -1;

    /** Setters and getters */

    public City getCity() { return city; }
    public Vector2d getTargetPos() { return targetPos; }
    public int getTargetX() { return targetX; }
    public int getTargetY() { return targetY; }

    public void setCity(City c) {this.city = c; }
    public void setTargetX(int targetX) { this.targetX = targetX; }
    public void setTargetY(int targetY) { this.targetY = targetY; }
    public void setTargetPos(Vector2d pos) { targetPos = pos; }

}
