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
        LinkedList<Vector2d> tiles = gs.getBoard().getCityTiles(city.getActorID());

        for(Vector2d tile : tiles){
            for(Types.BUILDING building: Types.BUILDING.values()){
                Build action = new Build(city);
                action.setBuildingType(building);
                action.targetPos = tile;
                if(action.isFeasible(gs)){
                    actions.add(action);
                }
            }
        }
        return actions;
    }

    @Override
    public boolean isFeasible(final GameState gs) {
        //TODO: Is feasible to build
        //Use is_buildable in Building?
        Tribe tribe = gs.getTribe(city.getTribeId());
        TechnologyTree t = tribe.getTechTree();
        int stars = tribe.getStars();
        return false;
    }

    @Override
    public boolean execute(GameState gs) {
        //TODO: Executes the action
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
                //What about monuments and special buildings? missing objects and from addBuildings
            }
        }
        return false;
    }
}
