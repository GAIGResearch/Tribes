package players.portfolio.scripts.utils;

import core.Types;
import core.actions.Action;
import core.actions.cityactions.Spawn;
import core.actions.tribeactions.ResearchTech;
import core.actions.unitactions.Attack;
import core.actions.unitactions.Convert;
import core.actions.unitactions.command.AttackCommand;
import core.actors.Actor;
import core.actors.units.Unit;
import core.game.GameState;
import players.portfolio.scripts.BaseScript;
import utils.Vector2d;

import java.util.ArrayList;
import java.util.Random;

public class MilitaryFunc {

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

    public Action getPreferredResearchTech(GameState gs, ArrayList<Action> actions, ArrayList<Types.TECHNOLOGY> preferredTechs, Random rnd)
    {
        ArrayList<Action> candidate_actions = new ArrayList<>();
        int lowestTier = Integer.MAX_VALUE;
        boolean preferredBranch = false;

        for(Action act : actions)
        {
            ResearchTech rt = (ResearchTech)act;
            int tier = rt.getTech().getTier();

            if( preferredTechs.contains(rt.getTech()))
            {
                if(!preferredBranch || tier < lowestTier)
                {
                    //Either we have our first from the preferred branch, or we have a lower tier in the preferred branch.
                    candidate_actions.clear();
                    lowestTier = tier;
                }

                //Only consider if in the lowest tier found
                if(tier == lowestTier)
                    candidate_actions.add(rt);

                preferredBranch = true;
            }
            else if(!preferredBranch)
            {
                //Only insert here if we didn't find any from the preferred branch.
                if(tier < lowestTier)
                {
                    //We have a new lowest tier in the not preferred branch.
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
            return candidate_actions.get(rnd.nextInt(nActions));

        return null;
    }
}
