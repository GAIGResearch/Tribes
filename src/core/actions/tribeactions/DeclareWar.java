package core.actions.tribeactions;

import core.Types;
import core.game.GameState;

public class DeclareWar extends TribeAction {

    public DeclareWar(int tribeId){
        super(Types.ACTION.DECLARE_WAR);
        this.tribeId = tribeId;
    }

    @Override
    public boolean isFeasible(final GameState gs) {
        // if allegience below 30
        return true;
        // else return false
    }
}
