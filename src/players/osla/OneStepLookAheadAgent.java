package players.osla;

import core.actions.Action;
import core.actions.tribeactions.EndTurn;
import core.actions.unitactions.Capture;
import core.actions.unitactions.Move;
import core.game.GameState;
import players.Agent;
import players.heuristics.TribesSimpleHeuristic;
import utils.ElapsedCpuTimer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class OneStepLookAheadAgent extends Agent {

    private Random m_rnd;
    private OSLAParams params;
    private int fmCalls;

    public OneStepLookAheadAgent(long seed, OSLAParams params)
    {
        super(seed);
        m_rnd = new Random(seed);
        this.params = params;
    }

    @Override
    public Action act(GameState gs, ElapsedCpuTimer ect)
    {
        //Gather all available actions:
        ArrayList<Action> allActions = gs.getAllAvailableActions();

        if(allActions.size() == 1)
            return allActions.get(0); //EndTurn

        fmCalls = 0;

//        System.out.println("tick: " + gs.getTick() + ", player: " + playerID + ", action space: " + allActions.size());

        //THIS IS JUST FOR DEBUG.
        //HashMap<Action, Double> scores = new HashMap<>();

        Action bestAction = null;
        double maxQ = Double.NEGATIVE_INFINITY;
        TribesSimpleHeuristic heuristic = new TribesSimpleHeuristic(this.getPlayerID());
        boolean end = false;
        int actionIdx = 0;
        while(!end)
        {
            Action act = allActions.get(actionIdx);

            if(act instanceof EndTurn) continue;

            GameState gsCopy = gs.copy();
            advance(gsCopy, act, false);
            double Q = heuristic.evaluateState(gsCopy);
            Q = noise(Q, params.epsilon, this.m_rnd.nextDouble());

            //scores.put(act, Q);
            //System.out.println(act + " : " + Q);

            //System.out.println("Action:" + action + " score:" + Q);
            if (Q > maxQ) {
                maxQ = Q;
                bestAction = act;
            }

            actionIdx++;
            end = (actionIdx == allActions.size() || (params.stop_type == params.STOP_FMCALLS && fmCalls >= params.num_fmcalls));
        }

        System.out.println("[Tribe: " + playerID + "] Tick " +  gs.getTick() + ", num actions: " + allActions.size() + ", FM calls: " + fmCalls + ". Executing " + bestAction);

        return bestAction;
    }

    /**
     * Adds a small noise to the input value.
     * @param input value to be altered
     * @param epsilon relative amount the input will be altered
     * @param random random variable in range [0,1]
     * @return epsilon-random-altered input value
     */
    private double noise(double input, double epsilon, double random)
    {
        return (input + epsilon) * (1.0 + epsilon * (random - 0.5));
    }

    private void advance(GameState gs, Action act, boolean computeActions)
    {
        gs.advance(act, computeActions);
        fmCalls++;
    }

    @Override
    public Agent copy() {
        return null;
    }
}
