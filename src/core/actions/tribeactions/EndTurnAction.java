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
        LinkedList<Action> endTurns = new LinkedList<>();
        if(isFeasible(gs))
            endTurns.add(new EndTurnAction(this.tribe));
        return endTurns;
    }

    @Override
    public boolean isFeasible(final GameState gs) {
        return gs.canEndTurn(tribe.getTribeId());
    }

    @Override
    public boolean execute(GameState gs) {
        if(isFeasible(gs))
        {
            gs.endTurn(tribe.getTribeId());
        }
        return false;
    }
}
