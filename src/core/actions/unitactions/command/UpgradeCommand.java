package core.actions.unitactions.command;

import core.Types;
import core.actions.Action;
import core.actions.ActionCommand;
import core.actions.unitactions.Upgrade;
import core.actors.City;
import core.actors.Tribe;
import core.actors.units.Battleship;
import core.actors.units.Boat;
import core.actors.units.Ship;
import core.actors.units.Unit;
import core.game.Board;
import core.game.GameState;

import static core.Types.UNIT.*;
import static core.Types.UNIT.SHIP;

public class UpgradeCommand implements ActionCommand {

    @Override
    public boolean execute(Action a, GameState gs) {
        Upgrade action = (Upgrade)a;
        int unitId = action.getUnitId();

        Unit unit = (Unit) gs.getActor(unitId);
        Tribe tribe = gs.getTribe(unit.getTribeId());
        Board board = gs.getBoard();
        City city = (City) board.getActor(unit.getCityId());

        if(action.isFeasible(gs)){
            Types.UNIT unitType = unit.getType();
            Types.UNIT nextType;

            //get the correct type - or nothing!
            if(unitType == BOAT) nextType = SHIP;
            else if(unitType == SHIP) nextType = BATTLESHIP;
            else return false; //this shouldn't happen, isFeasible should've captured this case

            //Create the new unit
            Unit newUnit = Types.UNIT.createUnit(unit.getPosition(), unit.getKills(), unit.isVeteran(), unit.getCityId(), unit.getTribeId(), nextType);
            newUnit.setCurrentHP(unit.getCurrentHP());
            newUnit.setMaxHP(unit.getMaxHP());
            if(nextType == SHIP)
                ((Ship)newUnit).setBaseLandUnit(((Boat)unit).getBaseLandUnit());
            else
                ((Battleship)newUnit).setBaseLandUnit(((Ship)unit).getBaseLandUnit());

            //adjustments in tribe and board.
            tribe.subtractStars(nextType.getCost());

            Types.TURN_STATUS turn_status = unit.getStatus();
            //We first remove the unit, so there's space for the new one to take its place.
            board.removeUnitFromBoard(unit);
            board.removeUnitFromCity(unit, city, tribe);
            board.addUnit(city, newUnit);
            newUnit.setStatus(turn_status);
            return true;
        }
        return false;
    }
}
