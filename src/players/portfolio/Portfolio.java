package players.portfolio;

import core.Types;
import core.game.GameState;
import players.portfolio.scripts.BaseScript;

import java.util.ArrayList;
import java.util.TreeMap;

public abstract class Portfolio {

    public abstract void initPortfolio();
    public abstract ArrayList<ActionAssignment> produceActionAssignments(GameState state);
    public abstract TreeMap<Types.ACTION, BaseScript[]> getPortfolio();
}
