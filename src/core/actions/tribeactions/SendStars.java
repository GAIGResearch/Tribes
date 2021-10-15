package core.actions.tribeactions;

import core.Types;
import core.actions.Action;
import core.actors.Tribe;
import core.game.GameState;

import static core.TribesConfig.MIN_STARS_SEND;

public class SendStars extends TribeAction{

    private int numStars;
    private int targetID;

    public SendStars(int tribeId){
        super(Types.ACTION.SEND_STARS);
        this.tribeId = tribeId;
    }

    public void setNumStars(int numStars){this.numStars = numStars;}
    public int getNumStars(){return this.numStars;}

    public void setTargetID(int targetID){this.targetID = targetID;}
    public int getTargetID(){return this.targetID;}

    @Override
    public boolean isFeasible(GameState gs) {
        Tribe tribe = gs.getTribe(tribeId);
        return tribe.canSendStars(numStars) && numStars <= MIN_STARS_SEND;
    }

    @Override
    public Action copy(){
        SendStars sendStars = new SendStars(this.tribeId);
        sendStars.setNumStars(numStars);
        sendStars.setTargetID(targetID);
        return sendStars;
    }

    public String toString()
    {
        return "SEND_STARS by tribe " + this.tribeId +" to: " + this.targetID + " : " + this.numStars + " stars";
    }
}
