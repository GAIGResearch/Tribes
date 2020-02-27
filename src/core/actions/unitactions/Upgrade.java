package core.actions.unitactions;

import core.Types;
import core.actions.Action;
import core.actors.City;
import core.actors.Tribe;
import core.game.Board;
import core.game.GameState;
import core.actors.units.Unit;

import java.util.ArrayList;
import java.util.LinkedList;

public class Upgrade extends UnitAction
{
    public Upgrade(Unit target)
    {
        super.unit = target;
    }

    @Override
    public LinkedList<Action> computeActionVariants(final GameState gs) {
        //TODO: Compute all the available Upgrade actions

        LinkedList<Action> upgradeActions = new LinkedList<>();

        return upgradeActions;
    }

    @Override
    public boolean isFeasible(final GameState gs) {
        //TODO: Compute if upgrade is feasible or not

        return false;
    }

    @Override
    public boolean execute(GameState gs) {
        //TODO: executes this Upgrade action


        return false;
    }
}
