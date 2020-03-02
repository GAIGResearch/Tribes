package core.actions.unitactions;

import core.actions.Action;
import core.actions.cityactions.*;
import core.actors.City;
import core.actors.units.Unit;
import core.game.GameState;

import java.util.ArrayList;

public class UnitActionBuilder
{

    public ArrayList<Action> getActions(GameState gs, Unit unit)
    {
        ArrayList<Action> allActions = new ArrayList<>();

        //Attack
        allActions.addAll(new Attack(unit).computeActionVariants(gs));

        //Capture
        allActions.addAll(new Capture(unit).computeActionVariants(gs));

        //Convert
        allActions.addAll(new Convert(unit).computeActionVariants(gs));

        //Disband
        allActions.addAll(new Disband(unit).computeActionVariants(gs));

        //Examine
        allActions.addAll(new Examine(unit).computeActionVariants(gs));

        //Heal Others
        allActions.addAll(new HealOthers(unit).computeActionVariants(gs));

        //Make Veteran
        allActions.addAll(new MakeVeteran(unit).computeActionVariants(gs));

        //Recover
        allActions.addAll(new Recover(unit).computeActionVariants(gs));

        //Upgrade
        allActions.addAll(new Upgrade(unit).computeActionVariants(gs));

        return allActions;
    }

}
