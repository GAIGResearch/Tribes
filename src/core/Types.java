package core;

import utils.ImageIO;
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
        FISH(0, "img/resource/fish.png"),
        FRUIT(1, "img/resource/fruit.png"),
        ANIMAL(2, "img/resource/animal.png"),
        WHALES(3, "img/resource/whale.png"),
        FOREST(4, "img/resource/forest.png"),
        ORE(5, "img/resource/pre.png"),
        CROPS(6, "img/resource/crops.png");

        private int key;
        private String imageFile;
        RESOURCE(int numVal, String imageFile) {  this.key = numVal;  this.imageFile = imageFile;}
        public int getKey() {  return key; }
        public Image getImage() { return ImageIO.GetInstance().getImage(imageFile); }
    }

    /**
     * Types of buildings that can be built by cities
     */
    public enum BUILDING
    {
        PORT (0,"img/building/port.png"),
        MINE (1,"img/building/mine.png"),
        FORGE (2,"img/building/forge.png"),
        FARM (3, "img/building/farm.png"),
        WINDMILL (4,"img/building/windmill.png"),
        ROAD (5,"none.png"),
        CUSTOM_HOUSE (6,"img/building/custom_house.png"),
        LUMBER_HUT(7,"img/building/lumner_hut.png"),
        SAWMILL (8,"img/building/sawmill.png"),
        TEMPLE (9, "img/building/temple.png"),
        WATER_TEMPLE (10,"img/building/temple.png"),
        FOREST_TEMPLE (11,"img/building/temple.png"),
        MOUNTAIN_TEMPLE (12,"img/building/temple.png"),
        ALTAR_OF_PEACE (13,"img/building/monument.png"),
        EMPERORS_TOMB (14,"img/building/monument.png"),
        EYE_OF_GOD (15,"img/building/monument.png"),
        GATE_OF_POWER (16,"img/building/monument.png"),
        GRAND_BAZAR (17,"img/building/monument.png"),
        PARK_OF_FORTUNE (18,"img/building/monument.png"),
        TOWER_OF_WISDOM (19, "img/building/monument.png");

        private int key;
        private String imageFile;
        BUILDING(int numVal, String imageFile) {  this.key = numVal;  this.imageFile = imageFile;}
        public int getKey() {  return key; }
        public Image getImage() { return ImageIO.GetInstance().getImage(imageFile); }
    }


    /**
     * Types of units
     */
    public enum UNIT
    {
        WARRIOR (0,"img/unit/warrior/"),
        RIDER (1,"img/unit/rider/"),
        DEFENDER (2,"img/unit/defender/"),
        SWORDMAN (3,"img/unit/swordman/"),
        ARCHER (4,"img/unit/archer/"),
        CATAPULT (5,"img/unit//"),
        KNIGHT (6,"img/unit/knight/"),
        MIND_BEARER (7,"img/unit/mind_bearer/");

        private int key;
        private String imageFile;
        UNIT(int numVal, String imageFile) {  this.key = numVal;  this.imageFile = imageFile;}
        public int getKey() {  return key; }
        public Image getImage(int playerID) { return ImageIO.GetInstance().getImage(imageFile + playerID + ".png"); }
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
        PLAIN(0, "img/terrain/grass.png"),
        SHALLOW_WATER(1, "img/terrain/shallow_water.jpg"),
        DEEP_WATER(2, "img/terrain/deep_water.jpg"),
        MOUNTAIN(3, "img/terrain/mountain.png"),
        VILLAGE(4, "img/terrain/village.png"),
        CITY(5, "img/terrain/city.png"),
        RUINS(6, "img/terrain/ruins.png");

        private String imageFile;
        private int key;
        TERRAIN(int numVal, String imageFile) {  this.key = numVal;  this.imageFile = imageFile;}
        public int getKey() {  return key; }
        public Image getImage() { return ImageIO.GetInstance().getImage(imageFile); }


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
