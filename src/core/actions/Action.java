package core.actions;

import core.game.GameState;
import core.Types;

public abstract class Action
{
    //Indicates the action type.
    protected Types.ACTION actionType;

    /**
     * Analyzes if the current action is feasible given a game state. Requires all parameters of the action to be
     * previously set. Example:
     *
     *  ResearchTech researchAction = new ResearchTech(myTribe);
     *  researchAction.setTech(Types.TECHNOLOGY.ARCHERY);
     *  boolean feasible = researchAction.isFeasible(currentGameState);
     *
     * Implementations of this function MUST NOT modify the GameState gs passed by parameter.
     *
     * @param gs the game state where the action will be or not feasible.
     * @return whether the action is feasible.
     */
    public abstract boolean isFeasible(final GameState gs);

    /**
     * Returns the type of this action
     * @return the type of this action
     */
    public Types.ACTION getActionType() {
        return actionType;
    }

    public abstract Action copy();
}
