package utils;

import core.Constants;
import core.game.Board;
import core.game.Game;
import players.KeyController;

import javax.swing.*;

public class GUI extends JFrame {
    private GameView view;
    private Game game;
    private KeyController ki;

    /**
     * Constructor
     * @param title Title of the window.
     */
    public GUI(Game game, String title, KeyController ki, boolean closeAppOnClosingWindow) {
        super(title);
        this.game = game;
        this.ki = ki;

        // TODO: Create frame layout, panels etc...
        view = new GameView(game.getBoard(), Constants.CELL_SIZE);
        JPanel mainPanel = new JPanel();
        mainPanel.add(view);
        getContentPane().add(mainPanel);

        // Frame properties
        pack();
        this.setVisible(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        if(closeAppOnClosingWindow){
            setDefaultCloseOperation(EXIT_ON_CLOSE);
        }
        repaint();
    }


    /**
     * Paints the GUI, to be called at every game tick.
     */
    public void paint(Board b) {
        view.paint(b);
    }
}
