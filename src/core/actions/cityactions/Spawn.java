package core.actions.cityactions;

import core.Types;
import core.actions.Action;
import core.actions.tribeactions.BuildRoad;
import core.actors.Tribe;
import core.actors.units.Unit;
import core.game.GameState;
import core.actors.City;
import utils.Vector2d;

public class Spawn extends CityAction
{
    private Types.UNIT unit_type;

    public Spawn(int cityId)
    {
        super(Types.ACTION.SPAWN);
        super.cityId = cityId;
    }
    public void setUnitType(Types.UNIT unit_type) {this.unit_type = unit_type;}
    public Types.UNIT getUnitType() {
        return unit_type;
    }

    @Override
    public boolean isFeasible(GameState gs) {

        City city = (City) gs.getActor(this.cityId);
        Tribe t = gs.getTribe(city.getTribeId());

        //It's a buildable type (no naval units, no giants)
        if(!unit_type.spawnable()) return false;

        //I have enough money.
        if(t.getStars() < unit_type.getCost()) return false;

        //I have enough space in this city.
        if(!city.canAddUnit()) return false;

        //There's no one in the city's position
        Vector2d cityPos = city.getPosition();
        if(gs.getBoard().getUnitAt(cityPos.x, cityPos.y) != null) return false;

        //and I have the tech to build it...
        Types.TECHNOLOGY tech = unit_type.getTechnologyRequirement();
        return tech == null || t.getTechTree().isResearched(tech);

    }

    @Override
    public Action copy() {
        Spawn spawn = new Spawn(this.cityId);
        spawn.setUnitType(this.unit_type);
        spawn.setTargetPos(this.targetPos.copy());
        return spawn;
    }

    public String toString()
    {
        return "SPAWN by city " + this.cityId+ " : " + unit_type.toString();
    }

    public boolean equals(Object o) {
        if(!(o instanceof Spawn))
            return false;

        Spawn other = (Spawn) o;

        return super.equals(other) && unit_type == other.unit_type;
    }
}
