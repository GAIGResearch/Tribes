package core.actions.tribeactions;

import core.Diplomacy;
import core.Types;
import core.actions.Action;
import core.game.GameState;

import static core.TribesConfig.ALLEGIANCE_MAX;

public class DeclareWar extends TribeAction {

    private int targetID;

    public DeclareWar(int tribeId){
        super(Types.ACTION.DECLARE_WAR);
        this.tribeId = tribeId;
    }

    public void setTargetID(int targetID){this.targetID = targetID;}
    public int getTargetID(){return this.targetID;}

    @Override
    public boolean isFeasible(final GameState gs) {
        // getting the diplomacy of the current GameState
        Diplomacy d = gs.getBoard().getDiplomacy();
        // getting the allegiances
        int[][] allegiances = d.getAllegianceStatus();

        // if the two tribes are already at war, or the tribe has already declared war this turn, return false, else return true
        return allegiances[this.tribeId][this.targetID] > -(float)(ALLEGIANCE_MAX/2.0) && !gs.getTribe(this.tribeId).getHasDeclaredWar();
    }

    @Override
    public Action copy(){
        DeclareWar declareWar = new DeclareWar(this.tribeId);
        declareWar.setTargetID(targetID);
        return declareWar;
    }

    public String toString()
    {
        return "DECLARE_WAR by tribe " + this.tribeId + " on tribe " + this.targetID;
    }
}
