package utils;

import core.Constants;
import core.TribesConfig;
import core.Types;
import core.actions.tribeactions.EndTurn;
import core.actions.tribeactions.TribeAction;
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
    private int levelingUp = 0;

    private Game game;
    private GameState gs;

    private ArrayList<HashMap<Integer,ArrayList<Action>>> actionHistory;
    private ArrayList<GameState> stateHistory;
    private ActionController replayer;
    private boolean pauseAfterTurn = false;  // If game should automatically pause after one turn (of one tribe) is played
    private boolean pauseAfterTick = false;  // If game should automatically pause after one tick (all tribes) is played

//    private KeyController ki;
    private WindowInput wi;
    private ActionController ac;
    private Examine lastExamineAction;

    private GameView boardView;
    private TribeView tribeView;
    private TechView techView;
    private InfoView infoView;

    // Zoomed screen dragging vars
    private Vector2d startDrag, endDrag, panTranslate;

    public static double screenDiagonal;
    double scale = 1;

    /**
     * Constructor
     * @param title Title of the window.
     */
    public GUI(Game game, String title, KeyController ki, WindowInput wi, ActionController ac, boolean closeAppOnClosingWindow, TribesConfig tc) {
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
        this.wi = wi;
        this.game = game;

        this.actionHistory = new ArrayList<>();
        this.stateHistory = new ArrayList<>();
        this.replayer = new ActionController();

        infoView = new InfoView(ac);
        panTranslate = new Vector2d(0,0);
        boardView = new GameView(game, infoView, panTranslate, tc);

        // Create frame layout
        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.weighty = 0;

        setLayout(gbl);

        // Main panel definition
        JPanel mainPanel = createGamePanel();
        JPanel sidePanel = createSidePanel();
        JPanel frameworkPanel = createFrameworkPanel();

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

        gbc.gridx++;
        getContentPane().add(frameworkPanel, gbc);

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
                Vector2d translate = boardView.getPanTranslate();
                Vector2d ep = new Vector2d(e.getX() - translate.x, e.getY() - translate.y);
                Vector2d p = GameView.rotatePointReverse((int)ep.x, (int)ep.y);

                // If unit highlighted and action at new click valid for unit, execute action
                if (game.getPlayers()[gs.getActiveTribeID()] instanceof HumanAgent ||
                        !DISABLE_NON_HUMAN_GRID_HIGHLIGHT) {
                    // Only do this if actions should be executed, or it is human agent playing
                    Action candidate = getActionAt(p.x, p.y, infoView.getHighlightX(), infoView.getHighlightY());
                    if (candidate != null) {
                        int n = 0;
                        if (candidate instanceof Disband) {  // These actions needs confirmation before executing
                            n = JOptionPane.showConfirmDialog(mainPanel,
                                    "Confirm action " + candidate.toString(),
                                    "Are you sure?",
                                    JOptionPane.YES_NO_OPTION,
                                    JOptionPane.QUESTION_MESSAGE);
                        }
                        if (candidate instanceof TribeAction) {
                            ((TribeAction) candidate).setTribeId(gs.getActiveTribeID());
                        }
                        if (n == 0) {
                            ac.addAction(candidate, gs);
                        }
                        infoView.resetHighlight();
                    } else {
                        // Otherwise highlight new cell
                        infoView.setHighlight(p.x, p.y);
//                    System.out.println("Highlighting: " + (int)p.getX() + " " + (int)p.getY());
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                startDrag = new Vector2d(e.getX(), e.getY());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                endDrag = new Vector2d(e.getX(), e.getY());
                if (startDrag != null && !(startDrag.equals(endDrag))) {
                    panTranslate = new Vector2d(+ endDrag.x - startDrag.x, + endDrag.y - startDrag.y);
                    if (panTranslate.dist(0, 0) >= GUI_MIN_PAN) {
                        boardView.updatePan(panTranslate);
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
            Vector2d viewCenter = new Vector2d(boardView.getPreferredSize().width/2, boardView.getPreferredSize().height/2);
            Vector2d diff = new Vector2d((int)(mouseLocation.getX() - panelLocation.getX() - viewCenter.x)/GUI_ZOOM_FACTOR,
                    (int)(mouseLocation.getY() - panelLocation.getY() - viewCenter.y)/GUI_ZOOM_FACTOR);
            if (e.getWheelRotation() < 0) {
                // Zooming in
                CELL_SIZE += GUI_ZOOM_FACTOR;
                diff = new Vector2d(diff.x*-1, diff.y*-1);
            } else {
                // Zooming out
                CELL_SIZE -= GUI_ZOOM_FACTOR;
            }
            boardView.updatePan(diff);
        });

        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.SOUTH;
        c.weighty = 0;

        c.gridy = 0;
        mainPanel.add(Box.createRigidArea(new Dimension(0, GUI_COMP_SPACING/2)), c);

        c.gridy++;
        mainPanel.add(boardView, c);

        c.gridy++;
        mainPanel.add(Box.createRigidArea(new Dimension(0, GUI_COMP_SPACING/2)), c);

        return mainPanel;
    }

    /**
     * Creates side panel supplement for game view, containing:
     * - turn info
     * - active tribe info
     * - other info (Examine action / tribe win status),
     * - info on game grid highlights (units, terrain etc.) or research in tech tree highlights
     * - actions available for grid highlight
     * - all tribes points ranking
     * - tech tree view
     * - buttons to "End Turn", "Play Turn" (game paused after tribe's turn), "Play Tick" (game paused after all tribe's
     *      turns in current tick), "Pause/Resume" (to pause/resume game at any point), all keeping GUI responsive
     * - quick select buttons (all units / units of type / cities of a tribe) for info display TODO
     * @return JPanel containing all the sub components
     */
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
        tribeView = new TribeView(game);
        techView = new TechView(game, ac, infoView);
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
        JPanel buttons = new JPanel();
        JButton endTurn = new JButton("End Turn");
        endTurn.addActionListener(e -> ac.addAction(new EndTurn(gs.getActiveTribeID()), gs));
        JButton playTurn = new JButton("Play Turn");
        playTurn.addActionListener(e -> { pauseAfterTurn = true; game.setPaused(false); });
        JButton playTick = new JButton("Play Tick");
        playTick.addActionListener(e -> { pauseAfterTick = true; game.setPaused(false); });
        JButton pause = new JButton("Pause");
        pause.addActionListener(e -> {
            if (game.isPaused()) {
                game.setPaused(false);
                pause.setText("Pause");
            } else {
                game.setPaused(true);
                pause.setText("Resume");
            }
        });

        buttons.add(endTurn);
        buttons.add(playTurn);
        buttons.add(playTick);
        buttons.add(pause);
        sidePanel.add(buttons, c);

        c.gridy++;
        sidePanel.add(Box.createRigidArea(new Dimension(0, GUI_COMP_SPACING/2)), c);

        return sidePanel;
    }

    /**
     * Panel containing functionality for interacting with the framework and getting high-level information, including:
     * - Game setup:
     *      - Choosing which players should play in the next game TODO
     *      - Choosing which tribe should be associated with each player in the next game TODO
     *      - Choosing and editing map for next game (text view) TODO
     *      - Choosing game seed for next game TODO
     *      - Start/Restart/End game buttons TODO
     * - Debugging:
     *      - observability toggle TODO
     *      - visuals on/off TODO
     *      - results printout for all games in the same run (if multiple) TODO
     *      - save game toggle TODO
     *      - add comments to saved game files TODO
     *      - action history display TODO
     *      - change game configuration? TODO
     * @return
     */
    private JPanel createFrameworkPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.SOUTH;
        c.weighty = 0;
        return panel;
    }


    /**
     * Paints the GUI, to be called at every game tick.
     */
    public void update(GameState gs, Action a) {
//        if (this.gs == null || this.gs.getTick() != gs.getTick()) {
//            // Tick change
//            HashMap<Integer, ArrayList<Action>> tick = new HashMap<>();
//            for (int i = 0; i < game.getPlayers().length; i++) {
//                tick.put(i, new ArrayList<>());
//            }
//            actionHistory.add(tick);
//        }
        if (this.gs == null || this.gs.getActiveTribeID() != gs.getActiveTribeID()) {
            // Tribe change
            infoView.resetHighlight();  // Reset highlights
            if(Constants.GUI_PAN_TO_TRIBE)
                boardView.setPanToTribe(gs);  // Pan camera to tribe capital
            ac.reset();  // Clear action queue
            otherInfo.setText("");  // Reset info
        }

        // Display result of Examine action
        if (a instanceof Examine) {
            lastExamineAction = (Examine) a;
        }
        // Draw animations for these actions
        if (a instanceof Attack || a instanceof Convert || a instanceof HealOthers) {
            boardView.paintAction((UnitAction)a);
        }

//        if (this.gs != null) {
//            if (a != null) {
//                actionHistory.get(this.gs.getTick()).get(this.gs.getActiveTribeID()).add(a);
//            }
//            stateHistory.add(this.gs);
//        }
        this.gs = gs;
        if (gs.isLevelingUp()) this.levelingUp++;
        else this.levelingUp = 0;
        performUpdate();

        // Check if city is levelling up, pop up dialogue to choose options if human agent
        if (this.levelingUp == 1 && game.getPlayers()[gs.getActiveTribeID()] instanceof HumanAgent) {
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
        boardView.paint(gs);
        tribeView.paint(gs);
        techView.paint(gs);
        infoView.paint(gs);
        appTurn.setText("Turn: " + gs.getTick() + (game.isPaused()? " [PAUSED]" : ""));
        if (gs.getActiveTribe() != null) {
            activeTribe.setText("Tribe acting: " + gs.getActiveTribe().getName());
            activeTribeInfo.setText("stars: " + gs.getActiveTribe().getStars() + " (+" + gs.getActiveTribe().getMaxProduction(gs) + ")");
            Types.RESULT winStatus = gs.getTribeWinStatus(gs.getActiveTribeID());
            if (winStatus != Types.RESULT.INCOMPLETE) {
                otherInfo.setText("Game result: " + winStatus);
            } else if (lastExamineAction != null && lastExamineAction.getBonus() != null) {
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
        } else if (a instanceof Recover) {
            Unit u = (Unit) gs.getActor(((UnitAction) a).getUnitId());
            pos = u.getPosition();
        } else if (a instanceof Capture || a instanceof Examine) {
            Unit u = (Unit) gs.getActor(((UnitAction) a).getUnitId());
            pos = new Vector2d(u.getPosition().x-1, u.getPosition().y);
        } else if (a instanceof Convert) {
            Unit target = (Unit) gs.getActor(((Convert) a).getTargetId());
            pos = target.getPosition();
        }
        return pos;
    }

    public boolean isClosed() {
        return wi.windowClosed;
    }

    public boolean pauseAfterTurn() {
        return pauseAfterTurn;
    }

    public void setPauseAfterTurn(boolean p) {
        pauseAfterTurn = p;
    }

    public boolean pauseAfterTick() {
        return pauseAfterTick;
    }

    public void setPauseAfterTick(boolean p) {
        pauseAfterTick = p;
    }

    public Action getAnimatedAction() {
        return boardView.getAnimatedAction();
    }
}
