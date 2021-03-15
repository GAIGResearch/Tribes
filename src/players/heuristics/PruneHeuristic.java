package players.heuristics;

import core.game.GameState;
import players.portfolio.ActionAssignment;
import players.portfolioMCTS.PortfolioTreeNode;

import java.util.ArrayList;

public interface PruneHeuristic {
    default double evaluatePrune(GameState state, ActionAssignment aas){ return 0.0; }
    default boolean[] prune (PortfolioTreeNode parent, ArrayList<ActionAssignment> actions, GameState gameState, int k) {return null;}
    default boolean[] unprune (PortfolioTreeNode parent, ArrayList<ActionAssignment> actions, GameState gameState, boolean[] pruned) {return null;}
}
