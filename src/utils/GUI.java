package utils;

import core.actions.tribeactions.EndTurn;
import core.actions.unitactions.*;
import core.actors.units.Unit;
import core.game.Game;
import core.game.GameState;
import core.actions.Action;
import players.ActionController;
import players.HumanAgent;
import players.KeyController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static core.Constants.*;


public class GUI extends JFrame {
    private JLabel appTurn;
    private JLabel activeTribe, activeTribeInfo, otherInfo;
    private int otherInfoDelay = GUI_INFO_DELAY;

    private Game game;
    private GameState gs;
//    private KeyController ki;
    private ActionController ac;
    private Examine lastExamineAction;

    private GameView view;
    private TribeView tribeView;
    private TechView techView;
    private InfoView infoView;

    // Zoomed screen dragging vars
    private Point2D startDrag, endDrag, panTranslate;

    public static double screenDiagonal;
    double scale = 1;

    /**
     * Constructor
     * @param title Title of the window.
     */
    public GUI(Game game, String title, KeyController ki, ActionController ac, boolean closeAppOnClosingWindow) {
        super(title);

        try {
            UIManager.setLookAndFeel( UIManager.getCrossPlatformLookAndFeelClassName() );
        } catch (Exception e) {
            e.printStackTrace();
        }

        Rectangle rect = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        screenDiagonal = Math.sqrt(rect.width*rect.width + rect.height* rect.height);

        CELL_SIZE = (int)(0.038*screenDiagonal*scale);
        GUI_GAME_VIEW_SIZE = (int)(0.36*screenDiagonal*scale);
        GUI_MIN_PAN = (int)(0.015*screenDiagonal*scale);
        GUI_COMP_SPACING = (int)(0.0045*screenDiagonal*scale);
        GUI_CITY_TAG_WIDTH = (int)(0.009*screenDiagonal*scale);
        GUI_SIDE_PANEL_WIDTH = (int)(0.25*screenDiagonal*scale);
        GUI_INFO_PANEL_HEIGHT = (int)(0.18*screenDiagonal*scale);
        GUI_ACTION_PANEL_HEIGHT = (int)(0.045*screenDiagonal*scale);
        GUI_TECH_PANEL_HEIGHT = (int)(0.16*screenDiagonal*scale);
        GUI_TECH_PANEL_FULL_SIZE = (int)(GUI_TECH_PANEL_FULL_SIZE*(1/scale));

//        this.ki = ki;
        this.ac = ac;
        this.game = game;

        infoView = new InfoView(ac);
        panTranslate = new Point2D.Double(0,0);
        view = new GameView(game.getBoard(), infoView, panTranslate);

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
        getContentPane().add(Box.createRigidArea(new Dimension(GUI_COMP_SPACING, 0)), gbc);

        gbc.gridx++;
        getContentPane().add(mainPanel, gbc);

        gbc.gridx++;
        getContentPane().add(Box.createRigidArea(new Dimension(GUI_COMP_SPACING, 0)), gbc);

        gbc.gridx++;
        getContentPane().add(sidePanel, gbc);

        gbc.gridx++;
        getContentPane().add(Box.createRigidArea(new Dimension(GUI_COMP_SPACING, 0)), gbc);

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
                Point2D translate = view.getPanTranslate();
                Point2D ep = new Point2D.Double(e.getX() - translate.getX(), e.getY() - translate.getY());
                Point2D p = GameView.rotatePointReverse((int)ep.getX(), (int)ep.getY());

                // If unit highlighted and action at new click valid for unit, execute action
                Action candidate = getActionAt((int)p.getX(), (int)p.getY(), infoView.getHighlightX(), infoView.getHighlightY());
                if (candidate != null) {
                    int n = 0;
                    if (candidate instanceof Disband) {  // These actions needs confirmation before executing
                        n = JOptionPane.showConfirmDialog(mainPanel,
                                "Confirm action " + candidate.toString(),
                                "Are you sure?",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.QUESTION_MESSAGE);
                    }
                    if (n == 0) {
                        ac.addAction(candidate, gs);
                    }
                    infoView.resetHighlight();
                } else {
                    // Otherwise highlight new cell
                    infoView.setHighlight((int)p.getX(), (int)p.getY());
//                    System.out.println("Highlighting: " + (int)p.getX() + " " + (int)p.getY());
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                startDrag = new Point2D.Double(e.getX(), e.getY());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                endDrag = new Point2D.Double(e.getX(), e.getY());
                if (startDrag != null && !(startDrag.equals(endDrag))) {
                    panTranslate = new Point2D.Double(+ endDrag.getX() - startDrag.getX(), + endDrag.getY() - startDrag.getY());
                    if (panTranslate.distance(0, 0) >= GUI_MIN_PAN) {
                        view.updatePan(panTranslate);
                        infoView.resetHighlight();
                    }
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        mainPanel.addMouseWheelListener(e -> {
            Point2D mouseLocation = MouseInfo.getPointerInfo().getLocation();
            Point2D panelLocation = mainPanel.getLocationOnScreen();
            Point2D viewCenter = new Point2D.Double(view.getPreferredSize().width/2.0, view.getPreferredSize().height/2.0);
            Point2D diff = new Point2D.Double((mouseLocation.getX() - panelLocation.getX() - viewCenter.getX())/GUI_ZOOM_FACTOR,
                    (mouseLocation.getY() - panelLocation.getY() - viewCenter.getY())/GUI_ZOOM_FACTOR);
            if (e.getWheelRotation() < 0) {
                // Zooming in
                CELL_SIZE += GUI_ZOOM_FACTOR;
                diff = new Point2D.Double(diff.getX()*-1, diff.getY()*-1);
            } else {
                // Zooming out
                CELL_SIZE -= GUI_ZOOM_FACTOR;
            }
            view.updatePan(diff);
        });

        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.SOUTH;
        c.weighty = 0;

        c.gridy = 0;
        mainPanel.add(Box.createRigidArea(new Dimension(0, GUI_COMP_SPACING/2)), c);

        c.gridy++;
        mainPanel.add(view, c);

        c.gridy++;
        mainPanel.add(Box.createRigidArea(new Dimension(0, GUI_COMP_SPACING/2)), c);

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

        activeTribeInfo = new JLabel("[stars: 0 (+0)]");
        activeTribeInfo.setFont(textFont);

        otherInfo = new JLabel(".");
        otherInfo.setFont(textFont);

        JTabbedPane tribeResearchInfo = new JTabbedPane();
        tribeView = new TribeView();
        techView = new TechView(ac, infoView);
        tribeResearchInfo.setPreferredSize(new Dimension(GUI_SIDE_PANEL_WIDTH, GUI_TECH_PANEL_HEIGHT));
        tribeResearchInfo.add("Tribe Info", tribeView);
        tribeResearchInfo.add("Tech Tree", techView);

        c.gridy = 0;
        sidePanel.add(appTitle, c);

        c.gridy++;
        sidePanel.add(Box.createRigidArea(new Dimension(0, GUI_COMP_SPACING/2)), c);

        c.gridy++;
        sidePanel.add(appTurn, c);

        c.gridy++;
        sidePanel.add(Box.createRigidArea(new Dimension(0, GUI_COMP_SPACING/2)), c);

        c.gridy++;
        sidePanel.add(activeTribe, c);
        c.gridy++;
        sidePanel.add(activeTribeInfo, c);
        c.gridy++;
        sidePanel.add(otherInfo, c);

        c.gridy++;
        sidePanel.add(Box.createRigidArea(new Dimension(0, GUI_COMP_SPACING/2)), c);

        c.gridy++;
        sidePanel.add(infoView, c);

        c.gridy++;
        sidePanel.add(Box.createRigidArea(new Dimension(0, GUI_COMP_SPACING/2)), c);

        c.gridy++;
        sidePanel.add(tribeResearchInfo, c);

        c.gridy++;
        sidePanel.add(Box.createRigidArea(new Dimension(0, GUI_COMP_SPACING/2)), c);

        c.gridy++;
        JButton endTurn = new JButton("End Turn");
        endTurn.addActionListener(e -> ac.addAction(new EndTurn(), gs));
        sidePanel.add(endTurn, c);

        c.gridy++;
        sidePanel.add(Box.createRigidArea(new Dimension(0, GUI_COMP_SPACING/2)), c);

        return sidePanel;
    }


    /**
     * Paints the GUI, to be called at every game tick.
     */
    public void update(GameState gs) {
        if (this.gs == null || this.gs.getActiveTribeID() != gs.getActiveTribeID()) {
            infoView.resetHighlight();  // Reset highlights on turn change
            view.setPanToTribe(gs);  // Pan camera to tribe capital on turn change
            ac.reset();  // Clear action queue on turn change
        }

        // Display result of Examine action
        Action a = ac.getLastActionPlayed();
        if (a instanceof Examine) {
            lastExamineAction = (Examine)a;
            ac.setLastActionPlayed(null);
        }

        this.gs = gs;
        performUpdate();

        // Check if city is levelling up, pop up dialogue to choose options if human agent
        if (gs.isLevelingUp() && game.getPlayers()[gs.getActiveTribeID()] instanceof HumanAgent) {
            int n = -1;
            Object[] options = new String[2];
            Action[] optionsA = new Action[2];
            HashMap<Integer, ArrayList<Action>> actions = gs.getCityActions();
            for (Map.Entry<Integer, ArrayList<Action>> e : actions.entrySet()) {
                for (int i = 0; i < e.getValue().size(); i++) {
                    options[i] = e.getValue().get(i).toString();
                    optionsA[i] = e.getValue().get(i);
                }
            }
            while (n == -1) {
                n = JOptionPane.showOptionDialog(this, //parent container of JOptionPane
                        "City is levelling up!",
                        "Level Up",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,  //do not use a custom Icon
                        options,  //the titles of buttons
                        options[0]);  //default button title
            }
            ac.addAction(optionsA[n], gs);
        }
    }

    /**
     * Retrieves action at specific location given by (actionX, actionY) coordinates, to be performed by
     * unit at coordinates (unitX, unitY).
     */
    private Action getActionAt(int actionX, int actionY, int unitX, int unitY) {
        if (!infoView.clickedTwice()) { // Only return action if we're highlighting the unit and not underneath it
            HashMap<Integer, ArrayList<Action>> possibleActions = gs.getUnitActions();
            for (Map.Entry<Integer, ArrayList<Action>> e : possibleActions.entrySet()) {
                Unit u = (Unit) gs.getActor(e.getKey());

                for (Action a : e.getValue()) {
                    Vector2d pos = getActionPosition(gs, a);
                    if (pos != null && pos.x == actionY && pos.y == actionX) {
                        if ((a instanceof Capture || a instanceof Examine) ||  // These actions don't need the unit highlighted
                                u.getPosition().x == unitY && u.getPosition().y == unitX) {
                            return a;
                        }
                    }
                }
            }
        }
        return null;
    }

    private void performUpdate() {
        view.paint(gs);
        tribeView.paint(gs);
        techView.paint(gs);
        infoView.paint(gs);
        appTurn.setText("Turn: " + gs.getTick());
        if (gs.getActiveTribe() != null) {
            activeTribe.setText("Tribe acting: " + gs.getActiveTribe().getName());
            activeTribeInfo.setText("stars: " + gs.getActiveTribe().getStars() + " (+" + gs.getActiveTribe().getMaxProduction(gs) + ")");
            if (lastExamineAction != null) {
                otherInfo.setText("Ruins: " + lastExamineAction.getBonus().toString());
                otherInfoDelay--;
                if (otherInfoDelay == 0) {
                    lastExamineAction = null;
                    otherInfo.setText(".");
                    otherInfoDelay = GUI_INFO_DELAY;
                }
            }
        }
        repaint();
    }


    public static Vector2d getActionPosition(GameState gs, Action a) {
        Vector2d pos = null;
        if (a instanceof Move) {
            pos = new Vector2d(((Move) a).getDestination().x, ((Move) a).getDestination().y);
        } else if (a instanceof Attack) {
            Unit target = (Unit) gs.getActor(((Attack) a).getTargetId());
            pos = target.getPosition();
        } else if (a instanceof Recover || a instanceof HealOthers || a instanceof Disband) {
            Unit u = (Unit) gs.getActor(((UnitAction) a).getUnitId());
            pos = u.getPosition();
        } else if (a instanceof Capture || a instanceof Convert || a instanceof Examine) {
            Unit u = (Unit) gs.getActor(((UnitAction) a).getUnitId());
            pos = new Vector2d(u.getPosition().x-1, u.getPosition().y);
        }
        return pos;
    }
}
