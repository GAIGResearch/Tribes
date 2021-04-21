package players.emcts;

import core.Types;
import core.actions.Action;
import core.actions.cityactions.CityAction;
import core.actions.unitactions.UnitAction;
import core.actors.Actor;
import core.actors.City;
import core.actors.units.Unit;
import core.game.GameState;
import players.Agent;
import players.heuristics.StateHeuristic;
import utils.ElapsedCpuTimer;

import java.util.ArrayList;
import java.util.Random;



public class EMCTSAgent extends Agent {

    private Random m_rnd;
    private StateHeuristic heuristic;
    private EMCTSParams params;

    private EMCTSTreeNode root;
    private EMCTSTreeNode bestNode;
    private GameState old_gs;

    private int fmCallsCount;
    private int fmCallsRun;


    public EMCTSAgent(long seed, EMCTSParams params) {
        super(seed);
        m_rnd = new Random(seed);
        this.params = params;
    }

    @Override
    public Action act(GameState gs, ElapsedCpuTimer ect) {

        double avgTimeTaken;
        double acumTimeTaken = 0;
        long remaining;
        int numIters = 0;

        this.fmCallsCount = 0;

        int remainingLimit = 5;
        boolean stop = false;


        this.heuristic = params.getHeuristic(playerID, allPlayerIDs);

        old_gs = gs.copy();

        root = randomActions(gs.copy());
        eval(gs.copy(), root);

        bestNode = root;
        fmCallsRun = 0;
        while (!stop) {
            numIters++;
            ElapsedCpuTimer elapsedTimerIteration = new ElapsedCpuTimer();
            fmCallsRun = 0;

            EMCTSTreeNode toMutate = nodeToExpand();

            //mutate to a cirtian depth
            int depth = 0;
            while (depth < params.depth) {
                EMCTSTreeNode child = mutate(toMutate, gs.copy());
                eval(gs.copy(), child);

                if (child.getValue() > bestNode.getValue()) {
                    bestNode = child;
                }

                child.visited();
                //extend(child, gs.copy());
                toMutate = child;
                depth++;
            }

            //update node values
            for (int i = 0; i < params.depth; i++) {
                toMutate.refreshScore(params.bias);
                toMutate = toMutate.getParent();
            }

            //System.out.println(numIters);
            if (params.stop_type == params.STOP_TIME) {
                acumTimeTaken += (elapsedTimerIteration.elapsedMillis());
                avgTimeTaken = acumTimeTaken / numIters;
                remaining = ect.remainingTimeMillis();
                stop = remaining <= 2 * avgTimeTaken || remaining <= remainingLimit;
            } else if (params.stop_type == params.STOP_ITERATIONS) {
                stop = numIters >= params.num_iterations;
            } else if (params.stop_type == params.STOP_FMCALLS) {
                stop = (fmCallsCount > params.num_fmcalls) || (fmCallsRun > (params.num_fmcalls - fmCallsCount));
            }
        }
        //if(fmCallsCount>2000){System.out.println(fmCallsCount);}

        return bestNode.returnNext();

    }

    @Override
    public Agent copy() {
        return null;
    }

    public void eval(GameState gs, EMCTSTreeNode node) {
        if(node.getGs() == null) {
            for (Action move : node.getSequence()) {
                advance(gs, move);
            }
            node.setGs(gs);
            node.setValue(heuristic.evaluateState(old_gs.copy(), gs.copy()));
        }else{
            node.setValue(heuristic.evaluateState(old_gs.copy(), node.getGs()));
        }
    }

    private EMCTSTreeNode nodeToExpand() {
        ArrayList<EMCTSTreeNode> children = root.getChildren();

        EMCTSTreeNode toMutate = null;

        if (children.size() == 0) {
            toMutate = root;
        } else {
            boolean found = false;
            double nodeScore = 0;
            EMCTSTreeNode nodeOn = root;
            nodeScore = nodeOn.getScore();
            while (!found) {
                if (nodeOn.getChildren().size() == 0) {
                    found = true;
                    toMutate = nodeOn;
                } else {
                    boolean bigger = false;
                    for (EMCTSTreeNode node : nodeOn.getChildren()) {
                        if (node.getScore() > nodeScore) {
                            bigger = true;
                            nodeOn = node;
                            nodeScore = node.getScore();
                        }
                        if (!bigger) {
                            found = true;
                            toMutate = nodeOn;
                        }
                    }
                }
            }
        }
        return toMutate;
    }

    //this version of random Actions will return a list of random actions and tries to make the length as long as possible
    private EMCTSTreeNode randomActions(GameState gs) {
        ArrayList<Action> individual = new ArrayList<>();
        while ((!gs.isGameOver() && (gs.getActiveTribeID() == getPlayerID())) && (individual.size() < (params.NODE_SIZE-1))){
            ArrayList<Action> allAvailableActions = this.allGoodActions(gs, m_rnd);
            Action a = allAvailableActions.get(m_rnd.nextInt(allAvailableActions.size()));
            if (!(a.getActionType() == Types.ACTION.END_TURN)) {
                advance(gs, a);
                individual.add(a);
            }else if(allAvailableActions.size() == 1){
                advance(gs,a);
                individual.add(a);
            }
        }
        if(individual.size() == (params.NODE_SIZE-1) && (individual.get((params.NODE_SIZE-2)).getActionType() != Types.ACTION.END_TURN)){
            ArrayList<Action> allAvailableActions = this.allGoodActions(gs, m_rnd);
            Action end = null; ;
            for(Action a : allAvailableActions){
                if(a.getActionType() == Types.ACTION.END_TURN){
                    end = a;
                    break;
                }
            }
            individual.add(end);
        }
        EMCTSTreeNode node = new EMCTSTreeNode(individual, null);
        node.setGs(gs.copy());
        return node;
    }


    private void advance(GameState gs, Action move) {
        this.fmCallsCount++;
        this.fmCallsRun++;
        gs.advance(move, true);
    }

    //mutate a random action
    private EMCTSTreeNode mutate(EMCTSTreeNode node, GameState gs) {
        GameState clone = gs.copy();
        ArrayList<Action> seq = node.getSequence();
        int moveToMutate = m_rnd.nextInt(seq.size());
        ArrayList<Action> newSeq = new ArrayList<>();
        for (int i = 0; i < moveToMutate; i++) {
            advance(gs, seq.get(i));
            newSeq.add(seq.get(i));
        }
        ArrayList<Action> allAvailableActions = this.allGoodActions(gs, m_rnd);
        Action a = allAvailableActions.get(m_rnd.nextInt(allAvailableActions.size()));
        newSeq.add(a);

        for (int i = moveToMutate + 1; i < seq.size(); i++) {
            newSeq.add(seq.get(i));
        }
        newSeq = repair(newSeq, clone);
        EMCTSTreeNode newNode = new EMCTSTreeNode(newSeq, node);
        node.addChild(newNode);

        return newNode;
    }

    //repair the node and make sure that it is valid
    private ArrayList<Action> repair(ArrayList<Action> child, GameState gs) {
        ArrayList<Action> repairedChild = new ArrayList<>();

        for (int a = 0; a < child.size(); a++) {
            if (!(gs.getActiveTribeID() == getPlayerID())) {
                return repairedChild;
            }

            try {
                boolean done = checkActionFeasibility(child.get(a), gs.copy());

                if (!done) {
                    ArrayList<Action> allAvailableActions = this.allGoodActions(gs.copy(), m_rnd);
                    Action ac = allAvailableActions.get(m_rnd.nextInt(allAvailableActions.size()));
                    advance(gs, ac);
                    repairedChild.add(ac);
                } else {
                    repairedChild.add(child.get(a));
                    advance(gs, child.get(a));
                }
            } catch (Exception e) {
                ArrayList<Action> allAvailableActions = this.allGoodActions(gs, m_rnd);
                Action ac = allAvailableActions.get(m_rnd.nextInt(allAvailableActions.size()));
                advance(gs, ac);
                repairedChild.add(ac);
            }
        }
        if (!(repairedChild.get(repairedChild.size() - 1).getActionType() == Types.ACTION.END_TURN)) {
            ArrayList<Action> allAvailableActions = this.allGoodActions(gs, m_rnd);
            Action end = null; ;
            for(Action a : allAvailableActions){
                if(a.getActionType() == Types.ACTION.END_TURN){
                    end = a;
                    break;
                }
            }
            repairedChild.add(end);
        }

        return repairedChild;
    }

    private boolean checkActionFeasibility(Action a, GameState gs) {
        if (gs.isGameOver())
            return false;

        if (a instanceof UnitAction) {
            UnitAction ua = (UnitAction) a;
            int unitId = ua.getUnitId();
            Actor act = gs.getActor(unitId);
            if (!(act instanceof Unit) || act.getTribeId() != gs.getActiveTribeID())
                return false;
        } else if (a instanceof CityAction) {
            CityAction ca = (CityAction) a;
            int cityId = ca.getCityId();
            Actor act = gs.getActor(cityId);
            if (!(act instanceof City) || act.getTribeId() != gs.getActiveTribeID())
                return false;
        }

        boolean feasible = false;
        try {
            feasible = a.isFeasible(gs);
        } catch (Exception e) {
        }

        return feasible;
    }

    // try to see of the node can be extended
    private void extend(EMCTSTreeNode node, GameState gs) {
        GameState clone = gs.copy();
        ArrayList<Action> currentActions = node.getSequence();
        ArrayList<Action> newActions = new ArrayList<>();
        for (int i = 0; i < currentActions.size() - 1; i++) {
            newActions.add(currentActions.get(i));
            advance(gs, currentActions.get(i));
        }

        if (this.allGoodActions(gs, m_rnd).size() > 1) {
            while (!gs.isGameOver() && (gs.getActiveTribeID() == getPlayerID())) {
                ArrayList<Action> allAvailableActions = this.allGoodActions(gs, m_rnd);
                Action a = allAvailableActions.get(m_rnd.nextInt(allAvailableActions.size()));
                if (!(a.getActionType() == Types.ACTION.END_TURN)) {
                    advance(gs, a);
                    newActions.add(a);
                } else if (allAvailableActions.size() == 1) {
                    advance(gs, a);
                    newActions.add(a);
                    break;
                }
            }
        } else {
            return;
        }
        //newActions = repair(newActions, clone.copy());
        EMCTSTreeNode newNode = new EMCTSTreeNode(newActions, node.getParent());
        eval(clone, newNode);
        if (newNode.getValue() > node.getValue()) {
            node.setSequence(newActions);
            node.setValue(newNode.getValue());
        }
        //clear links so that JVM can clear it
        newNode.unlink();
    }
}