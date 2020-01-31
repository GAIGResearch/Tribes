import core.game.Game;
import core.units.Tribe;
import players.Agent;
import players.DoNothingAgent;
import players.KeyController;

import java.util.ArrayList;

/**
 * Entry point of the framework.
 */
public class Test {

    public static void main(String[] args) {

        KeyController ki = new KeyController(true);
        long seed = System.currentTimeMillis();
        String filename = "SampleLevel.csv";

        Game game = new Game(seed);
        ArrayList<Agent> players = new ArrayList<>();
        ArrayList<Tribe> tribes = new ArrayList<>();

        Agent ag1 = new DoNothingAgent(seed);
        players.add(ag1);
        tribes.add(new Tribe());

        Agent ag2 = new DoNothingAgent(seed);
        players.add(ag2);
        tribes.add(new Tribe());


        game.init(players, tribes, filename);

        Run.runGame(game, ki);
        System.out.println("Running Tribes...");
    }
}