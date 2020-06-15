package players.mcts_new;

import core.actions.Action;
import core.game.GameState;
import players.heuristics.StateHeuristic;
import players.mcts.MCTSParams;
import utils.ElapsedCpuTimer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class MCTS {

    private MCTSParams params;
    private int playerID;
    private Random m_rnd;
    private int fmCallsCount;

    private double[] bounds = new double[]{Double.MAX_VALUE, -Double.MAX_VALUE};

    private TreeNode root;
    private GameState rootState;
    private StateHeuristic rootStateHeuristic;

    public MCTS(MCTSParams params, int playerID, GameState rootState, Random m_rnd, ArrayList<Integer> allIDs, ArrayList<Action> actions) {
        this.params = params;
        this.playerID = playerID;
        this.rootState = rootState;
        this.m_rnd = m_rnd;
        this.rootStateHeuristic = params.getHeuristic(playerID, allIDs);
        root = new TreeNode(actions, rootState.copy(), playerID, params);

        System.out.println("----------------------------------");
        expand(root);
        expand(root);
        System.out.println(root.toString());
        expand(root.getExploredNodes().get(0));
        expand(root.getExploredNodes().get(1));

        root.setnVisits(5);
        root.getExploredNodes().get(0).setnVisits(3);
        root.getExploredNodes().get(1).setnVisits(2);
        uct(root);

        System.out.println(root.getExploredNodes().get(0));
        System.out.println(root.getExploredNodes().get(1));
    }

    public void search(ElapsedCpuTimer ect){

        double avgTimeTaken;
        double acumTimeTaken = 0;
        long remaining;
        int numIters = 0;

        int remainingLimit = 5;
        boolean stop = false;

        while(!stop){
            ElapsedCpuTimer elapsedTimerIteration = new ElapsedCpuTimer();
            TreeNode selected = treePolicy();
            double delta = rollOut(selected);
//            backUp(selected, delta);
            numIters++;

            //Stopping condition
            if(params.stop_type == params.STOP_TIME) {
                acumTimeTaken += (elapsedTimerIteration.elapsedMillis()) ;
                avgTimeTaken  = acumTimeTaken/numIters;
                remaining = ect.remainingTimeMillis();
                stop = remaining <= 2 * avgTimeTaken || remaining <= remainingLimit;
            }else if(params.stop_type == params.STOP_ITERATIONS) {
                stop = numIters >= params.num_iterations;
            }else if(params.stop_type == params.STOP_FMCALLS)
            {
                stop = fmCallsCount > params.num_fmcalls;
            }
        }
    }

    private TreeNode treePolicy() {

        TreeNode currentNode = root;

        while (!currentNode.getGameState().isGameOver() && currentNode.getDepth() < params.ROLLOUT_LENGTH) {
            if (currentNode.isExpandable()) {
                return expand(currentNode);
            } else {
                currentNode = uct(currentNode);
            }
        }

        return currentNode;
    }

    private TreeNode expand(TreeNode currentNode){
        if (currentNode.totalAction() == 0){
            currentNode.action2Node();
        }
        return currentNode.explore(m_rnd.nextInt(currentNode.getUnexploredNodeNum()));
    }

    private TreeNode uct(TreeNode currentNode){

        Collections.sort(currentNode.getExploredNodes());
        return currentNode.getExploredNodes().get(0);

    }

    private double rollOut(TreeNode currentNode){

        return 0;
    }

}
