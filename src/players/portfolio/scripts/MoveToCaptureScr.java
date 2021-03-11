package players.portfolio.scripts;

import core.Types;
import core.actions.Action;
import core.actions.cityactions.GrowForest;
import core.actions.unitactions.Move;
import core.actors.Actor;
import core.actors.City;
import core.game.Board;
import core.game.GameState;
import players.portfolio.scripts.utils.BuildingFunc;
import players.portfolio.scripts.utils.InterestPoint;
import players.portfolio.scripts.utils.MilitaryFunc;
import utils.Vector2d;

import java.util.ArrayList;
import java.util.Random;

import static core.Types.BUILDING.LUMBER_HUT;

public class MoveToCaptureScr extends BaseScript {

    //This script returns the Move action that places the unit on a ruin, village or enemy city.
    private Random rnd;

    public MoveToCaptureScr(Random rnd)
    {
        this.rnd = rnd;
    }

    @Override
    public Action process(GameState gs, Actor ac)
    {
        return new MilitaryFunc().moveTowards(gs, ac, actions, rnd, new InterestPoint() {
            @Override
            public boolean ofInterest(GameState gs, Actor ac, int posX, int posY) {
                Board b = gs.getBoard();
                Types.RESOURCE r = b.getResourceAt(posX, posY);
                Types.TERRAIN t = b.getTerrainAt(posX, posY);
                if(r == Types.RESOURCE.RUINS || t == Types.TERRAIN.VILLAGE)
                    return true;
                else if(t == Types.TERRAIN.CITY)
                {
                    //if city does not belong to me.
                    City c = b.getCityInBorders(posX, posY);
                    return c.getTribeId() != ac.getTribeId();
                }
                return false;
            }
        });

    }



}
