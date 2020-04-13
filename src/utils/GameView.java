package utils;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.*;

import core.Types;
import core.actions.cityactions.CityAction;
import core.actions.cityactions.ResourceGathering;
import core.actions.unitactions.Capture;
import core.actions.unitactions.Examine;
import core.actors.Actor;
import core.actors.City;
import core.actors.Tribe;
import core.actors.units.Catapult;
import core.actors.units.SuperUnit;
import core.actors.units.Unit;
import core.game.Board;
import core.game.GameState;
import core.actions.Action;
import javafx.util.Pair;

import static core.Constants.*;
import static core.Types.TERRAIN.*;

@SuppressWarnings({"SuspiciousNameCombination", "unchecked"})
public class GameView extends JComponent {

    static int gridSize;
    private Board board; //This only counts terrains. Needs to be enhanced with actors, resources, etc.
    private GameState gameState;
//    private Image backgroundImg;
    private Image fogImg, shineImg;
    private InfoView infoView;
    private Point2D panTranslate;  // Used to translate all coordinates for objects drawn on screen

    private Color progressColor = new Color(53, 183, 255);
    private Color negativeColor = new Color(255, 63, 73);
    private Image starImg, starShadow, capitalImg, capitalShadow, cityWalls, road;

    boolean[][] actionable;

    /**
     * Dimensions of the window.
     */
    public static Dimension dimension;
    private static double isometricAngle = -45;

    GameView(Board board, InfoView inforView, Point2D panTranslate)
    {
        this.board = board.copy();
        this.infoView = inforView;
        this.panTranslate = panTranslate;

        gridSize = board.getSize();
//        int size = gridSize * CELL_SIZE;
//        int d = (int) Math.sqrt(size * size * 2);
        dimension = new Dimension(GUI_GAME_VIEW_SIZE, GUI_GAME_VIEW_SIZE);

//        backgroundImg = PLAIN.getImage(null);
        fogImg = ImageIO.GetInstance().getImage("img/fog.png");
        shineImg = ImageIO.GetInstance().getImage("img/shine3.png");
        starImg = ImageIO.GetInstance().getImage("img/decorations/star.png");
        starShadow = ImageIO.GetInstance().getImage("img/decorations/starShadow.png");
        capitalImg = ImageIO.GetInstance().getImage("img/decorations/capital.png");
        capitalShadow = ImageIO.GetInstance().getImage("img/decorations/capitalShadow.png");
        cityWalls = ImageIO.GetInstance().getImage("img/terrain/walls.png");
        road = ImageIO.GetInstance().getImage("img/terrain/road.png");
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
        g.fillRect(0, 0, dimension.width, dimension.height);

        // Update list of actionable tiles to be highlighted (collectible resources)
        updateActionableTiles();
        paintTerrains(g);
        paintRoads(g);
        paintResourcesBuildings(g);

        int highlightX = infoView.getHighlightX();
        int highlightY = infoView.getHighlightY();

        highlightTile(g, highlightX, highlightY);

        drawCityDecorations(g);
        paintUnits(g);
        paintActionsHighlightedUnit(g, highlightX, highlightY);
        paintOtherActions(g);

        g.setColor(Color.BLACK);
        //player.draw(g); //if we want to give control to the agent to paint something (for debug), start here.
    }

    private void updateActionableTiles() {
        actionable = new boolean[gridSize][gridSize];
        HashMap<Integer, ArrayList<Action>> actions = gameState.getCityActions();
        for (Map.Entry<Integer, ArrayList<Action>> e: actions.entrySet()) {
            for (Action a: e.getValue()) {
                if (a instanceof CityAction) {
                    Vector2d pos = ((CityAction) a).getTargetPos();
                    if (pos != null) {
                        actionable[pos.x][pos.y] = a instanceof ResourceGathering;
                    }
                }
            }
        }
    }

    private void paintTerrains(Graphics2D g) {
        for(int i = 0; i < gridSize; ++i) {
            for(int j = 0; j < gridSize; ++j) {
                Types.TERRAIN t = board.getTerrainAt(i,j);
                if(t == null) {
                    paintImageRotated(g, j * CELL_SIZE, i * CELL_SIZE, fogImg, CELL_SIZE, panTranslate);
                } else {
                    Image toPaint = getContextImg(i, j, t);
                    paintImageRotated(g, j * CELL_SIZE, i * CELL_SIZE, toPaint, CELL_SIZE, panTranslate);
                }
            }
        }
    }

    private void paintRoads(Graphics2D g) {
        for(int i = 0; i < gridSize; ++i) {
            for (int j = 0; j < gridSize; ++j) {
                if (board.isRoad(i, j)) {
                    paintImageRotated(g, j * CELL_SIZE, i * CELL_SIZE, road, CELL_SIZE, panTranslate);
                }
            }
        }
    }

    private void paintResourcesBuildings(Graphics2D g) {
        for(int i = 0; i < gridSize; ++i) {
            for(int j = 0; j < gridSize; ++j) {
                Types.TERRAIN t = board.getTerrainAt(i,j);
                Types.RESOURCE r = board.getResourceAt(i,j);
                if (actionable[i][j]) paintImageRotated(g, j * CELL_SIZE, i * CELL_SIZE, shineImg, CELL_SIZE, panTranslate);
                int imgSize = (int) (CELL_SIZE * 0.75);
                paintImageRotated(g, j * CELL_SIZE, i * CELL_SIZE, (r == null) ? null : r.getImage(t), imgSize, panTranslate);

                Types.BUILDING b = board.getBuildingAt(i,j);
                paintImageRotated(g, j*CELL_SIZE, i*CELL_SIZE, (b == null) ? null : b.getImage(), CELL_SIZE, panTranslate);
            }
        }
    }

    private void highlightTile(Graphics2D g, int highlightX, int highlightY) {
        if (highlightX != -1) {
            Stroke oldStroke = g.getStroke();
            g.setColor(Color.BLUE);
            g.setStroke(new BasicStroke(3));

            Point2D p = rotatePoint(highlightX, highlightY);
            drawRotatedRect(g, (int)p.getX(), (int)p.getY(), CELL_SIZE - 1, CELL_SIZE - 1, panTranslate);
            g.setStroke(oldStroke);
            g.setColor(Color.BLACK);
        }
    }

    private void paintUnits(Graphics2D g) {
        for(int i = 0; i < gridSize; ++i) {
            for(int j = 0; j < gridSize; ++j) {
                Unit u = board.getUnitAt(i,j);
                if (u != null) {
                    int imgSize = (int) (CELL_SIZE * 0.75);
                    if (u instanceof SuperUnit || u instanceof Catapult) imgSize = CELL_SIZE;
                    String imgFile = u.getType().getImageFile();

                    Point2D rotated = rotatePoint(j, i);
                    int x = (int)(rotated.getX() + CELL_SIZE*CELL_SIZE/4/imgSize);
                    int y = (int)(rotated.getY() - imgSize/1.5);

                    ArrayList<Action> possibleActions = gameState.getUnitActions(u);
                    boolean exhausted = (possibleActions == null || possibleActions.size() == 0);

                    Tribe t = gameState.getTribe(u.getTribeId());
                    int imageTribeId = t.getType().getKey();

                    if (exhausted) {
                        String exhaustedStr = imgFile + imageTribeId + "Exhausted.png";
                        Image exhaustedImg = ImageIO.GetInstance().getImage(exhaustedStr);
                        paintImage(g, x, y, exhaustedImg, imgSize, panTranslate);
                    } else {
                        paintImage(g, x, y, u.getType().getImage(imageTribeId), imgSize, panTranslate);
                    }
                }
            }
        }
    }

    private void paintActionsHighlightedUnit(Graphics2D g, int highlightX, int highlightY) {
        if (infoView.highlightInGridBounds()) {
            Unit u = board.getUnitAt(highlightY, highlightX);
            if (u != null && !infoView.clickedTwice()) {
                ArrayList<Action> possibleActions = gameState.getUnitActions(u);
                if (possibleActions != null && possibleActions.size() > 0) {
                    for (Action a : possibleActions) {
                        if (!(a instanceof Examine || a instanceof Capture)) {
                            Image actionImg = Types.ACTION.getImage(a);

                            if (actionImg != null) {
                                Vector2d pos = GUI.getActionPosition(gameState, a);

                                if (pos != null) {
                                    paintImageRotated(g, pos.y * CELL_SIZE, pos.x * CELL_SIZE, actionImg, CELL_SIZE, panTranslate);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void paintOtherActions(Graphics2D g) {
        HashMap<Integer, ArrayList<Action>> actions = gameState.getUnitActions();
        for (Map.Entry<Integer, ArrayList<Action>> e: actions.entrySet()) {
            for (Action a : e.getValue()) {
                if (a instanceof Examine || a instanceof Capture) {
                    Image actionImg = Types.ACTION.getImage(a);
                    if (actionImg != null) {
                        Vector2d pos = GUI.getActionPosition(gameState, a);

                        if (pos != null) {
                            Point2D rotated = rotatePoint(pos.y, pos.x);
                            int imgSize = (int)(CELL_SIZE*0.5);
                            paintImage(g, (int)(rotated.getX()) + CELL_SIZE, (int)(rotated.getY() - imgSize/2),
                                    actionImg, imgSize, panTranslate);
                        }
                    }
                }
            }
        }
    }

    // *****************************************************************************************************************

    private static void paintImageRotated(Graphics2D gphx, int x, int y, Image img, int imgSize, Point2D panTranslate)
    {
        if (img != null) {
            int w = img.getWidth(null);
            int h = img.getHeight(null);
            float scaleX = (float)imgSize/w;
            float scaleY = (float)imgSize/h;

            Graphics2D g2 = (Graphics2D)gphx.create();
            g2.translate(panTranslate.getX(), panTranslate.getY() + dimension.width/2.0);
            g2.rotate(Math.toRadians(isometricAngle));
            g2.drawImage(img, (int)(x + CELL_SIZE/2.0 - imgSize/2.0),
                    (int)(y + CELL_SIZE/2.0 - imgSize/2.0),
                    (int) (w*scaleX), (int) (h*scaleY), null);
            g2.dispose();
        }
    }

    private static void paintImage(Graphics2D gphx, int x, int y, Image img, int imgSize, Point2D panTranslate)
    {
        if (img != null) {
            int w = img.getWidth(null);
            int h = img.getHeight(null);
            float scaleX = (float)imgSize/w;
            float scaleY = (float)imgSize/h;
            gphx.drawImage(img, (int)(x + panTranslate.getX()), (int)(y + panTranslate.getY()),
                    (int) (w*scaleX), (int) (h*scaleY), null);
        }
    }

    private static void drawRotatedRect(Graphics2D g, int x, int y, int width, int height, Point2D panTranslate) {
        Graphics2D g2 = (Graphics2D) g.create();
        x += panTranslate.getX();
        y += panTranslate.getY();
        g2.rotate(Math.toRadians(isometricAngle), x, y);
        g2.drawRect(x, y, width, height);
        g2.dispose();
    }

    /**
     * Expects coordinates in grid, translates to screen coordinates.
     */
    public static Point2D rotatePoint(double x, double y) {
        double d = Math.sqrt(2*CELL_SIZE*CELL_SIZE);
        double x2 = x * CELL_SIZE + y * d/2 - x * d/5;
        double y2 = y * CELL_SIZE - y * d/5 - x * d/2;
        y2 += dimension.width/2.0;
        return new Point2D.Double(x2, y2);
    }

    /**
     * Expects screen coordinates, returns coordinates in grid.
     */
    public static Point2D rotatePointReverse(int x, int y) {
        double d = Math.sqrt(2*CELL_SIZE*CELL_SIZE);
        y -= GameView.dimension.width/2;
        double x2 = (x*(CELL_SIZE-d/5.0) - y*d/2.0)/(CELL_SIZE*CELL_SIZE - 2*CELL_SIZE*d/5.0 + (d/5)*(d/5) + d*d/4.0);
        double y2 = (y + x2 * d/2.0)/(CELL_SIZE - d/5.0);
        return new Point2D.Double(x2, y2);
    }

    private void drawCityDecorations(Graphics2D g) {
        for(int i = 0; i < gridSize; ++i) {
            for (int j = 0; j < gridSize; ++j) {
                Types.TERRAIN terrainAt = board.getTerrainAt(i, j);
                if (terrainAt == CITY) {
                    int d = (int)Math.sqrt(CELL_SIZE*CELL_SIZE*2);
                    int fontSize = CELL_SIZE/3;
                    Font textFont = new Font(getFont().getName(), Font.PLAIN, fontSize);
                    g.setFont(textFont);

                    int cityID = board.getCityIdAt(i,j);
                    City c = (City) board.getActor(cityID);

                    if (c != null) {  // TODO: this shouldn't happen, there's a city here
                        int cityCapacity = c.getLevel() + 1;
                        int progress = c.getPopulation();
                        int units = c.getUnitsID().size();
                        Tribe tr = gameState.getTribe(c.getTribeId());
                        Color col = Types.TRIBE.values()[tr.getType().getKey()].getColorDark();
                        Color colTransparent = new Color(col.getRed(), col.getGreen(), col.getBlue(), 170);

                        // Draw city walls
                        if (c.hasWalls()) {
                            Point2D rotatedP = rotatePoint(j + 0.4, i - 0.2);
                            paintImage(g, (int)(rotatedP.getX()), (int)(rotatedP.getY()), cityWalls, CELL_SIZE, panTranslate);
                        }

                        // Draw city border
                        LinkedList<Vector2d> tiles = board.getCityTiles(cityID);
                        int nCityTiles = tiles.size();
                        int nNeighbours = 4;
                        boolean[][] tileNeighbours = new boolean[nCityTiles][nNeighbours];  // order: left, right, up, down
                        Pair<Vector2d, Vector2d>[] lines = new Pair[]{new Pair<>(new Vector2d(0, 0), new Vector2d(0, 1)),
                                new Pair<>(new Vector2d(1, 0), new Vector2d(1, 1)),
                                new Pair<>(new Vector2d(0, 0), new Vector2d(1, 0)),
                                new Pair<>(new Vector2d(0, 1), new Vector2d(1, 1))};
                        for (int k = 0; k < nCityTiles-1; k++) {
                            Vector2d t1 = tiles.get(k);
                            for (int p = k+1; p < nCityTiles; p++) {
                                Vector2d t2 = tiles.get(p);
                                if (t1.equals(t2)) continue;
                                if (t1.x - t2.x == 1 && t1.y == t2.y) {  // t1 to the right of t2
                                    tileNeighbours[k][0] = true;
                                    tileNeighbours[p][1] = true;
                                } else if (t1.x - t2.x == -1 && t1.y == t2.y) { // t1 to the left of t2
                                    tileNeighbours[k][1] = true;
                                    tileNeighbours[p][0] = true;
                                } else if (t1.x == t2.x && t1.y - t2.y == 1) {  // t1 underneath t2
                                    tileNeighbours[k][2] = true;
                                    tileNeighbours[p][3] = true;
                                } else if (t1.x == t2.x && t1.y - t2.y == -1) {  // t1 above t2
                                    tileNeighbours[k][3] = true;
                                    tileNeighbours[p][2] = true;
                                }
                            }
                        }
                        g.setColor(col);
                        Stroke oldStroke = g.getStroke();
                        g.setStroke(new BasicStroke(5, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0));
                        // Draw lines for tiles that don't have a neighbour on a side
                        for (int t = 0; t < nCityTiles; t++) {
                            Vector2d tile = tiles.get(t);
                            for (int n = 0; n < nNeighbours; n++) {
                                if (!tileNeighbours[t][n]) {
                                    // draw line on this side
                                    Point2D from = rotatePoint(lines[n].getKey().y + tile.y, lines[n].getKey().x + tile.x);
                                    Point2D to = rotatePoint(lines[n].getValue().y + tile.y, lines[n].getValue().x + tile.x);
                                    g.drawLine((int)from.getX() + (int)panTranslate.getX(), (int)from.getY() + (int)panTranslate.getY(),
                                            (int)to.getX() + (int)panTranslate.getX(), (int)to.getY() + (int)panTranslate.getY());
                                }
                            }
                        }
                        g.setStroke(oldStroke);

                        // Draw capital img + city name/ID + number of stars
                        String cityName = "" + cityID;
                        String production = "" + c.getProduction();

                        int sections = 2;
                        if (c.isCapital()) {
                            sections = 3;
                        }
                        double h = d / 4.0;
                        double nameWidth = GUI_CITY_TAG_WIDTH + sections * h;
                        Point2D namePos = rotatePoint(j, i);
                        Rectangle nameRect = new Rectangle((int) (namePos.getX() + d / 2.0 - nameWidth * 2 / 3.0),
                                (int) (namePos.getY() + d / 2.0 - h), (int) nameWidth, (int) h);
                        g.setColor(colTransparent);
                        g.fillRect((int) (nameRect.x + panTranslate.getX()), (int) (nameRect.y + panTranslate.getY()), nameRect.width, nameRect.height);
                        g.setColor(Color.WHITE);

                        g.drawString(cityName, (int) (nameRect.x + (sections-2) * h + fontSize / 4.0 + panTranslate.getX()),
                                (int) (nameRect.y + h * 1.1 - fontSize / 4.0 + panTranslate.getY()));

                        // Draw number of stars
                        paintImage(g, (int) (nameRect.x + nameRect.width * (0.35 + (sections-2)*0.2) + SHADOW_OFFSET),
                                nameRect.y + SHADOW_OFFSET, starShadow, (int) h, panTranslate);
                        paintImage(g, (int) (nameRect.x + nameRect.width * (0.35 + (sections-2)*0.2)), nameRect.y, starImg, (int) h, panTranslate);
                        drawStringShadow(g, production, (int) (nameRect.x + nameRect.width - fontSize * 0.75),
                                (int) (nameRect.y + h * 1.1 - fontSize / 4.0));
                        g.setColor(Color.WHITE);
                        g.drawString(production, (int) (nameRect.x + nameRect.width - fontSize * 0.75 + panTranslate.getX()),
                                (int) (nameRect.y + h * 1.1 - fontSize / 4.0 + panTranslate.getY()));

                        // Draw capital sign
                        if (c.isCapital()) {
                            paintImage(g, nameRect.x + SHADOW_OFFSET, nameRect.y + SHADOW_OFFSET, capitalShadow, (int) h, panTranslate);
                            paintImage(g, nameRect.x, nameRect.y, capitalImg, (int) h, panTranslate);
                        }

                        // Draw level
                        h /= 2;
                        int sectionWidth = (int)h;
                        int w = cityCapacity * sectionWidth;
                        Rectangle bgRect = new Rectangle(nameRect.x + nameRect.width / 2 - w / 2, nameRect.y + nameRect.height, w, (int) h);
                        drawRoundRectShadowHighlight(g, bgRect);
                        g.setColor(Color.WHITE);
                        g.fillRoundRect((int) (bgRect.x + panTranslate.getX()), (int) (bgRect.y + panTranslate.getY()),
                                bgRect.width, bgRect.height, ROUND_RECT_ARC, ROUND_RECT_ARC);

                        // Draw population/progress
                        if (progress >= 0) {
                            g.setColor(progressColor);
                        } else {
                            g.setColor(negativeColor);
                        }
                        int pw = Math.abs(progress) * sectionWidth;
                        Rectangle pgRect = new Rectangle(bgRect.x, bgRect.y, pw, bgRect.height);
                        g.fillRoundRect((int) (pgRect.x + panTranslate.getX()), (int) (pgRect.y + +panTranslate.getY()),
                                pgRect.width, pgRect.height, ROUND_RECT_ARC, ROUND_RECT_ARC);

                        // Draw unit counts
                        g.setColor(Color.black);
                        double radius = h / 2.0;
                        double unitHeight = bgRect.y + h / 2 - radius / 2;
                        for (int u = 0; u < units; u++) {
                            g.fillOval((int) (bgRect.x + sectionWidth * u + sectionWidth / 2.0 - radius / 2.0 + panTranslate.getX()),
                                    (int) (unitHeight + panTranslate.getY()), (int) radius, (int) radius);
                        }

                        // Draw section separations
                        for (int l = 0; l < cityCapacity - 1; l++) {
                            int lx = bgRect.x + sectionWidth * (l + 1);
                            g.drawLine((int) (lx + panTranslate.getX()), (int) (bgRect.y + panTranslate.getY()),
                                    (int) (lx + panTranslate.getX()), (int) (bgRect.y + bgRect.height + panTranslate.getY()));
                        }
                    }
                }
            }
        }
    }

    private void drawRoundRectShadowHighlight(Graphics2D g, Rectangle rect) {
        g.setColor(new Color(0, 0, 0, 122));
        g.fillRoundRect((int)(rect.x + SHADOW_OFFSET + panTranslate.getX()),
                (int)(rect.y + SHADOW_OFFSET + panTranslate.getY()), rect.width, rect.height,
                ROUND_RECT_ARC, ROUND_RECT_ARC);
        g.setColor(new Color(255, 255, 255, 122));
        g.fillRoundRect((int)(rect.x - SHADOW_OFFSET + panTranslate.getX()),
                (int)(rect.y - SHADOW_OFFSET + panTranslate.getY()), rect.width, rect.height,
                ROUND_RECT_ARC, ROUND_RECT_ARC);
    }

    private void drawStringShadow (Graphics2D g, String s, int x, int y) {
        g.setColor(new Color(0, 0, 0, 122));
        g.drawString(s, (int)(x+SHADOW_OFFSET + panTranslate.getX()), (int)(y+SHADOW_OFFSET + panTranslate.getY()));
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
    }

    void updatePan(Point2D panTranslate) {
        this.panTranslate = new Point2D.Double(this.panTranslate.getX() + panTranslate.getX(),
                this.panTranslate.getY() + panTranslate.getY());
    }

    void setPanToTribe(GameState gs) {
        // Focus on capital of tribe
        Tribe t = gs.getTribe(gs.getActiveTribeID());
        int capitalID = t.getCapitalID();
        Actor a = gs.getActor(capitalID);
        Vector2d pos = a.getPosition();

        // Get position in screen coordinates, and set pan to the negative difference to center
        Point2D screenPoint = rotatePoint(pos.y, pos.x);
        panTranslate = new Point2D.Double(-screenPoint.getX() - CELL_SIZE/2.0 + dimension.width/2.0,
                -screenPoint.getY() - CELL_SIZE/2.0 + dimension.height/2.0);
    }

    public Point2D getPanTranslate() {
        return panTranslate;
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

        if (t == DEEP_WATER || t == SHALLOW_WATER) {
            // If this is the last tile on the row
            boolean down = (i == gridSize - 1);
            // If the tile above is not water
            boolean top = (i > 0 && board.getTerrainAt(i - 1, j) != SHALLOW_WATER && board.getTerrainAt(i - 1, j) != DEEP_WATER);
            // If first tile on column
            boolean left = (j == 0);
            // If the tile to the right is not water
            boolean right = (j < gridSize - 1 && board.getTerrainAt(i, j + 1) != SHALLOW_WATER && board.getTerrainAt(i, j + 1) != DEEP_WATER);
            if (down) {
                if (left) {
                    toPaint = t.getImage("down-left");
                } else if (right) {
                    if (top) {
                        toPaint = t.getImage("top-down-right");
                    } else {
                        if (t == SHALLOW_WATER && (diagUR == SHALLOW_WATER || diagUR == DEEP_WATER)) {
                            toPaint = t.getImage("down-right-ur");
                        } else {
                            toPaint = t.getImage("down-right");
                        }
                    }
                } else if (top) {
                    if (t == SHALLOW_WATER && (diagUR == SHALLOW_WATER || diagUR == DEEP_WATER)) {
                        toPaint = t.getImage("top-down-ur");
                    } else {
                        toPaint = t.getImage("top-down");
                    }
                } else {
                    toPaint = t.getImage("down");
                }
            } else if (top) {
                if (t == SHALLOW_WATER && (j == gridSize-1 || diagUR == SHALLOW_WATER || diagUR == DEEP_WATER)) {
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
                if (i == 0 || j < gridSize - 1 && t == SHALLOW_WATER && (diagUR == SHALLOW_WATER || diagUR == DEEP_WATER)) {
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
            boolean down = (i == gridSize - 1 || board.getTerrainAt(i+1, j) == SHALLOW_WATER);
            // If the same is true for the column instead, this is a '-left' img
            boolean left = (j == 0 || board.getTerrainAt(i, j-1) == SHALLOW_WATER);

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
                            if (i < gridSize - 1 && (board.getTerrainAt(i, j + 1) == SHALLOW_WATER ||
                                            board.getTerrainAt(i, j + 1) == DEEP_WATER)) {
                                toPaint = t.getImage("down-left-el-dr");
                            } else {
                                toPaint = t.getImage("down-left-el");
                            }
                        } else if (i == gridSize - 1) {
                            if (board.getTerrainAt(i - 1, j) == SHALLOW_WATER
                                    || board.getTerrainAt(i - 1, j) == DEEP_WATER) {
                                toPaint = t.getImage("down-left-ed-ul");
                            } else {
                                toPaint = t.getImage("down-left-ed");
                            }
                        } else {
                            toPaint = t.getImage("down-left");
                        }
                    } else {
                        if (j < gridSize - 1 && i < gridSize - 1 &&
                                (board.getTerrainAt(i, j + 1) == SHALLOW_WATER ||
                                board.getTerrainAt(i, j + 1) == DEEP_WATER)) {
                            toPaint = t.getImage("down-dr");
                        } else {
                            toPaint = t.getImage("down");
                        }
                    }
                }
            } else if (left) {
                if (i == 0 || board.getTerrainAt(i - 1, j) == SHALLOW_WATER ||
                        board.getTerrainAt(i - 1, j) == DEEP_WATER) {
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
                if (i < gridSize-1 && j > 0 && (tld == SHALLOW_WATER || tld == DEEP_WATER) &&
                        (tl != SHALLOW_WATER && tl != DEEP_WATER) &&
                        (td != SHALLOW_WATER && td != DEEP_WATER)) {
                    toPaint = t.getImage("dl");
                } else {
                    toPaint = t.getImage(null);
                }
            }
        }
        return toPaint;
    }
}
