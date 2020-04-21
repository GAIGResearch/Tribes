package core.actions.unitactions;

import core.TechnologyTree;
import core.Types;
import core.actions.Action;
import core.actors.City;
import core.actors.Tribe;
import core.actors.units.Battleship;
import core.game.Board;
import core.game.GameState;
import core.actors.units.Unit;
import utils.Vector2d;

import java.util.Random;

import static core.Types.EXAMINE_BONUS.*;

public class Examine extends UnitAction
{
    Types.EXAMINE_BONUS bonus;

    public Examine(int unitId)
    {
        super.unitId = unitId;
    }


    @Override
    public boolean isFeasible(final GameState gs) {
        Unit unit = (Unit) gs.getActor(this.unitId);
        Vector2d unitPos = unit.getPosition();
        return unit.isFresh() && gs.getBoard().getResourceAt(unitPos.x, unitPos.y) == Types.RESOURCE.RUINS;
    }

    @Override
    public boolean execute(GameState gs) {
        if(isFeasible(gs)) {
            Unit unit = (Unit) gs.getActor(this.unitId);
            Tribe t = gs.getTribe(unit.getTribeId());
            Random rnd = gs.getRandomGenerator();
            TechnologyTree technologyTree = t.getTechTree();
            int capital = t.getCapitalID();

            boolean allTech = technologyTree.isEverythingResearched();
            bonus = Types.EXAMINE_BONUS.random(rnd);
            while (allTech && bonus == RESEARCH) {
                bonus = Types.EXAMINE_BONUS.random(rnd);
            }

            switch (bonus) {
                case SUPERUNIT:
                    Board board = gs.getBoard();

                    Types.TERRAIN terr = board.getTerrainAt(unit.getPosition().x, unit.getPosition().y);
                    Unit newUnit;
                    Vector2d spawnPos;
                    if(terr.isWater())
                    {
                        //instead of a super unit, in the water we create a Battleship of out a warrior
                        spawnPos = unit.getPosition().copy();
                        newUnit = Types.UNIT.createUnit(spawnPos, 0, false, -1, unit.getTribeId(), Types.UNIT.BATTLESHIP);
                        ((Battleship)newUnit).setBaseLandUnit(Types.UNIT.WARRIOR);
                    }
                    else
                    {
                        spawnPos = gs.getActor(capital).getPosition().copy();
                        newUnit = Types.UNIT.createUnit(spawnPos, 0, false, capital, unit.getTribeId(), Types.UNIT.SUPERUNIT);
                    }

                    Unit unitInCity = board.getUnitAt(spawnPos.x, spawnPos.y);
                    if(unitInCity != null)
                        gs.pushUnit(unitInCity, spawnPos.x, spawnPos.y);
                    board.addUnit((City)gs.getActor(capital), newUnit);
                    break;

                case RESEARCH:
                    technologyTree.researchAtRandom(rnd);
                    break;

                case POP_GROWTH:
                    City c = (City) gs.getActor(capital);
                    c.addPopulation(t, bonus.getBonus());
                    break;

                case EXPLORER:
                    Vector2d cityPos = gs.getActor(capital).getPosition().copy();
                    gs.getBoard().launchExplorer(cityPos.x, cityPos.y, unit.getTribeId(), rnd);
                    break;

                case RESOURCES:
                    gs.getTribe(unit.getTribeId()).addStars(bonus.getBonus());
                    break;
            }
            Vector2d unitPos = unit.getPosition();
            gs.getBoard().setResourceAt(unitPos.x, unitPos.y, null);
            unit.setStatus(Types.TURN_STATUS.FINISHED);
            return true;
        }
        return false;
    }

    public Types.EXAMINE_BONUS getBonus() {
        return bonus;
    }

    @Override
    public Action copy() {
        Examine copy = new Examine(this.unitId);
        copy.bonus = bonus;
        return copy;
    }
}
