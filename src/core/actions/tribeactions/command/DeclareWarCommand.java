package core.actions.tribeactions.command;

import core.Diplomacy;
import core.actions.Action;
import core.actions.ActionCommand;
import core.actions.tribeactions.DeclareWar;
import core.actors.Tribe;
import core.game.GameState;

import static core.TribesConfig.ALLEGIANCE_MAX;

public class DeclareWarCommand implements ActionCommand {

    @Override
    public boolean execute(Action a, GameState gs){
        DeclareWar action = (DeclareWar)a;
        if (action.isFeasible(gs)){
            Diplomacy d = gs.getBoard().getDiplomacy();
            int tribeID = action.getTribeId();
            int targetID = action.getTargetID();
            Tribe tribe = gs.getTribe(tribeID);
            tribe.setHasDeclaredWar(true);
            tribe.setnWarsDeclared(tribe.getnWarsDeclared() + 1);
            d.updateAllegiance((int) (-(float)(ALLEGIANCE_MAX/2.0) - d.getAllegianceStatus()[tribeID][targetID]), tribeID, targetID);
            d.checkConsequences((int) (-(float)(ALLEGIANCE_MAX/2.0)), tribeID, targetID);
            return true;
        }
        return false;
    }
}