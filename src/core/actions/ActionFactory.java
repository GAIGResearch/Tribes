package core.actions;

import core.actors.Actor;
import core.game.GameState;

import java.util.LinkedList;

public interface ActionFactory
{
    /**
     * Computes all the available actions that are possible of a given type. Example of usage:
     *
     *  LinkedList<Action> list = new ResearchTechFactory().computeActionVariants(tribe, gs);
     *
     * Implementations of this function MUST NOT modify the GameState gs passed by parameter.
     *
     * @param actor Tribe for whom actions are computed.
     * @param gs current game state that will help determine which actions are valid.
     * @return the list of possible actions
     */
    LinkedList<Action> computeActionVariants(final Actor actor, final GameState gs);

}
