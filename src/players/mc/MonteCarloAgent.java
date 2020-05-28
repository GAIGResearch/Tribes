package players.mc;

import core.actions.Action;
import core.actions.tribeactions.EndTurn;
import core.game.GameState;
import players.Agent;
import players.heuristics.StateHeuristic;
import players.heuristics.TribesSimpleHeuristic;
import utils.ElapsedCpuTimer;
import utils.StatSummary;

import java.util.ArrayList;
import java.util.Random;

public class MonteCarloAgent extends Agent {

    private Random m_rnd;
    private MCParams params;
    private StateHeuristic heuristic;

    public MonteCarloAgent(long seed, MCParams params)
    {
        super(seed);
        m_rnd = new Random(seed);
        this.params = params;
        this.heuristic = params.getHeuristic(playerID);
    }

    @Override
    public Action act(GameState gs, ElapsedCpuTimer ect) {
        //Gather all available actions:
        ArrayList<Action> allActions = gs.getAllAvailableActions();
        int numActions = allActions.size();

        if(numActions == 1)
            return allActions.get(0); //EndTurn, it's possible.

        int numRollouts = allActions.size() * params.N_ROLLOUT_MULT;
        StatSummary[] scores = new StatSummary[allActions.size()];

        Action bestAction = null;
        double maxQ = Double.NEGATIVE_INFINITY;
        for(int i = 0; i < numRollouts; ++i)
        {
            int rootActionIndex = m_rnd.nextInt(numActions);
            Action act = allActions.get(rootActionIndex);
            while(act instanceof EndTurn)
            {
                rootActionIndex = m_rnd.nextInt(numActions);
                act = allActions.get(rootActionIndex);
            }

            if(scores[rootActionIndex] == null)
                scores[rootActionIndex] = new StatSummary();

            double score = rollout(gs, act);

            //System.out.println("Rollout " + i + " scores " + score);

            scores[rootActionIndex].add(score);

            if(scores[rootActionIndex].mean() > maxQ)
            {
                maxQ = scores[rootActionIndex].mean();
                bestAction = act;
            }
        }

//        System.out.println("[Tribe: " + playerID + "] Tick " +  gs.getTick() + ", num actions: " + allActions.size() + ". Executing " + bestAction.toString());

        return bestAction;
    }

    private double rollout(GameState gs, Action act)
    {
        GameState gsCopy = gs.copy();
        boolean end = false;
        int step = 0;
        int turnEndCountDown = params.FORCE_TURN_END;
        boolean run = true;

        while(!end)
        {
            run = true;

            //If it's time to force a turn end, do it
            if(turnEndCountDown == 0)
            {
                EndTurn endTurn = new EndTurn(this.playerID);
                boolean canEndTurn = endTurn.isFeasible(gsCopy);

                if(canEndTurn) //check if we can actually end the turn.
                {
                    gsCopy.advance(endTurn, true);
                    turnEndCountDown = params.FORCE_TURN_END;
                    run = false;
                }
            }

            if(run)
            {
                gsCopy.advance(act, true);
                turnEndCountDown--;
            }

            step++;
            end = gsCopy.isGameOver() || (step == params.ROLLOUT_LENGTH);

            if(!end)
            {
                ArrayList<Action> allActions = gsCopy.getAllAvailableActions();
                int numActions = allActions.size();
                if(numActions == 1) {
                    act = allActions.get(m_rnd.nextInt(numActions));

                    if(act instanceof EndTurn)
                        turnEndCountDown = params.FORCE_TURN_END + 1;

                }else
                {
                    do {
                        int actIdx = m_rnd.nextInt(numActions);
                        act = allActions.get(actIdx);

                    }  while(act instanceof EndTurn);
                }
            }
        }

        return heuristic.evaluateState(gsCopy);
    }

    @Override
    public Agent copy() {
        return null;
    }
}
