package players.mcts;

import core.actions.Action;
import core.actions.tribeactions.EndTurn;
import core.game.GameState;
import players.heuristics.StateHeuristic;
import players.heuristics.TribesEntropyHeuristic;
import players.heuristics.TribesSimpleHeuristic;
import utils.ElapsedCpuTimer;
import utils.Vector2d;

import java.util.ArrayList;
import java.util.Random;

public class SingleTreeNode
{
    public MCTSParams params;

    private SingleTreeNode parent;
    private SingleTreeNode[] children;
    private double totValue;
    private int nVisits;
    private Random m_rnd;
    private int m_depth;
    private double[] bounds = new double[]{Double.MAX_VALUE, -Double.MAX_VALUE};
    private int childIdx;
    private int fmCallsCount;
    private int playerID;
    private int turnEndCountDown;

    private ArrayList<Action> actions;

    private GameState rootState;
    private StateHeuristic rootStateHeuristic;

    SingleTreeNode(MCTSParams p, Random rnd, int num_actions, ArrayList<Action> actions, int playerID) {
        this(p, null, -1, rnd, num_actions, actions, 0, null, playerID);
    }

    private SingleTreeNode(MCTSParams p, SingleTreeNode parent, int childIdx, Random rnd, int num_actions,
                           ArrayList<Action> actions, int fmCallsCount, StateHeuristic sh, int playerID) {
        this.params = p;
        this.fmCallsCount = fmCallsCount;
        this.parent = parent;
        this.m_rnd = rnd;
        this.actions = actions;
        children = new SingleTreeNode[num_actions];
        totValue = 0.0;
        this.childIdx = childIdx;
        this.playerID = playerID;
        this.turnEndCountDown = p.getFORCE_TURN_END();
        if(parent != null) {
            m_depth = parent.m_depth + 1;
            this.rootStateHeuristic = sh;
        }
        else
            m_depth = 0;

    }

    void setRootGameState(GameState gs)
    {
        this.rootState = gs;
        if (params.heuristic_method == params.ENTROPY_HEURISTIC)
            this.rootStateHeuristic = new TribesEntropyHeuristic(playerID);
        else if (params.heuristic_method == params.SIMPLE_HEURISTIC) // New method: combined heuristics
            this.rootStateHeuristic = new TribesSimpleHeuristic(playerID);
    }


    void mctsSearch(ElapsedCpuTimer elapsedTimer) {

        double avgTimeTaken;
        double acumTimeTaken = 0;
        long remaining;
        int numIters = 0;

        int remainingLimit = 5;
        boolean stop = false;

        while(!stop){

            GameState state = rootState.copy();
            ElapsedCpuTimer elapsedTimerIteration = new ElapsedCpuTimer();
            SingleTreeNode selected = treePolicy(state);
            double delta = selected.rollOut(state);
            backUp(selected, delta);

            //Stopping condition
            if(params.stop_type == params.STOP_TIME) {
                numIters++;
                acumTimeTaken += (elapsedTimerIteration.elapsedMillis()) ;
                avgTimeTaken  = acumTimeTaken/numIters;
                remaining = elapsedTimer.remainingTimeMillis();
                stop = remaining <= 2 * avgTimeTaken || remaining <= remainingLimit;
            }else if(params.stop_type == params.STOP_ITERATIONS) {
                numIters++;
                stop = numIters >= params.num_iterations;
            }else if(params.stop_type == params.STOP_FMCALLS)
            {
                fmCallsCount+=params.rollout_depth;
                stop = (fmCallsCount + params.rollout_depth) > params.num_fmcalls;
            }
        }
    }

    private SingleTreeNode treePolicy(GameState state) {

        SingleTreeNode cur = this;

        while (!state.isGameOver() && state.getAllAvailableActions().size() > 1 && cur.m_depth < params.rollout_depth)
        {
            if (cur.notFullyExpanded()) {
                return cur.expand(state);

            } else {
                cur = cur.uct(state);
            }
        }

        return cur;
    }


    private SingleTreeNode expand(GameState state) {

        int bestAction = 0;
        double bestValue = -1;

        for (int i = 0; i < children.length; i++) {
            double x = m_rnd.nextDouble();
            if (x > bestValue && children[i] == null) {
                bestAction = i;
                bestValue = x;
            }
        }

        //Roll the state
        ArrayList<Action> newActions = roll(state, this.actions.get(bestAction), false);

        SingleTreeNode tn = new SingleTreeNode(params,this,bestAction,this.m_rnd,newActions.size(),
                newActions, fmCallsCount, rootStateHeuristic, this.playerID);
        children[bestAction] = tn;
        return tn;
    }

    private ArrayList<Action> roll(GameState gs, Action act, boolean isRollOut)
    {
        if (turnEndCountDown <= 0 && isRollOut){
            EndTurn endTurn = new EndTurn(gs.getActiveTribeID());
            if (endTurn.isFeasible(gs)){
                gs.advance(new EndTurn(gs.getActiveTribeID()), true);
                turnEndCountDown = params.getFORCE_TURN_END();
            }else{
                gs.advance(act, true);
            }
        }else{
            gs.advance(act, true);
        }
        return gs.getAllAvailableActions();
    }

    private SingleTreeNode uct(GameState state) {
        SingleTreeNode selected = null;
        double bestValue = -Double.MAX_VALUE;
        for (SingleTreeNode child : this.children)
        {
            double hvVal = child.totValue;
            double childValue =  hvVal / (child.nVisits + params.epsilon);

            childValue = normalise(childValue, bounds[0], bounds[1]);

            double uctValue = childValue +
                    params.K * Math.sqrt(Math.log(this.nVisits + 1) / (child.nVisits + params.epsilon));

            uctValue = noise(uctValue, params.epsilon, this.m_rnd.nextDouble());     //break ties randomly

            // small sampleRandom numbers: break ties in unexpanded nodes
            if (uctValue > bestValue) {
                selected = child;
                bestValue = uctValue;
            }
        }
        if (selected == null)
        {
            throw new RuntimeException("Warning! returning null: " + bestValue + " : " + this.children.length + " " +
                    + bounds[0] + " " + bounds[1]);
        }

        //Roll the state:
        roll(state, actions.get(selected.childIdx), false);

        return selected;
    }

    private double rollOut(GameState state)
    {
        int thisDepth = this.m_depth;

        while (!finishRollout(state,thisDepth)) {
            roll(state, state.getAllAvailableActions().get(m_rnd.nextInt(state.getAllAvailableActions().size())), true);
            thisDepth++;
        }

        return normalise(this.rootStateHeuristic.evaluateState(state), 0, 1);
    }

    private boolean finishRollout(GameState rollerState, int depth)
    {
        if (depth >= params.rollout_depth)      //rollout end condition.
            return true;

        if (rollerState.isGameOver())               //end of game
            return true;

        return false;
    }


    private void backUp(SingleTreeNode node, double result)
    {
        SingleTreeNode n = node;
        while(n != null)
        {
            n.nVisits++;
            n.totValue += result;
            if (result < n.bounds[0]) {
                n.bounds[0] = result;
            }
            if (result > n.bounds[1]) {
                n.bounds[1] = result;
            }
            n = n.parent;
        }
    }


    int mostVisitedAction() {
        int selected = -1;
        double bestValue = -Double.MAX_VALUE;
        boolean allEqual = true;
        double first = -1;

        for (int i=0; i<children.length; i++) {

            if(children[i] != null)
            {
                if(first == -1)
                    first = children[i].nVisits;
                else if(first != children[i].nVisits)
                {
                    allEqual = false;
                }

                double childValue = children[i].nVisits;
                childValue = noise(childValue, params.epsilon, this.m_rnd.nextDouble());     //break ties randomly
                if (childValue > bestValue) {
                    bestValue = childValue;
                    selected = i;
                }
            }
        }

        if (selected == -1)
        {
            selected = 0;
        }else if(allEqual)
        {
            //If all are equal, we opt to choose for the one with the best Q.
            selected = bestAction();
        }

        return selected;
    }

    private int bestAction()
    {
        int selected = -1;
        double bestValue = -Double.MAX_VALUE;

        for (int i=0; i<children.length; i++) {

            if(children[i] != null) {
                double childValue = children[i].totValue / (children[i].nVisits + params.epsilon);
                childValue = noise(childValue, params.epsilon, this.m_rnd.nextDouble());     //break ties randomly
                if (childValue > bestValue) {
                    bestValue = childValue;
                    selected = i;
                }
            }
        }

        if (selected == -1)
        {
            System.out.println("Unexpected selection!");
            selected = 0;
        }

        return selected;
    }


    private boolean notFullyExpanded() {
        for (SingleTreeNode tn : children) {
            if (tn == null) {
                return true;
            }
        }

        return false;
    }

    private double normalise(double a_value, double a_min, double a_max)
    {
        if(a_min < a_max)
            return (a_value - a_min)/(a_max - a_min);
        else    // if bounds are invalid, then return same value
            return a_value;
    }

    private double noise(double input, double epsilon, double random)
    {
        if(input != -epsilon) {
            return (input + epsilon) * (1.0 + epsilon * (random - 0.5));
        }else {
            //System.out.format("utils.tiebreaker(): WARNING: value equal to epsilon: %f\n",input);
            return (input + epsilon) * (1.0 + epsilon * (random - 0.5));
        }
    }

}
