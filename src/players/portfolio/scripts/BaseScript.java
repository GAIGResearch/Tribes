package players.portfolio.scripts;

import core.actions.Action;
import core.actors.Actor;
import core.game.GameState;
import utils.Pair;

import java.util.ArrayList;

public class BaseScript
{
    public double DEFAULT_VALUE = 0.5;
    public enum Feature
    {
        DISTANCE,
        ATTACK,
        DEFENCE,
        MOVEMENT,
        RANGE,
        DAMAGE,
        HP,
        COST
    }

    //List of actions this script could pick from. Subclasses should override the process
    // method to choose one of them. Otherwise, default behaviour is to take the first one.
    protected ArrayList<Action> actions;

    /**
     * Main processing function. Given a game state and an actor, returns the action it should execute.
     * @param gs Game state where the actor must act.
     * @param ac Actor who will execute the action returned
     * @return action to execute.
     */
    public Pair<Action, Double> process(GameState gs, Actor ac) {
        return new Pair<>(actions.get(0), DEFAULT_VALUE);
    }

    public void setActions(ArrayList<Action> actions) {
        this.actions = actions;
    }
}
