package players.portfolio.scripts;

import core.Types;
import core.actions.Action;
import core.actors.Actor;
import core.actors.City;
import core.game.Board;
import core.game.GameState;
import players.portfolio.scripts.utils.InterestPoint;
import players.portfolio.scripts.utils.MilitaryFunc;

import java.util.Random;

public class MoveToOwnCityCentreScr extends BaseScript {

    //This script returns the Move action that places the unit on an own city centre.
    private Random rnd;

    public MoveToOwnCityCentreScr(Random rnd)
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
                Types.TERRAIN t = b.getTerrainAt(posX, posY);
                if(t == Types.TERRAIN.CITY)
                {
                    //if city does not belong to me.
                    City c = b.getCityInBorders(posX, posY);
                    return c.getTribeId() == ac.getTribeId();
                }
                return false;
            }
        });

    }



}
