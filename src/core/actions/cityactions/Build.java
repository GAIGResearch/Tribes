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
        LinkedList<Vector2d> tiles = board.getCityTiles(city.getActorID());

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
        //TODO: Add monuments
        Tribe tribe = gs.getTribe(city.getTribeId());
        Board board = gs.getBoard();
        TechnologyTree t = tribe.getTechTree();
        int stars = tribe.getStars();

        switch (buildingType) {
            case PORT:
                if(stars >= TribesConfig.PORT_COST && t.isResearched(Types.BUILDING.PORT.getTechnologyRequirement())){
                    for(Types.TERRAIN goodTerrain : Types.BUILDING.PORT.getTerrainRequirements()){
                        if(board.getTerrainAt(targetPos.x, targetPos.y) != goodTerrain){
                            return false;
                        }
                    }
                    return true;
                }
            case FARM:
                if(stars >= TribesConfig.FARM_COST && t.isResearched(Types.BUILDING.FARM.getTechnologyRequirement())){
                    for(Types.TERRAIN goodTerrain : Types.BUILDING.FARM.getTerrainRequirements()){
                        if(board.getTerrainAt(targetPos.x, targetPos.y) != goodTerrain){
                            return false;
                        }
                    }
                    return true;
                }
            case MINE:
                if(stars >= TribesConfig.MINE_COST && t.isResearched(Types.BUILDING.MINE.getTechnologyRequirement())){
                    for(Types.TERRAIN goodTerrain : Types.BUILDING.MINE.getTerrainRequirements()){
                        if(board.getTerrainAt(targetPos.x, targetPos.y) != goodTerrain){
                            return false;
                        }
                    }
                    return true;
                }
            case LUMBER_HUT:
                if(stars >= TribesConfig.LUMBER_HUT_COST && t.isResearched(Types.BUILDING.LUMBER_HUT.getTechnologyRequirement())){
                    for(Types.TERRAIN goodTerrain : Types.BUILDING.LUMBER_HUT.getTerrainRequirements()){
                        if(board.getTerrainAt(targetPos.x, targetPos.y) != goodTerrain){
                            return false;
                        }
                    }
                    return true;
                }
            case TEMPLE:
                if(stars >= TribesConfig.TEMPLE_COST && t.isResearched(Types.BUILDING.TEMPLE.getTechnologyRequirement())){
                    for(Types.TERRAIN goodTerrain : Types.BUILDING.TEMPLE.getTerrainRequirements()){
                        if(board.getTerrainAt(targetPos.x, targetPos.y) != goodTerrain){
                            return false;
                        }
                    }
                    return true;
                }
            case WATER_TEMPLE:
                if(stars >= TribesConfig.TEMPLE_COST && t.isResearched(Types.BUILDING.WATER_TEMPLE.getTechnologyRequirement())){
                    for(Types.TERRAIN goodTerrain : Types.BUILDING.WATER_TEMPLE.getTerrainRequirements()){
                        if(board.getTerrainAt(targetPos.x, targetPos.y) != goodTerrain){
                            return false;
                        }
                    }
                    return true;
                }
            case MOUNTAIN_TEMPLE:
                if(stars >= TribesConfig.TEMPLE_COST && t.isResearched(Types.BUILDING.MOUNTAIN_TEMPLE.getTechnologyRequirement())){
                    for(Types.TERRAIN goodTerrain : Types.BUILDING.MOUNTAIN_TEMPLE.getTerrainRequirements()){
                        if(board.getTerrainAt(targetPos.x, targetPos.y) != goodTerrain){
                            return false;
                        }
                    }
                    return true;
                }
            case FOREST_TEMPLE:
                if(stars >= TribesConfig.TEMPLE_FOREST_COST && t.isResearched(Types.BUILDING.FOREST_TEMPLE.getTechnologyRequirement())){
                    for(Types.TERRAIN goodTerrain : Types.BUILDING.FOREST_TEMPLE.getTerrainRequirements()){
                        if(board.getTerrainAt(targetPos.x, targetPos.y) != goodTerrain){
                            return false;
                        }
                    }
                    return true;
                }
        }
        return false;
    }

    @Override
    public boolean execute(GameState gs) {
        //TODO: Add monuments, roads
        Tribe tribe = gs.getTribe(city.getTribeId());
        Board board = gs.getBoard();

        if(isFeasible(gs)) {
            switch (buildingType) {
                case FARM:
                    tribe.subtractStars(TribesConfig.FARM_COST);
                    board.setBuildingAt(targetPos.x, targetPos.y, Types.BUILDING.FARM);
                    city.addBuildings(new Farm(targetPos.x, targetPos.y));
                    return true;
                case MINE:
                    tribe.subtractStars(TribesConfig.MINE_COST);
                    board.setBuildingAt(targetPos.x, targetPos.y, Types.BUILDING.MINE);
                    city.addBuildings(new Mine(targetPos.x, targetPos.y));
                    return true;
                case PORT:
                    tribe.subtractStars(TribesConfig.PORT_COST);
                    board.setBuildingAt(targetPos.x, targetPos.y, Types.BUILDING.PORT);
                    city.addBuildings(new Port(targetPos.x, targetPos.y));
                    return true;
                case ROAD:
                    //is road a building? Road object is missing
                    return true;
                case FORGE:
                    tribe.subtractStars(TribesConfig.FORGE_COST);
                    board.setBuildingAt(targetPos.x, targetPos.y, Types.BUILDING.FORGE);
                    city.addBuildings(new Forge(targetPos.x, targetPos.y));
                    return true;
                case TEMPLE:
                    tribe.subtractStars(TribesConfig.TEMPLE_COST);
                    board.setBuildingAt(targetPos.x, targetPos.y, Types.BUILDING.TEMPLE);
                    city.addBuildings(new Temple(targetPos.x, targetPos.y, TribesConfig.TEMPLE_COST, Types.BUILDING.TEMPLE));
                    return true;
                case MOUNTAIN_TEMPLE:
                    tribe.subtractStars(TribesConfig.TEMPLE_COST);
                    board.setBuildingAt(targetPos.x, targetPos.y, Types.BUILDING.MOUNTAIN_TEMPLE);
                    city.addBuildings(new Temple(targetPos.x, targetPos.y, TribesConfig.TEMPLE_COST, Types.BUILDING.MOUNTAIN_TEMPLE));
                    return true;
                case WATER_TEMPLE:
                    tribe.subtractStars(TribesConfig.TEMPLE_COST);
                    board.setBuildingAt(targetPos.x, targetPos.y, Types.BUILDING.WATER_TEMPLE);
                    city.addBuildings(new Temple(targetPos.x, targetPos.y, TribesConfig.TEMPLE_COST, Types.BUILDING.WATER_TEMPLE));
                    return true;
                case FOREST_TEMPLE:
                    tribe.subtractStars(TribesConfig.TEMPLE_FOREST_COST);
                    board.setBuildingAt(targetPos.x, targetPos.y, Types.BUILDING.FOREST_TEMPLE);
                    city.addBuildings(new Temple(targetPos.x, targetPos.y, TribesConfig.TEMPLE_FOREST_COST, Types.BUILDING.FOREST_TEMPLE));
                    return true;
                case SAWMILL:
                    tribe.subtractStars(TribesConfig.SAW_MILL_COST);
                    board.setBuildingAt(targetPos.x, targetPos.y, Types.BUILDING.SAWMILL);
                    city.addBuildings(new Sawmill(targetPos.x, targetPos.y));
                    return true;
                case WINDMILL:
                    tribe.subtractStars(TribesConfig.WIND_MILL_COST);
                    board.setBuildingAt(targetPos.x, targetPos.y, Types.BUILDING.WINDMILL);
                    city.addBuildings(new Windmill(targetPos.x, targetPos.y));
                    return true;
                case LUMBER_HUT:
                    tribe.subtractStars(TribesConfig.LUMBER_HUT_COST);
                    board.setBuildingAt(targetPos.x, targetPos.y, Types.BUILDING.LUMBER_HUT);
                    city.addBuildings(new LumberHut(targetPos.x, targetPos.y));
                    return true;
                case CUSTOM_HOUSE:
                    tribe.subtractStars(TribesConfig.CUSTOM_COST);
                    board.setBuildingAt(targetPos.x, targetPos.y, Types.BUILDING.CUSTOM_HOUSE);
                    city.addBuildings(new CustomHouse(targetPos.x, targetPos.y));
                    return true;
                //Ask Judy to add monuments to addBuildings.
            }
        }
        return false;
    }
}
