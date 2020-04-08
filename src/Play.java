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

        KeyController ki = new KeyController(true);
        ActionController ac = new ActionController();

        long seed = System.currentTimeMillis();
        System.out.println("Game seed: " + seed);
//        String filename = "SampleLevel2p.csv";
        String filename = "SampleLevel.csv";
//        String filename = "MinimalLevel.csv";

        ArrayList<Agent> players = new ArrayList<>();
        ArrayList<Tribe> tribes = new ArrayList<>();

        Agent ag1 = new HumanAgent(ac);
//        long randomSeed = System.currentTimeMillis();
//        System.out.println("Agent 1 random seed: " + randomSeed);
//        Agent ag1 = new RandomAgent(randomSeed);
        players.add(ag1);
//        tribes.add(new Tribe(Types.TRIBE.OUMAJI));
        tribes.add(new Tribe(Types.TRIBE.XIN_XI));

        Agent ag2 = new HumanAgent(ac);
////        Agent ag2 = new RandomAgent(randomSeed);
//        players.add(ag2);
        tribes.add(new Tribe(Types.TRIBE.IMPERIUS));
//
        Agent ag3 = new HumanAgent(ac);
////        Agent ag3 = new RandomAgent(randomSeed);
//        players.add(ag3);
        tribes.add(new Tribe(Types.TRIBE.BARDUR));
//
        Agent ag4 = new HumanAgent(ac);
////        Agent ag4 = new RandomAgent(randomSeed);
//        players.add(ag4);
        tribes.add(new Tribe(Types.TRIBE.OUMAJI));

        Game game = new Game();
        game.init(players, tribes, filename, seed);

        Run.runGame(game, ki, ac);
        System.out.println("Running Tribes...");
    }
}