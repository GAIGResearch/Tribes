package core.actions.unitactions;

import core.TechnologyTree;
import core.Types;
import core.actions.Action;
import core.actors.City;
import core.game.GameState;
import core.actors.units.Unit;

import java.util.LinkedList;
import java.util.Random;

public class Examine extends UnitAction
{

    public Examine(Unit invader)
    {
        super.unit = invader;
    }


    @Override
    public LinkedList<Action> computeActionVariants(final GameState gs) {
        LinkedList<Action> actions = new LinkedList<>();


        if (isFeasible(gs)) {
            actions.add(new Examine(unit));
        }

        return actions;
    }


    @Override
    public boolean isFeasible(final GameState gs) {
        return gs.getBoard().getResourceAt(unit.getCurrentPosition().x, unit.getCurrentPosition().y) == Types.RESOURCE.RUINS;
    }

    @Override
    public boolean execute(GameState gs) {
        if(isFeasible(gs)) {
            Random r = new Random();
            Types.TECHNOLOGY[] techs = Types.TECHNOLOGY.values();
            TechnologyTree technologyTree = gs.getTribe(unit.getTribeID()).getTechTree();
            int capital = gs.getTribe(unit.getTribeID()).getCapitalID();
            boolean allTech = technologyTree.getEverythingResearched();

            int bonus = r.nextInt(5);
            while (allTech && bonus == 1) {
                bonus = r.nextInt(5);
            }

            switch (bonus) {
                case 0:
                    //TODO: Spawn a superunit
                    break;
                case 1:
                    int randomPick = r.nextInt(techs.length);
                    boolean hasResearch = false;
                    while (!hasResearch) {
                        hasResearch = technologyTree.doResearch(techs[randomPick]);
                    }
                    break;
                case 2:
                    City c = (City) gs.getActor(capital);
                    c.addPopulation(3);
                    break;
                case 3:
                    //TODO: Spawn a Explorer
                    break;
                case 4:
                    gs.getTribe(unit.getTribeID()).addStars(10);
                    break;
            }
            return true;
        }
        return false;
    }
}
