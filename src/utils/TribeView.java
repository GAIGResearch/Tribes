package utils;

import core.game.Board;
import core.game.GameState;

import javax.swing.*;
import java.awt.*;


public class TribeView extends JComponent {

    private int cellSize = 15;
    private int offsetX = 40, offsetY = 18;

    /**
     * Dimensions of the window.
     */
    private Dimension size;

    TribeView()
    {
        this.size = new Dimension(4 * (this.cellSize + offsetX), this.cellSize + offsetY);

    }

    TribeView(int nTribes)
    {
        this.size = new Dimension(nTribes * (this.cellSize + offsetX), this.cellSize + offsetY);
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

    }


    /**

     */
    void paint(GameState gameState)
    {
        this.repaint();
    }

    private void copyObjects()
    {

    }

    /**
     * Gets the dimensions of the window.
     * @return the dimensions of the window.
     */
    public Dimension getPreferredSize() {
        return size;
    }

}
