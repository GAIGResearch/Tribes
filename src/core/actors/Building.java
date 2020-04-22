package core.actors;

import core.Types;
import org.json.JSONObject;
import utils.Vector2d;

public class Building {

    public Vector2d position;
    public Types.BUILDING type;
    public int cityId;

    public Building(int x, int y, Types.BUILDING type, int cityId) {
        this.position = new Vector2d(x, y);
        this.type = type;
        this.cityId = cityId;
    }

    public Building(JSONObject obj, Types.BUILDING type, int cityID){
        this.position = new Vector2d(obj.getInt("x"), obj.getInt("y"));
        this.type = type;
        this.cityId = cityID;
    }

    public Building copy()
    {
        return new Building(position.x, position.y, type, cityId);
    }

    public int getBonus(){
        return type.getBonus();
    }
}
