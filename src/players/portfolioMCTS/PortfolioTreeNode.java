package players.portfolioMCTS;

import core.actions.Action;
import core.game.GameState;
import players.heuristics.PruneHeuristic;
import players.heuristics.StateHeuristic;
import players.portfolio.ActionAssignment;
import utils.ElapsedCpuTimer;
import utils.Utils;

import java.util.ArrayList;
import java.util.Random;

public class PortfolioTreeNode
{
    private PortfolioMCTSParams params;

    private PortfolioTreeNode root;
    private PortfolioTreeNode parent;
    private PortfolioTreeNode[] children;
    private boolean[] pruned;
    private double totValue;
    private int nVisits;
    private Random m_rnd;
    private int m_depth;
    private double[] bounds = new double[]{Double.MAX_VALUE, -Double.MAX_VALUE};
    private int fmCallsCount;
    private int playerID;
    private int k_plus;

    private ArrayList<ActionAssignment> actions;
    private GameState state;

    private GameState rootState;
    private StateHeuristic rootStateHeuristic;
    private PruneHeuristic rootPruneHeuristic;

    //TODO: Branching via pairs <unit, script>

    //From MCTSPlayer
    PortfolioTreeNode(PortfolioMCTSParams p, Random rnd, int playerID) {
        this(p, null, rnd, 0, null, null, null, playerID, null, null);
    }

    private PortfolioTreeNode(PortfolioMCTSParams p, PortfolioTreeNode parent, Random rnd, int num_actions,
                           ArrayList<ActionAssignment> actions, StateHeuristic sh, PruneHeuristic ph, int playerID, PortfolioTreeNode root, GameState state) {
        this.params = p;
        this.fmCallsCount = 0;
        this.parent = parent;
        this.m_rnd = rnd;
        this.actions = actions;
        this.root = root;
        children = new PortfolioTreeNode[num_actions];
        pruned = null;
        totValue = 0.0;
        k_plus = 0;
        this.playerID = playerID;
        this.state = state;
        if(parent != null) {
            m_depth = parent.m_depth + 1;
            this.rootStateHeuristic = sh;
            this.rootPruneHeuristic = ph;
        }
        else {
            m_depth = 0;
        }

    }

    void setRootGameState(PortfolioTreeNode root, GameState gs, ArrayList<Integer> allIDs)
    {
        this.state = gs;
        this.root = root;
        this.rootState = gs;
        this.rootStateHeuristic = params.getStateHeuristic(playerID, allIDs);

        //Init portfolio and action assignments at the root
        this.actions = this.params.getPortfolio().produceActionAssignments(gs);
        this.children = new PortfolioTreeNode[this.actions.size()];
        this.pruned = null;
        this.rootPruneHeuristic = params.getPruneHeuristic();

//        System.out.println("N Actions at root: " + this.actions.size());
//        for(ActionAssignment aas : this.actions)
//            System.out.println(aas);

    }


    void mctsSearch(ElapsedCpuTimer elapsedTimer) {

        double avgTimeTaken;
        double acumTimeTaken = 0;
        long remaining;
        int numIters = 0;

        int remainingLimit = 5;
        boolean stop = false;

        while(!stop){
//            System.out.println("------- " + root.actions.size() + " -------");
            ElapsedCpuTimer elapsedTimerIteration = new ElapsedCpuTimer();
            PortfolioTreeNode selected = treePolicy();
            double delta = selected.rollOut();
            backUp(selected, delta);
            numIters++;

            //Stopping condition
            if(params.stop_type == params.STOP_TIME) {
                acumTimeTaken += (elapsedTimerIteration.elapsedMillis()) ;
                avgTimeTaken  = acumTimeTaken/numIters;
                remaining = elapsedTimer.remainingTimeMillis();
                stop = remaining <= 2 * avgTimeTaken || remaining <= remainingLimit;
            }else if(params.stop_type == params.STOP_ITERATIONS) {
                stop = numIters >= params.num_iterations;
            }else if(params.stop_type == params.STOP_FMCALLS)
            {
                stop = fmCallsCount > params.num_fmcalls;
            }
        }
    }

    private PortfolioTreeNode treePolicy() {

        PortfolioTreeNode cur = this;

        while (!cur.state.isGameOver() && cur.m_depth < params.ROLLOUT_LENGTH)
        {
            if (cur.notFullyExpanded()) {
                return cur.expand();

            } else {
                cur = cur.uct();
            }
        }

        return cur;
    }


    private PortfolioTreeNode expand() {

        int bestAction = -1;

        //No turn end, expand
        double bestValue = -1;

        for (int i = 0; i < children.length; i++) {
            double x = m_rnd.nextDouble();
            if (x > bestValue && children[i] == null) {
                bestAction = i;
                bestValue = x;
            }
        }

        //Roll the state, create a new node and assign it.
        GameState nextState = state.copy();
        ArrayList<ActionAssignment> nextActions = advance(nextState, actions.get(bestAction), true);
        PortfolioTreeNode tn = new PortfolioTreeNode(params, this, this.m_rnd, nextActions.size(),
                nextActions, rootStateHeuristic, rootPruneHeuristic, this.playerID, this.m_depth == 0 ? this : this.root, nextState);
        children[bestAction] = tn;
        return tn;
    }

    private ArrayList<ActionAssignment> advance(GameState gs, ActionAssignment actionAssignment, boolean computeActions)
    {
        Action act = actionAssignment.getAction();
        gs.advance(act, computeActions);
        root.fmCallsCount++;
        return params.getPortfolio().produceActionAssignments(gs);
    }


    private PortfolioTreeNode uct() {

        PortfolioTreeNode selected;
        boolean IamMoving = (state.getActiveTribeID() == this.playerID);

        if(params.PRUNING && this.nVisits >= params.getPruneT(children.length)) {
            if (k_plus == 0) {
                //Time to prune
                pruned = rootPruneHeuristic.prune(this, actions, state, params.getPruneKinit(children.length));
                k_plus++;
//                System.out.print("Depth:" + this.m_depth + ": ");
//                params.printPruneLine(children.length);
            }
            else
            {
                //Progressive unpruning:
                int limit = params.getUnpruneLimit(children.length, k_plus);
                if(this.nVisits > limit)
                {
                    //Time to unprune
                    boolean[] prunedNext = rootPruneHeuristic.unprune(this, actions, state, pruned, m_rnd);
                    if(prunedNext != null) {
                        pruned = prunedNext;
                        k_plus++;
                    }
                }
            }
        }

        //No end turn, use uct.
        double[] vals = new double[this.children.length];
        for(int i = 0; i < this.children.length; ++i)
        {
            if(pruned != null && !pruned[i]) {

                PortfolioTreeNode child = children[i];

                double hvVal = child.totValue;
                double childValue = hvVal / (child.nVisits + params.epsilon);
                childValue = Utils.normalise(childValue, bounds[0], bounds[1]);
                double exploreValue = Math.sqrt(Math.log(this.nVisits + 1) / (child.nVisits + params.epsilon));
                double progBias = rootPruneHeuristic.evaluatePrune(state, actions.get(i)) / (child.nVisits + params.epsilon);

                double uctValue = childValue +
                        params.C * exploreValue;

                if(params.PRUNING)
                        uctValue += progBias;

                if(parent == null)
                {
                    int a = 0;
                }

                uctValue = noise(uctValue, params.epsilon, this.m_rnd.nextDouble());     //break ties randomly

//            double defValue = (childValue + params.K * exploreValue);
//            System.out.println("Default UCT: " + defValue +  ", UCT: " + uctValue +
//                    ", Q: " + childValue + ", explore: " + exploreValue + ", prog. Bias: " + progBias);

                vals[i] = uctValue;
            }
        }

        int which = -1;
        double bestValue = IamMoving ? -Double.MAX_VALUE : Double.MAX_VALUE;
        for(int i = 0; i < vals.length; ++i) {
            if ((IamMoving && vals[i] > bestValue) || (!IamMoving && vals[i] < bestValue)){
                which = i;
                bestValue = vals[i];
            }
        }

        if (which == -1)
        {
            //if(this.children.length == 0)
            System.out.println("Warning! couldn't find the best UCT value " + which + " : " + this.children.length + " " +
                    //throw new RuntimeException("Warning! couldn't find the best UCT value " + which + " : " + this.children.length + " " +
                    + bounds[0] + " " + bounds[1]);
            System.out.print(this.m_depth + ", AmIMoving? " + IamMoving + ";");
            for(int i = 0; i < this.children.length; ++i)
                System.out.printf(" %f2", vals[i]);
            System.out.println("; selected: " + which);

            which = m_rnd.nextInt(children.length);
        }

        selected = children[which];

//            System.out.print(this.m_depth + ", AmIMoving? " + IamMoving + ";");
//            for(int i = 0; i < this.children.length; ++i)
//                System.out.printf(" %f2", vals[i]);
//            System.out.println("; selected: " + which);

        //Roll the state. This is closed loop, we don't advance the state. We can't do open loop here because the
        // number of actions available on a state depend on the state itself, and random events triggered by multiple
        // runs over the same tree node would have different outcomes (i.e Examine ruins).
        //advance(state, actions.get(selected.childIdx), true);

        root.fmCallsCount++;

        return selected;
    }

    private double rollOut()
    {
        if(params.ROLOUTS_ENABLED) {
            GameState rolloutState = state.copy();
            int thisDepth = this.m_depth;
            while (!finishRollout(rolloutState, thisDepth)) {
                ArrayList<ActionAssignment> allActionPairs = params.getPortfolio().produceActionAssignments(rolloutState);
                ActionAssignment next = allActionPairs.get(m_rnd.nextInt(allActionPairs.size()));
                advance(rolloutState, next, true);
                thisDepth++;
            }
            return Utils.normalise(this.rootStateHeuristic.evaluateState(root.rootState, rolloutState), 0, 1);
        }

        return Utils.normalise(this.rootStateHeuristic.evaluateState(root.rootState, this.state), 0, 1);
    }

    private boolean finishRollout(GameState rollerState, int depth)
    {
        if (depth >= params.ROLLOUT_LENGTH)      //rollout end condition.
            return true;

        //end of game
        return rollerState.isGameOver();
    }


    private void backUp(PortfolioTreeNode node, double result)
    {
        PortfolioTreeNode n = node;
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

    public ArrayList<ActionAssignment> getActions() {
        return actions;
    }


    private boolean notFullyExpanded() {
        for (PortfolioTreeNode tn : children) {
            if (tn == null) {
                return true;
            }
        }

        return false;
    }



    private double noise(double input, double epsilon, double random)
    {
        return (input + epsilon) * (1.0 + epsilon * (random - 0.5));
    }

    public PortfolioTreeNode[] getChildren() {
        return children;
    }

    public int getPruneK() {
        return k_plus;
    }

    public PortfolioMCTSParams getParams()
    {
        return params;
    }
}
