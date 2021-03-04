package players.portfolio;

import core.actions.Action;
import core.actors.Actor;
import core.game.GameState;
import players.portfolio.scripts.BaseScript;

public class ActionAssignment {
    private BaseScript script;
    private Actor actor;


    public ActionAssignment(Actor a, BaseScript s)
    {
        actor = a;
        script = s;
    }

    public Action process(GameState gs)
    {
        return script.process(gs, actor);
    }

    public BaseScript getScript() {
        return script;
    }

    public Actor getActor() {
        return actor;
    }

}
