package core;

import utils.Vector2d;

import java.awt.*;
import java.util.ArrayList;

public class Types {


    /**
     * Defines all actions in the game.
     */
    public enum ACTIONS {
        ACTION_STOP(0),
        ACTION_UP(1),
        ACTION_DOWN(2),
        ACTION_LEFT(3),
        ACTION_RIGHT(4),
        ACTION_USE(5);

        private int key;
        ACTIONS(int numVal) {  this.key = numVal; }
        public int getKey() {return this.key;}

        /**
         * Gets all actions of the game
         * @return all the actions in an array list.
         */
        public static ArrayList<ACTIONS> all()
        {
            ArrayList<ACTIONS> allActions = new ArrayList<ACTIONS>();
            allActions.add(ACTION_STOP);
            allActions.add(ACTION_UP);
            allActions.add(ACTION_DOWN);
            allActions.add(ACTION_LEFT);
            allActions.add(ACTION_RIGHT);
            allActions.add(ACTION_USE);
            return allActions;
        }

        /**
         * For directional actions, returns the corresponding direction.
         * @return the direction that represents the movement action. NONE if this is not a movement action.
         */
        public DIRECTIONS getDirection()
        {
            if(this == ACTION_UP)
                return DIRECTIONS.UP;
            else if(this == ACTION_DOWN)
                return DIRECTIONS.DOWN;
            else if(this == ACTION_LEFT)
                return DIRECTIONS.LEFT;
            else if(this == ACTION_RIGHT)
                return DIRECTIONS.RIGHT;
            else
                return DIRECTIONS.NONE;
        }
    }

    /**
     * Defines the directions that game objects can have for movement.
     */
    public enum DIRECTIONS {
        NONE(0, 0),
        LEFT(-1, 0),
        RIGHT(1, 0),
        UP(0, -1),
        DOWN(0, 1);

        private int x, y;

        DIRECTIONS(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Vector2d toVec() {
            return new Vector2d(x, y);
        }

        public int x() {return x;}
        public int y() {return y;}
    }


    /**
     * Results of the game.
     */
    public enum RESULT {
        WIN(0),
        LOSS(1),
        TIE(2),
        INCOMPLETE(3);

        private int key;
        RESULT(int numVal) { this.key = numVal; }
        public int getKey() { return this.key; }

        /**
         * Returns the colour that represents such victory condition for the GUI.
         * @return colours of results.
         */
        public Color getColor() {
            if (key == WIN.key) return Color.green;
            if (key == LOSS.key) return Color.red;
            if (key == TIE.key) return Color.orange;
            return null;
        }
    }

    /**
     * Different TILETYPES allowed in the game.
     * If more types are added, check methods in this enum to add them where they corresponds
     * (example: if new power-up is added, include it in getPowerUpTypes() so the board generator
     *  can place them in the game).
     */
    public enum TILETYPE {

        //Types and IDs
        GRASS(0),
        WATER(1);

        //... add more

        private int key;
        TILETYPE(int numVal) {  this.key = numVal;  }
        public int getKey() {  return key; }

        /**
         * Sprites (Image objects) to use in the game for the different elements.
         * @return the image to use
         */

        public Image getImage()
        {
            if      (key == GRASS.key) return null /*Image for GUI */;
            else if (key == WATER.key) return null /*Image for GUI */;

            // ... add more

            else return null;
        }

        /**
         * Checks if two boards (arrays of tiletypes) are the same
         * @param board1 one board to check
         * @param board2 the other board to check
         * @return true if they're equals.
         */
        public static boolean boardEquals(TILETYPE[][] board1, TILETYPE[][] board2) {

            if( (board1.length != board2.length) || (board1[0].length != board2[0].length))
                return false;

            for (int i = 0; i < board1.length; i++) {
                for (int i1 = 0; i1 < board1[i].length; i1++) {
                    TILETYPE b1i = board1[i][i1];
                    TILETYPE b2i = board2[i][i1];
                    if (b1i != null && b2i != null && b1i != b2i)
                        return false;
                }
            }
            return true;
        }
    }

}
