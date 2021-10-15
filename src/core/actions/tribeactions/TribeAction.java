package core.actions.tribeactions;
import core.Types;
import core.actions.Action;
import core.game.GameState;

public class TribeAction extends Action {

    TribeAction(Types.ACTION aType)
    {
        this.actionType = aType;
    }

    protected int tribeId;
    public void setTribeId(int tribeId) {this.tribeId = tribeId;}
    public int getTribeId() {return this.tribeId;}

    @Override
    public boolean isFeasible(GameState gs) {
        return false;
    }

    @Override
    public Action copy() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof TribeAction))
            return false;
        TribeAction other = (TribeAction) o;
        return tribeId == other.tribeId && actionType == other.actionType;
    }
}