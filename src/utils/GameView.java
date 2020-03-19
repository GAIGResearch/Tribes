package utils;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
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

    private static int cellSize, gridSize;
    private Board board; //This only counts terrains. Needs to be enhanced with actors, resources, etc.
    private GameState gameState;
//    private Image backgroundImg;
    private InfoView infoView;

    private int shadowOffset = 1;
    private int roundRectArc = 5;
    private Color progressColor = new Color(53, 183, 255);
    private Image starImg, starShadow, capitalImg, capitalShadow;

    /**
     * Dimensions of the window.
     */
    private static Dimension dimension;
    private static double isometricAngle = -45;

    GameView(Board board, InfoView inforView)
    {
        this.board = board.copy();
        this.infoView = inforView;

        cellSize = CELL_SIZE;
        gridSize = board.getSize();
        int size = gridSize * cellSize;
        int d = (int) Math.sqrt(size * size * 2);
        dimension = new Dimension(d, d);

//        backgroundImg = Types.TERRAIN.PLAIN.getImage(null);
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
                if(t == null) {
                    paintFog(g, i, j, cellSize);
                } else {
                    Image toPaint = getContextImg(i, j, t);
                    paintImage(g, j * cellSize, i * cellSize, toPaint, cellSize);
                }

                Types.RESOURCE r = board.getResourceAt(i,j);
                int imgSize = (int) (cellSize * 0.75);
                paintImage(g, j*cellSize, i*cellSize, (r == null) ? null : r.getImage(), imgSize);

                Types.BUILDING b = board.getBuildingAt(i,j);
                paintImage(g, j*cellSize, i*cellSize, (b == null) ? null : b.getImage(), cellSize);
            }
        }

        //If there is a highlighted tile, highlight it.
        int highlightX = infoView.getHighlightX();
        int highlightY = infoView.getHighlightY();
        if (highlightX != -1) {
            Point2D p = rotatePoint(highlightX * cellSize, highlightY * cellSize);
            System.out.println(p.toString());
            Stroke oldStroke = g.getStroke();
            g.setColor(Color.BLUE);
            g.setStroke(new BasicStroke(3));
            drawRotatedRect(g, (int)p.getX(), (int)p.getY(), cellSize - 1, cellSize - 1);
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
            if (u.getPosition().x == highlightY && u.getPosition().y == highlightX) {
                for (Action a: e.getValue()) {
                    Image actionImg = Types.ACTION.getImage(a);

                    if (actionImg != null) {
                        Vector2d pos = getActionPosition(a);

                        if (pos != null) {
                            paintImage(g, pos.y * cellSize, pos.x * cellSize, actionImg, cellSize);
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
            int w = img.getWidth(null);
            int h = img.getHeight(null);
            float scaleX = (float)imgSize/w;
            float scaleY = (float)imgSize/h;

            Graphics2D g2 = (Graphics2D)gphx.create();
            g2.translate(0, dimension.width/2);
            g2.rotate(Math.toRadians(isometricAngle));
            g2.drawImage(img, x + cellSize/2 - imgSize/2, y + cellSize/2 - imgSize/2, (int) (w*scaleX), (int) (h*scaleY), null);
            g2.dispose();
        }
    }

    private static void drawRotatedRect(Graphics2D g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.translate(0, dimension.width/2);
        g2.rotate(Math.toRadians(isometricAngle));
        g2.drawRect(x, y, width, height);
        g2.dispose();
    }


    // Take into account rotation of terrain to adjust x, y points
//    private static void paintImage(Graphics2D gphx, int x, int y, Image img, int imgSize)
//    {
//        if (img != null) {
//
//            Point2D rotatedPos = rotatePoint(x, y);
//            int w = img.getWidth(null);
//            int h = img.getHeight(null);
//            float scaleX = (float)imgSize/w;
//            float scaleY = (float)imgSize/h;
//            gphx.drawImage(img, (int)rotatedPos.getX(), (int)rotatedPos.getY(), (int) (w*scaleX), (int) (h*scaleY), null);
//        }
//    }
    
    public static Point2D rotatePoint(int x, int y) {
        Point2D newPoint = _rotate(x, y, isometricAngle);
        return new Point2D.Double(newPoint.getX(), newPoint.getY() + dimension.width/2.0);
    }

    public static Point2D rotatePointReverse(int x, int y) {
        y -= dimension.width/2;
        return _rotate(x, y, -isometricAngle);
    }

    private static Point2D _rotate(int x, int y, double angle) {
        Point2D center = new Point2D.Double(0, 0);
        int newX = (int)(center.getX() + (x-center.getX())*Math.cos(angle) - (y-center.getY())*Math.sin(angle));
        int newY = (int)(center.getY() + (x-center.getX())*Math.sin(angle) + (y-center.getY())*Math.cos(angle));
//        newX += dimension.width/4 + cellSize + cellSize/4;
        return new Point2D.Double(newX, newY);
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
        Point2D namePos = rotatePoint(j*cellSize + cellSize/2 - nameWidth/2,(i+1)*cellSize - h*3/2);
        Rectangle nameRect = new Rectangle((int)namePos.getX(), (int)namePos.getY(), nameWidth, h);
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
        Point2D bgPos = rotatePoint(j*cellSize + cellSize/2 - w/2, (i+1)*cellSize - h/2);
        Rectangle bgRect = new Rectangle((int)bgPos.getX(), (int)bgPos.getY(), w, h);
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
        int unitHeight = bgRect.y + h/2 - radius/2;
        for (int u = 0; u < units; u++) {
            g.fillOval(bgRect.x + sectionWidth * u + sectionWidth/2 - radius/2, unitHeight, radius, radius);
        }

        // Draw section separations
        for (int l = 0; l < level - 1; l++) {
            int lx = bgRect.x + sectionWidth * (l + 1);
            g.drawLine(lx, bgRect.y, lx, bgRect.y + bgRect.height);
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


    private Image getContextImg(int i, int j, Types.TERRAIN t) {
        Image toPaint;
        boolean cornerUL = (i == 0 && j == 0);
        boolean cornerDR = (i == gridSize - 1 && j == gridSize -1);
        Types.TERRAIN diagUR = null;
        if (i > 0 && j < gridSize - 1) diagUR = board.getTerrainAt(i - 1, j + 1);

        if (t == Types.TERRAIN.DEEP_WATER || t == Types.TERRAIN.SHALLOW_WATER) {
            // If this is the last tile on the row
            boolean down = (i == gridSize - 1);
            // If the tile above is not water
            boolean top = (i > 0 && board.getTerrainAt(i - 1, j) != Types.TERRAIN.SHALLOW_WATER && board.getTerrainAt(i - 1, j) != Types.TERRAIN.DEEP_WATER);
            // If first tile on column
            boolean left = (j == 0);
            // If the tile to the right is not water
            boolean right = (j < gridSize - 1 && board.getTerrainAt(i, j + 1) != Types.TERRAIN.SHALLOW_WATER && board.getTerrainAt(i, j + 1) != Types.TERRAIN.DEEP_WATER);
            if (down) {
                if (left) {
                    toPaint = t.getImage("down-left");
                } else if (right) {
                    if (top) {
                        toPaint = t.getImage("top-down-right");
                    } else {
                        if (diagUR == Types.TERRAIN.SHALLOW_WATER || diagUR == Types.TERRAIN.DEEP_WATER) {
                            toPaint = t.getImage("down-right-ur");
                        } else {
                            toPaint = t.getImage("down-right");
                        }
                    }
                } else if (top) {
                    if (diagUR == Types.TERRAIN.SHALLOW_WATER || diagUR == Types.TERRAIN.DEEP_WATER) {
                        toPaint = t.getImage("top-down-ur");
                    } else {
                        toPaint = t.getImage("top-down");
                    }
                } else {
                    toPaint = t.getImage("down");
                }
            } else if (top) {
                if (j == gridSize-1 || diagUR == Types.TERRAIN.SHALLOW_WATER || diagUR == Types.TERRAIN.DEEP_WATER) {
                    if (left) {
                        toPaint = t.getImage("top-left-ur");
                    } else {
                        toPaint = t.getImage("top-ur");
                    }
                } else {
                    if (right) {
                        if (left) {
                            toPaint = t.getImage("top-left-right");
                        } else {
                            toPaint = t.getImage("top-right");
                        }
                    } else if (left) {
                        toPaint = t.getImage("top-left");
                    } else {
                        toPaint = t.getImage("top");
                    }
                }
            } else if (right) {
                if (i == 0 || j < gridSize - 1 && (diagUR == Types.TERRAIN.SHALLOW_WATER || diagUR == Types.TERRAIN.DEEP_WATER)) {
                    toPaint = t.getImage("right-ur");
                } else {
                    toPaint = t.getImage("right");
                }
            } else if (left) {
                toPaint = t.getImage("left");
            } else {
                toPaint = t.getImage(null);
            }
        } else {
            // If this is the last tile on the row, or the tile underneath is water, get the '-down' img
            boolean down = (i == gridSize - 1 || board.getTerrainAt(i+1, j) == Types.TERRAIN.SHALLOW_WATER);
            // If the same is true for the column instead, this is a '-left' img
            boolean left = (j == 0 || board.getTerrainAt(i, j-1) == Types.TERRAIN.SHALLOW_WATER);

            if (down) {
                if (j == gridSize - 1) {
                    if (cornerDR) {
                        toPaint = t.getImage("corner-dr");
                    } else {
                        toPaint = t.getImage("down-dr");
                    }
                } else {
                    if (left) {
                        if (j == 0) {
                            if (i < gridSize - 1 && (board.getTerrainAt(i, j + 1) == Types.TERRAIN.SHALLOW_WATER ||
                                            board.getTerrainAt(i, j + 1) == Types.TERRAIN.DEEP_WATER)) {
                                toPaint = t.getImage("down-left-el-dr");
                            } else {
                                toPaint = t.getImage("down-left-el");
                            }
                        } else if (i == gridSize - 1) {
                            if (board.getTerrainAt(i - 1, j) == Types.TERRAIN.SHALLOW_WATER
                                    || board.getTerrainAt(i - 1, j) == Types.TERRAIN.DEEP_WATER) {
                                toPaint = t.getImage("down-left-ed-ul");
                            } else {
                                toPaint = t.getImage("down-left-ed");
                            }
                        } else {
                            toPaint = t.getImage("down-left");
                        }
                    } else {
                        if (j < gridSize - 1 && i < gridSize - 1 &&
                                (board.getTerrainAt(i, j + 1) == Types.TERRAIN.SHALLOW_WATER ||
                                board.getTerrainAt(i, j + 1) == Types.TERRAIN.DEEP_WATER)) {
                            toPaint = t.getImage("down-dr");
                        } else {
                            toPaint = t.getImage("down");
                        }
                    }
                }
            } else if (left) {
                if (i == 0 || board.getTerrainAt(i - 1, j) == Types.TERRAIN.SHALLOW_WATER ||
                        board.getTerrainAt(i - 1, j) == Types.TERRAIN.DEEP_WATER) {
                    if (cornerUL) {
                        toPaint = t.getImage("corner-ul");
                    } else {
                        if (j == 0) {
                            toPaint = t.getImage("left");
                        } else {
                            toPaint = t.getImage("left-ul");
                        }
                    }
                } else {
                    toPaint = t.getImage("left");
                }
            } else {
                Types.TERRAIN tl = board.getTerrainAt(i, j - 1);
                Types.TERRAIN td = board.getTerrainAt(i + 1, j);
                Types.TERRAIN tld = board.getTerrainAt(i + 1, j - 1);
                if (i < gridSize-1 && j > 0 && (tld == Types.TERRAIN.SHALLOW_WATER || tld == Types.TERRAIN.DEEP_WATER) &&
                        (tl != Types.TERRAIN.SHALLOW_WATER && tl != Types.TERRAIN.DEEP_WATER) &&
                        (td != Types.TERRAIN.SHALLOW_WATER && td != Types.TERRAIN.DEEP_WATER)) {
                    toPaint = t.getImage("dl");
                } else {
                    toPaint = t.getImage(null);
                }
            }
        }
        return toPaint;
    }
}
