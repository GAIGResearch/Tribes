package core.actions.unitactions.command;

import core.Types;
import core.actions.Action;
import core.actions.ActionCommand;
import core.actions.unitactions.Move;
import core.actors.Tribe;
import core.actors.units.Unit;
import core.game.Board;
import core.game.GameState;
import utils.Vector2d;

public class MoveCommand implements ActionCommand {

    @Override
    public boolean execute(Action a, GameState gs) {
        Move action = (Move)a;
        if(action.isFeasible(gs)) {
            int unitId = action.getUnitId();
            Vector2d destination = action.getDestination();
            Unit unit = (Unit) gs.getActor(unitId);
            Board board = gs.getBoard();
            Tribe tribe = gs.getTribe(unit.getTribeId());
            Types.TERRAIN destinationTerrain = board.getTerrainAt(destination.x, destination.y);

            board.moveUnit(unit, unit.getPosition().x, unit.getPosition().y, destination.x, destination.y, gs.getRandomGenerator());

            if(unit.getType().isWaterUnit()){
                if(destinationTerrain != Types.TERRAIN.SHALLOW_WATER && destinationTerrain != Types.TERRAIN.DEEP_WATER){ // && destinationTerrain != Types.TERRAIN.CITY){
                    board.disembark(unit, tribe, destination.x, destination.y);
                }
            }else {
                if(board.getBuildingAt(destination.x, destination.y) == Types.BUILDING.PORT){
                    board.embark(unit, tribe, destination.x, destination.y);
                }
            }

            unit.transitionToStatus(Types.TURN_STATUS.MOVED);

            return true;
        }
        return false;
    }
}
