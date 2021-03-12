package players.heuristics;

import core.game.GameState;
import players.portfolio.ActionAssignment;

public interface PruneHeuristic {
    default double evaluateState(GameState state, ActionAssignment aas){ return 0.0; }
}
