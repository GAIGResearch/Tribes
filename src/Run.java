import core.Types;
import core.game.Game;
import players.ActionController;
import players.KeyController;
import gui.GUI;
import gui.WindowInput;

import static core.Constants.*;
import static core.Types.TRIBE.*;
import static core.Types.TRIBE.OUMAJI;

class Run {

    /**
     * Runs 1 game.
     * @param g - game to run
     * @param ki - Key controller
     * @param ac - Action controller
     */
    static void runGame(Game g, KeyController ki, ActionController ac) {
        WindowInput wi = null;
        GUI frame = null;
        if (VISUALS) {
            wi = new WindowInput();
            wi.windowClosed = false;
            frame = new GUI(g, "Tribes", ki, wi, ac, false);
            frame.addWindowListener(wi);
            frame.addKeyListener(ki);
        }

        g.run(frame, wi);
    }


    /**
     * Runs a game, no visuals nor human player
     * @param g - game to run
     */
    static void runGame(Game g) {
        g.run(null, null);
    }

    static Tournament.PlayerType parsePlayerTypeStr(String arg) throws Exception
    {
        switch(arg)
        {
            case "Human": return Tournament.PlayerType.HUMAN;
            case "Do Nothing": return Tournament.PlayerType.DONOTHING;
            case "Random": return Tournament.PlayerType.RANDOM;
            case "Rule Based": return Tournament.PlayerType.SIMPLE;
            case "OSLA": return Tournament.PlayerType.OSLA;
            case "MC": return Tournament.PlayerType.MC;
            case "MCTS": return Tournament.PlayerType.MCTS;
            case "RHEA": return Tournament.PlayerType.RHEA;
            case "OEP": return Tournament.PlayerType.OEP;
            case "pMCTS": return Tournament.PlayerType.PORTFOLIO_MCTS;
        }
        throw new Exception("Error: unrecognized Player Type: " + arg);
    }

    static Types.TRIBE parseTribeStr(String arg) throws Exception
    {
        switch(arg)
        {
            case "Xin Xi": return XIN_XI;
            case "Imperius": return IMPERIUS;
            case "Bardur": return BARDUR;
            case "Oumaji": return OUMAJI;
        }
        throw new Exception("Error: unrecognized Tribe: " + arg);
    }

}
