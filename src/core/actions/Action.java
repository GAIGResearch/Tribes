package core.actions;

import core.game.GameState;

public abstract class Action
{
    public abstract boolean isFeasible(GameState gs);

    public abstract void execute(GameState gs);

}
