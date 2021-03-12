package players.oep;

import core.actions.Action;
import core.actions.tribeactions.EndTurn;
import core.game.GameState;
import players.Agent;
import players.heuristics.StateHeuristic;
import utils.ElapsedCpuTimer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import static core.Types.ACTION.*;

public class OEPAgent extends Agent {

    private Random m_rnd;
    private StateHeuristic heuristic;
    private OEPParams params;

    ArrayList<Genome> pop = new ArrayList<>();
    int currentTurn = -1;
    long timeBudget = 0;
    boolean newTurn = true;
    int actionIndex = 0;


    public OEPAgent(long seed, OEPParams params) {
        super(seed);
        m_rnd = new Random(seed);
        this.params = params;
    }

    @Override
    public Action act(GameState gs, ElapsedCpuTimer ect) {

        this.heuristic = params.getStateHeuristic(playerID, allPlayerIDs);

        if (currentTurn != gs.getTick()){
            currentTurn = gs.getTick();
            actionIndex = 0;
            timeBudget = ect.remainingTimeMillis() / (gs.getAllAvailableActions().size() *3);
            newTurn = true;
        }

        if (newTurn){
            ect.setMaxTimeMillis(timeBudget);
            init(gs.copy());
            while (ect.remainingTimeMillis() > 0){
                for (Genome g : pop) {
                    GameState clone = gs.copy();
                    for (Action a : g.getActions()){
                        clone.advance(a, true);
                    }
                    if (g.getVisit() == 0){
                        g.setValue(eval(clone));
                        g.visited();
                    }
                }
                Collections.sort(pop);
                for (int i = params.POP_SIZE-1; i >= (params.POP_SIZE/2); i--){
                    pop.remove(i);
                }
                procreate(gs.copy());
            }

            System.out.println(gs.getTick());
            System.out.println(pop.get(0).getActions());
            System.out.println(pop.get(0).getActions().get(actionIndex));
            newTurn = false;
        }else{
            System.out.println(pop.get(0).getActions().get(actionIndex));
        }
        return pop.get(0).getActions().get(actionIndex++);

    }

    @Override
    public Agent copy() {
        return null;
    }

    private void init(GameState gs){
        pop = new ArrayList<>();
        for(int i = 0; i < params.POP_SIZE; i++){
            GameState clone = gs.copy();
            Genome g = new Genome(randomActions(clone));
            pop.add(g);
        }
    }

    private ArrayList<Action> randomActions(GameState gs){
        ArrayList<Action> actions = new ArrayList<>();
        while (!gs.isGameOver() && (gs.getActiveTribeID() == getPlayerID())){
            ArrayList<Action> allAvailableActions = gs.getAllAvailableActions();
            Action a = allAvailableActions.get(m_rnd.nextInt(allAvailableActions.size()));
            gs.advance(a, true);
            actions.add(a);
        }
        return actions;
    }

    private void procreate(GameState gs){
        int originalSize = pop.size();
        while (pop.size() < params.POP_SIZE){
            GameState clone = gs.copy();
            ArrayList<Action> actions = crossover(clone, originalSize);
            if (m_rnd.nextDouble() < params.MUTATION_RATE){
                mutation(gs.copy(), actions);
            }
            pop.add(new Genome(actions));
        }

    }

    private ArrayList<Action> crossover(GameState clone, int originalSize){
        ArrayList<Action> actions = new ArrayList<>();

        // Choose Parent
        int firstNum = m_rnd.nextInt(originalSize);
        int secondNum = m_rnd.nextInt(originalSize);
        while (firstNum == secondNum){
            secondNum = m_rnd.nextInt(pop.size());
        }
        int[] parent_index = {firstNum, secondNum};
        int min_action = Math.min(pop.get(parent_index[0]).getActions().size(), pop.get(parent_index[1]).getActions().size());
        int max_action = Math.max(pop.get(parent_index[0]).getActions().size(), pop.get(parent_index[1]).getActions().size());
        int max_action_parent = (pop.get(parent_index[0]).getActions().size() > pop.get(parent_index[1]).getActions().size()) ? parent_index[0]:parent_index[1];

        int index = 0;
        int actionIndex = 0;

        // While two parents has actions
        while (actionIndex < min_action){
            Action a = pop.get(parent_index[index%2]).getActions().get(actionIndex);
            Action b = pop.get(parent_index[(index+1)%2]).getActions().get(actionIndex);
            if (a.isFeasible(clone) && !(a.getActionType() == END_TURN)){
                clone.advance(a, true);
                actions.add(a);
                index ++;
                actionIndex++;
            }else if(b.isFeasible(clone) && !(b.getActionType() == END_TURN)){
                clone.advance(b, true);
                actions.add(b);
                actionIndex++;
            }else if((a.getActionType() == END_TURN) && (b.getActionType() == END_TURN)){
                Action endTurn = new EndTurn(getPlayerID());
                while (!endTurn.isFeasible(clone)){
                    ArrayList<Action> allAvailableActions = clone.getAllAvailableActions();
                    Action selectedAction = allAvailableActions.get(m_rnd.nextInt(allAvailableActions.size()));
                    actions.add(selectedAction);
                    clone.advance(selectedAction, true);
                    endTurn = new EndTurn(getPlayerID());
                }
                actions.add(endTurn);
                return actions;
            }else{
                actionIndex++;
            }
        }

        // While only one parent has action
        while (actionIndex < max_action){
            Action a = pop.get(max_action_parent).getActions().get(actionIndex);
            if (a.isFeasible(clone)){
                clone.advance(a, false);
                actions.add(a);
                actionIndex++;
            }else {
                actionIndex++;
            }
        }

        return actions;
    }

    private void mutation(GameState gs, ArrayList<Action> actions){

        int mutationIndex = m_rnd.nextInt(actions.size());
        for (int i=0; i<mutationIndex-1; i++){
            gs.advance(actions.get(i), false);
        }
        if (mutationIndex != 0){
            gs.advance(actions.get(mutationIndex-1), true);
        }
        Action selectedAction;
        ArrayList<Action> allAvailableActions = gs.getAllAvailableActions();
        if (allAvailableActions.size() == 1){
            actions.set(mutationIndex, allAvailableActions.get(0));
            for (int j=actions.size()-1; j>mutationIndex; j--) {
                actions.remove(j);
            }
            return;
        }else{
            selectedAction = allAvailableActions.get(m_rnd.nextInt(allAvailableActions.size()));
            while (selectedAction.getActionType() == END_TURN){
                selectedAction = allAvailableActions.get(m_rnd.nextInt(allAvailableActions.size()));
            }
            actions.set(mutationIndex, selectedAction);
        }

        for (int i=mutationIndex; i<actions.size(); i++){
            if (actions.get(i).isFeasible(gs)){
                gs.advance(actions.get(i), true);
            }else if (i == actions.size() - 1){
                Action a = new EndTurn(getPlayerID());
                while (!a.isFeasible(gs)){
                    allAvailableActions = gs.getAllAvailableActions();
                    selectedAction = allAvailableActions.get(m_rnd.nextInt(allAvailableActions.size()));
                    actions.add(selectedAction);
                    gs.advance(selectedAction, true);
                    a = new EndTurn(getPlayerID());
                }
                actions.add(a);
            }else{
                allAvailableActions = gs.getAllAvailableActions();
                if (allAvailableActions.size() == 1) {
                    actions.set(i, selectedAction);
                    for (int j=actions.size()-1; j>i; j--) {
                        actions.remove(j);
                    }
                }else{
                    selectedAction = allAvailableActions.get(m_rnd.nextInt(allAvailableActions.size()));
                    while (selectedAction.getActionType() == END_TURN){
                        selectedAction = allAvailableActions.get(m_rnd.nextInt(allAvailableActions.size()));
                    }
                    actions.set(i, selectedAction);
                    gs.advance(selectedAction, true);
                }
            }
        }
    }

    public double eval(GameState gs){

        for (int i=0; i<params.DEPTH; i++){
            ArrayList<Action> allAvailableActions = gs.getAllAvailableActions();
            gs.advance(allAvailableActions.get(m_rnd.nextInt(allAvailableActions.size())), true);
        }

        return heuristic.evaluateState(gs);
    }
}
