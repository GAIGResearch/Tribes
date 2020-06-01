package core.actions.cityactions;

import core.TechnologyTree;
import core.Types;
import core.actions.Action;
import core.actors.Building;
import core.actors.Temple;
import core.actors.Tribe;
import core.game.Board;
import core.game.GameState;
import core.actors.City;
import utils.Vector2d;

public class Build extends CityAction
{
    private Types.BUILDING buildingType;

    public Build(int cityId)
    {
        super.cityId = cityId;
    }

    public void setBuildingType(Types.BUILDING buildingType) {this.buildingType = buildingType;}

    public Types.BUILDING getBuildingType() {
        return buildingType;
    }

    @Override
    public boolean isFeasible(final GameState gs) {

        switch (buildingType) {
            //Buildings that can be repeated in a city:
            case PORT:
            case FARM:
            case MINE:
            case LUMBER_HUT:
            case TEMPLE:
            case WATER_TEMPLE:
            case MOUNTAIN_TEMPLE:
            case FOREST_TEMPLE:
                return isBuildable(gs, buildingType.getCost(buildingType.getKey(),gs.getTribesConfig()), false);

            //Buildings that must be unique in a city
            case SAWMILL:
            case CUSTOMS_HOUSE:
            case WINDMILL:
            case FORGE:
                return isBuildable(gs, buildingType.getCost(buildingType.getKey(),gs.getTribesConfig()), true);

            //Buildings that must be unique in a tribe (i.e. monuments)
            case ALTAR_OF_PEACE:
            case EMPERORS_TOMB:
            case EYE_OF_GOD:
            case GATE_OF_POWER:
            case PARK_OF_FORTUNE:
            case TOWER_OF_WISDOM:
            case GRAND_BAZAR:
                boolean buildingConstraintsOk = isBuildable(gs, buildingType.getCost(buildingType.getKey(),gs.getTribesConfig()), false);
                if(buildingConstraintsOk)
                {
                    City city = (City) gs.getActor(this.cityId);
                    Tribe tribe = gs.getTribe(city.getTribeId());
                    return tribe.isMonumentBuildable(buildingType);
                }
                else return false;
        }
        return false;
    }

    @Override
    public boolean execute(GameState gs) {
        City city = (City) gs.getActor(this.cityId);
        Tribe tribe = gs.getTribe(city.getTribeId());
        Board board = gs.getBoard();

        if(isFeasible(gs)) {

            tribe.subtractStars(buildingType.getCost(buildingType.getKey(),gs.getTribesConfig()));
            board.setBuildingAt(targetPos.x, targetPos.y, buildingType);
            board.setResourceAt(targetPos.x, targetPos.y, null);

            if(buildingType.isTemple())
                city.addBuilding(gs, new Temple(targetPos.x, targetPos.y, buildingType, this.cityId));
            else
                city.addBuilding(gs, new Building(targetPos.x, targetPos.y, buildingType, this.cityId));



            if(buildingType == Types.BUILDING.PORT)
                board.buildPort(targetPos.x, targetPos.y);
            if(buildingType.isMonument())
                tribe.monumentIsBuilt(buildingType);

            return true;
        }
        return false;
    }

    @Override
    public Action copy() {
        Build build = new Build(this.cityId);
        build.setBuildingType(this.buildingType);
        build.setTargetPos(this.targetPos.copy());
        return build;
    }

    private boolean isBuildable(final GameState gs, int cost, boolean checkIfUnique) {
        City city = (City) gs.getActor(this.cityId);
        Tribe tribe = gs.getTribe(city.getTribeId());
        Board board = gs.getBoard();
        TechnologyTree techTree = tribe.getTechTree();
        int stars = tribe.getStars();

        //Cost constraint
        if(cost > 0 && stars < cost) { return false; }

        //Technology constraint
        if(buildingType.getTechnologyRequirement() != null &&
                !techTree.isResearched(buildingType.getTechnologyRequirement())) { return false; }

        //Terrain constraint
        if (!(buildingType.getTerrainRequirements().contains(board.getTerrainAt(targetPos.x, targetPos.y)))) return false;

        //Resource constraint
        Types.RESOURCE resNeeded = buildingType.getResourceConstraint(gs.getTribesConfig());
        if (resNeeded != null)
        {
            //if there's a constraint, resource at location must be what's needed.
            Types.RESOURCE resAtLocation = board.getResourceAt(targetPos.x, targetPos.y);
            if(resAtLocation == null || resNeeded != resAtLocation)
                return false;
        }

        //Adjacency constraint
        Types.BUILDING buildingNeeded = buildingType.getAdjacencyConstraint();
        if(buildingNeeded != null)
        {
            boolean adjFound = false;
            for(Vector2d adjPos : targetPos.neighborhood(1,0,board.getSize()))
            {
                if(board.getBuildingAt(adjPos.x, adjPos.y) == buildingNeeded)
                {
                    adjFound = true;
                    break;
                }
            }

            if(!adjFound) return false;
        }

        //Uniqueness constrain
        if(checkIfUnique) {
            for(Vector2d tile : board.getCityTiles(this.cityId)) {
                if(board.getBuildingAt(tile.x, tile.y) == buildingType) { return false; }
            }
        }

        return true;
    }

    public String toString()
    {
        return "BUILD by city " + this.cityId+ " at " + targetPos + " : " + buildingType.toString();
    }
}
