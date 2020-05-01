package players.heuristics;

import core.game.GameState;

public interface StateHeuristic {

    default double evaluateState(GameState gameState){ return 0.0; }

    default double evaluateState(GameState oldState, GameState newState){ return 0.0; }

}
