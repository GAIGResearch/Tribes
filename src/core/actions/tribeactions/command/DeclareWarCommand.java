package core.actions.tribeactions.command;

import core.actions.Action;
import core.actions.ActionCommand;
import core.game.Game;
import core.game.GameState;

public class DeclareWarCommand implements ActionCommand {

    @Override
    public boolean execute(Action a, GameState gs){
        return true;
    }
}
