package core.actions.unitactions;

import core.actions.Action;
import core.game.GameState;
import core.actors.City;
import core.actors.units.Unit;

import java.util.LinkedList;

public class Examine extends UnitAction
{
    //Location of the ruins to examine.
    private int ruinsX;
    private int ruinsY;

    public Examine(Unit invader)
    {
        super.unit = invader;
    }

    public void setRuinsLoc(int x, int y) {this.ruinsX = x; this.ruinsY = y;}
    public int geRuinsX() {
        return ruinsX;
    }
    public int getRuinsY() {
        return ruinsY;
    }

    @Override
    public LinkedList<Action> computeActionVariants(final GameState gs) {
        //TODO: compute all the Examine actions.
        return null;
    }

    @Override
    public boolean isFeasible(final GameState gs)
    {
        //TODO: check if this action is feasible.
        return false;
    }

    @Override
    public boolean execute(GameState gs) {
        //TODO: Execute Examine action
        return false;
    }
}
