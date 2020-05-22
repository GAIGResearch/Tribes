package core;

import core.actions.Action;
import core.actions.unitactions.*;
import core.actors.units.*;
import org.json.JSONObject;
import utils.ImageIO;
import utils.Vector2d;

import java.awt.*;
import java.util.*;

import static core.Types.BUILDING.MONUMENT_STATUS.*;
import static core.Types.TECHNOLOGY.*;
import static core.Types.TERRAIN.*;
import static core.Types.UNIT.*;

public class Types {

    public enum TECHNOLOGY {
        CLIMBING(1, null),
        FISHING(1, null),
        HUNTING(1, null),
        ORGANIZATION(1, null),
        RIDING(1, null),
        ARCHERY(2, HUNTING),
        FARMING(2, ORGANIZATION),
        FORESTRY(2, HUNTING),
        FREE_SPIRIT(2, RIDING),
        MEDITATION(2, CLIMBING),
        MINING(2, CLIMBING),
        ROADS(2, RIDING),
        SAILING(2, FISHING),
        SHIELDS(2, ORGANIZATION),
        WHALING(2, FISHING),
        AQUATISM(3, WHALING),
        CHIVALRY(3, FREE_SPIRIT),
        CONSTRUCTION(3, FARMING),
        MATHEMATICS(3, FORESTRY),
        NAVIGATION(3, SAILING),
        SMITHERY(3, MINING),
        SPIRITUALISM(3, ARCHERY),
        TRADE(3, ROADS),
        PHILOSOPHY(3, MEDITATION);

        private int tier;
        private TECHNOLOGY parent;
        private ArrayList<TECHNOLOGY> children;
        TribesConfig tc;

        TECHNOLOGY(int tier, TECHNOLOGY parent) {
            this.tier = tier; this.parent = parent;
            tc = new TribesConfig();
        }

        public TECHNOLOGY getParentTech() {return this.parent;}
        public ArrayList<TECHNOLOGY> getChildTech() {
            if (children == null) {
                ArrayList<TECHNOLOGY> c = new ArrayList<>();
                for (TECHNOLOGY t : TECHNOLOGY.values()) {
                    if (t.getParentTech() == this) {
                        c.add(t);
                    }
                }
                children = c;
            }
            return children;
        }

        public int getCost(int numOfCities, TechnologyTree tt) {
            int cost = tc.TECH_BASE_COST + this.tier * numOfCities;
            if(tt.isResearched(tc.TECH_DISCOUNT))
            {
                double disc_cost = cost * tc.TECH_DISCOUNT_VALUE;
                cost = (int)disc_cost;
            }
            return cost;
        }

    }

    public enum TRIBE{
        XIN_XI(0, "Xin-Xi", CLIMBING, WARRIOR, 515,
                new Color(251, 2, 7), new Color(253, 130, 123), new Color(174, 66, 48)),
        IMPERIUS(1, "Imperius", ORGANIZATION, WARRIOR, 515,
                new Color(0, 0, 255), new Color(102, 125, 255), new Color(50, 73, 177)),
        BARDUR(2, "Bardur", HUNTING, WARRIOR, 515,
                new Color(76, 76, 76), new Color(176, 178, 178), new Color(70, 58, 58)),
        OUMAJI(3, "Oumaji", RIDING, RIDER, 520, //yes, 520.
                new Color(255, 255, 10), new Color(242, 255, 100), new Color(146, 144, 0));

        private int key;
        private String name;
        private TECHNOLOGY initialTech;
        private UNIT startingUnit;
        private int startingScore;
        private Color color, color_light, color_dark;
        TRIBE(int numVal, String name, TECHNOLOGY initialTech, UNIT startingUnit, int startingScore, Color color, Color color_light, Color color_dark) {
            this.key = numVal;  this.name = name; this.initialTech = initialTech; this.startingUnit = startingUnit;
            this.color = color;
            this.startingScore = startingScore;
            this.color_light = color_light;
            this.color_dark = color_dark;
        }

        public static TRIBE getTypeByKey(int key) {
            for(TRIBE t : Types.TRIBE.values()){
                if(t.key == key)
                    return t;
            }
            return null;
        }

        public int getKey() {  return key; }
        public String getName() { return name; }
        public TECHNOLOGY getInitialTech() {
            return initialTech;
        }
        public UNIT getStartingUnit() {return startingUnit;}
        public int getInitialScore() { return startingScore; }
        public Color getColorDark() {return color_dark;}
    }

    /**
     * Defines the status of the turn for a unit
     */
    public enum TURN_STATUS {
        FRESH,
        MOVED,
        ATTACKED,
        MOVED_AND_ATTACKED,
        PUSHED,
        FINISHED
    }

    /**
     * Enum for resources. May need to be merged with TILES or somehow put in common
     */
    public enum RESOURCE
    {
        FISH(0, "img/resource/fish2.png", null,'h', FISHING),
        FRUIT(1, "img/resource/fruit2.png", null, 'f', ORGANIZATION),
        ANIMAL(2, "img/resource/animal2.png", null, 'a', HUNTING),
        WHALES(3, "img/resource/whale2.png", "img/resource/whale3.png", 'w', WHALING),
        ORE(5, "img/resource/ore2.png", null, 'o', MINING),
        CROPS(6, "img/resource/crops2.png", null, 'c', FARMING),
        RUINS(7, "img/resource/ruins2.png", null, 'r', null);

        private int key;
        private String imageFile, secondaryImageFile;
        private char mapChar;
        private int cost;
        private int bonus;
        private TECHNOLOGY tech;
        TribesConfig tc;


        RESOURCE(int numVal, String imageFile, String secondaryImageFile, char mapChar, TECHNOLOGY t) {
            this.tc = new TribesConfig();
            this.key = numVal;
            this.imageFile = imageFile;
            this.secondaryImageFile = secondaryImageFile;
            this.mapChar = mapChar;
            this.cost = setCost(numVal);
            this.bonus = setBonus(numVal);
            this.tech = t;
        }
        public int setCost(int numVal){
            switch (numVal){
                case 0:
                    return tc.FISH_COST;
                case 1:
                    return tc.FRUIT_COST;
                case 2:
                    return tc.ANIMAL_COST;
                case 3:
                    return tc.WHALES_COST;
            }
            return 0;
        }

        public int setBonus(int numVal){
            switch (numVal){
                case 0:
                    return tc.FISH_POP;
                case 1:
                    return tc.FRUIT_POP;
                case 2:
                    return tc.ANIMAL_POP;
                case 3:
                    return tc.WHALES_STARS;
            }
            return 0;
        }
        public int getKey() {  return key; }
        public Image getImage(TERRAIN t) {
            if (this == WHALES && t != null) {
                if (t == DEEP_WATER) {
                    return ImageIO.GetInstance().getImage(imageFile);
                } else {
                    return ImageIO.GetInstance().getImage(secondaryImageFile);
                }
            }
            return ImageIO.GetInstance().getImage(imageFile);
        }
        public int getCost() {return cost;}
        public int getBonus() {return bonus;}
        public char getMapChar() {return mapChar;}

        public static RESOURCE getType(char resourceChar) {
            for(RESOURCE r : Types.RESOURCE.values()){
                if(r.mapChar == resourceChar)
                    return r;
            }
            return null;
        }

        public static RESOURCE getTypeByKey(int key) {
            for(RESOURCE t : RESOURCE.values()){
                if(t.key == key)
                    return t;
            }
            return null;
        }

        public TribesConfig getTribesConfig(){
            return this.tc;
        }


        public TECHNOLOGY getTechnologyRequirement() {
            return tech;
        }
    }

    /**
     * Types of buildings that can be built by cities
     */
    public enum BUILDING
    {
        PORT (0,"img/building/dock2.png", SAILING, new HashSet<>(Collections.singletonList(SHALLOW_WATER))),
        MINE (1,"img/building/mine2.png", MINING, new HashSet<>(Collections.singletonList(MOUNTAIN))),
        FORGE (2,"img/building/forge2.png", SMITHERY, new HashSet<>(Collections.singletonList(PLAIN))),
        FARM (3, "img/building/farm2.png", FARMING, new HashSet<>(Collections.singletonList(PLAIN))),
        WINDMILL (4,"img/building/windmill2.png", CONSTRUCTION, new HashSet<>(Collections.singletonList(PLAIN))),
        CUSTOMS_HOUSE(5,"img/building/custom_house2.png", TRADE, new HashSet<>(Collections.singletonList(PLAIN))),
        LUMBER_HUT(6,"img/building/lumber_hut2.png", FORESTRY, new HashSet<>(Collections.singletonList(FOREST))),
        SAWMILL (7,"img/building/sawmill2.png", MATHEMATICS, new HashSet<>(Collections.singletonList(PLAIN))),
        TEMPLE (8, "img/building/temple2.png", FREE_SPIRIT, new HashSet<>(Collections.singletonList(PLAIN))),
        WATER_TEMPLE (9,"img/building/temple2.png", AQUATISM, new HashSet<>(Arrays.asList(SHALLOW_WATER, DEEP_WATER))),
        FOREST_TEMPLE (10,"img/building/temple2.png", SPIRITUALISM, new HashSet<>(Collections.singletonList(FOREST))),
        MOUNTAIN_TEMPLE (11,"img/building/temple2.png", MEDITATION, new HashSet<>(Collections.singletonList(MOUNTAIN))),
        ALTAR_OF_PEACE (12,"img/building/monument2.png", MEDITATION, new HashSet<>(Arrays.asList(SHALLOW_WATER, PLAIN))),
        EMPERORS_TOMB (13,"img/building/monument2.png", TRADE, new HashSet<>(Arrays.asList(SHALLOW_WATER, PLAIN))),
        EYE_OF_GOD (14,"img/building/monument2.png", NAVIGATION, new HashSet<>(Arrays.asList(SHALLOW_WATER, PLAIN))),
        GATE_OF_POWER (15,"img/building/monument2.png", null, new HashSet<>(Arrays.asList(SHALLOW_WATER, PLAIN))),
        GRAND_BAZAR (16,"img/building/monument2.png", ROADS, new HashSet<>(Arrays.asList(SHALLOW_WATER, PLAIN))),
        PARK_OF_FORTUNE (17,"img/building/monument2.png", null, new HashSet<>(Arrays.asList(SHALLOW_WATER, PLAIN))),
        TOWER_OF_WISDOM (18, "img/building/monument2.png", PHILOSOPHY,new HashSet<>(Arrays.asList(SHALLOW_WATER, PLAIN)));

        public static BUILDING stringToType(String type) {
            switch (type) {
                case "PORT": return PORT;
                case "MINE": return MINE;
                case "FORGE": return FORGE;
                case "FARM": return FARM;
                case "WINDMILL": return WINDMILL;
                case "CUSTOMS_HOUSE": return CUSTOMS_HOUSE;
                case "LUMBER_HUT": return LUMBER_HUT;
                case "SAWMILL": return SAWMILL;
                case "TEMPLE": return TEMPLE;
                case "WATER_TEMPLE": return WATER_TEMPLE;
                case "FOREST_TEMPLE": return FOREST_TEMPLE;
                case "MOUNTAIN_TEMPLE": return MOUNTAIN_TEMPLE;
                case "ALTAR_OF_PEACE": return ALTAR_OF_PEACE;
                case "EMPERORS_TOMB": return EMPERORS_TOMB;
                case "EYE_OF_GOD": return EYE_OF_GOD;
                case "GATE_OF_POWER": return GATE_OF_POWER;
                case "GRAND_BAZAR": return GRAND_BAZAR;
                case "PARK_OF_FORTUNE": return PARK_OF_FORTUNE;
                case "TOWER_OF_WISDOM": return TOWER_OF_WISDOM;
            }
            return null;
        }

        public enum MONUMENT_STATUS {
            UNAVAILABLE(0),
            AVAILABLE(1),
            BUILT(2);

            private int key;
            MONUMENT_STATUS(int numVal){
                this.key = numVal;
            }

            public int getKey() {
                return key;
            }

            public static MONUMENT_STATUS getTypeByKey(int key) {
                for(MONUMENT_STATUS t : Types.BUILDING.MONUMENT_STATUS.values()){
                    if(t.key == key)
                        return t;
                }
                return null;
            }
        }

        private int key;
        private String imageFile;
        private TECHNOLOGY technologyRequirement;
        private HashSet<TERRAIN> terrainRequirements;
        private int cost;
        private int bonus;
        private TribesConfig tc;
        BUILDING(int numVal, String imageFile, TECHNOLOGY technologyRequirement, HashSet<TERRAIN> terrainRequirements)
        {
            this.tc = new TribesConfig();
            this.key = numVal;
            this.cost = setCost(numVal);
            this.bonus = setBonus(numVal);
            this.imageFile = imageFile;
            this.technologyRequirement = technologyRequirement;
            this.terrainRequirements = terrainRequirements;
        }

        public int setCost(int numVal){
            switch (numVal){
                case 0:
                    return tc.PORT_COST;
                case 1:
                    return tc.MINE_COST;
                case 2:
                    return tc.FORGE_COST;
                case 3:
                    return tc.FARM_COST;
                case 4:
                    return tc.WIND_MILL_COST;
                case 5:
                    return tc.CUSTOMS_COST;
                case 6:
                    return tc.LUMBER_HUT_COST;
                case 7:
                    return tc.SAW_MILL_COST;
                case 8:
                case 11:
                case 9:
                    return tc.TEMPLE_COST;
                case 10:
                    return tc.TEMPLE_FOREST_COST;


            }
            return 0;
        }

        public int setBonus(int numVal){
            switch (numVal){
                case 0:
                    return tc.PORT_BONUS;
                case 1:
                    return tc.MINE_BONUS;
                case 2:
                    return tc.FORGE_BONUS;
                case 3:
                    return tc.FARM_BONUS;
                case 4:
                    return tc.WIND_MILL_BONUS;
                case 5:
                    return tc.CUSTOMS_BONUS;
                case 6:
                    return tc.LUMBER_HUT_BONUS;
                case 7:
                    return tc.SAW_MILL_BONUS;
                case 8:
                case 11:
                case 10:
                case 9:
                    return tc.TEMPLE_BONUS;
            }
            return tc.MONUMENT_BONUS;
        }
        public TECHNOLOGY getTechnologyRequirement() { return technologyRequirement; }
        public HashSet<TERRAIN> getTerrainRequirements() { return terrainRequirements; }
        public int getKey() {  return key; }
        public int getCost() {return cost; }
        public int getBonus() {return bonus; }
        public Image getImage() { return ImageIO.GetInstance().getImage(imageFile); }

        public Types.RESOURCE getResourceConstraint()
        {
            if(this == MINE) return tc.MINE_RES_CONSTRAINT;
            if(this == FARM) return tc.FARM_RES_CONSTRAINT;
            return null;
        }

        public Types.BUILDING getAdjacencyConstraint()
        {
            if(this == CUSTOMS_HOUSE) return PORT;
            if(this == WINDMILL) return FARM;
            if(this == FORGE) return MINE;
            if(this == SAWMILL) return LUMBER_HUT;
            return null;
        }

        public BUILDING getMatchingBuilding()
        {
            switch (this)
            {
                case PORT: return CUSTOMS_HOUSE;
                case FARM: return WINDMILL;
                case MINE: return FORGE;
                case LUMBER_HUT: return SAWMILL;
                case CUSTOMS_HOUSE: return PORT;
                case WINDMILL: return FARM;
                case FORGE: return MINE;
                case SAWMILL: return LUMBER_HUT;
            }
            return null;
        }

        public boolean isBase()
        {
            return this == FARM || this == MINE || this == LUMBER_HUT;
        }
        public boolean isMonument()
        {
            return this == ALTAR_OF_PEACE || this == EMPERORS_TOMB || this == EYE_OF_GOD ||
                    this == GATE_OF_POWER || this == PARK_OF_FORTUNE || this == TOWER_OF_WISDOM
                    || this == GRAND_BAZAR;
        }

        public boolean isTemple()
        {
            return this == TEMPLE || this == WATER_TEMPLE  || this == MOUNTAIN_TEMPLE  || this == FOREST_TEMPLE;
        }


        public static HashMap<BUILDING, MONUMENT_STATUS> initMonuments()
        {
            HashMap<BUILDING, MONUMENT_STATUS> monuments = new HashMap<>();
            monuments.put(ALTAR_OF_PEACE, UNAVAILABLE);
            monuments.put(EMPERORS_TOMB, UNAVAILABLE);
            monuments.put(EYE_OF_GOD, UNAVAILABLE);
            monuments.put(GATE_OF_POWER, UNAVAILABLE);
            monuments.put(PARK_OF_FORTUNE, UNAVAILABLE);
            monuments.put(TOWER_OF_WISDOM, UNAVAILABLE);
            monuments.put(GRAND_BAZAR, UNAVAILABLE);
            return monuments;
        }

        public static HashMap<BUILDING, MONUMENT_STATUS> initMonuments(JSONObject JMonuments)
        {
            HashMap<BUILDING, MONUMENT_STATUS> monuments = new HashMap<>();
            Iterator<String> keys = JMonuments.keys();
            while (keys.hasNext()){
                String key = keys.next();
                monuments.put(BUILDING.getTypeByKey(Integer.parseInt(key)), BUILDING.MONUMENT_STATUS.getTypeByKey(JMonuments.getInt(key)));
            }
            return monuments;
        }

        public static BUILDING getTypeByKey(int key) {
            for(BUILDING t : Types.BUILDING.values()){
                if(t.key == key)
                    return t;
            }
            return null;
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
        private TribesConfig tc;

        CITY_LEVEL_UP(int level) {
            this.level = level;
            tc = new TribesConfig();
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
            return cityLevel >= 4 && (this == PARK || this == SUPERUNIT);
        }

        public int getLevelUpPoints(){
            if (level == 1){
                return 100;
            }
            return 50 - level * 5;
        }

        public boolean grantsMonument()
        {
            return this.level == tc.PARK_OF_FORTUNE_LEVEL;
        }
    }


    /**
     * Types of actors
     */
    public enum UNIT
    {
        WARRIOR (0,"img/unit/warrior/", "img/weapons/melee/tile006.png", null), //+10
        RIDER (1,"img/unit/rider/", "img/weapons/melee/tile001.png", RIDING), //+15
        DEFENDER (2,"img/unit/defender/", "img/weapons/melee/tile002.png", SHIELDS), // +15
        SWORDMAN (3,"img/unit/swordsman/", "img/weapons/melee/tile000.png", SMITHERY), //+25
        ARCHER (4,"img/unit/archer/", "img/weapons/arrows/", ARCHERY),//+15
        CATAPULT (5,"img/unit/catapult/", "img/weapons/bombs/rock.png", MATHEMATICS), //+40
        KNIGHT (6,"img/unit/knight/", "img/weapons/melee/spear.png", CHIVALRY), //+40
        MIND_BENDER(7,"img/unit/mind_bender/", "img/weapons/effects/bender/", PHILOSOPHY), //+25
        BOAT(8,"img/unit/boat/", "img/weapons/arrows/boat.png", SAILING), //+0
        SHIP(9,"img/unit/ship/", "img/weapons/bombs/", SAILING),//+0
        BATTLESHIP(10,"img/unit/battleship/", "img/weapons/bombs/", NAVIGATION),//+0
        SUPERUNIT(11, "img/unit/superunit/", "img/weapons/melee/tile003.png", null); //+50

        private int key;
        private String imageFile, weapon;
        private int cost;
        private TECHNOLOGY requirement;
        private int points;
        private TribesConfig tc;

        UNIT(int numVal, String imageFile, String weaponFile, Types.TECHNOLOGY requirement) {
            this.tc = new TribesConfig();

            this.key = numVal;
            this.imageFile = imageFile;
            this.cost = setCost(numVal);
            this.requirement = requirement;
            this.points = setPoints(numVal);
            this.weapon = weaponFile;
        }

        public int setCost(int numVal){
            switch (numVal){
                case 0:
                    return tc.WARRIOR_COST;
                case 1:
                    return tc.RIDER_COST;
                case 2:
                    return tc.DEFENDER_COST;
                case 3:
                    return tc.SWORDMAN_COST;
                case 4:
                    return tc.ARCHER_COST;
                case 5:
                    return tc.CATAPULT_COST;
                case 6:
                    return tc.KNIGHT_COST;
                case 7:
                    return tc.MINDBENDER_COST;
                case 8:
                    return tc.BOAT_COST;

                case 9:
                    return tc.SHIP_COST;
                case 10:
                    return tc.BATTLESHIP_COST;
                case 11:
                    return tc.SUPERUNIT_COST;


            }
            return 0;
        }

        public int setPoints(int numVal){
            switch (numVal){
                case 0:
                    return tc.WARRIOR_POINTS;
                case 1:
                    return tc.RIDER_POINTS;
                case 2:
                    return tc.DEFENDER_POINTS;
                case 3:
                    return tc.SWORDMAN_POINTS;
                case 4:
                    return tc.ARCHER_POINTS;
                case 5:
                    return tc.CATAPULT_POINTS;
                case 6:
                    return tc.KNIGHT_POINTS;
                case 7:
                    return tc.MINDBENDER_POINTS;
                case 8:
                    return tc.BOAT_POINTS;

                case 9:
                    return tc.SHIP_POINTS;
                case 10:
                    return tc.BATTLESHIP_POINTS;
                case 11:
                    return tc.SUPERUNIT_POINTS;
            }
            return tc.MONUMENT_BONUS;
        }
        public static UNIT stringToType(String type) {
            switch(type) {
                case "WARRIOR": return WARRIOR;
                case "RIDER": return RIDER;
                case "DEFENDER": return DEFENDER;
                case "SWORDMAN": return SWORDMAN;
                case "ARCHER": return ARCHER;
                case "CATAPULT": return CATAPULT;
                case "KNIGHT": return KNIGHT;
                case "MIND_BENDER": return MIND_BENDER;
                case "BOAT": return BOAT;
                case "SHIP": return SHIP;
                case "BATTLESHIP": return BATTLESHIP;
                case "SUPERUNIT": return SUPERUNIT;
            }
            return null;
        }

        public Image getImage(int tribeKey) { return ImageIO.GetInstance().getImage(imageFile + tribeKey + ".png"); }
        public String getImageFile() { return imageFile; }
        public Image getWeaponImage(int tribeKey) {
            if (this == SHIP || this == BATTLESHIP || this == ARCHER || this == MIND_BENDER) {
                return ImageIO.GetInstance().getImage(weapon + tribeKey + ".png");
            }
            return ImageIO.GetInstance().getImage(weapon);
        }
        public int getCost() {
            return cost;
        }
        public TECHNOLOGY getTechnologyRequirement() {
            return requirement;
        }
        public int getPoints() { return points; }
        public int getKey() {return key;}

        public static Unit createUnit (Vector2d pos, int kills, boolean isVeteran, int ownerID, int tribeID, UNIT type)
        {
            TribesConfig tc = new TribesConfig();
            switch (type)
            {
                case WARRIOR: return new Warrior(pos, kills, isVeteran, ownerID, tribeID,tc);
                case RIDER: return new Rider(pos, kills, isVeteran, ownerID, tribeID, tc);
                case DEFENDER: return new Defender(pos, kills, isVeteran, ownerID, tribeID,tc);
                case SWORDMAN: return new Swordman(pos, kills, isVeteran, ownerID, tribeID,tc);
                case ARCHER: return new Archer(pos, kills, isVeteran, ownerID, tribeID,tc);
                case CATAPULT: return new Catapult(pos, kills, isVeteran, ownerID, tribeID,tc);
                case KNIGHT: return new Knight(pos, kills, isVeteran, ownerID, tribeID,tc);
                case MIND_BENDER: return new MindBender(pos, kills, isVeteran, ownerID, tribeID,tc);
                case BOAT: return new Boat(pos, kills, isVeteran, ownerID, tribeID,tc);
                case SHIP: return new Ship(pos, kills, isVeteran, ownerID, tribeID,tc);
                case BATTLESHIP: return new Battleship(pos, kills, isVeteran, ownerID, tribeID,tc);
                case SUPERUNIT: return new SuperUnit(pos, kills, isVeteran, ownerID, tribeID,tc);

                default:
                    System.out.println("WARNING: TypescreateUnit(), type creation not implemented.");
            }
            return null;
        }


        public boolean spawnable()
        {
            return !(this == BOAT || this == SHIP || this == BATTLESHIP || this == SUPERUNIT);
        }

        public boolean isWaterUnit()
        {
            return this == BOAT || this == SHIP || this == BATTLESHIP;
        }

        public static ArrayList<UNIT> getSpawnableTypes() {
            ArrayList<UNIT> units = new ArrayList<>();
            for (UNIT u: UNIT.values()) {
                if (u.spawnable()) {
                    units.add(u);
                }
            }
            return units;
        }

        public static UNIT getTypeByKey(int key) {
            for(UNIT t : UNIT.values()){
                if(t.key == key)
                    return t;
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

        public int x() {return x;}
        public int y() {return y;}
    }

    /**
     *  Game Mode to be played
     */
    public enum GAME_MODE {
        CAPITALS(0),
        SCORE(1);

        private int key;
        GAME_MODE(int numVal) { this.key = numVal; }
        public int getKey() { return this.key; }

        public int getMaxTurns() {
            return (this == CAPITALS) ? Constants.MAX_TURNS_CAPITALS : Constants.MAX_TURNS;
        }

        public static GAME_MODE getTypeByKey(int key) {
            for(GAME_MODE t : GAME_MODE.values()){
                if(t.key == key)
                    return t;
            }
            return null;
        }
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

        public static RESULT getTypeByKey(int key) {
            for(RESULT t : RESULT.values()){
                if(t.key == key)
                    return t;
            }
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
        CITY(5, "img/terrain/city3.png", 'c'),
        FOREST(6, "img/terrain/forest2.png", 'f'),
        FOG(7, "img/fog.png", ' ');

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


        public static TERRAIN getTypeByKey(int key) {
            for(TERRAIN t : TERRAIN.values()){
                if(t.key == key)
                    return t;
            }
            return null;
        }

        public boolean isWater() {return this == SHALLOW_WATER || this == DEEP_WATER;}


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
        MOVE("img/actions/move.png", null),
        CLIMB_MOUNTAIN(null, CLIMBING),
        ATTACK("img/actions/attack.png", null),
        CAPTURE("img/actions/capture.png", null),
        CONVERT("img/actions/convert.png", null),
        EXAMINE("img/actions/examine.png", null),
        DISBAND("img/actions/disband.png", FREE_SPIRIT),
        HEAL("img/actions/heal2.png", null),
        UPGRADE_BOAT("img/actions/upgrade.png", SAILING),
        UPGRADE_SHIP("img/actions/upgrade.png", NAVIGATION),
        BURN_FOREST(null, CHIVALRY),
        CLEAR_FOREST(null, FORESTRY),
        DESTROY(null, CONSTRUCTION),
        GROW_FOREST(null, SPIRITUALISM),
        BUILD_ROAD(null, ROADS);

        private String imgPath;
        private TECHNOLOGY tech;  // Requires this technology to perform action

        ACTION(String imgPath, TECHNOLOGY t) {
            this.imgPath = imgPath; this.tech = t;
        }

        public TECHNOLOGY getTechnologyRequirement() {
            return tech;
        }

        public static Image getImage(Action a) {
            if (a instanceof Move) {
                return ImageIO.GetInstance().getImage(MOVE.imgPath);
            } else if (a instanceof Attack) {
                return ImageIO.GetInstance().getImage(ATTACK.imgPath);
            } else if (a instanceof Capture) {
                return ImageIO.GetInstance().getImage(CAPTURE.imgPath);
            } else if (a instanceof Examine) {
                return ImageIO.GetInstance().getImage(EXAMINE.imgPath);
            } else if (a instanceof Disband) {
                return ImageIO.GetInstance().getImage(DISBAND.imgPath);
            } else if (a instanceof Recover || a instanceof HealOthers) {
                return ImageIO.GetInstance().getImage(HEAL.imgPath);
            } else if (a instanceof Upgrade) {
                return ImageIO.GetInstance().getImage(UPGRADE_BOAT.imgPath);
            } else if (a instanceof Convert) {
                return ImageIO.GetInstance().getImage(CONVERT.imgPath);
            }
            return null;
        }
    }

}
