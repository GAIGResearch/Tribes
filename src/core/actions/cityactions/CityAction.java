package core.actions.cityactions;

import core.actions.Action;
import core.actors.City;

public abstract class CityAction extends Action {


    protected City city;
    protected int targetX = -1;
    protected int targetY = -1;

    /** Setters and getters */

    public City getCity() { return city; }
    public int getTargetX() { return this.targetX; }
    public int getTargetY() { return this.targetY; }

    public void setCity(City c) {this.city = c; }
    public void setTargetX(int x) {this.targetX = x; }
    public void setTargetY(int y) {this.targetY = y; }

}
