package players.mcts;

import core.actions.Action;
import core.actions.tribeactions.EndTurn;
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
    private boolean changeFM;

    public MCTSPlayer(long seed, boolean changeFM)
    {
        super(seed);
        m_rnd = new Random(seed);
        this.params = new MCTSParams();
        this.changeFM = changeFM;
    }

    public MCTSPlayer(long seed, MCTSParams params, boolean changeFM) {
        this(seed, changeFM);
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

        SingleTreeNode m_root = new SingleTreeNode(params, m_rnd, rootActions.size(), rootActions, this.playerID, changeFM);
        m_root.setRootGameState(m_root, gs, allPlayerIDs);

        m_root.mctsSearch(ect);

        return rootActions.get(m_root.mostVisitedAction());

    }

    public boolean isChangeFM(){
        return changeFM;
    }


    @Override
    public Agent copy() {
        return null;
    }

}