package utils;

import javax.swing.*;
import java.awt.*;

import core.Types;
import core.actors.City;
import core.actors.units.Unit;
import core.game.Board;
import core.game.GameState;

import static core.Constants.*;

public class GameView extends JComponent {

    private int cellSize, gridSize;
    private Board board; //This only counts terrains. Needs to be enhanced with actors, resources, etc.
    private GameState gameState;
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
        inforView.setGameView(this);
    }


    public void paintComponent(Graphics gx)
    {
        Graphics2D g = (Graphics2D) gx;
        paintWithGraphics(g);
    }

    private void paintWithGraphics(Graphics2D g)
    {
        if(gameState == null)
            return;

        //For a better graphics, enable this: (be aware this could bring performance issues depending on your HW & OS).
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(Color.BLACK);
        g.fillRect(0, dimension.height, dimension.width, dimension.height);

        for(int i = 0; i < gridSize; ++i) {
            for(int j = 0; j < gridSize; ++j) {
                // We paint all base terrains, resources and buildings first
                Types.TERRAIN t = board.getTerrainAt(i,j);
                if(t == null)
                    paintFog(g, i, j, cellSize);
                else
                    paintImage(g, i, j, cellSize, t.getImage());

                Types.RESOURCE r = board.getResourceAt(i,j);
                paintImage(g, i, j, cellSize, (r == null) ? null : r.getImage());

                Types.BUILDING b = board.getBuildingAt(i,j);
                paintImage(g, i, j, cellSize, (b == null) ? null : b.getImage());
            }
        }

        //If there is a highlighted tile, highlight it.

        int highlightX = infoView.getHighlightX();
        if (highlightX != -1) {
            int highlightY = infoView.getHighlightY();

            Stroke oldStroke = g.getStroke();

            g.setColor(Color.BLUE);
            g.setStroke(new BasicStroke(3));

            g.drawRect(highlightX * cellSize, highlightY * cellSize, cellSize - 1, cellSize - 1);

            g.setStroke(oldStroke);
            g.setColor(Color.BLACK);
        }

        for(int i = 0; i < gridSize; ++i) {
            for(int j = 0; j < gridSize; ++j) {
                // We then paint cities and units.
                Types.TERRAIN t = board.getTerrainAt(i,j);
                if (t == Types.TERRAIN.CITY) {
                    drawCityDecorations(g, i, j, t.getImage());
                }

                Unit u = board.getUnitAt(i,j);
                paintImage(g, i, j, cellSize, (u == null) ? null : u.getType().getImage(u.getTribeId()));
            }
        }

        g.setColor(Color.BLACK);
        //player.draw(g); //if we want to give control to the agent to paint something (for debug), start here.
    }

    private static void paintFog(Graphics2D gphx, int i, int j, int cellSize)
    {
        Rectangle rect = new Rectangle(j*cellSize, i*cellSize, cellSize, cellSize);
        gphx.setColor(Color.black);
        gphx.fill(rect);
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

    private void drawCityDecorations(Graphics2D g, int i, int j, Image img) {
        int cityID = board.getCityIdAt(i,j);
        City c = (City) board.getActor(cityID);
        int level = c.getLevel();
        int progress = c.getPopulation();
        int units = c.getUnitsID().size();

        int sectionWidth = 10;

        // Draw level
        g.setColor(Color.WHITE);
        int w = level * sectionWidth;
        int h = cellSize/4;
        Rectangle bgRect = new Rectangle(j*cellSize + cellSize/2 - w/2, (i+1)*cellSize - h/2, w, h);
        g.fillRoundRect(bgRect.x, bgRect.y, bgRect.width, bgRect.height, 5, 5);

        // Draw population
        g.setColor(new Color(53, 183, 255));
        int pw = progress * sectionWidth;
        Rectangle pgRect = new Rectangle(bgRect.x, bgRect.y, pw, bgRect.height);
        g.fillRoundRect(pgRect.x, pgRect.y, pgRect.width, pgRect.height, 5, 5);

        // Draw unit counts
        g.setColor(Color.black);
        int radius = h/2;
        int unitHeight = bgRect.y + h/2 - radius/2;
        for (int u = 0; u < units; u++) {
            g.fillOval(bgRect.x + sectionWidth * u + sectionWidth/2 - radius/2, unitHeight, radius, radius);
        }

        for (int l = 0; l < level - 1; l++) {
            int lx = bgRect.x + sectionWidth * (l + 1);
            g.drawLine(lx, bgRect.y, lx, bgRect.y + bgRect.height);
        }
    }


    /**
     * Paints the board
     * @param gs current game state
     */
    void paint(GameState gs)
    {
        //The tribe Id of which the turn gs at this point
        //int gameTurn = 0;// gs.getTick() % gs.getTribes().length;
        gameState = gs; //.copy(gameTurn);
        board = gameState.getBoard();
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
