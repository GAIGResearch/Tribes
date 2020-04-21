package core.actions.unitactions.factory;

import core.actions.Action;
import core.actors.units.*;
import core.game.GameState;
import core.Types;

import java.util.ArrayList;

public class UnitActionBuilder
{

    public ArrayList<Action> getActions(GameState gs, Unit unit)
    {
        ArrayList<Action> allActions = new ArrayList<>();

        if(unit.getTribeId() != gs.getActiveTribeID())
        {
            System.out.println("ERROR: creating actions for unit " + unit.getActorId() + " that the current tribe (" + gs.getActiveTribeID() +
                    ") does not control (" + unit.getTribeId() + ").");
            return allActions;
        }

        if(unit.getType().isWaterUnit())
        {
            Types.UNIT baseType = (unit instanceof Boat) ? ((Boat)unit).getBaseLandUnit() :
                    ((unit instanceof Ship) ? ((Ship)unit).getBaseLandUnit() : ((Battleship)unit).getBaseLandUnit());
            System.out.println(gs.getTick() + "; water unit " + unit.getActorId() + " with base unit: " + baseType);
        }


        if(unit.isFinished())
            return allActions;

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
