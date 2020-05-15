import core.game.Game;
import players.ActionController;
import players.KeyController;
import utils.GUI;
import utils.WindowInput;

import static core.Constants.*;

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
            frame = new GUI(g, "Tribes", ki, wi, ac, true);
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
}
