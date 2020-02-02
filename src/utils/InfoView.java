package utils;

import core.Types;
import core.game.Board;

import javax.swing.*;
import java.awt.*;


public class InfoView extends JComponent {

    /**
     * Dimensions of the window.
     */
    private Dimension size;

    private JTextArea textArea;

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

        textArea = new JTextArea();
        Font textFont = new Font(textArea.getFont().getName(), Font.PLAIN, 16);
        textArea.setFont(textFont);
        textArea.setEditable(false);

        gbc.gridy = 0;
        this.add(Box.createRigidArea(new Dimension(10, 0)), gbc);

        gbc.gridy++;
        this.add(textArea, gbc);

        gbc.gridy++;
        this.add(Box.createRigidArea(new Dimension(10, 0)), gbc);
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
            Types.UNIT u = board.getUnitAt(highlightY, highlightX);

            StringBuilder sb = new StringBuilder();
            sb.append(t != null ? t + "\n" : "");
            sb.append(r != null ? r + "\n" : "");
            sb.append(b != null ? b + "\n" : "");
            sb.append(u != null ? u : "");

            textArea.setText(sb.toString());
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
