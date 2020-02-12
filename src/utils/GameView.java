package utils;

import javax.swing.*;
import java.awt.*;

import core.Constants;
import core.Types;
import core.game.Board;

import static core.Constants.*;

public class GameView extends JComponent {

    private int cellSize, gridSize;
    private Board board; //This only counts terrains. Needs to be enhanced with actors, resources, etc.
    private Image backgroundImg;
    private InfoView infoView;

    /**
     * Dimensions of the window.
     */
    private Dimension dimension;

    GameView(Board board, InfoView inforView)
    {
        this.board = board.copy();
        this.cellSize = CELL_SIZE;
        this.infoView = inforView;
        this.gridSize = board.getSize();
        this.dimension = new Dimension(gridSize * cellSize, gridSize * cellSize);
        backgroundImg = Types.TERRAIN.PLAIN.getImage();
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

        g.setColor(Color.BLACK);
        g.fillRect(0, dimension.height, dimension.width, dimension.height);

        for(int i = 0; i < gridSize; ++i) {
            for(int j = 0; j < gridSize; ++j) {

                //We paint, in this order: terrain, resources, buildings and actors.
                Types.TERRAIN t = board.getTerrainAt(i,j);
                paintImage(g, i, j, cellSize, (t == null) ? null : t.getImage());

                Types.RESOURCE r = board.getResourceAt(i,j);
                paintImage(g, i, j, cellSize, (r == null) ? null : r.getImage());

                Types.BUILDING b = board.getBuildingAt(i,j);
                paintImage(g, i, j, cellSize, (b == null) ? null : b.getImage());

                Types.UNIT u = board.getUnitAt(i,j).getType();
                paintImage(g, i, j, cellSize, (u == null) ? null : u.getImage(0)); //TODO: This playerID will need to be checked.

            }
        }

        //If there is a highlighted tile, highlight it.
        int highlightedX = infoView.getHighlightX();
        if(highlightedX != -1)
        {
            int highlightedY = infoView.getHighlightY();

            Stroke oldStroke = g.getStroke();

            g.setColor(Color.BLUE);
            g.setStroke(new BasicStroke(3));

            g.drawRect(highlightedX*cellSize, highlightedY*cellSize, cellSize -1, cellSize -1);

            g.setStroke(oldStroke);
            g.setColor(Color.BLACK);

        }


        g.setColor(Color.BLACK);
        //player.draw(g); //if we want to give control to the agent to paint something (for debug), start here.
    }


    private static void paintImage(Graphics2D gphx, int i, int j, int cellSize, Image img)
    {
        if (img != null) {
            Rectangle rect = new Rectangle(j*cellSize, i*cellSize, cellSize, cellSize);

            int w = img.getWidth(null);
            int h = img.getHeight(null);
            float scaleX = (float)rect.width/w;
            float scaleY = (float)rect.height/h;

            gphx.drawImage(img, rect.x, rect.y, (int) (w*scaleX), (int) (h*scaleY), null);
        }
    }


    /**
     * Paints the board
     * @param b board from the game
     */
    void paint(Board b)
    {
        this.board = b.copy();
        this.repaint();
    }

    /**
     * Gets the dimensions of the window.
     * @return the dimensions of the window.
     */
    public Dimension getPreferredSize() {
        return dimension;
    }


}
