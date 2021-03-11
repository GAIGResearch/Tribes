package players.portfolio.scripts;

import core.actions.Action;
import core.actors.Actor;
import core.actors.Tribe;
import core.game.Board;
import core.game.GameState;
import players.portfolio.scripts.utils.InterestPoint;
import players.portfolio.scripts.utils.MilitaryFunc;

import java.util.Random;

public class MoveOffensivelyScr extends BaseScript {

    //This script returns the Move action that places the unit on an own city tile.
    private Random rnd;

    public MoveOffensivelyScr(Random rnd)
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
                int cityIdAt = b.getCityIdAt(posX, posY);
                if(cityIdAt != -1)
                {
                    Tribe tribe = gs.getTribe(ac.getTribeId());
                    return !tribe.getCitiesID().contains(cityIdAt);
                }
                return false;
            }
        });

    }



}
