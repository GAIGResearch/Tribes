package players.portfolio.scripts;

import core.Types;
import core.actions.Action;
import core.actions.cityactions.ClearForest;
import core.actions.unitactions.Attack;
import core.actors.Actor;
import core.actors.Building;
import core.actors.City;
import core.actors.units.Unit;
import core.game.GameState;
import utils.Vector2d;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

import static core.Types.BUILDING.*;
import static core.Types.TERRAIN.*;
import static core.Types.RESOURCE.*;

public class ClearForestByProdScr extends BaseScript {

    //This script returns the Clear Forest action that makes room for a custom house.

    private Random rnd;

    public ClearForestByProdScr(Random rnd)
    {
        this.rnd = rnd;
    }

    @Override
    public Action process(GameState gs, Actor ac)
    {
        int maxValue = Integer.MIN_VALUE;
        Action finalAction = null;

        Types.BUILDING[] targets = new Types.BUILDING[]{CUSTOMS_HOUSE, WINDMILL, FORGE, SAWMILL};

        for(Types.BUILDING target : targets)
        {
            ArrayList<Action> candidate_actions = new ArrayList<>();
            int candidateValue = new Func().candidatesForBuilding(gs, ac, target, actions, candidate_actions);

            if(candidateValue > maxValue && candidate_actions.size() > 0)
            {
                maxValue = candidateValue;
                int nActions = candidate_actions.size();
                finalAction = candidate_actions.get(rnd.nextInt(nActions));
            }
        }

        return finalAction;
    }



}
