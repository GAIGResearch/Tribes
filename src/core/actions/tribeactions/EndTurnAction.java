package core.actions.tribeactions;

import core.actions.Action;
import core.actors.Tribe;
import core.game.GameState;

import java.util.LinkedList;

public class EndTurnAction extends TribeAction {

    public EndTurnAction(){}
    public EndTurnAction(Tribe tribe)
    {
        this.tribe = tribe;
    }

    @Override
    public LinkedList<Action> computeActionVariants(final GameState gs) {
        //TODO: compute EndTurnActions
        return null;
    }

    @Override
    public boolean isFeasible(final GameState gs) {
        //TODO: Check if this EndTurnAction is feasible.
        return false;
    }

    @Override
    public boolean execute(GameState gs) {
        //TODO: executes an EndTurnAction.
        return false;
    }
}
