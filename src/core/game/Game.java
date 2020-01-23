package core.game;

import players.Agent;

import java.util.ArrayList;

public class Game {

    // State of the game (objects, ticks, etc).
    private GameState gs;

    // Seed for the game state.
    private long seed;

    // Size of the board.
    private int size;

    // List of players of the game
    private ArrayList<Agent> players;

    public Game()
    {}

    /**
     * Constructor of the game
     * @param seed Seed for the game (used only for board generation)
     * @param size Size of the board.
     */
    public Game(long seed, int size) {
        this.seed = seed;
        this.size = size;
        this.gs = new GameState(seed, size);
    }



}
