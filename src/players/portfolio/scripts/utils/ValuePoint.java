package players.portfolio.scripts.utils;

import core.actors.Actor;
import core.game.GameState;

public interface ValuePoint
{
    double ofInterest(GameState gs, Actor ac, int posX, int posY);
}
