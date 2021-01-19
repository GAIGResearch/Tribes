package players.emcts;

import core.actions.Action;
import core.actions.cityactions.CityAction;
import core.actions.unitactions.UnitAction;
import core.actors.Actor;
import core.actors.City;
import core.actors.units.Unit;
import core.game.GameState;
import players.Agent;
import players.heuristics.StateHeuristic;
import players.oep.Individual;
import utils.ElapsedCpuTimer;

import java.util.ArrayList;
import java.util.Random;



public class EMCTSAgent extends Agent {

    /*
    * ->create a root node that is a sequence of moves
    *---------------------Done To This Point--------------------------------
    * ->create a exploration alg that looks for a node to expand
    *
    * -> expand that node with a mutation to a given depth
    *
    * -> have an eval method to eval each node to return the most promising move
    *   ->ucb ->value + bias * root(Ln(Num parent visited)/num times visited)
    *
    * keep expanding untill every position hasnt been expanded.
    *
    * */

    private Random m_rnd;
    private StateHeuristic heuristic;
    private EMCTSParams params;

    private EMCTSTreeNode root;
    private EMCTSTreeNode bestNode;

    private int fmCallsCount;

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

        int remainingLimit = 5;
        boolean stop = false;

        this.heuristic = params.getHeuristic(playerID, allPlayerIDs);

        root = new EMCTSTreeNode(randomActions(gs.copy()), null);
        eval(gs.copy(), root);

        while(!stop) {
            numIters++;
            ElapsedCpuTimer elapsedTimerIteration = new ElapsedCpuTimer();

            EMCTSTreeNode toMutate = nodeToExpand();

            int depth = 0;
            while(depth < params.depth){
                EMCTSTreeNode child = mutate(toMutate, gs.copy());

            }

            if (params.stop_type == params.STOP_TIME) {
                acumTimeTaken += (elapsedTimerIteration.elapsedMillis());
                avgTimeTaken = acumTimeTaken / numIters;
                remaining = ect.remainingTimeMillis();
                stop = remaining <= 2 * avgTimeTaken || remaining <= remainingLimit;
            } else if (params.stop_type == params.STOP_ITERATIONS) {
                stop = numIters >= params.num_iterations;
            } else if (params.stop_type == params.STOP_FMCALLS) {
                stop = fmCallsCount > params.num_fmcalls;
            }
        }

        return null;
    }

    @Override
    public Agent copy() {
        return null;
    }

    public void eval(GameState gs, EMCTSTreeNode node){
        for(Action move : node.getSequence()){
            advance(gs, move);
        }
        node.setGs(gs);
        node.setValue(heuristic.evaluateState(gs));
    }

    private EMCTSTreeNode nodeToExpand(){
        ArrayList<EMCTSTreeNode> children = root.getChildren();

        EMCTSTreeNode toMutate = null;

        if(children.size() == 0){
            toMutate = root;
        }else{
            boolean found = false;
            double nodeScore = 0;
            EMCTSTreeNode nodeOn = root;
            nodeScore = nodeOn.getScore();
            while(!found){
                if(nodeOn.getChildren().size() == 0){
                    found = true;
                }else{
                    boolean bigger = false;
                    for(EMCTSTreeNode node : nodeOn.getChildren()){
                        if(node.getScore() > nodeScore){
                            bigger = true;
                            nodeOn = node;
                            nodeScore = node.getScore();
                        }
                        if(!bigger){
                            found = true;
                        }
                    }
                }
            }
        }
        return toMutate;
    }

    private boolean checkActionFeasibility(Action a, GameState gs)
    {
        if(gs.isGameOver())
            return false;

        if(a instanceof UnitAction)
        {
            UnitAction ua = (UnitAction)a;
            int unitId = ua.getUnitId();
            Actor act = gs.getActor(unitId);
            if(!(act instanceof Unit) || act.getTribeId() != gs.getActiveTribeID())
                return false;
        }else if (a instanceof CityAction)
        {
            CityAction ca = (CityAction)a;
            int cityId = ca.getCityId();
            Actor act = gs.getActor(cityId);
            if(!(act instanceof City) || act.getTribeId() != gs.getActiveTribeID())
                return false;
        }

        boolean feasible = false;
        try{
            feasible = a.isFeasible(gs);
        }catch (Exception e) { }

        return feasible;
    }

    private ArrayList<Action> randomActions(GameState gs){
        ArrayList<Action> individual = new ArrayList<>();
        while (!gs.isGameOver() && (gs.getActiveTribeID() == getPlayerID())){
            ArrayList<Action> allAvailableActions = this.allGoodActions(gs, m_rnd);
            Action a = allAvailableActions.get(m_rnd.nextInt(allAvailableActions.size()));
            advance(gs, a);
            individual.add(a);

        }
        return individual;
    }

    private void advance(GameState gs, Action move){
        this.fmCallsCount++;
        gs.advance(move,true);
    }

    private EMCTSTreeNode mutate(EMCTSTreeNode node, GameState gs){
        // mutate method
        return null;
    }
}
