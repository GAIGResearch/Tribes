package core.actions;

import core.game.GameState;

public interface Action
{

    /**
     * Analizes if the current action is feasible given a game state. Requires all parameters of the action to be
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
    boolean isFeasible(final GameState gs);

    /**
     * Executes this action in the game state. Requires all parameters of the action to be
     * previously set. Example:
     *
     *  ResearchTech researchAction = new ResearchTech(myTribe);
     *  researchAction.setTech(Types.TECHNOLOGY.ARCHERY);
     *  researchAction.execute(currentGameState);
     *
     * Implementations of this function will likely modify the GameState gs passed by parameter.
     *
     * @param gs the game state where the action must be executed.
     * @return false if it couldn't be executed.
     */
    boolean execute(GameState gs);

    Action copy();
}
