package players.mcts_new;

import core.actions.Action;
import core.actions.tribeactions.EndTurn;
import core.game.GameState;
import players.Agent;
import players.mcts.MCTSParams;
import players.mcts_new.MCTS;
import utils.ElapsedCpuTimer;

import java.util.ArrayList;
import java.util.Random;

public class MCTS_newPlayer extends Agent {

    private Random m_rnd;
    private MCTSParams params;
    private int time;

    public MCTS_newPlayer(long seed)
    {
        super(seed);
        m_rnd = new Random(seed);
        this.params = new MCTSParams();
    }

    public MCTS_newPlayer(long seed, MCTSParams params) {
        this(seed);
        this.params = params;
    }

    public Action act(GameState gs, ElapsedCpuTimer ect) {
        //Gather all available actions:
        ArrayList<Action> allActions = gs.getAllAvailableActions();

        if(allActions.size() == 1)
            return allActions.get(0); //EndTurn, it's possible.

        ArrayList<Action> rootActions = params.PRIORITIZE_ROOT ? determineActionGroup(gs, m_rnd) : allActions;
        if(rootActions == null)
            return new EndTurn();

        MCTS mcts = new MCTS(params, playerID, gs.copy(), m_rnd, allPlayerIDs, rootActions);
        mcts.search(ect);
        return mcts.mostVisitedAction();

    }


    @Override
    public Agent copy() {
        return null;
    }

}