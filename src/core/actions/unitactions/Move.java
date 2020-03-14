package core.actions.unitactions;

import core.TechnologyTree;
import core.Types;
import core.actions.Action;
import core.game.Board;
import core.game.GameState;
import core.actors.units.Unit;
import utils.Vector2d;
import utils.graph.NeighbourProvider;
import utils.graph.TreeNode;
import utils.graph.TreePathfinder;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Vector;

public class Move extends UnitAction
{
    private Vector2d destination;

    public Move(Unit u)
    {
        super.unit = u;
    }

    public void setDestination(Vector2d destination) {this.destination = destination; }
    public Vector2d getDestination() { return destination; }

    @Override
    public LinkedList<Action> computeActionVariants(final GameState gs)
    {
        LinkedList<Action> moves = new LinkedList<>();
        TreePathfinder tp = new TreePathfinder(unit.getPosition(), new StepMove(gs, unit));

        //If a units turn is FINISHED don't do unnecessary calculations.
        if(unit.checkStatus(Types.TURN_STATUS.MOVED)) {
            for(TreeNode tile : tp.findPaths()) {
                Move action = new Move(unit);
                action.setDestination(tile.getPosition());

                if(action.isFeasible(gs)) {
                    moves.add(action);
                }
            }
        }
        return moves;
    }

    @Override
    public boolean isFeasible(final GameState gs)
    {
        TreePathfinder tp = new TreePathfinder(unit.getPosition(), new StepMove(gs, unit));

        if(unit.checkStatus(Types.TURN_STATUS.MOVED)) {
            return !tp.findPathTo(destination).isEmpty();
        }
        return false;
    }

    @Override
    public boolean execute(GameState gs)
    {
        if(isFeasible(gs)) {
            unit.setStatus(Types.TURN_STATUS.MOVED);
            unit.setPosition(destination.x, destination.y);
            return true;
        }
        return false;
    }

    private class StepMove implements NeighbourProvider
    {
        private GameState gs;
        private Unit unit;

        StepMove(GameState curGameState, Unit movingUnit)
        {
            this.gs = curGameState;
            this.unit = movingUnit;
        }

        @Override
        // from: position from which we need neighbours
        // costFrom: is the total move cost computed up to "from"
        // Using this.gs, this.unit, from and costFrom, gets all the adjacent neighbours to tile in position "from"
        public ArrayList<TreeNode> getNeighbours(Vector2d from, double costFrom) {
            TechnologyTree techTree = gs.getTribe(unit.getTribeId()).getTechTree();
            ArrayList<TreeNode> neighbours = new ArrayList<>();
            Board board = gs.getBoard();
            boolean inZoneOfControl = false;

            //Check if there is an enemy unit adjacent.
            for(Vector2d tile : from.neighborhood(1, board.getSize())) {
                if(board.getUnitAt(tile.x, tile.y).getTribeId() != unit.getTribeId()) { inZoneOfControl = true; }
            }
            // Each one of the tree nodes added to "neighbours" must have a position (x,y) and also the cost of moving there from "from":
            //  TreeNode tn = new TreeNode (vector2d pos, double stepCost)
            // We only add nodes to neighbours if costFrom+stepCost <= total move range of this.unit

            for(Vector2d tile : from.neighborhood(1, board.getSize())) {
                Types.TERRAIN terrain = board.getTerrainAt(tile.x, tile.y);
                double stepCost = 0.0;

                //Cannot move into tiles that have not been discovered yet.
                if(!gs.getTribe(unit.getTribeId()).isVisible(tile.x, tile.y)) { continue; }

                //Unit is a water unit
                if(unit.getType() == Types.UNIT.BOAT || unit.getType() == Types.UNIT.SHIP || unit.getType() == Types.UNIT.BATTLESHIP) {
                    switch (terrain)
                    {
                        case PLAIN:
                        case CITY:
                        case FOREST:
                        case VILLAGE:
                        case MOUNTAIN:
                            continue;
                        case DEEP_WATER:
                        case SHALLOW_WATER:
                            stepCost = 1.0;
                    }
                }else //Ground unit
                    switch (terrain)
                    {
                        case SHALLOW_WATER:
                        case DEEP_WATER:
                            continue;
                        case PLAIN:
                        case CITY:
                        case VILLAGE:
                            if(board.getUnitAt(tile.x, tile.y) != null){
                                continue;
                            }else{
                                stepCost = 1.0;
                            }
                        case FOREST:
                            if(board.getUnitAt(tile.x, tile.y) != null){
                                continue;
                            }else{
                                stepCost = unit.MOV;
                            }
                        case MOUNTAIN:
                            if(techTree.isResearched(Types.TECHNOLOGY.CLIMBING)){
                                stepCost = unit.MOV;
                            }else{ continue; }
                    }
                if(inZoneOfControl){
                    stepCost = unit.MOV;
                }
                if(costFrom + stepCost <= unit.MOV){
                    neighbours.add(new TreeNode(tile, costFrom + stepCost));
                }
            }
            return neighbours;
        }

        @Override
        public void addJumpLink(Vector2d from, Vector2d to, boolean reverse) {
            //No jump links
        }
    }

    private boolean adjacentToEnemy(Board board, Vector2d pos) {
        for(Vector2d tile : pos.neighborhood(1, board.getSize())) {
            if(board.getUnitAt(tile.x, tile.y).getTribeId() != unit.getTribeId()) { return true; }
        }
        return false;
    }

}
