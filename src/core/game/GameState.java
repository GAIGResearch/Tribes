package core.game;

public class GameState {

    // Forward model for the game.
    ForwardModel model;

    // Seed for the game state.
    private long seed;

    // Size of the board.
    private int size;

    // Current tick of the game.
    private int tick = 0;

    //Default constructor.
    public GameState()
    {
    }

    //Another constructor.
    public GameState(long seed, int size) {
        this.seed = seed;
        this.size = size;
        this.model = new ForwardModel(size);
    }

}
