package core.actions.unitactions.factory;

import core.actions.Action;
import core.actions.unitactions.factory.*;
import core.actors.units.Unit;
import core.game.GameState;

import java.util.ArrayList;

public class UnitActionBuilder
{

    public ArrayList<Action> getActions(GameState gs, Unit unit)
    {
        ArrayList<Action> allActions = new ArrayList<>();

        //Attack
        allActions.addAll(new AttackFactory().computeActionVariants(unit, gs));

        //Capture
        allActions.addAll(new CaptureFactory().computeActionVariants(unit, gs));

        //Convert
        allActions.addAll(new ConvertFactory().computeActionVariants(unit, gs));

        //Disband
        allActions.addAll(new DisbandFactory().computeActionVariants(unit, gs));

        //Examine
        allActions.addAll(new ExamineFactory().computeActionVariants(unit, gs));

        //Heal Others
        allActions.addAll(new HealOthersFactory().computeActionVariants(unit, gs));

        //Make Veteran
        allActions.addAll(new MakeVeteranFactory().computeActionVariants(unit, gs));

        //Move
        allActions.addAll(new MoveFactory().computeActionVariants(unit, gs));

        //Recover
        allActions.addAll(new RecoverFactory().computeActionVariants(unit, gs));

        //Upgrade
        allActions.addAll(new UpgradeFactory().computeActionVariants(unit, gs));

        return allActions;
    }

}
