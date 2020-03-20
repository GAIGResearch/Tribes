package core.actions.tribeactions;
import core.actions.Action;

public abstract class TribeAction implements Action {
    protected int tribeId;
    public void setTribeId(int tribeId) {this.tribeId = tribeId;}
    public int getTribeId() {return this.tribeId;}
}