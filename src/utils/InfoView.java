package utils;

import core.TechnologyTree;
import core.Types;
import core.actions.cityactions.*;
import core.actions.tribeactions.BuildRoad;
import core.actions.tribeactions.ResearchTech;
import core.actions.unitactions.Disband;
import core.actions.unitactions.HealOthers;
import core.actions.unitactions.Upgrade;
import core.actors.City;
import core.actors.Tribe;
import core.actors.units.Battleship;
import core.actors.units.Boat;
import core.actors.units.Ship;
import core.actors.units.Unit;
import core.game.Board;
import core.game.GameState;
import core.actions.Action;
import players.ActionController;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import static core.Constants.*;
import static core.TribesConfig.VETERAN_KILLS;
import static utils.GameView.gridSize;

@SuppressWarnings({"StringConcatenationInsideStringBufferAppend", "SuspiciousNameCombination"})
public class InfoView extends JComponent {

    // Dimensions of the window.
    private Dimension size;
    private JEditorPane textArea;

    private JButton actionBF, actionCF, actionD, actionGF, actionRG;
    private JButton[] actionB, actionS;
    private JButton actionRoad;
    private JButton actionResearch;
    private JButton actionHealOthers, actionDisband, actionUpgrade;
    private TribesActionListener listenerBF, listenerCF, listenerD, listenerGF, listenerRG;
    private TribesActionListener listenerS, listenerB;
    private TribesActionListener listenerResearch;
    private TribesActionListener listenerRoad;
    private TribesActionListener listenerHealOthers, listenerDisband, listenerUpgrade;
    private ActionController ac;

    private int highlightX, highlightY;
    private int highlightXprev, highlightYprev;
    private boolean updateHighlight, updateTechHighlight;
    Types.TECHNOLOGY techHighlight;

    private GameState gs;

    InfoView(ActionController ac)
    {
        this.size = new Dimension(GUI_SIDE_PANEL_WIDTH, GUI_INFO_PANEL_HEIGHT);
        this.ac = ac;
        int scrollBarSize = (Integer) UIManager.get("ScrollBar.width");

        highlightX = -1;
        highlightY = -1;
        highlightXprev = -1;
        highlightYprev = -1;

        textArea = new JEditorPane("text/html", "");
        textArea.setPreferredSize(new Dimension(GUI_SIDE_PANEL_WIDTH, GUI_ACTION_PANEL_FULL_SIZE));
        Font textFont = new Font(textArea.getFont().getName(), Font.PLAIN, 12);
        textArea.setFont(textFont);
        textArea.setEditable(false);
        textArea.setBackground(Color.lightGray);
        DefaultCaret caret = (DefaultCaret)textArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);

        JPanel actionPanel = new JPanel();
        actionPanel.setPreferredSize(new Dimension(GUI_SIDE_PANEL_WIDTH - scrollBarSize*2, GUI_ACTION_PANEL_FULL_SIZE));

        // Simple actions: BurnForest, ClearForest, Destroy, GrowForest, GatherResource
        actionBF = new JButton("Burn");  // If forest
        listenerBF = new TribesActionListener("BurnForest");
        actionBF.addActionListener(listenerBF);
        actionBF.setVisible(false);
        actionCF = new JButton("Clear");  // If forest
        listenerCF = new TribesActionListener("ClearForest");
        actionCF.addActionListener(listenerCF);
        actionCF.setVisible(false);
        actionD = new JButton("Destroy");  // If building
        listenerD = new TribesActionListener("Destroy");
        actionD.addActionListener(listenerD);
        actionD.setVisible(false);
        actionGF = new JButton("Grow");  // If plain
        listenerGF = new TribesActionListener("GrowForest");
        actionGF.addActionListener(listenerGF);
        actionGF.setVisible(false);
        actionRG = new JButton("Gather");  // If resource
        listenerRG = new TribesActionListener("ResourceGathering");
        actionRG.addActionListener(listenerRG);
        actionRG.setVisible(false);
        actionPanel.add(actionRG);
        actionPanel.add(actionBF);
        actionPanel.add(actionCF);
        actionPanel.add(actionD);
        actionPanel.add(actionGF);

        // Complex actions: Build X, Spawn X
        int nBuildings = Types.BUILDING.values().length;
        actionB = new JButton[nBuildings];
        listenerB = new TribesActionListener("Build");
        for (int i = 0; i < nBuildings; i++) {
            actionB[i] = new JButton("Build " + Types.BUILDING.values()[i]);
            actionB[i].addActionListener(listenerB);
            actionB[i].setVisible(false);
            actionPanel.add(actionB[i]);
        }
        ArrayList<Types.UNIT> spawnableUnits = Types.UNIT.getSpawnableTypes();
        int nUnits = spawnableUnits.size();
        actionS = new JButton[nUnits];
        listenerS = new TribesActionListener("Spawn");
        for (int i = 0; i < nUnits; i++) {
            actionS[i] = new JButton("Spawn " + spawnableUnits.get(i));
            actionS[i].addActionListener(listenerS);
            actionS[i].setVisible(false);
            actionPanel.add(actionS[i]);
        }

        // Research action
        actionResearch = new JButton("Research");
        actionResearch.setVisible(false);
        listenerResearch = new TribesActionListener("Research");
        actionResearch.addActionListener(listenerResearch);
        actionPanel.add(actionResearch);

        // Build road action
        actionRoad = new JButton("Build Road");
        actionRoad.setVisible(false);
        listenerRoad = new TribesActionListener("BuildRoad");
        actionRoad.addActionListener(listenerRoad);
        actionPanel.add(actionRoad);

        // Unit actions
        actionHealOthers = new JButton("Heal Others");
        actionHealOthers.setVisible(false);
        listenerHealOthers = new TribesActionListener("HealOthers");
        actionHealOthers.addActionListener(listenerHealOthers);
        actionPanel.add(actionHealOthers);
        actionDisband = new JButton("Disband");
        actionDisband.setVisible(false);
        listenerDisband = new TribesActionListener("Disband");
        actionDisband.addActionListener(listenerDisband);
        actionPanel.add(actionDisband);
        actionUpgrade = new JButton("Upgrade");
        actionUpgrade.setVisible(false);
        listenerUpgrade = new TribesActionListener("Upgrade");
        actionUpgrade.addActionListener(listenerUpgrade);
        actionPanel.add(actionUpgrade);

        this.setLayout(new FlowLayout());
        JScrollPane scrollPane1 = new JScrollPane(textArea);
        JScrollPane scrollPane2 = new JScrollPane(actionPanel);
        scrollPane1.setPreferredSize(new Dimension(GUI_SIDE_PANEL_WIDTH, GUI_INFO_PANEL_HEIGHT - GUI_ACTION_PANEL_HEIGHT));
        scrollPane2.setPreferredSize(new Dimension(GUI_SIDE_PANEL_WIDTH, GUI_ACTION_PANEL_HEIGHT));
        this.add(scrollPane1);
        this.add(scrollPane2);
    }


    public void paintComponent(Graphics gx)
    {
        Graphics2D g = (Graphics2D) gx;
        paintWithGraphics(g);
    }

    private void paintWithGraphics(Graphics2D g)
    {
        if (gs == null) return;

        //For a better graphics, enable this: (be aware this could bring performance issues depending on your HW & OS).
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Board board = gs.getBoard();

        if (highlightInGridBounds()) {

            Types.TERRAIN t = board.getTerrainAt(highlightY, highlightX);
            Types.RESOURCE r = board.getResourceAt(highlightY, highlightX);
            Types.BUILDING b = board.getBuildingAt(highlightY, highlightX);
            Unit u = board.getUnitAt(highlightY, highlightX);

            // t < r < b < u

            String s;

            if (u != null && !clickedTwice()) {
                // Unit is always on top, show this, unless clicked twice
                s = getUnitInfo(u);
            } else {
                s = "<h1>";
                if (t != null) {
                    if (t == Types.TERRAIN.CITY) { // It's a city, show just this
                        s = getCityInfo();
                    } else {
                        // Show everything else
                        s += t.toString();
                        if (r != null) {
                            // Resource next
                            s += ", " + r.toString();
                        }
                        if (b != null) {
                            // Buildings
                            s += ", " + b.toString();
                        }
                    }
                } else {
                    // Show buildings and resources
                    if (r != null) {
                        // Resource next
                        s += r.toString();
                        if (b != null) {
                            // Buildings
                            s += ", " + b.toString();
                        }
                    } else if (b != null) {
                        // Buildings
                        s += b.toString();
                    }
                }
                s += "</h1>";
            }

            if (!textArea.getText().equals(s) && updateHighlight) {
                textArea.setText(s);
                techHighlight = null;
                updateHighlight = false;
                updateButtons();
            }
        }
        updateTechButton();
    }

    private String getUnitInfo(Unit u) {
//        String img = u.getType().getImageStr(u.getTribeId());

        Tribe t = gs.getTribe(u.getTribeId());
        int idx = t.getType().getKey();

        StringBuilder sb = new StringBuilder();
        sb.append("<h1>" + Types.TRIBE.values()[idx] + " " + u.getType() + "</h1>");
//        sb.append("<table border=\"0\"><tr><td><img src=\"file:" + img + "\"/></p></td><td>");
        sb.append("From city " + u.getCityId() + "<br/>");
        if (u.isVeteran()) {
            sb.append("<b>Veteran unit.</b>");
        } else {
            int kills = Math.min(u.getKills(), VETERAN_KILLS);
            sb.append("" + kills + "/" + VETERAN_KILLS + " kills to become a veteran.");
        }
//        sb.append("</td></tr></table>");
        sb.append("<ul>");
        sb.append("<li><b>Health:</b> " + u.getCurrentHP() + "/" + u.getMaxHP() + "</li>");
        sb.append("<li><b>Attack:</b> " + u.ATK + "</li>");
        sb.append("<li><b>Defence:</b> " + u.DEF + "</li>");
        sb.append("<li><b>Movement:</b> " + u.MOV + "</li>");
        sb.append("<li><b>Range:</b> " + u.RANGE + "</li>");
        sb.append("<li><b>Status:</b> " + u.getStatus() + "</li>");

        if(u.getType() == Types.UNIT.BOAT)
            sb.append("<li><b>Land unit:</b> " + ((Boat)u).getBaseLandUnit() + "</li>");
        else if(u.getType() == Types.UNIT.SHIP)
            sb.append("<li><b>Land unit:</b> " + ((Ship)u).getBaseLandUnit() + "</li>");
        else if(u.getType() == Types.UNIT.BATTLESHIP)
            sb.append("<li><b>Land unit:</b> " + ((Battleship)u).getBaseLandUnit() + "</li>");

        sb.append("</ul>");
        return sb.toString();
    }

    private String getCityInfo() {
        Board board = gs.getBoard();
        int cityID = board.getCityIdAt(highlightY, highlightX);
        City c = (City) board.getActor(cityID);

        StringBuilder sb = new StringBuilder();
        if(c != null) {
            sb.append("<h1>" + Types.TRIBE.values()[c.getTribeId()] + " city " + cityID + "</h1>");
//            sb.append("<table border=\"0\"><tr><td><img width=\"" + CELL_SIZE + "\" src=\"file:" + Types.TERRAIN.CITY.getImageStr() + "\"/></p></td><td>");
            sb.append("<ul>");
            sb.append("<li><b>Is Capital:</b> " + c.isCapital() + "</li>");
            sb.append("<li><b>Points:</b> " + c.getPointsWorth() + "</li>");
            sb.append("<li><b>Production:</b> " + c.getProduction() + "</li>");
            sb.append("</ul>");
//            sb.append("</td></tr></table>");
        }
        return sb.toString();
    }

    private void updateButtons() {
        Board board = gs.getBoard();
        int cityID = board.getCityIdAt(highlightY, highlightX);
        Types.RESOURCE r = board.getResourceAt(highlightY, highlightX);
        Vector2d position = new Vector2d(highlightY, highlightX);
        Unit u = board.getUnitAt(highlightY, highlightX);
        resetButtonVisibility();

        if (board.getTribe(board.getActiveTribeID()).getTechTree().isResearched(Types.TECHNOLOGY.ROADS)) {
            ArrayList<Action> acts = gs.getTribeActions();
            for (Action a: acts) {
                if (a instanceof BuildRoad && ((BuildRoad) a).getPosition().equals(position)) {
                    actionRoad.setVisible(true);
                    listenerRoad.update(board.getActiveTribeID(), position, ac, gs);
                }
            }
        }

        if (cityID != -1) {
            City c = (City) gs.getBoard().getActor(cityID);
            if (c != null) {
                ArrayList<Action> acts = gs.getCityActions(c);

                boolean foundRG = false;
                boolean foundBF = false;
                boolean foundCF = false;
                boolean foundGF = false;
                boolean foundD = false;
                boolean[] foundS = new boolean[actionS.length];
                boolean[] foundB = new boolean[actionB.length];
                if (acts != null && acts.size() > 0) {
                    for (Action a : acts) {
                        if (a != null && ((CityAction) a).getTargetPos() != null &&
                                ((CityAction) a).getTargetPos().equals(position)) {
                            if (a instanceof ResourceGathering) {
                                if (((ResourceGathering) a).getResource().equals(r)) {
                                    listenerRG.update(cityID, position, ac, gs);
                                    listenerRG.setResource(r);
                                    foundRG = true;
                                }
                            } else if (a instanceof BurnForest) {
                                listenerBF.update(cityID, position, ac, gs);
                                foundBF = true;
                            } else if (a instanceof ClearForest) {
                                listenerCF.update(cityID, position, ac, gs);
                                foundCF = true;
                            } else if (a instanceof GrowForest) {
                                listenerGF.update(cityID, position, ac, gs);
                                foundGF = true;
                            } else if (a instanceof Destroy) {
                                listenerD.update(cityID, position, ac, gs);
                                foundD = true;
                                break;
                            } else if (a instanceof Spawn) {
                                Types.UNIT unitType = ((Spawn) a).getUnitType();
                                int idx = Types.UNIT.getSpawnableTypes().indexOf(unitType);
                                listenerS.update(cityID, position, ac, gs);
                                foundS[idx] = true;
                            } else if (a instanceof Build) {
                                Types.BUILDING buildingType = ((Build) a).getBuildingType();
                                int idx = buildingType.getKey();
                                listenerB.update(cityID, position, ac, gs);
                                foundB[idx] = true;
                            }
                        }
                    }
                }
                actionRG.setVisible(foundRG);
                actionBF.setVisible(foundBF);
                actionCF.setVisible(foundCF);
                actionGF.setVisible(foundGF);
                actionD.setVisible(foundD);
                for (int i = 0; i < actionS.length; i++) {
                    actionS[i].setVisible(foundS[i]);
                }
                for (int i = 0; i < actionB.length; i++) {
                    actionB[i].setVisible(foundB[i]);
                }
            }
        }

        if (u != null) {
            ArrayList<Action> unitActions = gs.getUnitActions(u);
            if (unitActions != null && unitActions.size() > 0) {
                boolean foundHO = false;
                boolean foundD = false;
                boolean foundU = false;
                for (Action a : unitActions) {
                    if (a instanceof HealOthers) {
                        foundHO = true;
                        listenerHealOthers.update(u.getActorId(), ac, gs);
                    } else if (a instanceof Disband) {
                        foundD = true;
                        listenerDisband.update(u.getActorId(), ac, gs);
                    } else if (a instanceof Upgrade) {
                        foundU = true;
                        listenerUpgrade.update(u.getActorId(), ac, gs);
                    }
                }
                actionHealOthers.setVisible(foundHO);
                actionDisband.setVisible(foundD);
                actionUpgrade.setVisible(foundU);
            }
        }
    }

    private void updateTechButton() {
        if (techHighlight != null && updateTechHighlight) {
            resetButtonVisibility();
            updateHighlight = false;
            actionResearch.setVisible(true);
            listenerResearch.update(techHighlight, ac, gs);

            Tribe t = gs.getActiveTribe();
            if (t != null) {
                TechnologyTree tt = t.getTechTree();
                if (tt != null) {
                    boolean researched = tt.isResearched(techHighlight);
                    boolean techRequirement = tt.isResearchable(techHighlight);
                    int starCost = techHighlight.getCost(t.getNumCities(), tt);
                    boolean starRequirement = t.getStars() >= starCost;
                    boolean researchable = techRequirement && starRequirement;

                    String txt = "";
                    if (!(researchable || researched)) {
                        actionResearch.setEnabled(false);
                        actionResearch.setText("Unavailable");
                        if (!techRequirement) txt += "Requires " + techHighlight.getParentTech().toString();
                        if (!starRequirement) {
                            if (!techRequirement) txt += "<br/>";
                            txt += "Not enough stars, " + t.getStars() + " of " + starCost + " required";
                        }
                        actionResearch.setToolTipText("<html>" + txt + "</html>");
                    } else {
                        if (researched) {
                            actionResearch.setEnabled(false);
                            txt = "Researched";
                            actionResearch.setText("Researched");
                        } else {
                            actionResearch.setEnabled(true);
                            txt = "Researchable";
                            actionResearch.setText("Research");
                        }
                    }

                    String fullTxt = textArea.getText().replaceAll("</*html>|</*head>|</*body>|\n|\r", "") + "<hr>" + txt;
                    textArea.setText(fullTxt);
                }
            }
            updateTechHighlight = false;
        }
    }

    private void resetButtonVisibility(){
        actionBF.setVisible(false);
        actionCF.setVisible(false);
        actionD.setVisible(false);
        actionGF.setVisible(false);
        actionRG.setVisible(false);
        actionRoad.setVisible(false);
        actionResearch.setVisible(false);
        actionUpgrade.setVisible(false);
        actionHealOthers.setVisible(false);
        actionDisband.setVisible(false);
        for (JButton jb: actionB) {
            jb.setVisible(false);
        }
        for (JButton jb: actionS) {
            jb.setVisible(false);
        }
    }

    void paint(GameState gs)
    {
        this.gs = gs;
    }

    /**
     * Gets the dimensions of the window.
     * @return the dimensions of the window.
     */
    public Dimension getPreferredSize() {
        return size;
    }

    public void setHighlight(int x, int y)
    {
        highlightXprev = highlightX;
        highlightYprev = highlightY;
        highlightX = x;
        highlightY = y;
        updateHighlight = true;
    }

    public void resetHighlight() {
        highlightX = -1;
        highlightY = -1;
        highlightXprev = -1;
        highlightYprev = -1;
        techHighlight = null;

        updateHighlight = false;
        updateTechHighlight = false;

        // Reset highlight info text
        textArea.setText("");

        // Reset actions, none available yet for this player
        resetButtonVisibility();

        repaint();
    }

    public int getHighlightX() {return highlightX;}
    public int getHighlightY() {return highlightY;}
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean clickedTwice() {
        return highlightX != -1 && highlightX == highlightXprev && highlightY == highlightYprev;
    }
    public boolean highlightInGridBounds() {
        return highlightX > -1 && highlightY > -1 && highlightX < gridSize && highlightY < gridSize;
    }

    public void setTechHighlightText(String s) {
        if (!textArea.getText().equals(s)) {
            textArea.setText(s);
            updateTechHighlight = true;
        }
    }

    public void setTechHighlight(Types.TECHNOLOGY t) {
        techHighlight = t;
    }

    class TribesActionListener implements ActionListener {
        int cityID;
        Vector2d position;
        ActionController ac;
        GameState gs;
        String actionType;
        Types.RESOURCE resource;
        Types.TECHNOLOGY tech;
        int unitID;

        TribesActionListener(String type) {
            this.actionType = type;
        }

        public void update(int cityID, Vector2d position, ActionController ac, GameState gs) {
            this.cityID = cityID;
            this.position = position;
            this.ac = ac;
            this.gs = gs;
        }

        public void update(Types.TECHNOLOGY t, ActionController ac, GameState gs) {
            this.tech = t;
            this.ac = ac;
            this.gs = gs;
        }

        public void update(int unitID, ActionController ac, GameState gs) {
            this.unitID = unitID;
            this.ac = ac;
            this.gs = gs;
        }

        public void setResource(Types.RESOURCE resource) {
            this.resource = resource;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Action a = null;
            switch (actionType) {
                case "BuildRoad":
                    a = new BuildRoad(cityID);
                    ((BuildRoad) a).setPosition(position);
                    break;
                case "BurnForest":
                    a = new BurnForest(cityID);
                    ((BurnForest) a).setTargetPos(position);
                    break;
                case "ClearForest":
                    a = new ClearForest(cityID);
                    ((ClearForest) a).setTargetPos(position);
                    break;
                case "Destroy":
                    a = new Destroy(cityID);
                    ((Destroy) a).setTargetPos(position);
                    break;
                case "GrowForest":
                    a = new GrowForest(cityID);
                    ((GrowForest) a).setTargetPos(position);
                    break;
                case "ResourceGathering":
                    a = new ResourceGathering(cityID);
                    ((ResourceGathering) a).setTargetPos(position);
                    ((ResourceGathering) a).setResource(resource);
                    break;
                case "Spawn":
                    if( e.getSource() instanceof JButton) {
                        String type = ((JButton)e.getSource()).getText().split(" ")[1];
                        Types.UNIT uType = Types.UNIT.stringToType(type);
                        a = new Spawn(cityID);
                        ((Spawn) a).setTargetPos(position);
                        ((Spawn) a).setUnitType(uType);
                    }
                    break;
                case "Build":
                    if( e.getSource() instanceof JButton) {
                        String type = ((JButton)e.getSource()).getText().split(" ")[1];
                        Types.BUILDING bType = Types.BUILDING.stringToType(type);
                        a = new Build(cityID);
                        ((Build) a).setTargetPos(position);
                        ((Build) a).setBuildingType(bType);
                    }
                    break;
                case "Research":
                    if (e.getSource() instanceof JButton) {
                        a = new ResearchTech(gs.getActiveTribeID());
                        ((ResearchTech)a).setTech(tech);
                    }
                    break;
                case "HealOthers":
                    if (e.getSource() instanceof JButton) {
                        a = new HealOthers(unitID);
                    }
                    break;
                case "Disband":
                    if (e.getSource() instanceof JButton) {
                        a = new Disband(unitID);
                    }
                    break;
                case "Upgrade":
                    if (e.getSource() instanceof JButton) {
                        Unit u = (Unit) gs.getActor(unitID);
                        Types.ACTION actionType = null;
                        if(u.getType() == Types.UNIT.BOAT) actionType = Types.ACTION.UPGRADE_BOAT;
                        if(u.getType() == Types.UNIT.SHIP) actionType = Types.ACTION.UPGRADE_SHIP;
                        a = new Upgrade(actionType, unitID);
                    }
                    break;
            }
            if (a != null) {
                ac.addAction(a, gs);
                resetHighlight();
            }
        }
    }
}
