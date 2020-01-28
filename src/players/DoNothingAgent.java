package players;

import core.actions.Action;
import core.game.GameState;
import utils.ElapsedCpuTimer;

public class DoNothingAgent extends Agent {

    public DoNothingAgent(long seed)
    {
        super(seed);
    }

    @Override
    public Action act(GameState gs, ElapsedCpuTimer ect) {
        return null;
    }

    @Override
    public Agent copy() {
        return null;
    }
}
