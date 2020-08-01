package core.actions;

import core.game.GameState;

public interface ActionCommand
{
    /**
     * Executes an action in a game state. Requires all parameters of the action to be
     * previously set. Example:
     *
     *  ResearchTech researchAction = new ResearchTech(myTribe);
     *  researchAction.setTech(Types.TECHNOLOGY.ARCHERY);
     *  ...
     *  new ResearchTechCommand().execute(researchAction, currentGameState);
     *
     * Implementations of this function will likely modify the GameState gs passed by parameter.
     *
     * @param a action to execute in the game state gs
     * @param gs the game state where the action must be executed.
     * @return false if it couldn't be executed.
     */
    boolean execute(Action a, GameState gs);

}
