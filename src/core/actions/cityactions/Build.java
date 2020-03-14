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

import java.util.ArrayList;
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

        switch (buildingType) {
            case ROAD:
                //Not this way.
                System.out.println("ERROR: Action Build can't built roads. Use tribeactions.BuildRoad instead.");
                return false;

            //Buildings that can be repeated in a city:
            case PORT:
            case FARM:
            case MINE:
            case LUMBER_HUT:
            case TEMPLE:
            case WATER_TEMPLE:
            case MOUNTAIN_TEMPLE:
            case FOREST_TEMPLE:
                return isBuildable(gs, buildingType.getCost(), false);

            //Buildings that must be unique in a city
            case SAWMILL:
            case CUSTOM_HOUSE:
            case WINDMILL:
            case FORGE:
                return isBuildable(gs, buildingType.getCost(), true);

            //Buildings that must be unique in a tribe (i.e. monuments)
            case ALTAR_OF_PEACE:
            case EMPERORS_TOMB:
            case EYE_OF_GOD:
            case GATE_OF_POWER:
            case PARK_OF_FORTUNE:
            case TOWER_OF_WISDOM:
                boolean buildingConstraintsOk = isBuildable(gs, buildingType.getCost(), false);
                Tribe tribe = gs.getTribe(city.getTribeId());
                if(buildingConstraintsOk)
                    return tribe.isMonumentBuildable(buildingType);
                else return false;
        }
        return false;
    }

    @Override
    public boolean execute(GameState gs) {
        //TODO: Make sure all the side effects for Buildings are counted.
        Tribe tribe = gs.getTribe(city.getTribeId());
        Board board = gs.getBoard();

        if(isFeasible(gs)) {

            tribe.subtractStars(buildingType.getCost());
            board.setBuildingAt(targetPos.x, targetPos.y, buildingType);

            switch (buildingType) {
                case FARM:
                    city.addBuilding(new Farm(targetPos.x, targetPos.y));
                    return true;
                case MINE:
                    city.addBuilding(new Mine(targetPos.x, targetPos.y));
                    return true;
                case PORT:
                    city.addBuilding(new Port(targetPos.x, targetPos.y));
                    board.setTradeNetwork(targetPos.x, targetPos.y, true);
                    return true;
                case FORGE:
                    city.addBuilding(new Forge(targetPos.x, targetPos.y));
                    return true;
                case TEMPLE:
                    city.addBuilding(new Temple(targetPos.x, targetPos.y, TribesConfig.TEMPLE_COST, Types.BUILDING.TEMPLE));
                    return true;
                case MOUNTAIN_TEMPLE:
                    city.addBuilding(new Temple(targetPos.x, targetPos.y, TribesConfig.TEMPLE_COST, Types.BUILDING.MOUNTAIN_TEMPLE));
                    return true;
                case WATER_TEMPLE:
                    city.addBuilding(new Temple(targetPos.x, targetPos.y, TribesConfig.TEMPLE_COST, Types.BUILDING.WATER_TEMPLE));
                    return true;
                case FOREST_TEMPLE:
                    city.addBuilding(new Temple(targetPos.x, targetPos.y, TribesConfig.TEMPLE_FOREST_COST, Types.BUILDING.FOREST_TEMPLE));
                    return true;
                case SAWMILL:
                    city.addBuilding(new Sawmill(targetPos.x, targetPos.y));
                    return true;
                case WINDMILL:
                    city.addBuilding(new Windmill(targetPos.x, targetPos.y));
                    return true;
                case LUMBER_HUT:
                    city.addBuilding(new LumberHut(targetPos.x, targetPos.y));
                    return true;
                case CUSTOM_HOUSE:
                    city.addBuilding(new CustomHouse(targetPos.x, targetPos.y));
                    return true;

                case ALTAR_OF_PEACE:
                case EMPERORS_TOMB:
                case EYE_OF_GOD:
                case GATE_OF_POWER:
                case PARK_OF_FORTUNE:
                case TOWER_OF_WISDOM:
                    city.addBuilding(new Monument(targetPos.x, targetPos.y, buildingType));
                    tribe.monumentIsBuilt(buildingType);
                    break;
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
        if(cost > 0 && stars < cost) { return false; }

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
