package core.actions.tribeactions;

import core.actions.Action;
import core.actors.Tribe;
import core.game.GameState;

import java.util.LinkedList;

public class EndTurn extends TribeAction {

    public EndTurn(){}
    public EndTurn(int tribeId)
    {
        this.tribeId = tribeId;
    }

    @Override
    public boolean isFeasible(final GameState gs) {
        Tribe tribe = gs.getTribe(tribeId);
        return gs.canEndTurn(tribe.getTribeId());
    }

    @Override
    public boolean execute(GameState gs) {
        if(isFeasible(gs))
        {
            gs.endTurn(true);
        }
        return false;
    }

    @Override
    public Action copy() {
        return new EndTurn(this.tribeId);
    }

    public String toString() {
        return "END_TURN by tribe " + this.tribeId;
    }
}
