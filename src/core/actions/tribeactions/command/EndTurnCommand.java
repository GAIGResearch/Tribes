package core.actions.tribeactions.command;

import core.TribesConfig;
import core.actions.Action;
import core.actions.ActionCommand;
import core.actions.tribeactions.BuildRoad;
import core.actions.tribeactions.EndTurn;
import core.actors.Tribe;
import core.game.GameState;
import utils.Vector2d;

public class EndTurnCommand implements ActionCommand {

    @Override
    public boolean execute(Action a, GameState gs) {
        EndTurn action = (EndTurn)a;
        if(action.isFeasible(gs))
        {
            gs.setEndTurn(true);
            return true;
        }
        return false;
    }
}
