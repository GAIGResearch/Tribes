package core;

import core.actions.Action;
import core.actions.unitactions.*;
import core.actors.units.*;
import core.game.GameState;
import utils.ImageIO;
import utils.Vector2d;

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

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
        XIN_XI(0, "Xin-Xi", TECHNOLOGY.CLIMBING, UNIT.WARRIOR,
                new Color(251, 2, 7), new Color(253, 130, 123), new Color(174, 66, 48)),
        IMPERIUS(1, "Imperius", TECHNOLOGY.ORGANIZATION, UNIT.WARRIOR,
                new Color(0, 0, 255), new Color(102, 125, 255), new Color(50, 73, 177)),
        BARDUR(2, "Bardur", TECHNOLOGY.HUNTING, UNIT.WARRIOR,
                new Color(76, 76, 76), new Color(176, 178, 178), new Color(70, 58, 58)),
        OUMAJI(3, "Oumaji", TECHNOLOGY.RIDING, UNIT.RIDER,
                new Color(255, 255, 10), new Color(242, 255, 100), new Color(146, 144, 0));

        private int key;
        private String name;
        private TECHNOLOGY initialTech;
        private UNIT startingUnit;
        private Color color, color_light, color_dark;
        TRIBE(int numVal, String name, TECHNOLOGY initialTech, UNIT startingUnit, Color color, Color color_light, Color color_dark) {
            this.key = numVal;  this.name = name; this.initialTech = initialTech; this.startingUnit = startingUnit;
            this.color = color;
            this.color_light = color_light;
            this.color_dark = color_dark;
        }
        public int getKey() {  return key; }
        public String getName() { return name; }
        public TECHNOLOGY getInitialTech() {
            return initialTech;
        }
        public UNIT getStartingUnit() {return startingUnit;}
        public Color getColor() {return color;}
        public Color getColorLight() {return color_light;}
        public Color getColorDark() {return color_dark;}
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
        FISH(0, "img/resource/fish.png", 'h', TribesConfig.FISH_COST, TribesConfig.FISH_POP),
        FRUIT(1, "img/resource/fruit.png", 'f', TribesConfig.FRUIT_COST, TribesConfig.FRUIT_POP),
        ANIMAL(2, "img/resource/animal.png", 'a', TribesConfig.ANIMAL_COST, TribesConfig.ANIMAL_POP),
        WHALES(3, "img/resource/whale.png", 'w', TribesConfig.WHALES_COST, TribesConfig.WHALES_STARS),
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
        PORT (0,"img/building/port.png", TribesConfig.PORT_COST, TECHNOLOGY.SAILING, new TERRAIN[]{TERRAIN.SHALLOW_WATER}),
        MINE (1,"img/building/mine.png", TribesConfig.MINE_COST, TECHNOLOGY.MINING, new TERRAIN[]{TERRAIN.MOUNTAIN}),
        FORGE (2,"img/building/forge.png", TribesConfig.FORGE_COST, TECHNOLOGY.SMITHERY, new TERRAIN[]{TERRAIN.PLAIN}),
        FARM (3, "img/building/farm.png", TribesConfig.FARM_COST, TECHNOLOGY.FARMING, new TERRAIN[]{TERRAIN.PLAIN}),
        WINDMILL (4,"img/building/windmill.png", TribesConfig.WIND_MILL_COST, TECHNOLOGY.CONSTRUCTION, new TERRAIN[]{TERRAIN.PLAIN}),
        ROAD (5,"none.png", TribesConfig.ROAD_COST, TECHNOLOGY.ROADS, new TERRAIN[]{TERRAIN.PLAIN, TERRAIN.FOREST}),
        CUSTOM_HOUSE (6,"img/building/custom_house.png", TribesConfig.CUSTOM_COST, TECHNOLOGY.TRADE, new TERRAIN[]{TERRAIN.PLAIN}),
        LUMBER_HUT(7,"img/building/lumner_hut.png", TribesConfig.LUMBER_HUT_COST, TECHNOLOGY.MATHEMATICS, new TERRAIN[]{TERRAIN.FOREST}),
        SAWMILL (8,"img/building/sawmill.png", TribesConfig.SAW_MILL_COST, TECHNOLOGY.MATHEMATICS, new TERRAIN[]{TERRAIN.PLAIN}),
        TEMPLE (9, "img/building/temple.png", TribesConfig.TEMPLE_COST, TECHNOLOGY.FREE_SPIRIT, new TERRAIN[]{TERRAIN.PLAIN}),
        WATER_TEMPLE (10,"img/building/temple.png", TribesConfig.TEMPLE_COST, TECHNOLOGY.AQUATISM, new TERRAIN[]{TERRAIN.SHALLOW_WATER, TERRAIN.DEEP_WATER}),
        FOREST_TEMPLE (11,"img/building/temple.png", TribesConfig.TEMPLE_FOREST_COST, TECHNOLOGY.SPIRITUALISM, new TERRAIN[]{TERRAIN.FOREST}),
        MOUNTAIN_TEMPLE (12,"img/building/temple.png", TribesConfig.TEMPLE_COST, TECHNOLOGY.MEDITATION, new TERRAIN[]{TERRAIN.MOUNTAIN}),
        ALTAR_OF_PEACE (13,"img/building/monument.png", 0,null, new TERRAIN[]{TERRAIN.SHALLOW_WATER,TERRAIN.PLAIN}),
        EMPERORS_TOMB (14,"img/building/monument.png", 0, TECHNOLOGY.TRADE, new TERRAIN[]{TERRAIN.SHALLOW_WATER,TERRAIN.PLAIN}),
        EYE_OF_GOD (15,"img/building/monument.png", 0, TECHNOLOGY.NAVIGATION, new TERRAIN[]{TERRAIN.SHALLOW_WATER,TERRAIN.PLAIN}),
        GATE_OF_POWER (16,"img/building/monument.png", 0, null, new TERRAIN[]{TERRAIN.SHALLOW_WATER,TERRAIN.PLAIN}),
        GRAND_BAZAR (17,"img/building/monument.png", 0, TECHNOLOGY.ROADS, new TERRAIN[]{TERRAIN.SHALLOW_WATER,TERRAIN.PLAIN}),
        PARK_OF_FORTUNE (18,"img/building/monument.png", 0, null, new TERRAIN[]{TERRAIN.SHALLOW_WATER,TERRAIN.PLAIN}),
        TOWER_OF_WISDOM (19, "img/building/monument.png", 0, TECHNOLOGY.PHILOSOPHY, new TERRAIN[]{TERRAIN.SHALLOW_WATER,TERRAIN.PLAIN});

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
        BUILDING(int numVal, String imageFile, int cost, TECHNOLOGY technologyRequirement, TERRAIN[] terrainRequirements)
        {
            this.key = numVal;
            this.cost = cost;
            this.imageFile = imageFile;
            this.technologyRequirement = technologyRequirement;
            this.terrainRequirements = terrainRequirements;
        }
        public TECHNOLOGY getTechnologyRequirement() { return technologyRequirement; }
        public TERRAIN[] getTerrainRequirements() { return terrainRequirements; }
        public int getKey() {  return key; }
        public int getCost() {return cost; }
        public Image getImage() { return ImageIO.GetInstance().getImage(imageFile); }


        public static HashMap<BUILDING, MONUMENT_STATUS> initMonuments()
        {
            HashMap<BUILDING, MONUMENT_STATUS> monuments = new HashMap<>();
            monuments.put(ALTAR_OF_PEACE, MONUMENT_STATUS.UNAVAILABLE);
            monuments.put(EMPERORS_TOMB, MONUMENT_STATUS.UNAVAILABLE);
            monuments.put(EYE_OF_GOD, MONUMENT_STATUS.UNAVAILABLE);
            monuments.put(GATE_OF_POWER, MONUMENT_STATUS.UNAVAILABLE);
            monuments.put(PARK_OF_FORTUNE, MONUMENT_STATUS.UNAVAILABLE);
            monuments.put(TOWER_OF_WISDOM, MONUMENT_STATUS.UNAVAILABLE);
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
        WARRIOR (0,"img/unit/warrior/", TribesConfig.WARRIOR_COST, null, TribesConfig.WARRIOR_POINTS), //+10
        RIDER (1,"img/unit/rider/", TribesConfig.RIDER_COST, TECHNOLOGY.RIDING, TribesConfig.RIDER_POINTS), //+15
        DEFENDER (2,"img/unit/defender/", TribesConfig.DEFENDER_COST, TECHNOLOGY.SHIELDS, TribesConfig.DEFENDER_POINTS), // +15
        SWORDMAN (3,"img/unit/swordman/", TribesConfig.SWORDMAN_COST, TECHNOLOGY.SMITHERY, TribesConfig.SWORDMAN_POINTS), //+25
        ARCHER (4,"img/unit/archer/", TribesConfig.ARCHER_COST, TECHNOLOGY.ARCHERY, TribesConfig.ARCHER_POINTS),//+15
        CATAPULT (5,"img/unit/", TribesConfig.CATAPULT_COST, TECHNOLOGY.MATHEMATICS, TribesConfig.CATAPULT_POINTS), //+40
        KNIGHT (6,"img/unit/knight/", TribesConfig.KNIGHT_COST, TECHNOLOGY.CHIVALRY, TribesConfig.KNIGHT_POINTS), //+40
        MIND_BENDER(7,"img/unit/mind_bender/", TribesConfig.MINDBENDER_COST, TECHNOLOGY.PHILOSOPHY, TribesConfig.MINDBENDER_POINTS), //+25
        BOAT(8,"img/unit/boat/", TribesConfig.BOAT_COST, TECHNOLOGY.SAILING, TribesConfig.BOAT_POINTS), //+0
        SHIP(9,"img/unit/ship/", TribesConfig.BATTLESHIP_COST, TECHNOLOGY.SAILING, TribesConfig.SHIP_POINTS),//+0
        BATTLESHIP(10,"img/unit/battleship/", TribesConfig.BATTLESHIP_COST, TECHNOLOGY.NAVIGATION, TribesConfig.BATTLESHIP_POINTS),//+0
        SUPERUNIT(11, "img/unit/superunit/", TribesConfig.SUPERUNIT_COST, null, TribesConfig.SUPERUNIT_POINTS); //+50

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
        public String getImageStr(int playerID) { return imageFile + playerID + ".png"; }
        public String getImageFile() { return imageFile; }
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
                    System.out.println("WARNING: Types.Unit.createUnit(), type creation not implemented.");
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
        PLAIN(0, "img/terrain/plain.png", '.'),
        SHALLOW_WATER(1, "img/terrain/water.png", 's'),
        DEEP_WATER(2, "img/terrain/deepwater.png", 'd'),
        MOUNTAIN(3, "img/terrain/mountain3.png", 'm'),
        VILLAGE(4, "img/terrain/village2.png", 'v'),
        CITY(5, "img/terrain/city.png", 'c'),
        FOREST(6, "img/terrain/forest2.png", 'f');

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
        public Image getImage(String suffix) {
            if (suffix == null || suffix.equals("")) {
                return ImageIO.GetInstance().getImage(imageFile);
            }
            String[] splitPath = imageFile.split("\\.");
            return ImageIO.GetInstance().getImage(splitPath[0] + "-" + suffix + "." + splitPath[1]);
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

    public enum ACTION {
        MOVE("img/actions/move.png"),
        ATTACK("img/actions/attack.png"),
        CAPTURE("img/actions/capture.png"),
        DISBAND("img/actions/disband.png"),
        HEAL("img/actions/heal.png"),
        UPGRADE("img/actions/upgrade.png");

        private String imgPath;

        ACTION(String imgPath) {
            this.imgPath = imgPath;
        }

        public static Image getImage(Action a) {
            if (a instanceof Move) {
                return ImageIO.GetInstance().getImage(MOVE.imgPath);
            } else if (a instanceof Attack) {
                return ImageIO.GetInstance().getImage(ATTACK.imgPath);
            } else if (a instanceof Capture || a instanceof Convert) {
                return ImageIO.GetInstance().getImage(CAPTURE.imgPath);
            } else if (a instanceof Disband) {
                return ImageIO.GetInstance().getImage(DISBAND.imgPath);
            } else if (a instanceof Recover) {
                return ImageIO.GetInstance().getImage(HEAL.imgPath);
            } else if (a instanceof Upgrade) {
                return ImageIO.GetInstance().getImage(UPGRADE.imgPath);
            }
            return null;
        }
    }

    public static Vector2d getActionPosition(GameState gs, Action a) {
        Vector2d pos = null;
        if (a instanceof Move) {
            pos = new Vector2d(((Move) a).getDestination().x, ((Move) a).getDestination().y);
        } else if (a instanceof Attack) {
            Unit target = (Unit) gs.getActor(((Attack) a).getTargetId());
            pos = target.getPosition();
        } else if (a instanceof Capture || a instanceof Convert || a instanceof Disband || a instanceof Recover ||
                a instanceof Upgrade) {
            Unit u = (Unit) gs.getActor(((UnitAction) a).getUnitId());
            pos = u.getPosition();
        }
        return pos;
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
