package players.portfolio.scripts.utils;

import core.actors.Actor;
import core.game.Board;
import core.game.GameState;
import utils.Vector2d;

public interface InterestPoint
{
    boolean ofInterest(GameState gs, Actor ac, int posX, int posY);
}
