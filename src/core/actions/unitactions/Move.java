package core.actions.unitactions;

import core.Types;
import core.actions.Action;
import core.actors.Tribe;
import core.game.Board;
import core.game.GameState;
import core.actors.units.Unit;
import utils.Vector2d;
import utils.graph.PathNode;
import utils.graph.Pathfinder;

import java.util.ArrayList;

public class Move extends UnitAction
{
    private Vector2d destination;

    public Move(int unitId)
    {
        super.unitId = unitId;
    }

    public void setDestination(Vector2d destination) {this.destination = destination; }
    public Vector2d getDestination() { return destination; }

    @Override
    public boolean isFeasible(final GameState gs)
    {
        Unit unit = (Unit) gs.getActor(this.unitId);
        Pathfinder tp = new Pathfinder(unit.getPosition(), new StepMove(gs, unit));

        //If the unit can move and the destination is vacant, try to reach it.
        if(unit.canMove() && gs.getBoard().getUnitAt(destination.x, destination.y) == null) {
            ArrayList<PathNode> path = tp.findPathTo(destination);
            if(path == null)
            {
                System.out.println("ERROR calculating a path (if actions was created by MoveFactory)");
            }
            return path != null;
        }
        return false;
    }

    @Override
    public boolean execute(GameState gs)
    {
        if(isFeasible(gs)) {
            Unit unit = (Unit) gs.getActor(this.unitId);
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

    @Override
    public Action copy() {
        Move move = new Move(this.unitId);
        move.setDestination(this.destination);
        return move;
    }

    public String toString()
    {
        return "MOVE by unit " + this.unitId + " to " + destination;
    }

}
