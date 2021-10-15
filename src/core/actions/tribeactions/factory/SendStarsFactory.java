package core.actions.tribeactions.factory;

import core.actions.Action;
import core.actions.ActionFactory;
import core.actions.tribeactions.SendStars;
import core.actors.Actor;
import core.actors.Tribe;
import core.game.GameState;

import java.util.LinkedList;

public class SendStarsFactory implements ActionFactory {
    @Override
    public LinkedList<Action> computeActionVariants(final Actor actor, final GameState gs) {
        LinkedList<Action> actions = new LinkedList<>();
        Tribe tribe = (Tribe) actor;

        // if tribe has no stars, no actions
        if (tribe.getStars() == 0) {
            return actions;
        }
        for (int ids = 0; ids < gs.getBoard().getTribes().length; ids++) {
            for (int stars = 1; stars < Math.min(tribe.getStars(), 15); stars++) {
                if (ids != tribe.getTribeId()) {
                    if (tribe.canSendStars(stars)) {
                        SendStars ss = new SendStars(tribe.getTribeId());
                        ss.setNumStars(stars);
                        ss.setTargetID(ids);
                        actions.add(ss);
                    }
                }
            }
        }
        return actions;
    }
}