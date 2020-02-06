package core.actions.cityactions;

import core.actions.Action;
import core.actors.City;

public abstract class CityAction extends Action {

    protected City city;

    public City getCity()
    {
        return city;
    }
}
