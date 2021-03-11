package players.portfolio.scripts;

import core.Types;
import core.actions.Action;
import core.actors.Actor;
import core.actors.City;
import core.game.Board;
import core.game.GameState;
import players.portfolio.scripts.utils.InterestPoint;
import players.portfolio.scripts.utils.MilitaryFunc;
import utils.Vector2d;

import java.util.Random;

public class MoveToDisembarkScr extends BaseScript {

    //This script returns the Move action that places the unit on an own city centre.
    private Random rnd;

    public MoveToDisembarkScr(Random rnd)
    {
        this.rnd = rnd;
    }

    @Override
    public Action process(GameState gs, Actor ac)
    {
        Vector2d pos = ac.getPosition();
        Types.TERRAIN t = gs.getBoard().getTerrainAt(pos.x, pos.y);
        if(!t.isWater())
            return null;

        return new MilitaryFunc().moveTowards(gs, ac, actions, rnd, new InterestPoint() {
            @Override
            public boolean ofInterest(GameState gs, Actor ac, int posX, int posY) {
                Types.TERRAIN t = gs.getBoard().getTerrainAt(posX, posY);
                return !t.isWater();
            }
        });

    }



}
