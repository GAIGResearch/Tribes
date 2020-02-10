package core.actions.cityactions;

import core.Types;
import core.actions.Action;
import core.game.GameState;
import core.actors.City;

import java.util.LinkedList;

public class Build extends CityAction
{
    private Types.BUILDING building;

    public Build(City c)
    {
        super.city = c;
    }


    @Override
    public LinkedList<Action> computeActionVariants(final GameState gs) {
        //TODO: compute variants for a Build action
        return null;
    }

    @Override
    public boolean isFeasible(final GameState gs) {
        //TODO: Is feasible to build
        return false;
    }

    @Override
    public boolean execute(GameState gs) {
        //TODO: Executes the action
        return false;
    }



    /** Getters and Setters **/
    public Types.BUILDING getBuilding() { return building; }
    public void setBuilding(Types.BUILDING building) {this.building = building;}



}
