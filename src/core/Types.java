package core;

import core.actors.units.*;
import utils.ImageIO;
import utils.Vector2d;

import java.awt.*;

public class Types {

    public enum TECHNOLOGY {
        CLIMBING(5),
        FISHING(5),
        HUNTING(5),
        ORGANIZATION(5),
        RIDING(5),
        ARCHERY(6),
        FARMING(6),
        FORESTRY(6),
        FREE_SPIRIT(6),
        MEDITATION(6),
        MINING(6),
        ROADS(6),
        SAILING(6),
        SHIELDS(6),
        WHALING(6),
        AQUATISM(7),
        CHIVALRY(7),
        CONSTRUCTION(7),
        MATHEMATICS(7),
        NAVIGATION(7),
        SMITHERY(7),
        SPIRITUALISM(7),
        TRADE(7),
        PHILOSOPHY(7);

        private int baseCost;

        TECHNOLOGY(int baseCost) {
            this.baseCost = baseCost;
        }

        public int getCost(int numOfCities) {
            return baseCost * numOfCities;
        }

    }

    public enum TRIBE{
        XIN_XI(0, "Xin-Xi", TECHNOLOGY.CLIMBING, UNIT.WARRIOR),
        IMPERIUS(1, "Imperius", TECHNOLOGY.ORGANIZATION, UNIT.WARRIOR),
        BARDUR(2, "Bardur", TECHNOLOGY.HUNTING, UNIT.WARRIOR),
        OUMAJI(3, "Oumaji", TECHNOLOGY.RIDING, UNIT.RIDER);

        private int key;
        private String name;
        private TECHNOLOGY initialTech;
        private UNIT startingUnit;
        TRIBE(int numVal, String name, TECHNOLOGY initialTech, UNIT startingUnit) {
            this.key = numVal;  this.name = name; this.initialTech = initialTech; this.startingUnit = startingUnit;
        }
        public int getKey() {  return key; }
        public String getName() { return name; }
        public TECHNOLOGY getInitialTech() {
            return initialTech;
        }
        public UNIT getStartingUnit() {return startingUnit;}
    }

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
        FISH(0, "img/resource/fish.png", 'h'),
        FRUIT(1, "img/resource/fruit.png", 'f'),
        ANIMAL(2, "img/resource/animal.png", 'a'),
        WHALES(3, "img/resource/whale.png", 'w'),
        ORE(5, "img/resource/ore.png", 'o'),
        CROPS(6, "img/resource/crops.png", 'c'),
        RUINS(7, "img/resource/ruins.png", 'r');

        private int key;
        private String imageFile;
        private char mapChar;
        RESOURCE(int numVal, String imageFile, char mapChar) {  this.key = numVal;  this.imageFile = imageFile; this.mapChar = mapChar;}
        public int getKey() {  return key; }
        public Image getImage() { return ImageIO.GetInstance().getImage(imageFile); }

        public static RESOURCE getType(char resourceChar) {
            for(RESOURCE r : Types.RESOURCE.values()){
                if(r.mapChar == resourceChar)
                    return r;
            }
            return null;
        }
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
     * Types of actors
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
        MIND_BEARER (7,"img/unit/mind_bearer/"),
        BOAT(8,"img/unit/boat/"),
        SHIP(9,"img/unit/ship/"),
        BATTLESHIP(10,"img/unit/battleship/");


        private int key;
        private String imageFile;
        UNIT(int numVal, String imageFile) {  this.key = numVal;  this.imageFile = imageFile;}
        public int getKey() {  return key; }
        public Image getImage(int playerID) { return ImageIO.GetInstance().getImage(imageFile + playerID + ".png"); }

        public Unit createUnit (Vector2d pos, int kills, boolean isVeteran, int ownerID, int tribeID, UNIT type)
        {
            switch (type)
            {
                case WARRIOR: return new Warrior(pos, kills, isVeteran, ownerID, tribeID);
                case RIDER: return new Rider(pos, kills, isVeteran, ownerID, tribeID);
                default:
                    System.out.println("WARNING: Types.Unit.createUnit(), type creation not implemented.");
            }
            return null;
        }


        /*
         * Tribes colours as used in the unit scripts
        0 -
Red - FB0207
Red_light - FD827B
Red_dark - ae4230

1 -
Bule - 0000FF
Blue_light - 667DFF
Blue_dark - 3249b1

2 -
Grey - 4C4C4C
Grey_light - B0B2B2
Grey_dark - 463a3a

3 -
Yellow - FFFF0A
Yellow_light - F2FF64
Yellow_dark - 929000
         */

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
        PLAIN(0, "img/terrain/grass.png", '.'),
        SHALLOW_WATER(1, "img/terrain/shallow_water.jpg", 's'),
        DEEP_WATER(2, "img/terrain/deep_water.jpg", 'd'),
        MOUNTAIN(3, "img/terrain/mountain.png", 'm'),
        VILLAGE(4, "img/terrain/village.png", 'v'),
        CITY(5, "img/terrain/city.png", 'c'),
        FOREST(6, "img/terrain/forest.png", 'f');

        private String imageFile;
        private int key;
        private char mapChar;
        TERRAIN(int numVal, String imageFile, char mapChar) {  this.key = numVal;  this.imageFile = imageFile; this.mapChar = mapChar; }

        public static TERRAIN getType(char terrainChar) {
            for(TERRAIN t : Types.TERRAIN.values()){
                if(t.mapChar == terrainChar)
                    return t;
            }
            return null;
        }

        public int getKey() {  return key; }
        public char getMapChar() {return mapChar;}
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
