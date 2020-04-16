package players.heuristics;

import core.game.GameState;

public interface StateHeuristic {

    double evaluateState(GameState gameState);
}
