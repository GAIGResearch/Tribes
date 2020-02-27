package utils;

import core.Constants;
import core.Types;
import core.actions.tribeactions.EndTurnAction;
import core.game.Board;
import core.game.Game;
import core.game.GameState;
import players.ActionController;
import players.KeyController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import static core.Constants.FRAME_DELAY;

public class GUI extends JFrame implements Runnable {
    private JLabel appTurn;
    private JLabel activeTribe;

    private GameState gs;
    private KeyController ki;
    private ActionController ac;

    private GameView view;
//    private TribeView tribeView;
    private InfoView infoView;

    private boolean finishedUpdate = true;

    /**
     * Constructor
     * @param title Title of the window.
     */
    public GUI(Game game, String title, KeyController ki, ActionController ac, boolean closeAppOnClosingWindow) {
        super(title);
        this.ki = ki;
        this.ac = ac;

        infoView = new InfoView();
//        tribeView = new TribeView();
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

                infoView.setHighlight(x,y);
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
        activeTribe = new JLabel("Tribe acting: ");
        activeTribe.setFont(textFont);

        c.gridy = 0;
        sidePanel.add(appTitle, c);

        c.gridy++;
        sidePanel.add(Box.createRigidArea(new Dimension(0, 5)), c);

        c.gridy++;
        sidePanel.add(appTurn, c);

        c.gridy++;
        sidePanel.add(Box.createRigidArea(new Dimension(0, 5)), c);

        c.gridy++;
        sidePanel.add(activeTribe, c);

        c.gridy++;
        sidePanel.add(Box.createRigidArea(new Dimension(0, 5)), c);

        c.gridy++;
        sidePanel.add(infoView, c);

        c.gridy++;
        sidePanel.add(Box.createRigidArea(new Dimension(0, 5)), c);

        c.gridy++;
//        sidePanel.add(tribeView, c);
        JButton endTurn = new JButton("End Turn");
        endTurn.addActionListener(e -> ac.addAction(new EndTurnAction(), gs));
        sidePanel.add(endTurn, c);

        c.gridy++;
        sidePanel.add(Box.createRigidArea(new Dimension(0, 5)), c);

        return sidePanel;
    }


    /**
     * Paints the GUI, to be called at every game tick.
     */
    public void update(GameState gs) {
        this.gs = gs;
    }

    public boolean nextMove() {
        return finishedUpdate;
    }

    @Override
    public void run() {
        finishedUpdate = false;
        view.paint(gs);
//        tribeView.paint(gs);
        infoView.paint(gs);
        appTurn.setText("Turn: " + gs.getTick());
        if (gs.getActiveTribe() != null) {
            activeTribe.setText("Tribe acting: " + gs.getActiveTribe().getName());
        }
        try {
            Thread.sleep(1);
//            Thread.sleep(FRAME_DELAY);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        finishedUpdate = true;
    }
}
