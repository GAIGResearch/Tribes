package players.osla;

import core.actions.Action;
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

    public double epsilon = 1e-6;
    private Random m_rnd;

    public OneStepLookAheadAgent(long seed)
    {
        super(seed);
        m_rnd = new Random(seed);
    }

    @Override
    public Action act(GameState gs, ElapsedCpuTimer ect)
    {
        //Gather all available actions:
        ArrayList<Action> allActions = gs.getAllAvailableActions();

        //THIS IS JUST FOR DEBUG.
        //HashMap<Action, Double> scores = new HashMap<>();

        Action bestAction = null;
        double maxQ = Double.NEGATIVE_INFINITY;
        TribesSimpleHeuristic heuristic = new TribesSimpleHeuristic(this.getPlayerID());
        for(Action act : allActions)
        {
            GameState gsCopy = gs.copy();
            gsCopy.next(act);
            double Q = heuristic.evaluateState(gsCopy);
            Q = noise(Q, this.epsilon, this.m_rnd.nextDouble());

            //scores.put(act, Q);
            //System.out.println(act + " : " + Q);

            //System.out.println("Action:" + action + " score:" + Q);
            if (Q > maxQ) {
                maxQ = Q;
                bestAction = act;
            }
        }

        //System.out.println("[Tribe: " + playerID + "] Tick " +  gs.getTick() + ", num actions: " + allActions.size() + ". Executing " + bestAction);

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

    @Override
    public Agent copy() {
        return null;
    }
}
