package utils;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import core.Types;
import core.actors.City;
import core.actors.units.Unit;
import core.game.Board;
import core.game.GameState;
import core.actions.Action;

import static core.Constants.*;
import static core.Types.getActionPosition;

public class GameView extends JComponent {

    private int cellSize, gridSize;
    private Board board; //This only counts terrains. Needs to be enhanced with actors, resources, etc.
    private GameState gameState;
    private Image backgroundImg;
    private InfoView infoView;

    private int shadowOffset = 1;
    private int roundRectArc = 5;
    private Color progressColor = new Color(53, 183, 255);
    private Image starImg, starShadow, capitalImg, capitalShadow;

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
        starImg = ImageIO.GetInstance().getImage("img/decorations/star.png");
        starShadow = ImageIO.GetInstance().getImage("img/decorations/starShadow.png");
        capitalImg = ImageIO.GetInstance().getImage("img/decorations/capital.png");
        capitalShadow = ImageIO.GetInstance().getImage("img/decorations/capitalShadow.png");
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
                    paintImage(g, j*cellSize, i*cellSize, t.getImage(), cellSize);

                Types.RESOURCE r = board.getResourceAt(i,j);
                paintImage(g, j*cellSize, i*cellSize, (r == null) ? null : r.getImage(), cellSize);

                Types.BUILDING b = board.getBuildingAt(i,j);
                paintImage(g, j*cellSize, i*cellSize, (b == null) ? null : b.getImage(), cellSize);
            }
        }

        //If there is a highlighted tile, highlight it.
        int highlightX = infoView.getHighlightX();
        int highlightY = infoView.getHighlightY();
        if (highlightX != -1) {
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
                    drawCityDecorations(g, i, j);
                }

                Unit u = board.getUnitAt(i,j);
                if (u != null) {

                    int imgSize = (int) (cellSize * 0.75);
                    String imgFile = u.getType().getImageFile();
                    boolean exhausted = false; //u.isExhausted(); // TODO: does unit have available actions?

                    if (exhausted) {
                        String exhaustedStr = imgFile + imgFile.split("/")[2] + "Exhausted.png";
                        Image exhaustedImg = ImageIO.GetInstance().getImage(exhaustedStr);
                        paintImage(g, j * cellSize + cellSize / 2 - imgSize / 2 - shadowOffset, i * cellSize + cellSize / 2 - imgSize / 2 - shadowOffset,
                                exhaustedImg, imgSize);
                    } else {
                        String highlightStr = imgFile + imgFile.split("/")[2] + "Highlight.png";
                        String shadowStr = imgFile + imgFile.split("/")[2] + "Shadow.png";
                        Image highlight = ImageIO.GetInstance().getImage(highlightStr);
                        Image shadow = ImageIO.GetInstance().getImage(shadowStr);
                        paintImage(g, j * cellSize + cellSize / 2 - imgSize / 2 - shadowOffset, i * cellSize + cellSize / 2 - imgSize / 2 - shadowOffset,
                                highlight, imgSize);
                        paintImage(g, j * cellSize + cellSize / 2 - imgSize / 2 + shadowOffset, i * cellSize + cellSize / 2 - imgSize / 2 + shadowOffset,
                                shadow, imgSize);
                        paintImage(g, j * cellSize + cellSize / 2 - imgSize / 2, i * cellSize + cellSize / 2 - imgSize / 2,
                                u.getType().getImage(u.getTribeId()), imgSize);
                    }
                }
            }
        }

        // Draw unit actions
        HashMap<Unit, ArrayList<Action>> possibleActions = gameState.getUnitActions();
        for (Map.Entry<Unit, ArrayList<Action>> e: possibleActions.entrySet()) {
            Unit u = e.getKey();

            // Only draw actions for highlighted unit
            if (u.getPosition().x == highlightX && u.getPosition().y == highlightY) {
                for (Action a: e.getValue()) {
                    Image actionImg = Types.ACTION.getImage(a);

                    if (actionImg != null) {
                        Vector2d pos = getActionPosition(a);

                        if (pos != null) {
                            paintImage(g, pos.x * cellSize, pos.y + cellSize, actionImg, cellSize);
                        }
                    }
                }
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

    private static void paintImage(Graphics2D gphx, int x, int y, Image img, int imgSize)
    {
        if (img != null) {
            Rectangle rect = new Rectangle(x, y, imgSize, imgSize);
            int w = img.getWidth(null);
            int h = img.getHeight(null);
            float scaleX = (float)rect.width/w;
            float scaleY = (float)rect.height/h;

            gphx.drawImage(img, rect.x, rect.y, (int) (w*scaleX), (int) (h*scaleY), null);
        }
    }

    private void drawCityDecorations(Graphics2D g, int i, int j) {
        int cityID = board.getCityIdAt(i,j);
        City c = (City) board.getActor(cityID);
        int level = c.getLevel();
        int progress = c.getPopulation();
        int units = c.getUnitsID().size();

        // Draw capital img + city name/ID + number of stars
        Color col = Types.TRIBE.values()[c.getTribeId()].getColorDark();
        String cityName = ""+cityID;
        String production = "" + c.getProduction();

        int nameWidth = 20;
        int h = cellSize/4;
        Rectangle nameRect = new Rectangle(j*cellSize + cellSize/2 - nameWidth/2,(i+1)*cellSize - h*3/2, nameWidth, h);
        drawRectShadowHighlight(g, nameRect);
        g.setColor(col);
        g.fillRect(nameRect.x, nameRect.y, nameRect.width, nameRect.height);
        g.setColor(Color.WHITE);
        g.drawString(cityName, nameRect.x + nameRect.width/2 - 4*cityName.length(), nameRect.y+h-2);

        // Draw number of stars
        paintImage(g, nameRect.x + nameRect.width + shadowOffset, nameRect.y + shadowOffset, starShadow, h);
        paintImage(g, nameRect.x + nameRect.width, nameRect.y, starImg, h);
        drawStringShadow(g, production, nameRect.x + nameRect.width + h, nameRect.y+h-2);
        g.setColor(Color.WHITE);
        g.drawString(production, nameRect.x + nameRect.width + h, nameRect.y+h-2);

        // Draw capital sign
        if (c.isCapital()) {
            paintImage(g, nameRect.x - h + shadowOffset, nameRect.y + shadowOffset, capitalShadow, h);
            paintImage(g, nameRect.x - h, nameRect.y, capitalImg, h);
        }

        // Draw level
        int sectionWidth = 10;
        int w = level * sectionWidth;
        Rectangle bgRect = new Rectangle(j*cellSize + cellSize/2 - w/2, (i+1)*cellSize - h/2, w, h);
        drawRoundRectShadowHighlight(g, bgRect);
        g.setColor(Color.WHITE);
        g.fillRoundRect(bgRect.x, bgRect.y, bgRect.width, bgRect.height, roundRectArc, roundRectArc);

        // Draw population/progress
        g.setColor(progressColor);
        int pw = progress * sectionWidth;
        Rectangle pgRect = new Rectangle(bgRect.x, bgRect.y, pw, bgRect.height);
        g.fillRoundRect(pgRect.x, pgRect.y, pgRect.width, pgRect.height, roundRectArc, roundRectArc);

        // Draw unit counts
        g.setColor(Color.black);
        int radius = h/2;
        int unitHeight = (int)bgRect.y + h/2 - radius/2;
        for (int u = 0; u < units; u++) {
            g.fillOval((int)bgRect.x + sectionWidth * u + sectionWidth/2 - radius/2, unitHeight, radius, radius);
        }

        // Draw section separations
        for (int l = 0; l < level - 1; l++) {
            int lx = (int)bgRect.x + sectionWidth * (l + 1);
            g.drawLine(lx, (int)bgRect.y, lx, (int)bgRect.y + (int)bgRect.height);
        }
    }

    private void drawRoundRectShadowHighlight(Graphics2D g, Rectangle rect) {
        g.setColor(new Color(0, 0, 0, 122));
        g.fillRoundRect(rect.x + shadowOffset, rect.y + shadowOffset, rect.width, rect.height,
                roundRectArc, roundRectArc);
        g.setColor(new Color(255, 255, 255, 122));
        g.fillRoundRect(rect.x - shadowOffset, rect.y - shadowOffset, rect.width, rect.height,
                roundRectArc, roundRectArc);
    }

    private void drawRectShadowHighlight(Graphics2D g, Rectangle rect) {
        g.setColor(new Color(0, 0, 0, 122));
        g.fillRect(rect.x + shadowOffset, rect.y + shadowOffset, rect.width, rect.height);
        g.setColor(new Color(255, 255, 255, 122));
        g.fillRect(rect.x - shadowOffset, rect.y - shadowOffset, rect.width, rect.height);
    }

    private void drawStringShadow (Graphics2D g, String s, int x, int y) {
        g.setColor(new Color(0, 0, 0, 122));
        g.drawString(s, x+shadowOffset, y+shadowOffset);
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
