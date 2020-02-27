package utils;

import core.Constants;
import core.Types;
import core.game.Board;
import core.game.Game;
import core.game.GameState;
import players.KeyController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class GUI extends JFrame {
    private JLabel appTurn;

    private Game game;
    private KeyController ki;

    private GameView view;
    private TribeView tribeView;
    private InfoView infoView;

    /**
     * Constructor
     * @param title Title of the window.
     */
    public GUI(Game game, String title, KeyController ki, boolean closeAppOnClosingWindow) {
        super(title);
        this.game = game;
        this.ki = ki;


        infoView = new InfoView();
        tribeView = new TribeView();
        view = new GameView(game.getBoard(), infoView);


        // Create frame layout
        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.weighty = 0;

        setLayout(gbl);

        // Main panel definition
        JPanel mainPanel = createGamePanel();
        JPanel sidePanel = createSidePanel();

        gbc.gridx = 0;
        getContentPane().add(Box.createRigidArea(new Dimension(10, 0)), gbc);

        gbc.gridx++;
        getContentPane().add(mainPanel, gbc);

        gbc.gridx++;
        getContentPane().add(Box.createRigidArea(new Dimension(10, 0)), gbc);

        gbc.gridx++;
        getContentPane().add(sidePanel, gbc);

        gbc.gridx++;
        getContentPane().add(Box.createRigidArea(new Dimension(10, 0)), gbc);

        // Frame properties
        pack();
        this.setVisible(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        if(closeAppOnClosingWindow){
            setDefaultCloseOperation(EXIT_ON_CLOSE);
        }
        repaint();
    }


    private JPanel createGamePanel()
    {
        JPanel mainPanel = new JPanel();

        mainPanel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //Only provide information if clicking on a visible tile
                int x = e.getX() / Constants.CELL_SIZE;
                int y = e.getY() / Constants.CELL_SIZE;

                view.setHighlight(x,y);
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.SOUTH;
        c.weighty = 0;

        c.gridy = 0;
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)), c);

        c.gridy++;
        mainPanel.add(view, c);

        c.gridy++;
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)), c);

        return mainPanel;
    }

    private JPanel createSidePanel()
    {
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.SOUTH;
        c.weighty = 0;

        JLabel appTitle = new JLabel("Tribes");
        Font textFont = new Font(appTitle.getFont().getName(), Font.PLAIN, 16);
        appTitle.setFont(textFont);

        appTurn = new JLabel("Turn: 0");
        appTurn.setFont(textFont);

        c.gridy = 0;
        sidePanel.add(appTitle, c);

        c.gridy++;
        sidePanel.add(Box.createRigidArea(new Dimension(0, 5)), c);

        c.gridy++;
        sidePanel.add(appTurn, c);

        c.gridy++;
        sidePanel.add(Box.createRigidArea(new Dimension(0, 5)), c);

        c.gridy++;
        sidePanel.add(infoView, c);

        c.gridy++;
        sidePanel.add(Box.createRigidArea(new Dimension(0, 5)), c);

        c.gridy++;
        sidePanel.add(tribeView, c);

        c.gridy++;
        sidePanel.add(Box.createRigidArea(new Dimension(0, 5)), c);

        return sidePanel;
    }


    /**
     * Paints the GUI, to be called at every game tick.
     */
    public void paint(GameState gs) {
        view.paint(gs);
        tribeView.paint(gs);
        infoView.paint(gs);
    }
}
