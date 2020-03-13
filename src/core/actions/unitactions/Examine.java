package core.actions.unitactions;

import core.TechnologyTree;
import core.Types;
import core.actions.Action;
import core.actors.City;
import core.game.GameState;
import core.actors.units.Unit;
import utils.Vector2d;

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
        return gs.getBoard().getResourceAt(unit.getPosition().x, unit.getPosition().y) == Types.RESOURCE.RUINS;
    }

    @Override
    public boolean execute(GameState gs) {
        if(isFeasible(gs)) {
            Random r = gs.getRandomGenerator();
            Types.TECHNOLOGY[] techs = Types.TECHNOLOGY.values();
            TechnologyTree technologyTree = gs.getTribe(unit.getTribeId()).getTechTree();
            int capital = gs.getTribe(unit.getTribeId()).getCapitalID();
            Vector2d cityPos = gs.getActor(capital).getPosition();
            boolean allTech = technologyTree.isEverythingResearched();


            int bonus = r.nextInt(5);
            while (allTech && bonus == 1) {
                bonus = r.nextInt(5);
            }

            switch (bonus) {
                case 0:
                    Unit unitInCity = gs.getBoard().getUnitAt(cityPos.x, cityPos.y);

                    //This can probably be encapsulated
                    Unit superUnit = Types.UNIT.createUnit(cityPos, 0, false, capital, unit.getTribeId(), Types.UNIT.SUPERUNIT);
                    gs.getBoard().addUnit((City)gs.getActor(capital), superUnit);

                    if(unitInCity != null)
                    {
                        gs.getBoard().pushUnit(unitInCity.getTribeId(), unitInCity, cityPos.x, cityPos.y);
                    }
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
                    gs.getBoard().launchExplorer(cityPos.x, cityPos.y, unit.getTribeId(), gs.getRandomGenerator());
                    break;
                case 4:
                    gs.getTribe(unit.getTribeId()).addStars(10);
                    break;
            }
            return true;
        }
        return false;
    }
}
