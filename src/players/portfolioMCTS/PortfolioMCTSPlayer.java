package players.portfolioMCTS;

import core.actions.Action;
import core.actions.tribeactions.EndTurn;
import core.actions.unitactions.Disband;
import core.game.GameState;
import players.Agent;
import players.portfolio.ActionAssignment;
import utils.ElapsedCpuTimer;

import java.util.ArrayList;
import java.util.Random;

public class PortfolioMCTSPlayer extends Agent {

    private final Random m_rnd;
    private PortfolioMCTSParams params;

    public PortfolioMCTSPlayer(long seed)
    {
        super(seed);
        m_rnd = new Random(seed);
        this.params = new PortfolioMCTSParams();
    }

    public PortfolioMCTSPlayer(long seed, PortfolioMCTSParams params) {
        this(seed);
        this.params = params;
    }

    public Action act(GameState gs, ElapsedCpuTimer ect) {
        //Gather all available actions:
        ArrayList<Action> allActions = gs.getAllAvailableActions();

        if(allActions.size() == 1)
            return allActions.get(0); //EndTurn, it's possible.

//        ArrayList<Action> rootActions = params.PRIORITIZE_ROOT ? determineActionGroup(gs, m_rnd) : allActions;
//        if(rootActions == null)
//            return new EndTurn();

        PortfolioTreeNode m_root = new PortfolioTreeNode(params, m_rnd, this.playerID);
        m_root.setRootGameState(m_root, gs, allPlayerIDs);
        m_root.mctsSearch(ect);

        ActionAssignment act = m_root.getActions().get(m_root.mostVisitedAction());
        return act.process(gs);
    }

    @Override
    public Agent copy() {
        return null;
    }

}