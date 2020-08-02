package core.actions.unitactions.command;

import core.TechnologyTree;
import core.Types;
import core.actions.Action;
import core.actions.ActionCommand;
import core.actions.unitactions.Examine;
import core.actors.City;
import core.actors.Tribe;
import core.actors.units.Battleship;
import core.actors.units.Unit;
import core.game.Board;
import core.game.GameState;
import utils.Vector2d;

import java.util.Random;

import static core.Types.EXAMINE_BONUS.RESEARCH;
import static core.Types.EXAMINE_BONUS.SUPERUNIT;

public class ExamineCommand implements ActionCommand {

    @Override
    public boolean execute(Action a, GameState gs) {
        Examine action = (Examine)a;
        int unitId = action.getUnitId();

        if(action.isFeasible(gs)) {
            Unit unit = (Unit) gs.getActor(unitId);
            Tribe t = gs.getTribe(unit.getTribeId());
            Random rnd = gs.getRandomGenerator();
            TechnologyTree technologyTree = t.getTechTree();

            int handlerCityId = t.getCitiesID().get(0);
            if(t.controlsCapital())
                handlerCityId = t.getCapitalID();

            boolean allTech = technologyTree.isEverythingResearched();
            Types.EXAMINE_BONUS bonus = Types.EXAMINE_BONUS.random(rnd);
            while (allTech && bonus == RESEARCH) {
                bonus = Types.EXAMINE_BONUS.random(rnd);
            }

            switch (bonus) {
                case SUPERUNIT:
                    Board board = gs.getBoard();

                    Vector2d spawnPos = unit.getPosition().copy();
                    Types.TERRAIN terr = board.getTerrainAt(spawnPos.x, spawnPos.y);
                    //instead of a super unit, in the water we create a Battleship of out a warrior
                    Types.UNIT unitType = terr.isWater() ? Types.UNIT.BATTLESHIP : Types.UNIT.SUPERUNIT;
                    Unit newUnit = Types.UNIT.createUnit(spawnPos, 0, false, -1, unit.getTribeId(), unitType);
                    if(terr.isWater())
                    {
                        ((Battleship)newUnit).setBaseLandUnit(Types.UNIT.WARRIOR);
                    }

                    Unit unitInCity = board.getUnitAt(spawnPos.x, spawnPos.y);
                    if(unitInCity != null)
                        gs.pushUnit(unitInCity, spawnPos.x, spawnPos.y);

                    board.addUnit((City)gs.getActor(handlerCityId), newUnit);

                    gs.getBoard().setResourceAt(spawnPos.x, spawnPos.y, null);
                    break;

                case RESEARCH:
                    boolean researched = technologyTree.researchAtRandom(rnd);
                    if(!researched)
                        System.out.println(gs.getTick() + " ERROR: researchAtRandom couldn't do any research.");
                    break;

                case POP_GROWTH:
                    City c = (City) gs.getActor(handlerCityId);
                    c.addPopulation(t, bonus.getBonus());
                    break;

                case EXPLORER:
                    spawnPos = unit.getPosition().copy();
                    gs.getBoard().launchExplorer(spawnPos.x, spawnPos.y, unit.getTribeId(), rnd);
                    break;

                case RESOURCES:
                    gs.getTribe(unit.getTribeId()).addStars(bonus.getBonus());
                    break;
            }
            Vector2d unitPos = unit.getPosition();
            if(bonus != SUPERUNIT)
                gs.getBoard().setResourceAt(unitPos.x, unitPos.y, null);

            unit.setStatus(Types.TURN_STATUS.FINISHED);
            return true;
        }
        return false;
    }
}
