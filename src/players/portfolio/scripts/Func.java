package players.portfolio.scripts;

import core.Types;
import core.actions.Action;
import core.actions.cityactions.ClearForest;
import core.actions.cityactions.Spawn;
import core.actions.unitactions.Attack;
import core.actions.unitactions.Convert;
import core.actions.unitactions.command.AttackCommand;
import core.actors.Actor;
import core.actors.Building;
import core.actors.City;
import core.actors.units.Unit;
import core.game.GameState;
import utils.Vector2d;

import java.util.ArrayList;
import java.util.LinkedList;

import static core.Types.BUILDING.*;
import static core.Types.RESOURCE.*;
import static core.Types.TERRAIN.*;

public class Func {

    public int candidatesForBuilding(GameState gs, Actor ac, Types.BUILDING unique_building, ArrayList<Action> actions, ArrayList<Action> candidates)
    {
        City c = (City) gs.getActor(ac.getActorId());
        for(Building b : c.getBuildings())
        {
            //No more than one building of type 'unique_building' can be built.
            if(b.type == unique_building)
                return 0;
        }

        int highestNeigh = 0;
        for(Action act : actions)
        {
            ClearForest action = (ClearForest)act;
            Vector2d forestPos = action.getTargetPos();
            int goodNeigh = 0;
            LinkedList<Vector2d> neighs = forestPos.neighborhood(1, 0, gs.getBoard().getSize());
            for(Vector2d neighPos : neighs)
            {
                goodNeigh += goodFor(gs, neighPos, unique_building) ? 1 : 0;
            }

            if(goodNeigh > highestNeigh)
            {
                candidates = new ArrayList<>();
                highestNeigh = goodNeigh;
                candidates.add(act);
            }else if (goodNeigh == highestNeigh)
            {
                candidates.add(act);
            }
        }

        return highestNeigh;
    }

    private boolean goodFor(GameState gs, Vector2d v, Types.BUILDING unique_building)
    {
        Types.TERRAIN t = gs.getBoard().getTerrainAt(v.x, v.y);
        Types.BUILDING b = gs.getBoard().getBuildingAt(v.x, v.y);
        Types.RESOURCE r = gs.getBoard().getResourceAt(v.x, v.y);
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
        }
        return false;
    }


    public Action getActionByActorAttr(GameState gs, ArrayList<Action> actions, Actor source,
                                       BaseScript.Feature feat, boolean maximize)
    {
        double targetValue = maximize ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
        Action finalAction = null;
        for(Action act : actions)
        {
            Unit target = null;
            if(act instanceof Attack)
                target = (Unit) gs.getActor(((Attack)act).getTargetId());
            else if(act instanceof Convert)
                target = (Unit) gs.getActor(((Convert)act).getTargetId());
            else if(act instanceof Spawn)
            {
                Types.UNIT uType = ((Spawn)act).getUnitType();
                target = Types.UNIT.createUnit(source.getPosition(), 0, false, -1, -1, uType) ;
            }
            else System.out.println("ACTION NOT SUPPORTED: " + act.toString());

            if(target != null)
            {
                boolean better = false;
                switch (feat)
                {
                    case HP:
                        better = maximize ? target.getCurrentHP() > targetValue : target.getCurrentHP() < targetValue;
                        break;
                    case COST:
                        better = maximize ? target.COST > targetValue : target.COST < targetValue;
                        break;
                    case MOVEMENT:
                        better = maximize ? target.MOV > targetValue : target.MOV < targetValue;
                        break;
                    case RANGE:
                        better = maximize ? target.RANGE > targetValue : target.RANGE < targetValue;
                        break;
                    case DEFENCE:
                        better = maximize ? target.DEF > targetValue : target.DEF < targetValue;
                        break;
                    case ATTACK:
                        better = maximize ? target.ATK > targetValue : target.ATK < targetValue;
                        break;
                    case DISTANCE:
                        Vector2d sourcePos = source.getPosition();
                        Vector2d targetPos = target.getPosition();
                        double dist = sourcePos.dist(targetPos);
                        better = maximize ? dist > targetValue : dist < targetValue;
                        break;
                    case DAMAGE:
                        GameState gsCopy = gs.copy();
                        int currHP = target.getCurrentHP();
                        new AttackCommand().execute(act, gsCopy);
                        int nextHP = target.getCurrentHP();
                        int diff = nextHP - currHP;
                        if(nextHP <= 0)
                            diff -= 100;
                        better = maximize ? Math.abs(diff) > targetValue : Math.abs(diff) < targetValue;
                        break;
                    default:
                        System.out.println("Error: getMinAttr not defined for feature " + feat);
                }

                if(better)
                {
                    targetValue = target.DEF;
                    finalAction = act;
                }
            }
        }

        return finalAction;

    }

}
