package utils;

import core.Types;
import core.actors.City;
import core.actors.units.Unit;
import core.game.Board;

import javax.swing.*;
import java.awt.*;


public class InfoView extends JComponent {

    /**
     * Dimensions of the window.
     */
    private Dimension size;

    private JTextArea terrainTextArea, cityTextArea, unitTextArea, tribeTextArea;

    private int highlightX, highlightY;

    private Board board;



    InfoView()
    {
        this.size = new Dimension(100, 200);
        highlightX = -1;
        highlightY = -1;


        // Create frame layout
        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.weighty = 0;

        setLayout(gbl);

        terrainTextArea = new JTextArea();
        initTextArea(terrainTextArea);

        unitTextArea = new JTextArea();
        initTextArea(unitTextArea);

        tribeTextArea = new JTextArea();
        initTextArea(tribeTextArea);

        cityTextArea = new JTextArea();
        initTextArea(cityTextArea);


        gbc.gridy = 0;
        this.add(Box.createRigidArea(new Dimension(10, 0)), gbc);

        gbc.gridy++;
        this.add(terrainTextArea, gbc);

        gbc.gridy++;
        this.add(Box.createRigidArea(new Dimension(10, 0)), gbc);

        gbc.gridy++;
        this.add(unitTextArea, gbc);

        gbc.gridy++;
        this.add(Box.createRigidArea(new Dimension(10, 0)), gbc);

        gbc.gridy++;
        this.add(cityTextArea, gbc);

        gbc.gridy++;
        this.add(Box.createRigidArea(new Dimension(10, 0)), gbc);

        gbc.gridy++;
        this.add(tribeTextArea, gbc);

        gbc.gridy++;
        this.add(Box.createRigidArea(new Dimension(10, 0)), gbc);

    }

    public void initTextArea(JTextArea tArea)
    {
        Font textFont = new Font(tArea.getFont().getName(), Font.PLAIN, 12);
        tArea.setFont(textFont);
        tArea.setEditable(false);
        tArea.setLineWrap(true);
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

        if(highlightX != -1)
        {
            Types.TERRAIN t = board.getTerrainAt(highlightY, highlightX);
            Types.RESOURCE r = board.getResourceAt(highlightY, highlightX);
            Types.BUILDING b = board.getBuildingAt(highlightY, highlightX);
            Unit u = board.getUnitAt(highlightY, highlightX);

            StringBuilder sb = new StringBuilder();
            sb.append(t != null ? t + "\n" : "");
            sb.append(r != null ? r + "\n" : "");
            terrainTextArea.setText(sb.toString());

            if(t == Types.TERRAIN.CITY)
            {
                //Retrieve the city
                int cityID = board.getTileCityId(highlightY, highlightX);
                City c = (City) board.getActor(cityID);
                sb = new StringBuilder();
                if(c != null) {
                    sb.append("City\n");
                    sb.append(" Tribe: " + c.getTribeId() + "\n");
                    sb.append(" Level: " + c.getLevel() + "\n");
                    sb.append(" Population: " + c.getPopulation() + "\n");
                    sb.append(" Num units: " + c.getUnitsID().size() + "\n");
                }
                cityTextArea.setText(sb.toString());
            }else cityTextArea.setText("");

            //sb.append(b != null ? b + "\n" : "");

            sb = new StringBuilder();
            if(u != null)
            {
                sb.append("Unit: " + u.getType() + "\n");
                sb.append(" Tribe: " + u.getTribeID() + "\n");
                sb.append(" Health: " + u.getCurrentHP() + "/" + u.getMaxHP() + "\n");
                if(u.isVeteran())
                    sb.append(" Veteran unit" + "\n");
                else
                {
                    int kills = Math.max(u.getKills(), 3);
                    sb.append(" " + kills + "/3 kills" + "\n");
                }
            }

            unitTextArea.setText(sb.toString());

        }


    }


    void paint(Board b)
    {
        this.repaint();
        this.board = b;
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
        highlightX = x;
        highlightY = y;
    }

    public int getHighlightX() {return highlightX;}
    public int getHighlightY() {return highlightY;}

}
