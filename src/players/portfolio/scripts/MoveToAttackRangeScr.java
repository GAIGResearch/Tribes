package players.portfolio.scripts;

import core.Types;
import core.actions.Action;
import core.actors.Actor;
import core.actors.City;
import core.actors.units.Unit;
import core.game.Board;
import core.game.GameState;
import players.portfolio.scripts.utils.InterestPoint;
import players.portfolio.scripts.utils.MilitaryFunc;
import players.portfolio.scripts.utils.ValuePoint;
import utils.Vector2d;

import java.util.LinkedList;
import java.util.Random;

public class MoveToAttackRangeScr extends BaseScript {

    //This script returns the Move action that places the unit on an own city centre.
    private Random rnd;

    public MoveToAttackRangeScr(Random rnd)
    {
        this.rnd = rnd;
    }

    @Override
    public Action process(GameState gs, Actor ac)
    {
        return new MilitaryFunc().position(gs, ac, actions, rnd, 1, new ValuePoint() {
            @Override
            public int ofInterest(GameState gs, Actor ac, int posX, int posY) {
                Board b = gs.getBoard();
                int attackRange = ((Unit)ac).RANGE;
                Vector2d targetPos = new Vector2d(posX, posY);
                LinkedList<Vector2d> neighs = targetPos.neighborhood(attackRange, 0, b.getSize());

                int numTargets = 0;
                for(Vector2d n : neighs)
                {
                    Unit u = b.getUnitAt(n.x, n.y);
                    if(u != null && u.getTribeId() != ac.getTribeId())
                        numTargets++;
                }

                return numTargets;
            }
        });

    }



}
