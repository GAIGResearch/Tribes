package core.actions.tribeactions;

import core.actions.Action;
import core.actors.Tribe;
import core.game.GameState;
import core.Types;

public class EndTurn extends TribeAction {

    public EndTurn(){ super(Types.ACTION.END_TURN); }
    public EndTurn(int tribeId)
    {
        super(Types.ACTION.END_TURN);
        this.tribeId = tribeId;
    }

    @Override
    public boolean isFeasible(final GameState gs) {
        Tribe tribe = gs.getTribe(tribeId);
        return gs.canEndTurn(tribe.getTribeId());
    }


    @Override
    public Action copy() {
        return new EndTurn(this.tribeId);
    }

    public String toString() {
        return "END_TURN by tribe " + this.tribeId;
    }
}
