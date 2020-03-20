package core;

import core.actors.units.*;
import utils.ImageIO;
import utils.Vector2d;

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

import static core.TribesConfig.*;
import static core.Types.BUILDING.MONUMENT_STATUS.*;
import static core.Types.TECHNOLOGY.*;
import static core.Types.TERRAIN.*;
import static core.Types.UNIT.*;

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
        XIN_XI(0, "Xin-Xi", CLIMBING, WARRIOR),
        IMPERIUS(1, "Imperius", ORGANIZATION, WARRIOR),
        BARDUR(2, "Bardur", HUNTING, WARRIOR),
        OUMAJI(3, "Oumaji", RIDING, RIDER);

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
     * Defines the status of the turn for an  (May be in java?)
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
        FISH(0, "img/resource/fish.png", 'h', FISH_COST, FISH_POP),
        FRUIT(1, "img/resource/fruit.png", 'f', FRUIT_COST, FRUIT_POP),
        ANIMAL(2, "img/resource/animal.png", 'a', ANIMAL_COST, ANIMAL_POP),
        WHALES(3, "img/resource/whale.png", 'w', WHALES_COST, WHALES_STARS),
        ORE(5, "img/resource/ore.png", 'o', 0, 0),
        CROPS(6, "img/resource/crops.png", 'c', 0, 0),
        RUINS(7, "img/resource/ruins.png", 'r', 0, 0);

        private int key;
        private String imageFile;
        private char mapChar;
        private int cost;
        private int bonus;

        RESOURCE(int numVal, String imageFile, char mapChar, int cost, int bonus) {
            this.key = numVal;
            this.imageFile = imageFile;
            this.mapChar = mapChar;
            this.cost = cost;
            this.bonus = bonus;
        }
        public int getKey() {  return key; }
        public Image getImage() { return ImageIO.GetInstance().getImage(imageFile); }
        public int getCost() {return cost;}
        public int getBonus() {return bonus;}

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
        PORT (0,"img/building/port.png", PORT_COST, PORT_BONUS, PORT_POINTS, SAILING, new TERRAIN[]{SHALLOW_WATER}),
        MINE (1,"img/building/mine.png", MINE_COST, MINE_BONUS, MINE_POINTS, MINING, new TERRAIN[]{MOUNTAIN}),
        FORGE (2,"img/building/forge.png", FORGE_COST, FORGE_BONUS, FORGE_POINTS, SMITHERY, new TERRAIN[]{PLAIN}),
        FARM (3, "img/building/farm.png", FARM_COST, FARM_BONUS, FARM_POINTS, FARMING, new TERRAIN[]{PLAIN}),
        WINDMILL (4,"img/building/windmill.png", WIND_MILL_COST, WIND_MILL_BONUS, WIND_MILL_POINTS, CONSTRUCTION, new TERRAIN[]{PLAIN}),
        ROAD (5,"none.png", ROAD_COST, 0, 0, ROADS, new TERRAIN[]{PLAIN, FOREST}),
        CUSTOM_HOUSE (6,"img/building/custom_house.png", CUSTOM_COST, CUSTOM_BONUS, CUSTOM_POINTS, TRADE, new TERRAIN[]{PLAIN}),
        LUMBER_HUT(7,"img/building/lumner_hut.png", LUMBER_HUT_COST, LUMBER_HUT_BONUS, LUMBER_HUT_POINTS, MATHEMATICS, new TERRAIN[]{FOREST}),
        SAWMILL (8,"img/building/sawmill.png", SAW_MILL_COST, SAW_MILL_BONUS, SAW_MILL_POINTS, MATHEMATICS, new TERRAIN[]{PLAIN}),
        TEMPLE (9, "img/building/temple.png", TEMPLE_COST, TEMPLE_BONUS, TEMPLE_POINTS, FREE_SPIRIT, new TERRAIN[]{PLAIN}),
        WATER_TEMPLE (10,"img/building/temple.png", TEMPLE_COST, TEMPLE_BONUS, TEMPLE_POINTS, AQUATISM, new TERRAIN[]{SHALLOW_WATER, DEEP_WATER}),
        FOREST_TEMPLE (11,"img/building/temple.png", TEMPLE_FOREST_COST, TEMPLE_BONUS, TEMPLE_POINTS, SPIRITUALISM, new TERRAIN[]{FOREST}),
        MOUNTAIN_TEMPLE (12,"img/building/temple.png", TEMPLE_COST, TEMPLE_BONUS, TEMPLE_POINTS, MEDITATION, new TERRAIN[]{MOUNTAIN}),
        ALTAR_OF_PEACE (13,"img/building/monument.png", 0, MONUMENT_BONUS, MONUMENT_POINTS, null, new TERRAIN[]{SHALLOW_WATER,PLAIN}),
        EMPERORS_TOMB (14,"img/building/monument.png", 0, MONUMENT_BONUS, MONUMENT_POINTS, TRADE, new TERRAIN[]{SHALLOW_WATER,PLAIN}),
        EYE_OF_GOD (15,"img/building/monument.png", 0, MONUMENT_BONUS, MONUMENT_POINTS, NAVIGATION, new TERRAIN[]{SHALLOW_WATER,PLAIN}),
        GATE_OF_POWER (16,"img/building/monument.png", 0, MONUMENT_BONUS, MONUMENT_POINTS, null, new TERRAIN[]{SHALLOW_WATER,PLAIN}),
        GRAND_BAZAR (17,"img/building/monument.png", 0, MONUMENT_BONUS, MONUMENT_POINTS, ROADS, new TERRAIN[]{SHALLOW_WATER,PLAIN}),
        PARK_OF_FORTUNE (18,"img/building/monument.png", 0, MONUMENT_BONUS, MONUMENT_POINTS, null, new TERRAIN[]{SHALLOW_WATER,PLAIN}),
        TOWER_OF_WISDOM (19, "img/building/monument.png", 0, MONUMENT_BONUS, MONUMENT_POINTS, PHILOSOPHY, new TERRAIN[]{SHALLOW_WATER,PLAIN});

        public enum MONUMENT_STATUS {
            UNAVAILABLE,
            AVAILABLE,
            BUILT;
        }

        private int key;
        private String imageFile;
        private TECHNOLOGY technologyRequirement;
        private TERRAIN[] terrainRequirements;
        private int cost;
        private int bonus;
        private int points;
        BUILDING(int numVal, String imageFile, int cost, int bonus, int points, TECHNOLOGY technologyRequirement, TERRAIN[] terrainRequirements)
        {
            this.key = numVal;
            this.cost = cost;
            this.bonus = bonus;
            this.points = points;
            this.imageFile = imageFile;
            this.technologyRequirement = technologyRequirement;
            this.terrainRequirements = terrainRequirements;
        }
        public TECHNOLOGY getTechnologyRequirement() { return technologyRequirement; }
        public TERRAIN[] getTerrainRequirements() { return terrainRequirements; }
        public int getKey() {  return key; }
        public int getCost() {return cost; }
        public int getBonus() {return bonus; }
        public int getPoints() {return points; }
        public Image getImage() { return ImageIO.GetInstance().getImage(imageFile); }


        public static HashMap<BUILDING, MONUMENT_STATUS> initMonuments()
        {
            HashMap<BUILDING, MONUMENT_STATUS> monuments = new HashMap<>();
            monuments.put(ALTAR_OF_PEACE, UNAVAILABLE);
            monuments.put(EMPERORS_TOMB, UNAVAILABLE);
            monuments.put(EYE_OF_GOD, UNAVAILABLE);
            monuments.put(GATE_OF_POWER, UNAVAILABLE);
            monuments.put(PARK_OF_FORTUNE, UNAVAILABLE);
            monuments.put(TOWER_OF_WISDOM, UNAVAILABLE);
            return monuments;
        }
    }

    public enum EXAMINE_BONUS
    {
        SUPERUNIT(0,0),
        RESEARCH(1,0),
        POP_GROWTH(2,3),
        EXPLORER(3,0),
        RESOURCES(4,10);

        private int bonus, key;
        EXAMINE_BONUS(int key, int bonus) {this.key = key; this.bonus = bonus;}
        public int getBonus() { return bonus;  }
        public int getKey() {return key;}

        public static EXAMINE_BONUS random(Random r)
        {
            EXAMINE_BONUS[] bonuses = EXAMINE_BONUS.values();
            return bonuses[r.nextInt(bonuses.length)];
        }
    }

    /**
     * Types of buildings that can be built by cities
     */
    public enum CITY_LEVEL_UP
    {
        WORKSHOP(2),
        EXPLORER(2),
        CITY_WALL(3),
        RESOURCES(3),
        POP_GROWTH(4),
        BORDER_GROWTH(4),
        PARK(5),
        SUPERUNIT(5);

        private int level;

        CITY_LEVEL_UP(int level) {
            this.level = level;
        }

        public int getLevel() { return level; }

        public static LinkedList<CITY_LEVEL_UP> getActions (int curLevel)
        {
            LinkedList<CITY_LEVEL_UP> actions = new LinkedList<>();
            switch (curLevel)
            {
                case 1:
                    actions.add(WORKSHOP);
                    actions.add(EXPLORER);
                    break;
                case 2:
                    actions.add(CITY_WALL);
                    actions.add(RESOURCES);
                    break;

                case 3:
                    actions.add(POP_GROWTH);
                    actions.add(BORDER_GROWTH);
                    break;

                default:
                    actions.add(PARK);
                    actions.add(SUPERUNIT);
                    break;

            }
            return actions;
        }

        public boolean validType(int cityLevel)
        {
            if(cityLevel == 1 && (this == WORKSHOP || this == EXPLORER)) return true;
            if(cityLevel == 2 && (this == CITY_WALL || this == RESOURCES)) return true;
            if(cityLevel == 3 && (this == POP_GROWTH || this == BORDER_GROWTH)) return true;
            if(cityLevel >= 4 && (this == PARK || this == SUPERUNIT)) return true;
            return false;
        }

        public int getLevelUpPoints(){
            //TODO: What happens when level > 10? Negative points? Unlikely!
            if (level == 1){
                return 100;
            }
            return 50 - level * 5;
        }
    }


    /**
     * Types of actors
     */
    public enum UNIT
    {
        WARRIOR (0,"img/unit/warrior/", WARRIOR_COST, null, WARRIOR_POINTS), //+10
        RIDER (1,"img/unit/rider/", RIDER_COST, RIDING, RIDER_POINTS), //+15
        DEFENDER (2,"img/unit/defender/", DEFENDER_COST, SHIELDS, DEFENDER_POINTS), // +15
        SWORDMAN (3,"img/unit/swordman/", SWORDMAN_COST, SMITHERY, SWORDMAN_POINTS), //+25
        ARCHER (4,"img/unit/archer/", ARCHER_COST, ARCHERY, ARCHER_POINTS),//+15
        CATAPULT (5,"img/unit/", CATAPULT_COST, MATHEMATICS, CATAPULT_POINTS), //+40
        KNIGHT (6,"img/unit/knight/", KNIGHT_COST, CHIVALRY, KNIGHT_POINTS), //+40
        MIND_BENDER(7,"img/unit/mind_bender/", MINDBENDER_COST, PHILOSOPHY, MINDBENDER_POINTS), //+25
        BOAT(8,"img/unit/boat/", BOAT_COST, SAILING, BOAT_POINTS), //+0
        SHIP(9,"img/unit/ship/", BATTLESHIP_COST, SAILING, SHIP_POINTS),//+0
        BATTLESHIP(10,"img/unit/battleship/", BATTLESHIP_COST, NAVIGATION, BATTLESHIP_POINTS),//+0
        SUPERUNIT(11, "img/unit/superunit/", SUPERUNIT_COST, null, SUPERUNIT_POINTS); //+50

        private int key;
        private String imageFile;
        private int cost;
        private TECHNOLOGY requirement;
        private int points;
        UNIT(int numVal, String imageFile, int cost, Types.TECHNOLOGY requirement, int points) {
            this.key = numVal;
            this.imageFile = imageFile;
            this.cost = cost;
            this.requirement = requirement;
            this.points = points;
        }
        public int getKey() {  return key; }
        public Image getImage(int playerID) { return ImageIO.GetInstance().getImage(imageFile + playerID + ".png"); }
        public int getCost() {
            return cost;
        }
        public TECHNOLOGY getRequirement() {
            return requirement;
        }
        public int getPoints() { return points; }

        public static Unit createUnit (Vector2d pos, int kills, boolean isVeteran, int ownerID, int tribeID, UNIT type)
        {
            switch (type)
            {
                case WARRIOR: return new Warrior(pos, kills, isVeteran, ownerID, tribeID);
                case RIDER: return new Rider(pos, kills, isVeteran, ownerID, tribeID);
                case DEFENDER: return new Defender(pos, kills, isVeteran, ownerID, tribeID);
                case SWORDMAN: return new Swordman(pos, kills, isVeteran, ownerID, tribeID);
                case ARCHER: return new Archer(pos, kills, isVeteran, ownerID, tribeID);
                case CATAPULT: return new Catapult(pos, kills, isVeteran, ownerID, tribeID);
                case KNIGHT: return new Knight(pos, kills, isVeteran, ownerID, tribeID);
                case MIND_BENDER: return new MindBender(pos, kills, isVeteran, ownerID, tribeID);
                case BOAT: return new Boat(pos, kills, isVeteran, ownerID, tribeID);
                case SHIP: return new Ship(pos, kills, isVeteran, ownerID, tribeID);
                case BATTLESHIP: return new Battleship(pos, kills, isVeteran, ownerID, tribeID);
                case SUPERUNIT: return new SuperUnit(pos, kills, isVeteran, ownerID, tribeID);

                default:
                    System.out.println("WARNING: TypescreateUnit(), type creation not implemented.");
            }
            return null;
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
