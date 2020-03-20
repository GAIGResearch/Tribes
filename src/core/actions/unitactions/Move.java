package core.actions.unitactions;

import core.TechnologyTree;
import core.Types;
import core.actions.Action;
import core.game.Board;
import core.game.GameState;
import core.actors.units.Unit;
import utils.Vector2d;
import utils.graph.NeighbourHelper;
import utils.graph.PathNode;
import utils.graph.Pathfinder;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Vector;

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
        if(unit.checkStatus(Types.TURN_STATUS.MOVED) && gs.getBoard().getUnitAt(destination.x, destination.y) == null) {
            return !tp.findPathTo(destination).isEmpty();
        }
        return false;
    }

    @Override
    public boolean execute(GameState gs)
    {
        if(isFeasible(gs)) {
            Unit unit = (Unit) gs.getActor(this.unitId);
            Board board = gs.getBoard();
            Types.TERRAIN destinationTerrain = board.getTerrainAt(destination.x, destination.y);

            unit.setStatus(Types.TURN_STATUS.MOVED);
            unit.setPosition(destination.x, destination.y);
            if(unit.getType() == Types.UNIT.BOAT || unit.getType() == Types.UNIT.SHIP || unit.getType() == Types.UNIT.BATTLESHIP) {
                if(destinationTerrain != Types.TERRAIN.SHALLOW_WATER && destinationTerrain != Types.TERRAIN.DEEP_WATER && destinationTerrain != Types.TERRAIN.CITY){
                    board.disembark(unit, destination.x, destination.y);
                }
            }else {
                if(board.getBuildingAt(destination.x, destination.y) == Types.BUILDING.PORT){
                    board.embark(unit, destination.x, destination.y);
                }
            }
            return true;
        }
        return false;
    }

}
