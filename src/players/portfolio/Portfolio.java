package players.portfolio;

import core.game.GameState;

import java.util.ArrayList;

public abstract class Portfolio {

    public abstract void initPortfolio();
    public abstract ArrayList<ActionAssignment> produceActionAssignments(GameState state);

}
