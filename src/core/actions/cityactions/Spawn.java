package core.actions.cityactions;

import core.TechnologyTree;
import core.Types;
import core.actions.Action;
import core.actors.Tribe;
import core.actors.units.Unit;
import core.game.GameState;
import core.actors.City;
import utils.Vector2d;

import java.util.LinkedList;

public class Spawn extends CityAction
{
    private Types.UNIT unit_type;

    public Spawn(City c)
    {
        super.city = c;
    }

    public void setUnitType(Types.UNIT unit_type) {this.unit_type = unit_type;}
    public Types.UNIT getUnitType() {
        return unit_type;
    }

    @Override
    public LinkedList<Action> computeActionVariants(GameState gs) {
        LinkedList<Action> actions = new LinkedList<>();

        Tribe tribe = gs.getTribe(city.getTribeId());
        TechnologyTree t = tribe.getTechTree();
        int star = tribe.getStars();
        Vector2d cityPos = city.getPosition();
        if (gs.getBoard().getUnitAt(cityPos.x, cityPos.y) == null){
            for(Types.UNIT unit: Types.UNIT.values()){
                // TODO: Update 7 to the the key after the unit enum will not be changed
                if (unit.getCost() <= star && unit.getKey() <= 7){
                    if (unit.getRequirement() == null){
                        Spawn newAction = new Spawn(city);
                        newAction.setUnitType(unit);
                        actions.add(newAction);
                    }else if(t.isResearched(unit.getRequirement())){
                        Spawn newAction = new Spawn(city);
                        newAction.setUnitType(unit);
                        actions.add(newAction);
                    }
                }
            }
        }
        return actions;
    }

    @Override
    public boolean isFeasible(GameState gs) {
        boolean costRequirement =  gs.getTribe(city.getTribeId()).getStars() >= unit_type.getCost();
        boolean techniqueRequirement = true;
        if (unit_type.getRequirement() != null){
            techniqueRequirement = gs.getTribe(city.getTribeId()).getTechTree().isResearched(unit_type.getRequirement());
        }
        return costRequirement && techniqueRequirement;
    }

    @Override
    public boolean execute(GameState gs) {
        if (isFeasible(gs)){
            Vector2d cityPos = city.getPosition();
            Unit newUnit = Types.UNIT.createUnit(new Vector2d(cityPos.x, cityPos.y), 0, false, city.getActorId(), city.getTribeId(), unit_type);
            gs.getBoard().addUnit(city, newUnit);
            Tribe tribe = gs.getTribe(city.getTribeId());
            tribe.subtractStars(unit_type.getCost());
            tribe.addScore(unit_type.getPoints());
            return true;
        }
        return false;
    }
}
