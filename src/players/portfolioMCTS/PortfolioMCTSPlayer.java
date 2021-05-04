package players.portfolioMCTS;

import core.actions.Action;
import core.actions.tribeactions.EndTurn;
import core.actions.unitactions.Disband;
import core.game.GameState;
import players.Agent;
import players.portfolio.ActionAssignment;
import utils.ElapsedCpuTimer;
import utils.stats.AIStats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class PortfolioMCTSPlayer extends Agent {

    private final Random m_rnd;
    private PortfolioMCTSParams params;
    private PortfolioTreeNode m_root;
    private AIStats aiStats;

    public PortfolioMCTSPlayer(long seed)
    {
        super(seed);
        m_rnd = new Random(seed);
        this.params = new PortfolioMCTSParams();
        this.aiStats = new AIStats(this.playerID);
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

        m_root = new PortfolioTreeNode(params, m_rnd, this.playerID);
        m_root.setRootGameState(m_root, gs, allPlayerIDs);
        m_root.mctsSearch(ect);

        ActionAssignment act = m_root.getActions().get(m_root.bestAction());
//        ActionAssignment act = m_root.getActions().get(m_root.mostVisitedAction());

        this.updateBranchingFactor(gs);

        return act.getAction();
    }

    @Override
    public Agent copy() {
        return null;
    }

    /**
     * Returns the number of actions available for each of the actors, from the perspective of this agent.
     * By default, it's the same as the game state says - but overriding this function allows for pruning analysis.
     */
    public ArrayList<Integer> actionsPerUnit(GameState gs)
    {
        HashMap<Integer, Integer> actionsPerUnit = new HashMap<>();
        for(ActionAssignment aas : m_root.getActions())
        {
            int actorID = aas.getActor().getActorId();

            int n = actionsPerUnit.containsKey(actorID) ? actionsPerUnit.get(actorID) + 1 : 1;
            actionsPerUnit.put(actorID, n);
        }

        ArrayList<Integer> actionCounts = new ArrayList<>();
        for(int id : actionsPerUnit.keySet())
        {
            actionCounts.add(actionsPerUnit.get(id));
        }

        return actionCounts;
    }

    /**
     * Returns the total number of actions available in a game state, from the perspective of this agent.
     * By default, it's the same as the game state says - but overriding this function allows for pruning analysis.
     */
    public int actionsPerGameState(GameState gs)
    {
        return m_root.getActions().size();
    }

    public PortfolioMCTSParams getParams() {
        return params;
    }

    private void updateBranchingFactor(GameState gameState) {
        ArrayList<Integer> actionCounts = actionsPerUnit(gameState);
        aiStats.addBranchingFactor(gameState.getTick(), actionCounts);
        aiStats.addActionsPerStep(gameState.getTick(), actionsPerGameState(gameState));
    }


    /**
     * Function called at the end of the game. May be used by agents for final analysis.
     * @param reward - final reward for this agent.
     */
    public void result(GameState gs, double reward)
    {
        aiStats.print();
    }

}