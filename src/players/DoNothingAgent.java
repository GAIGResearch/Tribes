package players;

import core.actions.Action;
import core.actions.tribeactions.EndTurn;
import core.game.GameState;
import utils.ElapsedCpuTimer;

public class DoNothingAgent extends Agent {

    public DoNothingAgent(long seed)
    {
        super(seed);
    }

    @Override
    public Action act(GameState gs, ElapsedCpuTimer ect) {
        return new EndTurn(gs.getActiveTribeID());
    }

    @Override
    public Agent copy() {
        return null;
    }
}
