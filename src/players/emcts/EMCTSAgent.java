package players.emcts;

import core.actions.Action;
import core.game.GameState;
import players.Agent;
import players.heuristics.StateHeuristic;
import utils.ElapsedCpuTimer;
import java.util.Random;



public class EMCTSAgent extends Agent {

    /*
    * ->create a root node that is a sequence of moves
    *
    * ->create a exploration alg that looks for a node to expand
    *
    * -> expand that node with a mutation
    *
    * -> have an eval method to eval each node to return the most promising move
    *   ->ucb ->value + bias * root(Ln(Num parent visited)/num times visited)
    *
    * */

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
