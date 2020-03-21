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
        Tribe tribe = (Tribe) gs.getActor(tribeId);
        return gs.canEndTurn(tribe.getTribeId());
    }

    @Override
    public boolean execute(GameState gs) {
        Tribe tribe = (Tribe) gs.getActor(tribeId);
        if(isFeasible(gs))
        {
            gs.endTurn(tribe.getTribeId());
        }
        return false;
    }

    @Override
    public Action copy() {
        return new EndTurn(this.tribeId);
    }
}
