package players;

import core.actions.Action;
import core.game.GameState;
import utils.ElapsedCpuTimer;
import utils.Vector2d;

import java.util.ArrayList;

public class SimpleAgent extends Agent {

    private ArrayList<Vector2d> recentlyVisitedPositions;
    /**
     * Default constructor, to be called in subclasses (initializes player ID and random seed for this agent.
     *
     * @param seed - random seed for this player.
     */
    protected SimpleAgent(long seed) {
        super(seed);
    }


    @Override
    public Agent copy() {
        SimpleAgent player = new SimpleAgent(seed);
        return player;
    }

    @Override
    public Action act(GameState gs, ElapsedCpuTimer ect) {
        //Gather all available actions:
        ArrayList<Action> allActions = gs.getAllAvailableActions();
        //Initially pick a random action so that at least that can be returned
        Action bestAction = allActions.get(gs.getRandomGenerator().nextInt(allActions.size()));
        float bestActionScore = evalAction(bestAction);
        for (Action a:allActions
             ) {
            float actionScore = evalAction(a);
            if(actionScore > bestActionScore){
                bestAction = a;
                bestActionScore = actionScore;
            }

        }

        return bestAction;
    }

    //TODO eval action
    float evalAction(Action action){

        return 0;
    }

}
