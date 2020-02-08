package core.actions.tribeactions;

import core.Types;
import core.actions.Action;
import core.actors.Tribe;
import core.game.GameState;

import java.util.LinkedList;

public class ResearchTech extends TribeAction {

    private Types.TECHNOLOGY tech;

    public ResearchTech(Tribe tribe)
    {
        this.tribe = tribe;
    }

    public void setTech(Types.TECHNOLOGY tech) {this.tech = tech;}
    public Types.TECHNOLOGY getTech() {return this.tech;}

    @Override
    public LinkedList<Action> computeActionVariants(final GameState gs) {
        //TODO: Computes all the ResearchTech actions that can be executed
        return null;
    }

    @Override
    public boolean isFeasible(final GameState gs) {
        //TODO: checks if this ResearchTech action is feasible
        return false;
    }

    @Override
    public boolean execute(GameState gs) {
        //TODO: Executes this ResearchTech action.
        return false;
    }
}
