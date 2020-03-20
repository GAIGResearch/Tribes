package core.actions.tribeactions;

import core.actions.Action;
import core.actions.ActionFactory;
import core.actors.Actor;
import core.actors.Tribe;
import core.game.GameState;

import java.util.LinkedList;

public class EndTurnFactory implements ActionFactory {

    @Override
    public LinkedList<Action> computeActionVariants(final Actor actor, final GameState gs) {
        Tribe tribe = (Tribe) actor;
        LinkedList<Action> endTurns = new LinkedList<>();
        if(gs.canEndTurn(tribe.getTribeId()))
            endTurns.add(new EndTurn(tribe.getActorId()));
        return endTurns;
    }

}
