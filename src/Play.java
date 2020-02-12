import core.Types;
import core.game.Game;
import core.actors.Tribe;
import players.Agent;
import players.DoNothingAgent;
import players.KeyController;

import java.util.ArrayList;

/**
 * Entry point of the framework.
 */
public class Play {

    public static void main(String[] args) {

        KeyController ki = new KeyController(true);
        long seed = System.currentTimeMillis();
//        String filename = "SampleLevel2p.csv";
        String filename = "SampleLevel.csv";

        ArrayList<Agent> players = new ArrayList<>();
        ArrayList<Tribe> tribes = new ArrayList<>();

        Agent ag1 = new DoNothingAgent(seed);
        players.add(ag1);
        tribes.add(new Tribe(Types.TRIBE.XIN_XI));

        Agent ag2 = new DoNothingAgent(seed);
        players.add(ag2);
        tribes.add(new Tribe(Types.TRIBE.IMPERIUS));


        Agent ag3 = new DoNothingAgent(seed);
        players.add(ag3);
        tribes.add(new Tribe(Types.TRIBE.BARDUR));

        Agent ag4 = new DoNothingAgent(seed);
        players.add(ag4);
        tribes.add(new Tribe(Types.TRIBE.OUMAJI));



        Game game = new Game();
        game.init(players, tribes, filename, seed);

        Run.runGame(game, ki);
        System.out.println("Running Tribes...");
    }
}