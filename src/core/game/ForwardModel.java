package core.game;

import core.actions.Action;
import core.actors.City;
import core.actors.Tribe;
import utils.Vector2d;

import java.util.ArrayList;
import java.util.Random;


public class ForwardModel {

    // Board of the game
    private Board board;

    // Size of the board.
    private int size;

    // Game tick counter as in GameState, for logging purposes (only valid for true model of the game)
    private int tick;

    /**
     * Forward model constructor
     */
    ForwardModel() {

    }

    /**
     * Initializes the data structures of the game: board, bombs, flames, etc.
     * Adds avatars to the game and sets them alive.
     * Generates the initial board of the game.
     */
    void init(Tribe[] tribes, Random rnd, String filename)
    {
        LevelLoader ll = new LevelLoader();
        board = ll.buildLevel(tribes, filename, rnd);

        //init the observability grid of the tribes
        for(Tribe tribe : tribes)
        {
            tribe.initObsGrid(board.getSize());
            int startingCityId = tribe.getCitiesID().get(0);
            City c = (City) board.getActor(startingCityId);
            Vector2d cityPos = c.getPosition();
            tribe.clearView(cityPos.x, cityPos.y);
        }
    }


    /**
     * Advances the game state applying a single action received.
     * @param playerAction to be executed in the current game state.
     */
    public void next(Action playerAction){
        //TODO: MAIN function of this class.
        // Takes the action passed as parameter and runs it in the game.

    }

    /**
     * Creates an exact, deep copy of this model
     * @param playerID player that views the model, used for
     *                 reducing the state for partial observability
     * @return a copy of the model.
     */
    public ForwardModel copy(int playerID)
    {
        //TODO: Make an exact copy of this model, reducing if for PO if playerID != -1
        return null;
    }

    /**
     * Returns the tribes playing the game
     * @return the tribes playing the game
     */
    public Tribe[] getTribes() {
        return board.getTribes();
    }

    /**
     * Returns the game board.
     * @return the game board.
     */
    public Board getBoard()
    {
        return board;
    }
}
