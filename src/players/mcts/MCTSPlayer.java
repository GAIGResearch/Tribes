package players.mcts;

import core.actions.Action;
import core.game.Game;
import core.game.GameState;
import players.Agent;
import utils.ElapsedCpuTimer;

import java.util.ArrayList;
import java.util.Random;

import static core.Constants.TURN_TIME_MILLIS;

public class MCTSPlayer extends Agent {

    private Random m_rnd;
    private MCTSParams params;

    public MCTSPlayer(long seed)
    {
        super(seed);
        m_rnd = new Random(seed);
        this.params = new MCTSParams();
    }

    public MCTSPlayer(long seed, MCTSParams params) {
        this(seed);
        this.params = params;
    }

    public Action act(GameState gs, ElapsedCpuTimer ect) {
        //Gather all available actions:
        ArrayList<Action> allActions = gs.getAllAvailableActions();
        int numActions = allActions.size();

        if(numActions == 1)
            return allActions.get(0); //EndTurn, it's possible.

        SingleTreeNode m_root = new SingleTreeNode(params, m_rnd, numActions, allActions, this.playerID);
        m_root.setRootGameState(gs);

        ect.setMaxTimeMillis(TURN_TIME_MILLIS/(numActions*2));
        m_root.mctsSearch(ect);


        Action action= allActions.get(m_root.mostVisitedAction());

        return action;

    }


    @Override
    public Agent copy() {
        return null;
    }

}