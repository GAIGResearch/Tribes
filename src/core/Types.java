package core;

import utils.Vector2d;

import java.awt.*;

public class Types {

    /**
     * Defines the status of the turn for an unit. (May be in Unit.java?)
     */
    public enum TURN_STATUS {
        FRESH,
        MOVED,
        ATTACKED,
        MOVED_AND_ATTACKED,
        FINISHED
    }

    /**
     * Enum for resources. May need to be merged with TILES or somehow put in common
     */
    public enum RESOURCE
    {
        FISH,
        FRUIT,
        ANIMAL,
        WHALES,
        FOREST
    }

    /**
     * Types of buildings that can be built by cities
     */
    public enum BUILDING
    {
        PORT,
        MINE,
        FORGE,
        FARM,
        WINDMILL,
        ROAD,
        CUSTOM_HOUSE,
        SAWMILL,
        TEMPLE,
        WATER_TEMPLE,
        FOREST_TEMPLE,
        MOUNTAIN_TEMPLE,
        ALTAR_OF_PEACE,
        EMPERORS_TOMB,
        EYE_OF_GOD,
        GATE_OF_POWER,
        GRAND_BAZAR,
        PARK_OF_FORTUNE,
        TOWER_OF_WISDOM
    }


    /**
     * Types of units
     */
    public enum UNIT
    {
        WARRIOR,
        RIDER,
        DEFENDER,
        SWORDMAN,
        ARCHER,
        CATAPULT,
        KNIGHT,
        MIND_BEARER
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
        INCOMPLETE(2);

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
            return null;
        }
    }

    /**
     * Different TERRAIN allowed in the game.
     * If more types are added, check methods in this enum to add them where they corresponds
     * (example: if new power-up is added, include it in getPowerUpTypes() so the board generator
     *  can place them in the game).
     */
    public enum TERRAIN {

        //Types and IDs
        PLAIN(0),
        SHALLOW_WATER(1),
        DEEP_WATER(2),
        MOUNTAIN(3),
        VILLAGE(4),
        CITY(5),
        RUINS(6);

        private int key;
        TERRAIN(int numVal) {  this.key = numVal;  }
        public int getKey() {  return key; }

        /**
         * Sprites (Image objects) to use in the game for the different elements.
         * @return the image to use
         */

        public Image getImage()
        {
            if      (key == PLAIN.key)          return null /*Image for GUI */;
            else if (key == SHALLOW_WATER.key)  return null /*Image for GUI */;
            else if (key == DEEP_WATER.key)     return null /*Image for GUI */;
            else if (key == MOUNTAIN.key)       return null /*Image for GUI */;
            else if (key == VILLAGE.key)        return null /*Image for GUI */;
            else if (key == CITY.key)           return null /*Image for GUI */;
            else if (key == RUINS.key)          return null /*Image for GUI */;
            // ... add more

            else return null;
        }

        /**
         * Checks if two boards (arrays of tiletypes) are the same
         * @param board1 one board to check
         * @param board2 the other board to check
         * @return true if they're equals.
         */
        public static boolean boardEquals(TERRAIN[][] board1, TERRAIN[][] board2) {

            if( (board1.length != board2.length) || (board1[0].length != board2[0].length))
                return false;

            for (int i = 0; i < board1.length; i++) {
                for (int i1 = 0; i1 < board1[i].length; i1++) {
                    TERRAIN b1i = board1[i][i1];
                    TERRAIN b2i = board2[i][i1];
                    if (b1i != null && b2i != null && b1i != b2i)
                        return false;
                }
            }
            return true;
        }
    }

}
