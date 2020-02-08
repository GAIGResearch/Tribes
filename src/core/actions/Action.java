package core.actions;

import core.game.GameState;

import java.util.LinkedList;

public abstract class Action
{

    /**
     * Computes all the available actions that are possible of a given type. Does not require all
     * parameters of 'this' action to be set. Example of usage:
     *
     *  LinkedList<Action> list = new DerivateClass(params).computeActionVariants(currentGameState);
     *  or
     *  LinkedList<Action> list = new ResearchTech(myTribe).computeActionVariants(currentGameState);
     *
     * Implementations of this function MUST NOT modify the GameState gs passed by parameter.
     *
     * @param gs current game state that will help determine which actions are valid.
     * @return the list of possible actions
     */
    public abstract LinkedList<Action> computeActionVariants(final GameState gs);


    /**
     * Analizes if the current action is feasible given a game state. Requires all parameters of the action to be
     * previously set. Example:
     *
     *  ResearchTech researchAction = new ReseachTech(myTribe);
     *  researchAction.setTech(Types.TECHNOLOGY.ARCHERY);
     *  boolean feasible = researchAction.isFeasible(currentGameState);
     *
     * Implementations of this function MUST NOT modify the GameState gs passed by parameter.
     *
     * @param gs the game state where the action will be or not feasible.
     * @return wheter the action is feasible.
     */
    public abstract boolean isFeasible(final GameState gs);


    /**
     * Executes this action in the game state. Requires all parameters of the action to be
     * previously set. Example:
     *
     *  ResearchTech researchAction = new ReseachTech(myTribe);
     *  researchAction.setTech(Types.TECHNOLOGY.ARCHERY);
     *  researchAction.execute(currentGameState);
     *
     * Implementations of this function will likely modify the GameState gs passed by parameter.
     *
     * @param gs the game state where the action must be executed.
     * @return false if it couldn't be executed.
     */
    public abstract boolean execute(GameState gs);

}
