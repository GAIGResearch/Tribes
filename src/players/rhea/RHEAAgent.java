package players.rhea;

import core.actions.Action;
import core.actions.cityactions.CityAction;
import core.actions.tribeactions.EndTurn;
import core.actions.unitactions.Attack;
import core.actions.unitactions.UnitAction;
import core.actors.Actor;
import core.actors.City;
import core.actors.units.Unit;
import core.game.GameState;
import players.Agent;
import players.heuristics.StateHeuristic;
import utils.ElapsedCpuTimer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class RHEAAgent extends Agent {

    private Random m_rnd;
    private StateHeuristic heuristic;
    private RHEAParams params;

    private ArrayList<Genome> pop;
    private int fmcalls;
    private int currentTurn;
    private boolean newTurn = true;
    private int actionInTurn = 0;


    public RHEAAgent(long seed, RHEAParams params) {
        super(seed);
        m_rnd = new Random(seed);
        this.params = params;
        pop = new ArrayList<>();
    }

    @Override
    public Action act(GameState gs, ElapsedCpuTimer ect) {

        this.heuristic = params.getHeuristic(playerID, allPlayerIDs);
        this.fmcalls = 0;

        if (currentTurn != gs.getTick()){
            currentTurn = gs.getTick();
            newTurn = true;
            actionInTurn = 0;
        }

        if (newTurn) {
//            System.out.println("------");
            init(gs);
        }else {
            pop = shiftPopulation(gs);
            actionInTurn++;
        }
        rheaLoop(gs);
        newTurn = false;

        Action toExecute = pop.get(0).getActions().get(0);
//        System.out.println(currentTurn + ":" + actionInTurn + ": toExecute: " + toExecute);

        return toExecute;
    }

    private void rheaLoop(GameState gs)
    {
        boolean end = false;
        int numIters = 0;
        while (!end){
            Collections.sort(pop);
            pop = nextGeneration(gs);
            numIters++;

            if(params.stop_type == params.STOP_FMCALLS)
                end = this.fmcalls >= params.num_fmcalls;
            else if(params.stop_type == params.STOP_ITERATIONS)
                end = numIters >= params.num_iterations;

        }
        //System.out.println(fmcalls);
        Collections.sort(pop);
    }

    private void init(GameState gs){
        pop = new ArrayList<>();
        for(int i = 0; i < params.POP_SIZE; i++){
            pop.add(newRandomIndividual(gs));
        }
    }

    private Genome newRandomIndividual(GameState gs)
    {
        //New individual
        ArrayList<Action> actions = new ArrayList<>();
        GameState gsCopy = gs.copy();

        while (!gsCopy.isGameOver() && actions.size() < params.INDIVIDUAL_LENGTH){
            Action a = getRandomAction(gsCopy);
            advance(gsCopy, a, true);
            actions.add(a);
        }

        Genome g = new Genome(actions);
        double score = heuristic.evaluateState(gs, gsCopy);
        g.setValue(score);
        return g;
    }

    private ArrayList<Genome> shiftPopulation(GameState gs)
    {
        ArrayList<Genome> newPop = new ArrayList<>();

        //We shift the first individual, which is the only one that is likely to be feasible
        shift(gs, pop.get(0));
        newPop.add(pop.get(0));

        //From 1 to (1+params.MUTATE_BEST), mutate the best individual
        for(int i = 1; i < 1+params.MUTATE_BEST && i < params.POP_SIZE; ++i) {
            Genome gen = mutate(pop.get(0), gs);
            newPop.add(gen);
        }

        //From 1+params.MUTATE_BEST to params.POP_SIZE, generate at random
        for(int i = 1+params.MUTATE_BEST; i < params.POP_SIZE; i++){
            newPop.add(newRandomIndividual(gs));
        }
        return newPop;
    }

    private void shift(GameState gs, Genome individual)
    {
        //shift buffer
        GameState clone = gs.copy();
        individual.shift();

        //advance the new game state
        boolean feasible = true;
        int j = 0;
        while(feasible && j < individual.getActions().size())
        {
            Action act = individual.getActions().get(j);
            feasible = checkActionFeasibility(act, clone);
            if(feasible)
            {
                advance(clone, act, true);
                j++;
            }
        }

        //add new random actions at the end, from where we stopped
        int i = j;
        while(!clone.isGameOver() && i < params.INDIVIDUAL_LENGTH)
        {
            Action newAction = getRandomAction(clone);
            individual.getActions().add(newAction);
            advance(clone, newAction, true);
            i++;
        }

        //Eval individual
        double score = heuristic.evaluateState(gs, clone);
        individual.setValue(score);
    }

    private Action getRandomAction(GameState gs)
    {
        if(gs.isGameOver())
            return null;

        ArrayList<Action> allAvailableActions = this.allGoodActions(gs, m_rnd);  //gs.getAllAvailableActions();
        return allAvailableActions.get(m_rnd.nextInt(allAvailableActions.size()));
    }

    private ArrayList<Genome> nextGeneration(GameState gs){
        ArrayList<Genome> newPop = new ArrayList<>();

        if(params.ELITISM && params.POP_SIZE > 1)
        {
            newPop.add(pop.get(0));
        }

        while (newPop.size() < params.POP_SIZE){
            Genome g;
            if(params.POP_SIZE > 1)
            {
                g = newIndividual(gs);
            }else{
                Genome gMut = mutate(pop.get(0), gs);
                g = (gMut.getValue() >= pop.get(0).getValue()) ? gMut : pop.get(0);
            }

            newPop.add(g);
        }
        return newPop;
    }

    private int[] selection(){

        //parent 1
        int p1idx = -1;
        double bestScore = -Double.MAX_VALUE;
        ArrayList<Integer> tournament = new ArrayList<>();
        while (tournament.size() < params.TOURNAMENT_SIZE)
        {
            int ind = m_rnd.nextInt(params.POP_SIZE);
            while(tournament.contains(ind))
            {
                ind = m_rnd.nextInt(params.POP_SIZE);
            }
            tournament.add(ind);

            //get the score of the individual, with small noise for tie breaks, to keep the best
            double indValue = noise(pop.get(ind).getValue(), params.epsilon, this.m_rnd.nextDouble());
            if(indValue > bestScore)
            {
                p1idx = ind;
                bestScore = indValue;
            }
        }

        //parent 2
        tournament = new ArrayList<>();
        int p2idx = -1;
        bestScore = -Double.MAX_VALUE;
        while (tournament.size() < params.TOURNAMENT_SIZE)
        {
            int ind = m_rnd.nextInt(params.POP_SIZE);
            while(tournament.contains(ind) && ind != p1idx)
            {
                ind = m_rnd.nextInt(params.POP_SIZE);
            }
            tournament.add(ind);

            double indValue = noise(pop.get(ind).getValue(), params.epsilon, this.m_rnd.nextDouble());
            if(indValue > bestScore)
            {
                p2idx = ind;
                bestScore = indValue;
            }
        }

        return new int[]{p1idx, p2idx};
    }

    private Genome newIndividual(GameState state){

        //The two parents to cross:
        int[] parentsIdx = selection();
        ArrayList<Action> newIndividual = new ArrayList<>();

        //Uniform crossover
        GameState crossState = state.copy();
        int actIdx = 0;

        while(!crossState.isGameOver() && actIdx < params.INDIVIDUAL_LENGTH)
        {
            Action candidate = null;
            boolean feasibleAction;

            //Let's see first if we are to mutate this gene.
            boolean mutate = m_rnd.nextDouble() < params.MUTATION_RATE;
            if(mutate)
            {
                candidate = getRandomAction(crossState);
                feasibleAction = true;
                //System.out.println("mutated");
            }else
            {
                //No mutation, crossover
                boolean firstParent = m_rnd.nextDouble()<0.5;
                Genome from = firstParent ? pop.get(parentsIdx[0]) : pop.get(parentsIdx[1]);
                if(actIdx < from.getActions().size()) {
                    candidate = from.getActions().get(actIdx);
                    feasibleAction = checkActionFeasibility(candidate, crossState);
                }else feasibleAction = false;

                if(!feasibleAction)
                {
                    //not feasible, try with the other parent
                    from = firstParent ? pop.get(parentsIdx[1]) : pop.get(parentsIdx[0]);
                    if(actIdx < from.getActions().size()) {
                        candidate = from.getActions().get(actIdx);
                        feasibleAction = checkActionFeasibility(candidate, crossState);

                    }else feasibleAction = false;

                   // System.out.println("second parent");
                }//else System.out.println("first parent");
            }

            if(feasibleAction && candidate != null)
            {
                advance(crossState, candidate, true);
                checkActionFeasibility(candidate, crossState);
                newIndividual.add(candidate);
            } //ELSE: Still not feasible: SKIP

            actIdx++;
        }

        //At this point, we have up newIndividual.size() actions, but if we skipped unfeasible actions, this is < INDIVIDUAL_LENGTH.
        // if that's the case, fill with random actions
        int curSize = newIndividual.size();
        while(!crossState.isGameOver() && curSize < params.INDIVIDUAL_LENGTH)
        {
            Action a = getRandomAction(crossState);
            advance(crossState, a, true);
            newIndividual.add(a);

            curSize++;
        }

        Genome newInd = new Genome(newIndividual);
        double score = heuristic.evaluateState(state, crossState);
        newInd.setValue(score);
        return newInd;
    }

    private Genome mutate(Genome gen, GameState state){

        ArrayList<Action> newIndividual = new ArrayList<>();
        GameState crossState = state.copy();
        int actIdx = 0;

        while(!crossState.isGameOver() && actIdx < gen.getActions().size())
        {
            Action candidate;

            //Let's see first if we are to mutate this gene.
            boolean mutate = m_rnd.nextDouble() < params.MUTATION_RATE;
            if(mutate)
            {
                candidate = getRandomAction(crossState);
            }else
            {
                //No mutation, keep it if possible
                candidate = gen.getActions().get(actIdx);
                boolean feasibleAction = checkActionFeasibility(candidate, crossState);
                if(!feasibleAction)
                {
                    //Generate at random anyway.
                    candidate = getRandomAction(crossState);
                }
            }

            advance(crossState, candidate, true);
            newIndividual.add(candidate);
            actIdx++;
        }

        Genome newInd = new Genome(newIndividual);
        double score = heuristic.evaluateState(state, crossState);
        newInd.setValue(score);

        return newInd;
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

    private void advance(GameState gs, Action act, boolean computeActions)
    {
        gs.advance(act, computeActions);
        fmcalls++;
    }

    private double noise(double input, double epsilon, double random)
    {
        return (input + epsilon) * (1.0 + epsilon * (random - 0.5));
    }

    @Override
    public Agent copy() {
        return null;
    }
}
