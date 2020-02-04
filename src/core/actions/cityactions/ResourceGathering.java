package core.actions.cityactions;

import core.Types;
import core.actors.City;

public class ResourceGathering extends CityAction
{
    private Types.RESOURCE resource;

    public ResourceGathering(City c, Types.RESOURCE resource)
    {
        super.city = c;
        this.resource = resource;
    }


    public Types.RESOURCE getResource() {
        return resource;
    }
}
