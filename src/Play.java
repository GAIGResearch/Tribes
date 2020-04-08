import core.Types;
import core.actions.Action;
import core.game.Game;
import core.actors.Tribe;
import players.*;

import java.util.ArrayList;

/**
 * Entry point of the framework.
 */
public class Play {


    public static void main(String[] args) {
//        String filename = "SampleLevel2p.csv";
        String filename = "SampleLevel.csv";
//        String filename = "MinimalLevel.csv";

        play4(filename, true, true, true, true);
//        play4(filename, false, false, false, true);
    }


    public static void play4(String filename, boolean h1, boolean h2, boolean h3, boolean h4)
    {
        KeyController ki = new KeyController(true);
        ActionController ac = new ActionController();

        long seed = System.currentTimeMillis();
        System.out.println("Game seed: " + seed);

        ArrayList<Agent> players = new ArrayList<>();
        ArrayList<Tribe> tribes = new ArrayList<>();

        long randomSeed = System.currentTimeMillis();
        Agent ag1 = h1 ? new HumanAgent(ac) : new RandomAgent(randomSeed);
        players.add(ag1);
        tribes.add(new Tribe(Types.TRIBE.XIN_XI));

        Agent ag2 = h2 ? new HumanAgent(ac) : new RandomAgent(randomSeed);
        players.add(ag2);
        tribes.add(new Tribe(Types.TRIBE.IMPERIUS));

        Agent ag3 = h3 ? new HumanAgent(ac) : new RandomAgent(randomSeed);
        players.add(ag3);
        tribes.add(new Tribe(Types.TRIBE.BARDUR));

        Agent ag4 = h4 ? new HumanAgent(ac) : new RandomAgent(randomSeed);
        players.add(ag4);
        tribes.add(new Tribe(Types.TRIBE.OUMAJI));

        Game game = new Game();
        game.init(players, tribes, filename, seed);

        Run.runGame(game, ki, ac);
        System.out.println("Running Tribes...");
    }

}