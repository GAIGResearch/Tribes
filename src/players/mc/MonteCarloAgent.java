package players.mc;

import core.actions.Action;
import core.actions.tribeactions.EndTurn;
import core.game.GameState;
import players.Agent;
import players.heuristics.StateHeuristic;
import utils.ElapsedCpuTimer;
import utils.StatSummary;

import java.util.ArrayList;
import java.util.Random;

public class MonteCarloAgent extends Agent {

    enum ACTION_TYPE
    {
        CITY,
        TRIBE,
        UNIT
    }


    private Random m_rnd;
    private MCParams params;
    private StateHeuristic heuristic;
    private int lastTurn;
    private int actionTurnCounter;

    private int fmCalls;

    public MonteCarloAgent(long seed, MCParams params)
    {
        super(seed);
        m_rnd = new Random(seed);
        this.params = params;
        this.heuristic = params.getHeuristic(playerID);
        this.lastTurn = -1;
        this.actionTurnCounter = 0;
    }

    @Override
    public Action act(GameState gs, ElapsedCpuTimer ect) {

        if(lastTurn == gs.getTick())
        {
            actionTurnCounter++;
        }else{
            lastTurn = gs.getTick();
            actionTurnCounter=0;
        }

        //Gather all available actions:
        ArrayList<Action> allActions = gs.getAllAvailableActions();
        int numActions = allActions.size();

        if(numActions == 1)
            return allActions.get(0); //EndTurn, it's possible.

        fmCalls = 0;
        boolean end = false;

        //Take one type of action at random:

        ArrayList<Action> rootActions = params.PRIORITIZE_ROOT ? determineActionGroup(gs) : allActions;

        params.num_iterations = rootActions.size() * params.N_ROLLOUT_MULT;
        StatSummary[] scores = new StatSummary[rootActions.size()];

        Action bestAction = null;
        double maxQ = Double.NEGATIVE_INFINITY;
//        for(int i = 0; i < numRollouts; ++i)
        int nRollouts = 0;
        while (!end)
        {
            int rootActionIndex = m_rnd.nextInt(rootActions.size());
            Action act = rootActions.get(rootActionIndex);

            while(act instanceof EndTurn)
            {
                rootActionIndex = m_rnd.nextInt(rootActions.size());
                act = rootActions.get(rootActionIndex);
            }

            if(scores[rootActionIndex] == null)
                scores[rootActionIndex] = new StatSummary();


//            System.out.println("----- " + gs.getTick() + ":" + actionTurnCounter + ":" + nRollouts + " ------");

            double score = rollout(gs, act);

            //System.out.println("Rollout " + i + " scores " + score);

            scores[rootActionIndex].add(score);

            if(scores[rootActionIndex].mean() > maxQ)
            {
                maxQ = scores[rootActionIndex].mean();
                bestAction = act;
            }

            nRollouts++;

            if(params.stop_type == params.STOP_FMCALLS && fmCalls >= params.num_fmcalls)
                end = true;
            if(params.stop_type == params.STOP_ITERATIONS && nRollouts >= params.num_iterations)
                end = true;

        }

//        System.out.println("[Tribe: " + playerID + "] Tick " +  gs.getTick() + ", num actions: " + rootActions.size() +
//                ", FM calls: " + fmCalls + ". Executing " + bestAction.toString());

        return bestAction;
    }

    private ArrayList<Action> determineActionGroup(GameState gs)
    {
        ArrayList<ACTION_TYPE> availableTypes = new ArrayList<>();

        ArrayList<Action> cityActions = gs.getAllCityActions();
        if(cityActions.size() > 0) availableTypes.add(ACTION_TYPE.CITY);

        ArrayList<Action> unitActions = gs.getAllUnitActions();
        if(unitActions.size() > 0) availableTypes.add(ACTION_TYPE.UNIT);

        ArrayList<Action> tribeActions = gs.getTribeActions();
        if(tribeActions.size() > 1) availableTypes.add(ACTION_TYPE.TRIBE); //Need something else than EndTurn only.

        int rndIdx = m_rnd.nextInt(availableTypes.size());
        ACTION_TYPE rootAction = availableTypes.get(rndIdx);
        if(rootAction == ACTION_TYPE.CITY)
        {
//            System.out.println("Going for cities");
            return cityActions;
        }
        if(rootAction == ACTION_TYPE.UNIT)
        {
//            System.out.println("Going for units");
            return unitActions;
        }
//        System.out.println("Going for tribe");
        return tribeActions;
    }


    private double rollout(GameState gs, Action act)
    {
        GameState gsCopy = gs.copy();
        boolean end = false;
        int step = 0;
        int turnEndCountDown = params.FORCE_TURN_END;
        boolean run;

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
                    advance(gsCopy, endTurn, true);
                    turnEndCountDown = params.FORCE_TURN_END;
                    run = false;
                }
            }

            if(run)
            {
                advance(gsCopy, act, true);
                turnEndCountDown--;
            }

            step++;
            end = gsCopy.isGameOver() || (step == params.ROLLOUT_LENGTH);

            boolean budgetOver = (params.stop_type == params.STOP_FMCALLS && fmCalls >= params.num_fmcalls);
            end |= budgetOver;

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

    private void advance(GameState gs, Action act, boolean computeActions)
    {
        gs.advance(act, computeActions);
//        System.out.println(act);
        fmCalls++;
    }

    @Override
    public Agent copy() {
        return null;
    }
}
