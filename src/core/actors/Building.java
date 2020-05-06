package core.actors;

import core.Types;
import org.json.JSONObject;
import utils.Vector2d;

public class Building {

    //Position of this building
    public Vector2d position;

    //Type of the building.
    public Types.BUILDING type;

    //ID this building belongs to
    public int cityId;

    /**
     * Creates a new building
     * @param x x position of this building
     * @param y y position of this building
     * @param type type of this new building.
     * @param cityId id of the city this building belongs to
     */
    public Building(int x, int y, Types.BUILDING type, int cityId) {
        this.position = new Vector2d(x, y);
        this.type = type;
        this.cityId = cityId;
    }

    /**
     * Creates a new building from a JSON object
     * @param obj object to read the building from
     * @param cityID id of the city
     */
    public Building(JSONObject obj, int cityID){
        this.position = new Vector2d(obj.getInt("x"), obj.getInt("y"));
        this.type = Types.BUILDING.getTypeByKey(obj.getInt("type"));
        this.cityId = cityID;
    }

    /**
     * Returns a copy of this building.
     * @return a copy of this building.
     */
    public Building copy()
    {
        return new Building(position.x, position.y, type, cityId);
    }

    /**
     * Returns the bonus this building provides to the city. This can be
     * production or population.
     * @return the bonus this building provides to the city.
     */
    int getBonus(){
        return type.getBonus();
    }
}
