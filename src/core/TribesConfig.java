package core;

public class TribesConfig
{
    /* UNITS */

    // Warrior
    public int WARRIOR_ATTACK = 2;
    public int WARRIOR_DEFENCE = 2;
    public int WARRIOR_MOVEMENT = 1;
    public int WARRIOR_MAX_HP = 10;
    public int WARRIOR_RANGE = 1;
    public int WARRIOR_COST = 2;
   int WARRIOR_POINTS = 10;

    //Archer
    public int ARCHER_ATTACK = 2;
    public int ARCHER_DEFENCE = 1;
    public int ARCHER_MOVEMENT = 1;
    public int ARCHER_MAX_HP = 10;
    public int ARCHER_RANGE = 2;
    public int ARCHER_COST = 3;
   int ARCHER_POINTS = 15;

    //Catapult
    public int CATAPULT_ATTACK = 4;
    public int CATAPULT_DEFENCE = 0;
    public int CATAPULT_MOVEMENT = 1;
    public int CATAPULT_MAX_HP = 10;
    public int CATAPULT_RANGE = 3;
    public int CATAPULT_COST = 8;
   int CATAPULT_POINTS = 40;

    //Swordsman
    public int SWORDMAN_ATTACK = 3;
    public int SWORDMAN_DEFENCE = 3;
    public int SWORDMAN_MOVEMENT = 1;
    public int SWORDMAN_MAX_HP = 15;
    public int SWORDMAN_RANGE = 1;
    public int SWORDMAN_COST = 5;
   int SWORDMAN_POINTS = 25;

    //MindBender
    public int MINDBENDER_ATTACK = 0;
    public int MINDBENDER_DEFENCE = 1;
    public int MINDBENDER_MOVEMENT = 1;
    public int MINDBENDER_MAX_HP = 10;
    public int MINDBENDER_RANGE = 1;
    public int MINDBENDER_COST = 5;
    public int MINDBENDER_HEAL = 4;
   int MINDBENDER_POINTS = 25;

    //Defender
    public int DEFENDER_ATTACK = 1;
    public int DEFENDER_DEFENCE = 3;
    public int DEFENDER_MOVEMENT = 1;
    public int DEFENDER_MAX_HP = 15;
    public int DEFENDER_RANGE = 1;
    public int DEFENDER_COST = 3;
   int DEFENDER_POINTS = 15;

    //Knight
    public int KNIGHT_ATTACK = 4;
    public int KNIGHT_DEFENCE = 1;
    public int KNIGHT_MOVEMENT = 3;
    public int KNIGHT_MAX_HP = 15;
    public int KNIGHT_RANGE = 1;
    public int KNIGHT_COST = 8;
   int KNIGHT_POINTS = 40;

    //Rider
    public int RIDER_ATTACK = 2;
    public int RIDER_DEFENCE = 1;
    public int RIDER_MOVEMENT = 2;
    public int RIDER_MAX_HP = 10;
    public int RIDER_RANGE = 1;
    public int RIDER_COST = 3;
   int RIDER_POINTS = 15;

    // Boat
    public int BOAT_ATTACK = 1;
    public int BOAT_DEFENCE = 1;
    public int BOAT_MOVEMENT = 2;
    public int BOAT_RANGE = 2;
    public int BOAT_COST = 0;
   int BOAT_POINTS = 0;

    // Ship
    public int SHIP_ATTACK = 2;
    public int SHIP_DEFENCE = 2;
    public int SHIP_MOVEMENT = 3;
    public int SHIP_RANGE = 2;
    public int SHIP_COST = 5;
   int SHIP_POINTS = 0;

    // Battleship
    public int BATTLESHIP_ATTACK = 4;
    public int BATTLESHIP_DEFENCE = 3;
    public int BATTLESHIP_MOVEMENT = 3;
    public int BATTLESHIP_RANGE = 2;
    public int BATTLESHIP_COST = 15;
   int BATTLESHIP_POINTS = 0;

    //Superunit
    public int SUPERUNIT_ATTACK = 5;
    public int SUPERUNIT_DEFENCE = 4;
    public int SUPERUNIT_MOVEMENT = 1;
    public int SUPERUNIT_MAX_HP = 40;
    public int SUPERUNIT_RANGE = 1;
    public int SUPERUNIT_COST = 10; //Useful for when unit is disbanded.
   int SUPERUNIT_POINTS = 50;

    //Explorer
    public int NUM_STEPS = 15;

    //General Unit constants
    public double ATTACK_MODIFIER = 4.5;
    public double DEFENCE = 1.5;
    public double DEFENCE_IN_WALLS = 4.0;
    public int VETERAN_KILLS = 3;
    public int VETERAN_PLUS_HP = 5;
    public int RECOVER_PLUS_HP = 2;
    public int RECOVER_IN_BORDERS_PLUS_HP = 2;

    /* BUILDINGS */

    // Farm
   int FARM_COST = 5;
   int FARM_BONUS = 2;
   Types.RESOURCE FARM_RES_CONSTRAINT = Types.RESOURCE.CROPS;

    //WindMill
   int WIND_MILL_COST = 5;
   int WIND_MILL_BONUS = 1;

    // LumberHut
   int LUMBER_HUT_COST = 2;
   int LUMBER_HUT_BONUS = 1;

    // SawMill
   int SAW_MILL_COST = 5;
   int SAW_MILL_BONUS = 1;

    //Mine
   int MINE_COST = 5;
   int MINE_BONUS = 2;
   Types.RESOURCE MINE_RES_CONSTRAINT = Types.RESOURCE.ORE;

    // Forge
   int FORGE_COST = 5;
   int FORGE_BONUS = 2;

    // Port
   int PORT_COST = 10;
   int PORT_BONUS = 2;
    public int PORT_TRADE_DISTANCE = 4; //Count includes destination port.

    // Customs House
   int CUSTOMS_COST = 5;
   int CUSTOMS_BONUS = 2;

    //Monuments
   int MONUMENT_BONUS = 3;
    public int MONUMENT_POINTS = 400;
    public int EMPERORS_TOMB_STARS = 100;
    public int GATE_OF_POWER_KILLS = 10;
    public int GRAND_BAZAR_CITIES = 5;
    public int ALTAR_OF_PEACE_TURNS = 5;
    public int PARK_OF_FORTUNE_LEVEL = 5;


    // Temple
   int TEMPLE_COST = 20;
   int TEMPLE_FOREST_COST = 15;
   int TEMPLE_BONUS = 1;
    public int TEMPLE_TURNS_TO_SCORE = 3;
    public int[] TEMPLE_POINTS = new int[]{100, 50, 50, 50, 150};

    //Resources
   int ANIMAL_COST = 2;
   int FISH_COST = 2;
   int WHALES_COST = 0;
   int FRUIT_COST = 2;
   int ANIMAL_POP = 1;
   int FISH_POP = 1;
   int WHALES_STARS = 10;
   int FRUIT_POP = 1;

    // ROAD
    public int ROAD_COST = 2;

    // City
    public int CITY_LEVEL_UP_WORKSHOP_PROD = 1;
    public int CITY_LEVEL_UP_RESOURCES = 5;
    public int CITY_LEVEL_UP_POP_GROWTH = 3;
    public int CITY_LEVEL_UP_PARK = 250;
    public int CITY_BORDER_POINTS = 20;
    public int PROD_CAPITAL_BONUS = 1;
    public int EXPLORER_CLEAR_RANGE = 1;
    public int FIRST_CITY_CLEAR_RANGE = 2;
    public int NEW_CITY_CLEAR_RANGE = 1;
    public int CITY_EXPANSION_TILES = 1;
    public int POINTS_PER_POPULATION = 5;

    // Research
    public int TECH_BASE_COST = 4;
    public Types.TECHNOLOGY TECH_DISCOUNT = Types.TECHNOLOGY.PHILOSOPHY;
    public double TECH_DISCOUNT_VALUE = 0.2;

    /* TRIBES */
    public int INITIAL_STARS = 5;//1000;

    /* ACTIONS */
    public int CLEAR_FOREST_STAR = 2;
    public int GROW_FOREST_COST = 5;
    public int BURN_FOREST_COST = 5;
    public int CLEAR_VIEW_POINTS = 5;

    public TribesConfig(){

    }



}
