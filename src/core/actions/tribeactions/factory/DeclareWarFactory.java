package core.actions.tribeactions.factory;

import core.Diplomacy;
import core.actions.Action;
import core.actions.ActionFactory;
import core.actions.tribeactions.DeclareWar;
import core.actors.Actor;
import core.actors.Tribe;
import core.game.GameState;

import java.util.LinkedList;

public class DeclareWarFactory implements ActionFactory {

    @Override
    public LinkedList<Action> computeActionVariants(final Actor actor, final GameState gs) {
        LinkedList<Action> actions = new LinkedList<>();
        Tribe tribe = (Tribe) actor;

        // getting the diplomacy of the current GameState
        Diplomacy d = gs.getBoard().getDiplomacy();
        // getting the allegiances
        int[][] allegiances = d.getAllegianceStatus();
        if (!tribe.getHasDeclaredWar()) {
            for (int i = 0; i < allegiances.length; i++) {
                if (allegiances[tribe.getTribeId()][i] > -30 && tribe.getTribeId() != i) {
                    DeclareWar declareWar = new DeclareWar(tribe.getTribeId());
                    declareWar.setTargetID(i);
                    actions.add(declareWar);
                }
            }
        }
        return actions;
    }
}