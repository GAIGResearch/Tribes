package players.portfolio;

import core.actions.Action;
import core.actors.Actor;
import core.game.GameState;
import players.portfolio.scripts.Script;

public class ActionAssignment {
    Actor actor;
    Script script;

    public ActionAssignment(Actor a, Script s)
    {
        actor = a;
        script = s;
    }

    public Action process(GameState gs)
    {
        return script.process(gs, actor);
    }


}
