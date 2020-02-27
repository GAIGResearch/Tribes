package utils;

import core.Types;
import core.actors.City;
import core.actors.units.Unit;
import core.game.Board;
import core.game.GameState;

import javax.swing.*;
import java.awt.*;

import static core.Constants.CELL_SIZE;


public class InfoView extends JComponent {

    // Dimensions of the window.
    private Dimension size;
    private JEditorPane textArea;

    private int highlightX0, highlightY0;
    private int highlightX1, highlightY1;
    private int highlightX2, highlightY2;
    private JTextArea terrainTextArea, cityTextArea, unitTextArea, tribeTextArea;

    private GameView gameView;

    private Board board;

    InfoView()
    {
        this.size = new Dimension(400, 200);

        highlightX0 = -1;
        highlightY0 = -1;
        highlightX1 = -1;
        highlightY1 = -1;
        highlightX2 = -1;
        highlightY2 = -1;

        textArea = new JEditorPane("text/html", "");
        textArea.setPreferredSize(this.size);
        Font textFont = new Font(textArea.getFont().getName(), Font.PLAIN, 12);
        textArea.setFont(textFont);
        textArea.setEditable(false);
        textArea.setBackground(Color.lightGray);

        this.setLayout(new FlowLayout());
        JScrollPane scrollPane = new JScrollPane(textArea);
        this.add(scrollPane);
    }


    public void paintComponent(Graphics gx)
    {
        Graphics2D g = (Graphics2D) gx;
        paintWithGraphics(g);
    }

    private void paintWithGraphics(Graphics2D g)
    {
        //For a better graphics, enable this: (be aware this could bring performance issues depending on your HW & OS).
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (highlightX0 != -1) {

            Types.TERRAIN t = board.getTerrainAt(highlightY0, highlightX0);
            Types.RESOURCE r = board.getResourceAt(highlightY0, highlightX0);
            Types.BUILDING b = board.getBuildingAt(highlightY0, highlightX0);
            Unit u = board.getUnitAt(highlightY0, highlightX0);

            // t < r < u
            // r < b < u
            // t < b < u

            String s = "";
            if (highlightX1 == highlightX0 && highlightY1 == highlightY0) {
                if (highlightX2 == highlightX1 && highlightY2 == highlightX2) { // Clicked thrice in same spot, show third layer
                    if (u != null && b != null && r != null) {
                        // Show resource if it's third layer
                        s = "<h1>Resource: " + r.toString() + "</h1>";
                    } else { // Otherwise just show terrain
                        if (t != null) {
                            if (t == Types.TERRAIN.CITY) { // It's a city
                                s = getCityInfo();
                            } else {
                                s = "<h1>Terrain: " + t.toString() + "</h1>";
                            }
                        }
                    }
                } else { // Clicked twice in same spot, show second layer
                    if (u != null && b != null) {
                        // Show building if second
                        s = "<h1>Building: " + b.toString() + "</h1>";
                    } else {
                        if (u != null && r != null) {
                            // Resource if second
                            s = "<h1>Resource: " + r.toString() + "</h1>";
                        } else {
                            if (t != null) { // Lastly just show terrain
                                if (t == Types.TERRAIN.CITY) { // It's a city
                                    s = getCityInfo();
                                } else {
                                    s = "<h1>Terrain: " + t.toString() + "</h1>";
                                }
                            }
                        }
                    }
                }
            } else { // First time clicking, show first layer
                if (u != null) {
                    // Unit is always on top, show this
                    s = getUnitInfo(u);
                } else {
                    if (b != null) {
                        // Building before resource
                        s = "<h1>Building: " + b.toString() + "</h1>";
                    } else {
                        if (r != null) {
                            // Resource next
                            s = "<h1>Resource: " + r.toString() + "</h1>";
                        } else {
                            if (t != null) { // Lastly just show terrain
                                if (t == Types.TERRAIN.CITY) { // It's a city
                                    s = getCityInfo();
                                } else {
                                    s = "<h1>Terrain: " + t.toString() + "</h1>";
                                }
                            }
                        }
                    }
                }
            }

            textArea.setText(s);
//            String[] splitLine = s.split("\n");
//            for (int i = 0; i < splitLine.length; i++) {
//                g.drawString(splitLine[i], 10, 10 + i * fontSize);
//            }
        }
    }

    private String getUnitInfo(Unit u) {
        String img = u.getType().getImageStr(u.getTribeId());

        StringBuilder sb = new StringBuilder();
        sb.append("<h1>" + Types.TRIBE.values()[u.getTribeId()] + " " + u.getType() + "</h1>");
        sb.append("<table border=\"0\"><tr><td><img src=\"file:" + img + "\"/></p></td><td>");
        sb.append("From city " + u.getCityID() + "<br/>");
        if (u.isVeteran()) {
            sb.append("<b>Veteran unit.</b>");
        } else {
            int kills = Math.min(u.getKills(), 3);
            sb.append("" + kills + "/3 kills to become a veteran.");
        }
        sb.append("</td></tr></table>");
        sb.append("<ul>");
        sb.append("<li><b>Health:</b> " + u.getCurrentHP() + "/" + u.getMaxHP() + "</li>");
        sb.append("<li><b>Attack:</b> " + u.ATK + "</li>");
        sb.append("<li><b>Defence:</b> " + u.DEF + "</li>");
        sb.append("<li><b>Movement:</b> " + u.MOV + "</li>");
        sb.append("<li><b>Range:</b> " + u.RANGE + "</li>");
        sb.append("</ul>");
        return sb.toString();
    }

    private String getCityInfo() {
        int cityID = board.getCityIdAt(highlightY0, highlightX0);
        City c = (City) board.getActor(cityID);

        StringBuilder sb = new StringBuilder();
        if(c != null) {
            sb.append("<h1>" + Types.TRIBE.values()[c.getTribeId()] + " city " + cityID + "</h1>");
            sb.append("<table border=\"0\"><tr><td><img width=\"" + CELL_SIZE + "\" src=\"file:" + Types.TERRAIN.CITY.getImageStr() + "\"/></p></td><td>");
            sb.append("<ul>");
            sb.append("<li><b>Is Capital:</b> " + c.isCapital() + "</li>");
            sb.append("<li><b>Points:</b> " + c.getPoints() + "</li>");
            sb.append("<li><b>Production:</b> " + c.getProduction() + "</li>");
            sb.append("</ul>");
            sb.append("</td></tr></table>");
        }
        return sb.toString();
    }

    void paint(GameState gs)
    {
        this.board = gs.getBoard();
        this.repaint();
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
        highlightX2 = highlightX1;
        highlightY2 = highlightY1;

        highlightX1 = highlightX0;
        highlightY1 = highlightY0;

        highlightX0 = x;
        highlightY0 = y;
    }

    public void resetHighlight() {
        highlightX2 = -1;
        highlightY2 = -1;

        highlightX1 = -1;
        highlightY1 = -1;

        highlightX0 = -1;
        highlightY0 = -1;
    }

    public int getHighlightX() {return highlightX0;}
    public int getHighlightY() {return highlightY0;}

    public void setGameView(GameView gameView) {
        this.gameView = gameView;
    }

}
