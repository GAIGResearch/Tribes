package core.actions.tribeactions;

import core.game.GameState;

public class EndTurnAction extends TribeAction {
    public EndTurnAction(){}

    @Override
    public boolean isFeasible(GameState gs) {
        return false;
    }

    @Override
    public void execute(GameState gs) {

    }
}
