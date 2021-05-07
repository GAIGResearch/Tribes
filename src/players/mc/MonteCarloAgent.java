package players.mc;

import core.Types;
import core.actions.Action;
import core.actions.tribeactions.EndTurn;
import core.actors.Tribe;
import core.game.GameState;
import players.Agent;
import players.heuristics.StateHeuristic;
import utils.ElapsedCpuTimer;
import utils.stats.StatSummary;

import java.util.ArrayList;
import java.util.Random;

public class MonteCarloAgent extends Agent {

    private Random m_rnd;
    private MCParams params;
    private StateHeuristic heuristic;
    private int lastTurn;
    private int actionTurnCounter;
    private int fmCalls;

    public MonteCarloAgent(long seed, MCParams params)
    {
        super(seed);
        m_rnd = new Random(seed);
        this.params = params;
        this.lastTurn = -1;
        this.actionTurnCounter = 0;
    }

    @Override
    public Action act(GameState gs, ElapsedCpuTimer ect) {

        //Set up the heuristic for this player
        this.heuristic = params.getStateHeuristic(playerID, allPlayerIDs);

        //Counter for turns and actions returned within the same turn.
        if(lastTurn == gs.getTick())
        {
            actionTurnCounter++;
        }else{
            lastTurn = gs.getTick();
            actionTurnCounter=0;
        }

        //Gather all available actions:
        ArrayList<Action> allActions = gs.getAllAvailableActions();
        int numActions = allActions.size();
        if(numActions == 1)
            return allActions.get(0); //EndTurn, it's possible.

        fmCalls = 0;
        boolean end = false;

        //Take one type of action at random. With Prioritize Root == true, we focus only on one subset of actions for the root (see determineActionGroup)
        // otherwise, all actions are in the bag.
        ArrayList<Action> rootActions = params.PRIORITIZE_ROOT ? determineActionGroup(gs, m_rnd) : allActions;
        if(rootActions == null)
            return new EndTurn(gs.getActiveTribeID());


        params.num_iterations = rootActions.size() * params.N_ROLLOUT_MULT;
        StatSummary[] scores = new StatSummary[rootActions.size()];

        Action bestAction = null;
        double maxQ = Double.NEGATIVE_INFINITY;
        int nRollouts = 0;
        while (!end)
        {
            int rootActionIndex = m_rnd.nextInt(rootActions.size());
            Action act = rootActions.get(rootActionIndex);

            //Let's avoid unwanted EndTurn actions
            while(act.getActionType() == Types.ACTION.END_TURN)
            {
                rootActionIndex = m_rnd.nextInt(rootActions.size());
                act = rootActions.get(rootActionIndex);
            }

//            System.out.println("----- " + gs.getTick() + ":" + actionTurnCounter + ":" + nRollouts + " ------ " + gs.getActiveTribeID());

            //Another rollout
            double score = rollout(gs, act);
            nRollouts++;

            //Update scores and keep a reference to the action with the highest average.
            if(scores[rootActionIndex] == null)
                scores[rootActionIndex] = new StatSummary();

            scores[rootActionIndex].add(score);
            if(scores[rootActionIndex].mean() > maxQ)
            {
                maxQ = scores[rootActionIndex].mean();
                bestAction = act;
            }

            //Stop conditions:
            if(params.stop_type == params.STOP_FMCALLS && fmCalls >= params.num_fmcalls)
                end = true;
            if(params.stop_type == params.STOP_ITERATIONS && nRollouts >= params.num_iterations)
                end = true;
        }

//        System.out.println("[Tribe: " + playerID + "] Tick " +  gs.getTick() + ", num actions: " + rootActions.size() +
//                ", FM calls: " + fmCalls + ". Executing " + bestAction.toString());

        //Chosen action to play
        return bestAction;
    }


    /**
     * Executes a Monte Carlo rollout.
     * @param gs current game state (a copy)
     * @param act action to start the rollout with.
     * @return the score of the state found at the end of the rollout, as evaluated by a heuristic
     */
    private double rollout(GameState gs, Action act)
    {
        GameState gsCopy = copyGameState(gs);
        boolean end = false;
        int step = 0;
        int turnEndCountDown = params.FORCE_TURN_END; // We force an EndTurn action every FORCE_TURN_END actions in the rollout.
        boolean run;

        while(!end)
        {
            run = true;

            //If it's time to force a turn end, do it
            if(turnEndCountDown == 0)
            {
                EndTurn endTurn = new EndTurn(gsCopy.getActiveTribeID());
                boolean canEndTurn = endTurn.isFeasible(gsCopy);

                if(canEndTurn) //check if we can actually end the turn (game may be expecting a non EndTurn action in Tribes).
                {
                    advance(gsCopy, endTurn, true);
                    turnEndCountDown = params.FORCE_TURN_END;
                    run = false;
                }
            }

            //Actually run the action
            if(run)
            {
                advance(gsCopy, act, true);
                turnEndCountDown--;
            }

            //Check if it's time to end this rollout. 1) either because it's a game end, 2) we've reached the end of it...
            step++;
            end = gsCopy.isGameOver() || (step == params.ROLLOUT_LENGTH);

            // ... or 3) we have no more thinking time available (agent's budget)
            boolean budgetOver = (params.stop_type == params.STOP_FMCALLS && fmCalls >= params.num_fmcalls);
            end |= budgetOver;

            if(!end)
            {
                //If we can continue, pick another action to run at random
                ArrayList<Action> allActions = gsCopy.getAllAvailableActions();
                int numActions = allActions.size();
                if(numActions == 1) {
                    //If there's only 1 action available, it should be an EndTurn
                    act = allActions.get(0);
                    if(act.getActionType() == Types.ACTION.END_TURN)
                        turnEndCountDown = params.FORCE_TURN_END + 1;
                    else
                        System.out.println("Warning: Unexpected non-EndTurn action in MC player");

                }else
                {
                    //If there are many actions, we select the next action for the rollout at random, avoiding EndTurn.
                    do {
                        int actIdx = m_rnd.nextInt(numActions);
                        act = allActions.get(actIdx);

                    }  while(act.getActionType() == Types.ACTION.END_TURN);
                }
            }
        }

        //We evaluate the state found at the end of the rollout with an heuristic.
        return heuristic.evaluateState(gs, gsCopy);
    }

    /**
     * Wrapper for advancing the game state. Updates the count of Forward Model usages.
     * @param gs game state to advance
     * @param act action to advance it with.
     * @param computeActions true if the game state should compute the available actions after advancing the state.
     */
    private void advance(GameState gs, Action act, boolean computeActions)
    {
        gs.advance(act, computeActions);
        fmCalls++;
    }

    /**
     * The technology trees of the opponents are always empty (no technology is researched).
     * As a simple case of gamestate injection, we research N technologies (N=turn/2) for them
     * @param gs current game state.
     */
    private void initTribesResearch(GameState gs)
    {
        int turn = gs.getTick();
        int techsToResearch = (int) (turn / 2.0);
        for(Tribe t : gs.getTribes())
        {
            if(t.getTribeId() != this.playerID)
            {
                for(int i = 0; i < techsToResearch; ++i)
                    t.getTechTree().researchAtRandom(this.m_rnd);
            }
        }
    }

    public GameState copyGameState(GameState gs)
    {
        GameState gsCopy = gs.copy();
        initTribesResearch(gsCopy);
        return gsCopy;
    }

    @Override
    public Agent copy() {
        return null; //not needed.
    }
}
