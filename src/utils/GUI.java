package utils;

import core.game.Game;
import players.KeyController;

import javax.swing.*;

public class GUI extends JFrame {
    private JLabel appTick;
    private Game game;
    private KeyController ki;

    /**
     * Constructor
     * @param title Title of the window.
     */
    public GUI(Game game, String title, KeyController ki, boolean closeAppOnClosingWindow, boolean displayPOHuman) {
        super(title);
        this.game = game;
        this.ki = ki;

        // TODO: Create frame layout, panels etc...



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
    public void paint() {

    }
}
