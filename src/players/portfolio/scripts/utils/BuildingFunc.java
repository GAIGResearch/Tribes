package players.portfolio.scripts.utils;

import core.Types;
import core.actions.Action;
import core.actions.cityactions.Build;
import core.actions.cityactions.CityAction;
import core.actions.cityactions.ClearForest;
import core.game.GameState;
import utils.Vector2d;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

import static core.Types.BUILDING.*;
import static core.Types.RESOURCE.*;
import static core.Types.TERRAIN.*;

public class BuildingFunc {

    //This function checks if neighPos is a good position as a neighbour of "Unique_building"
    public boolean goodNeighbourFor(GameState gs, Vector2d neighPos, Types.BUILDING unique_building)
    {
        Types.TERRAIN t = gs.getBoard().getTerrainAt(neighPos.x, neighPos.y);
        Types.BUILDING b = gs.getBoard().getBuildingAt(neighPos.x, neighPos.y);
        Types.RESOURCE r = gs.getBoard().getResourceAt(neighPos.x, neighPos.y);
        switch (unique_building)
        {
            case CUSTOMS_HOUSE:
                return (t.isWater() && (b == null || b == PORT));
            case WINDMILL:
                return (r == CROPS || b == FARM);
            case FORGE:
                return ((t == MOUNTAIN && r == ORE) || b == MINE);
            case SAWMILL:
                return t == FOREST || b == LUMBER_HUT;
            default:
                System.out.println("You're using this function wrong: " + unique_building);
        }
        return false;
    }


    //Evaluates if centrePos is a good position for a building of type "target".
    //Only for support buildings: custom-house, sawmill, windmill and forge.
    public int evalNeighSupportBuilding(Vector2d centrePos, GameState gs, Types.BUILDING target)
    {
        int goodNeigh = 0;
        LinkedList<Vector2d> neighs = centrePos.neighborhood(1, 0, gs.getBoard().getSize());
        for(Vector2d neighPos : neighs)
        {
            goodNeigh += goodNeighbourFor(gs, neighPos, target) ? 1 : 0;
        }
        return goodNeigh;
    }

    public boolean validConstruction(GameState gs, Vector2d pos, Types.BUILDING buildingType, int cityId, boolean checkUniqueness)
    {
        //Terrain constraint
        if(!buildingType.getTerrainRequirements().contains(gs.getBoard().getTerrainAt(pos.x, pos.y)))
            return false;

        //Uniqueness constraint.
        if(checkUniqueness) for(Vector2d tile : gs.getBoard().getCityTiles(cityId)) {
            if(gs.getBoard().getBuildingAt(tile.x, tile.y) == buildingType)
                return false;
        }

        return true;
    }

    //For support buildings: custom-house, sawmill, windmill and forge.
    public Action buildSupportBuilding(Types.BUILDING target, GameState gs, ArrayList<Action> actions, Random rnd)
    {
        ArrayList<Action> candidate_actions = new ArrayList<>();

        int highestNeigh = 0;
        for(Action act : actions)
        {
            boolean typeCheck = (act instanceof Build && ((Build)act).getBuildingType() == target)  //if building, check we're building the correct target
                             || (act instanceof ClearForest);                                       //if clearing forest, all is good.

            if(typeCheck)
            {
                Vector2d targetPos = ((CityAction)act).getTargetPos();
                int goodNeigh = evalNeighSupportBuilding(targetPos, gs, target);

                if(goodNeigh > highestNeigh)
                {
                    candidate_actions = new ArrayList<>();
                    highestNeigh = goodNeigh;
                    candidate_actions.add(act);
                }else if (goodNeigh == highestNeigh)
                {
                    candidate_actions.add(act);
                }
            }
        }

        if(candidate_actions.size() > 0)
        {
            int nActions = candidate_actions.size();
            return candidate_actions.get(rnd.nextInt(nActions));
        }

        return null;
    }

    //For base buildings: port, farm, mine, lumber huts.
    public Action buildBaseBuilding(Types.BUILDING toBuild, Types.BUILDING bonusNeighBuilding, GameState gs, ArrayList<Action> actions, Random rnd)
    {
        ArrayList<Action> candidate_actions = new ArrayList<>();
        int bestSupportedVal = -1;
        for(Action act : actions)
        {
            Build action = (Build)act;
            Vector2d targetPos = action.getTargetPos();

            if(action.getBuildingType() == toBuild) {
                LinkedList<Vector2d> neighs = targetPos.neighborhood(1, 0, gs.getBoard().getSize());

                int bestForBonus = 0;
                for (Vector2d neighPos : neighs) {
                    boolean valid = validConstruction(gs, neighPos, bonusNeighBuilding, action.getCityId(), true);
                    if (valid)
                    {
                        int goodForBonusBuilding = evalNeighSupportBuilding(neighPos, gs, bonusNeighBuilding);
                        if (bestForBonus < goodForBonusBuilding) {
                            bestForBonus = goodForBonusBuilding;
                        }
                    }
                }

                if(bestForBonus > bestSupportedVal)
                {
                    candidate_actions.add(act);
                    bestSupportedVal = bestForBonus;
                    candidate_actions = new ArrayList<>();
                }else if(bestForBonus == bestSupportedVal)
                {
                    candidate_actions.add(act);
                }
            }
        }

        int nActions = candidate_actions.size();
        if( nActions > 0)
            return candidate_actions.get(rnd.nextInt(nActions));

        return null;
    }

    public boolean goodForSupportingBuilding(GameState gs, Vector2d position, Types.BUILDING[] targets, int cityId)
    {
        for (Types.BUILDING target : targets) {
            //Check if the supporting building can be built here and if it has a good value.
            boolean valid = validConstruction(gs, position, target, cityId, true);
            if (valid && evalNeighSupportBuilding(position, gs, target) > 0) {
                return true;
            }
        }
        return false;
    }

    public boolean goodForBaseBuilding(GameState gs, Vector2d position,  Types.BUILDING[] targets, int cityId)
    {
        for (Types.BUILDING target : targets) {

            // a. Check if base building can be built here
            boolean valid = validConstruction(gs, position, target, cityId, false);
            if (valid) {
                // b. Check if this is a good place for a base building: would it neighbour any good place for a support building?
                LinkedList<Vector2d> neighs = position.neighborhood(1, 0, gs.getBoard().getSize());

                for (Vector2d neighPos : neighs) {
                    Types.BUILDING supportBuilding = target.getMatchingBuilding();
                    valid = validConstruction(gs, neighPos, supportBuilding, cityId, true);
                    if (valid && evalNeighSupportBuilding(neighPos, gs, supportBuilding) > 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    public Action buildInIdle(Types.BUILDING targetBuilding, GameState gs, ArrayList<Action> actions, Random rnd)
    {
        ArrayList<Action> candidate_actions = new ArrayList<>();

        for(Action act : actions)
        {
            Build action = (Build)act;
            if(action.getBuildingType() == targetBuilding)
            {
                Vector2d targetPos = action.getTargetPos();
                //1. Check that this is not a good place for a supporting building.
                if(!goodForSupportingBuilding(gs, targetPos, new Types.BUILDING[]{SAWMILL, CUSTOMS_HOUSE, WINDMILL, FORGE}, action.getCityId()))
                {
                    //2. Check that this is not a good place for a base building.
                    if(!goodForBaseBuilding(gs, targetPos, new Types.BUILDING[]{LUMBER_HUT, PORT, FARM, MINE}, action.getCityId()))
                        candidate_actions.add(act);
                }
            }
        }

        int nActions = candidate_actions.size();
        if( nActions > 0)
            return candidate_actions.get(rnd.nextInt(nActions));
        return null;
    }

}
