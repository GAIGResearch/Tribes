package players.portfolio.scripts.utils;

import core.Types;
import core.actions.Action;
import core.actions.cityactions.Spawn;
import core.actions.tribeactions.ResearchTech;
import core.actions.unitactions.Attack;
import core.actions.unitactions.Convert;
import core.actions.unitactions.Move;
import core.actions.unitactions.command.AttackCommand;
import core.actors.Actor;
import core.actors.units.Unit;
import core.game.Board;
import core.game.GameState;
import players.portfolio.scripts.BaseScript;
import utils.Pair;
import utils.Utils;
import utils.Vector2d;

import java.util.ArrayList;
import java.util.Random;

public class MilitaryFunc {

    public Pair<Action, Double> getActionByActorAttr(GameState gs, ArrayList<Action> actions, Actor source,
                                                     BaseScript.Feature feat, boolean maximize)
    {
        double targetValue = maximize ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
        Action finalAction = null;
        double min=Double.POSITIVE_INFINITY, max=Double.NEGATIVE_INFINITY;
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
                        if(better) targetValue = target.getCurrentHP();
                        break;
                    case COST:
                        better = maximize ? target.COST > targetValue : target.COST < targetValue;
                        if(better) targetValue = target.COST;
                        break;
                    case MOVEMENT:
                        better = maximize ? target.MOV > targetValue : target.MOV < targetValue;
                        if(better) targetValue = target.MOV;
                        break;
                    case RANGE:
                        better = maximize ? target.RANGE > targetValue : target.RANGE < targetValue;
                        if(better) targetValue = target.RANGE;
                        break;
                    case DEFENCE:
                        better = maximize ? target.DEF > targetValue : target.DEF < targetValue;
                        if(better) targetValue = target.DEF;
                        break;
                    case ATTACK:
                        better = maximize ? target.ATK > targetValue : target.ATK < targetValue;
                        if(better) targetValue = target.ATK;
                        break;
                    case DISTANCE:
                        Vector2d sourcePos = source.getPosition();
                        Vector2d targetPos = target.getPosition();
                        double dist = sourcePos.dist(targetPos);
                        better = maximize ? dist > targetValue : dist < targetValue;
                        if(better) targetValue = dist;
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
                        if(better) targetValue = Math.abs(diff);
                        break;
                    default:
                        System.out.println("Error: getMinAttr not defined for feature " + feat);
                }

                if(targetValue < min) min = targetValue;
                if(targetValue > max) max = targetValue;

                if(better)
                {
                    finalAction = act;
                }
            }
        }

        double actionValue = Utils.normalise(targetValue, 0, max);
        if(!maximize)
            actionValue = 1 - actionValue;

        return new Pair<>(finalAction,actionValue);
    }

    public Pair<Action, Double> getPreferredResearchTech(GameState gs, ArrayList<Action> actions, ArrayList<Types.TECHNOLOGY> preferredTechs, Random rnd)
    {
        ArrayList<Action> candidate_actions = new ArrayList<>();
        int lowestTier = Integer.MAX_VALUE;

        for(Action act : actions)
        {
            ResearchTech rt = (ResearchTech)act;
            int tier = rt.getTech().getTier();

            if( preferredTechs.contains(rt.getTech()))
            {
                if(tier < lowestTier)
                {
                    //Either we have our first from the preferred branch, or we have a lower tier in the preferred branch.
                    candidate_actions.clear();
                    lowestTier = tier;
                }

                //Only consider if in the lowest tier found
                if(tier == lowestTier)
                    candidate_actions.add(rt);

            }
        }

        int nActions = candidate_actions.size();
        if( nActions > 0)
        {
            double value = (4-lowestTier) / 4.0;
            return new Pair<>(candidate_actions.get(rnd.nextInt(nActions)), value);
        }
        return null;
    }

    public Pair<Action, Double> moveTowards(GameState gs, Actor ac, ArrayList<Action> actions, Random rnd, InterestPoint p)
    {
        ArrayList<Action> candidate_actions = new ArrayList<>();
        ArrayList<Vector2d> movePositions = new ArrayList<>();
        Board b = gs.getBoard();

        //Positions in the board that are of interest.
        int size = b.getSize();
        for(int i = 0; i < size; ++i) {
            for(int j = 0; j < size; ++j) {
                if(p.ofInterest(gs, ac, i, j))
                    movePositions.add(new Vector2d(i,j));
            }
        }

        double max = Double.NEGATIVE_INFINITY;
        double minDistance = Double.MAX_VALUE;
        for(Action act : actions)
        {
            Move action = (Move) act;
            Vector2d destPos = action.getDestination();

            for(Vector2d potentialPosition : movePositions)
            {
                double dist = destPos.custom_dist(potentialPosition);
                if(dist < minDistance)
                {
                    minDistance = dist;
                    candidate_actions.clear();
                    candidate_actions.add(act);
                }else if(dist == minDistance)
                {
                    candidate_actions.add(act);
                }
                if(dist>max)
                    max = dist;
            }
        }

        int nActions = candidate_actions.size();
        if( nActions > 0) {
            Action act =  candidate_actions.get(rnd.nextInt(nActions));
            double value = 1 - Utils.normalise(minDistance, 0, max);
            //System.out.println("TRIBE ID: " + ac.getTribeId() + " Moving from " +  ac.getPosition() +  " to " + ((Move)act).getDestination() + " distance: " + minDistance);
            return new Pair<>(act, value);
        }
        return null;
    }


    public Pair<Action, Double> position(GameState gs, Actor ac, ArrayList<Action> actions,
                           Random rnd, int minValue, ValuePoint p)
    {
        ArrayList<Action> candidate_actions = new ArrayList<>();

        //Positions in the board that are of interest.
        double bestValue = Double.NEGATIVE_INFINITY;
        for(Action act : actions)
        {
            Move action = (Move) act;
            Vector2d destPos = action.getDestination();
            double valuePos = p.ofInterest(gs, ac, destPos.x, destPos.y);
            if(valuePos > bestValue)
            {
                bestValue = valuePos;
                candidate_actions.clear();
                candidate_actions.add(act);
            }else if(valuePos == bestValue)
            {
                candidate_actions.add(act);
            }
        }

        int nActions = candidate_actions.size();
        if( nActions > 0 && bestValue >= minValue) {
            Action act =  candidate_actions.get(rnd.nextInt(nActions));
           //System.out.println("TRIBE ID: " + ac.getTribeId() + " Moving to RANGE " +  ac.getPosition() +  " to " + ((Move)act).getDestination() + " reaching: " + bestValue);
            return new Pair<>(act, bestValue);
        }
        return null;
    }


}
