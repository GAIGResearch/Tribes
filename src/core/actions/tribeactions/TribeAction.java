package core.actions.tribeactions;

import core.actions.Action;
import core.actors.Tribe;

public abstract class TribeAction extends Action {

    protected Tribe tribe;

    public void setTribe(Tribe tribe) {this.tribe = tribe;}
    public Tribe getTribe() {return this.tribe;}
}
