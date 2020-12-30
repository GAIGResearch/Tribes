package players.emcts;

import core.actions.Action;
import core.game.GameState;
import players.Agent;
import players.heuristics.StateHeuristic;
import utils.ElapsedCpuTimer;
import java.util.Random;



public class EMCTSAgent extends Agent {

    private Random m_rnd;
    private StateHeuristic heuristic;
    private EMCTSParams params;

    public EMCTSAgent(long seed, EMCTSParams params) {
        super(seed);
        m_rnd = new Random(seed);
        this.params = params;
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
