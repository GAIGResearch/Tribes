package core.actions.cityactions;

import core.TechnologyTree;
import core.TribesConfig;
import core.Types;
import core.actions.Action;
import core.actors.Tribe;
import core.actors.buildings.*;
import core.game.Board;
import core.game.GameState;
import core.actors.City;
import utils.Vector2d;

import java.util.LinkedList;

public class Build extends CityAction
{
    private Types.BUILDING buildingType;

    public Build(City c)
    {
        super.city = c;
    }

    public void setBuildingType(Types.BUILDING buildingType) {this.buildingType = buildingType;}

    public Types.BUILDING getBuildingType() {
        return buildingType;
    }

    @Override
    public LinkedList<Action> computeActionVariants(final GameState gs) {
        LinkedList<Action> actions = new LinkedList<>();
        Board board = gs.getBoard();
        LinkedList<Vector2d> tiles = board.getCityTiles(city.getActorId());

        for(Vector2d tile : tiles){
            for(Types.BUILDING building: Types.BUILDING.values()){
                //check if tile is empty
                if(board.getBuildingAt(tile.x, tile.y) == null) {
                    Build action = new Build(city);
                    action.setBuildingType(building);
                    action.targetPos = tile;
                    if (action.isFeasible(gs)) {
                        actions.add(action);
                    }
                }
            }
        }
        return actions;
    }

    @Override
    public boolean isFeasible(final GameState gs) {
        //TODO: Add monuments.
        Tribe tribe = gs.getTribe(city.getTribeId());
        Board board = gs.getBoard();
        TechnologyTree t = tribe.getTechTree();
        int stars = tribe.getStars();

        switch (buildingType) {
            case PORT:
                if(isBuildable(gs, TribesConfig.PORT_COST, false)) { return true; }
            case FARM:
                if(isBuildable(gs, TribesConfig.FARM_COST, false)) { return true; }
            case MINE:
                if(isBuildable(gs, TribesConfig.MINE_COST, false)) { return true; }
            case LUMBER_HUT:
                if(isBuildable(gs, TribesConfig.LUMBER_HUT_COST, false)) { return true; }
            case TEMPLE:
            case WATER_TEMPLE:
            case MOUNTAIN_TEMPLE:
                if(isBuildable(gs, TribesConfig.TEMPLE_COST, false)) { return true; }
            case FOREST_TEMPLE:
                if(isBuildable(gs, TribesConfig.TEMPLE_FOREST_COST, false)) { return true; }
            case SAWMILL:
                if(isBuildable(gs, TribesConfig.SAW_MILL_COST, true)) { return true; }
            case CUSTOM_HOUSE:
                if(isBuildable(gs, TribesConfig.CUSTOM_COST, true)) { return true; }
            case WINDMILL:
                if(isBuildable(gs, TribesConfig.WIND_MILL_COST, true)) { return true; }
            case FORGE:
                if(isBuildable(gs, TribesConfig.FORGE_COST, true)) { return true; }
        }
        return false;
    }

    @Override
    public boolean execute(GameState gs) {
        //TODO: Add monuments, roads
        //TODO: Make sure all the side effects for Buildings are counted.
        Tribe tribe = gs.getTribe(city.getTribeId());
        Board board = gs.getBoard();

        if(isFeasible(gs)) {
            switch (buildingType) {
                case FARM:
                    tribe.subtractStars(TribesConfig.FARM_COST);
                    board.setBuildingAt(targetPos.x, targetPos.y, Types.BUILDING.FARM);
                    city.addBuilding(new Farm(targetPos.x, targetPos.y));
                    return true;
                case MINE:
                    tribe.subtractStars(TribesConfig.MINE_COST);
                    board.setBuildingAt(targetPos.x, targetPos.y, Types.BUILDING.MINE);
                    city.addBuilding(new Mine(targetPos.x, targetPos.y));
                    return true;
                case PORT:
                    tribe.subtractStars(TribesConfig.PORT_COST);
                    board.setBuildingAt(targetPos.x, targetPos.y, Types.BUILDING.PORT);
                    city.addBuilding(new Port(targetPos.x, targetPos.y));
                    return true;
                case ROAD:
                    //is road a building? Road object is missing
                    return true;
                case FORGE:
                    tribe.subtractStars(TribesConfig.FORGE_COST);
                    board.setBuildingAt(targetPos.x, targetPos.y, Types.BUILDING.FORGE);
                    city.addBuilding(new Forge(targetPos.x, targetPos.y));
                    return true;
                case TEMPLE:
                    tribe.subtractStars(TribesConfig.TEMPLE_COST);
                    board.setBuildingAt(targetPos.x, targetPos.y, Types.BUILDING.TEMPLE);
                    city.addBuilding(new Temple(targetPos.x, targetPos.y, TribesConfig.TEMPLE_COST, Types.BUILDING.TEMPLE));
                    return true;
                case MOUNTAIN_TEMPLE:
                    tribe.subtractStars(TribesConfig.TEMPLE_COST);
                    board.setBuildingAt(targetPos.x, targetPos.y, Types.BUILDING.MOUNTAIN_TEMPLE);
                    city.addBuilding(new Temple(targetPos.x, targetPos.y, TribesConfig.TEMPLE_COST, Types.BUILDING.MOUNTAIN_TEMPLE));
                    return true;
                case WATER_TEMPLE:
                    tribe.subtractStars(TribesConfig.TEMPLE_COST);
                    board.setBuildingAt(targetPos.x, targetPos.y, Types.BUILDING.WATER_TEMPLE);
                    city.addBuilding(new Temple(targetPos.x, targetPos.y, TribesConfig.TEMPLE_COST, Types.BUILDING.WATER_TEMPLE));
                    return true;
                case FOREST_TEMPLE:
                    tribe.subtractStars(TribesConfig.TEMPLE_FOREST_COST);
                    board.setBuildingAt(targetPos.x, targetPos.y, Types.BUILDING.FOREST_TEMPLE);
                    city.addBuilding(new Temple(targetPos.x, targetPos.y, TribesConfig.TEMPLE_FOREST_COST, Types.BUILDING.FOREST_TEMPLE));
                    return true;
                case SAWMILL:
                    tribe.subtractStars(TribesConfig.SAW_MILL_COST);
                    board.setBuildingAt(targetPos.x, targetPos.y, Types.BUILDING.SAWMILL);
                    city.addBuilding(new Sawmill(targetPos.x, targetPos.y));
                    return true;
                case WINDMILL:
                    tribe.subtractStars(TribesConfig.WIND_MILL_COST);
                    board.setBuildingAt(targetPos.x, targetPos.y, Types.BUILDING.WINDMILL);
                    city.addBuilding(new Windmill(targetPos.x, targetPos.y));
                    return true;
                case LUMBER_HUT:
                    tribe.subtractStars(TribesConfig.LUMBER_HUT_COST);
                    board.setBuildingAt(targetPos.x, targetPos.y, Types.BUILDING.LUMBER_HUT);
                    city.addBuilding(new LumberHut(targetPos.x, targetPos.y));
                    return true;
                case CUSTOM_HOUSE:
                    tribe.subtractStars(TribesConfig.CUSTOM_COST);
                    board.setBuildingAt(targetPos.x, targetPos.y, Types.BUILDING.CUSTOM_HOUSE);
                    city.addBuilding(new CustomHouse(targetPos.x, targetPos.y));
                    return true;
                //Ask Judy to add monuments to addBuilding.
            }
        }
        return false;
    }

    private boolean isBuildable(final GameState gs, int cost, boolean checkIfUnique) {
        Tribe tribe = gs.getTribe(city.getTribeId());
        Board board = gs.getBoard();
        TechnologyTree techTree = tribe.getTechTree();
        int stars = tribe.getStars();

        //Cost constraint
        if(stars < cost) { return false; }

        //Technology constraint
        if(!techTree.isResearched(buildingType.getTechnologyRequirement())) { return false; }

        //Terrain constraint
        for(Types.TERRAIN goodTerrain : buildingType.getTerrainRequirements()) {
            if(board.getTerrainAt(targetPos.x, targetPos.y) != goodTerrain){ return false; }
        }

        //Uniqueness constrain
        if(checkIfUnique) {
            for(Vector2d tile : board.getCityTiles(city.getActorId())) {
                if(board.getBuildingAt(tile.x, tile.y) == buildingType) { return false; }
            }
        }

        return true;
    }
}
