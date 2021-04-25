package core.actions.tribeactions.command;

import core.Diplomacy;
import core.actions.Action;
import core.actions.ActionCommand;
import core.actions.tribeactions.SendStars;
import core.actors.Tribe;
import core.game.GameState;

public class SendStarsCommand implements ActionCommand {

    @Override
    public boolean execute(Action a, GameState gs){
        SendStars action = (SendStars) a;
        Tribe tribe = gs.getTribe(action.getTribeId());
        Tribe target = gs.getTribe(action.getTargetID());
        if (action.isFeasible(gs)){
            // adding and subtracting stars
            tribe.subtractStars(action.getNumStars());
            target.addStars(action.getNumStars());

            tribe.setStarsSent(tribe.getStarsSent() + action.getNumStars());

            // adding score for the tribe sending stars
            tribe.addScore(action.getNumStars()*10);

            // updating the diplomacy
            Diplomacy d = gs.getBoard().getDiplomacy();
            int tribeID = action.getTribeId();
            int targetID = action.getTargetID();
            d.updateAllegiance(action.getNumStars(), tribeID, targetID);
            return true;
        }
        return false;
    }
}
