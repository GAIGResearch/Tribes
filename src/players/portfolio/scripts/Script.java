package players.portfolio.scripts;

import core.actions.Action;
import core.actors.Actor;
import core.game.GameState;

import java.util.ArrayList;

public class Script
{
    //Used in classes where there are more than one scripts that can be used for action.
    protected ArrayList<Action> actions;

    //For wrapper scripts
    private Action action;

    /**
     * Main processing function. Given a game state and an actor, returns the action it should execute.
     * @param gs Game state where the actor must act.
     * @param ac Actor who will execute the action returned
     * @return action to execute.
     */
    public Action process(GameState gs, Actor ac) {
        return action;
    }

    public void setAction(Action action)
    {
        this.action = action;
    }

    public void setActions(ArrayList<Action> actions) {
        this.actions = actions;
    }

}
