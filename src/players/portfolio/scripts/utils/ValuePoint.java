package players.portfolio.scripts.utils;

import core.actors.Actor;
import core.game.GameState;

public interface ValuePoint
{
    int ofInterest(GameState gs, Actor ac, int posX, int posY);
}
