package core;

public class TribesConfig
{
    /* UNITS */

    // Warrior
    public  final int WARRIOR_ATTACK = 2;
    public  final int WARRIOR_DEFENCE = 2;
    public  final int WARRIOR_MOVEMENT = 1;
    public  final int WARRIOR_MAX_HP = 10;
    public  final int WARRIOR_RANGE = 1;
    public  final int WARRIOR_COST = 2;
    final int WARRIOR_POINTS = 10;

    //Archer
    public  final int ARCHER_ATTACK = 2;
    public  final int ARCHER_DEFENCE = 1;
    public  final int ARCHER_MOVEMENT = 1;
    public  final int ARCHER_MAX_HP = 10;
    public  final int ARCHER_RANGE = 2;
    public  final int ARCHER_COST = 3;
    final int ARCHER_POINTS = 15;

    //Catapult
    public  final int CATAPULT_ATTACK = 4;
    public  final int CATAPULT_DEFENCE = 0;
    public  final int CATAPULT_MOVEMENT = 1;
    public  final int CATAPULT_MAX_HP = 10;
    public  final int CATAPULT_RANGE = 3;
    public  final int CATAPULT_COST = 8;
    final int CATAPULT_POINTS = 40;

    //Swordsman
    public  final int SWORDMAN_ATTACK = 3;
    public  final int SWORDMAN_DEFENCE = 3;
    public  final int SWORDMAN_MOVEMENT = 1;
    public  final int SWORDMAN_MAX_HP = 15;
    public  final int SWORDMAN_RANGE = 1;
    public  final int SWORDMAN_COST = 5;
    final int SWORDMAN_POINTS = 25;

    //MindBender
    public  final int MINDBENDER_ATTACK = 0;
    public  final int MINDBENDER_DEFENCE = 1;
    public  final int MINDBENDER_MOVEMENT = 1;
    public  final int MINDBENDER_MAX_HP = 10;
    public  final int MINDBENDER_RANGE = 1;
    public  final int MINDBENDER_COST = 5;
    public  final int MINDBENDER_HEAL = 4;
    final int MINDBENDER_POINTS = 25;

    //Defender
    public  final int DEFENDER_ATTACK = 1;
    public  final int DEFENDER_DEFENCE = 3;
    public  final int DEFENDER_MOVEMENT = 1;
    public  final int DEFENDER_MAX_HP = 15;
    public  final int DEFENDER_RANGE = 1;
    public  final int DEFENDER_COST = 3;
    final int DEFENDER_POINTS = 15;

    //Knight
    public  final int KNIGHT_ATTACK = 4;
    public  final int KNIGHT_DEFENCE = 1;
    public  final int KNIGHT_MOVEMENT = 3;
    public  final int KNIGHT_MAX_HP = 15;
    public  final int KNIGHT_RANGE = 1;
    public  final int KNIGHT_COST = 8;
    final int KNIGHT_POINTS = 40;

    //Rider
    public  final int RIDER_ATTACK = 2;
    public  final int RIDER_DEFENCE = 1;
    public  final int RIDER_MOVEMENT = 2;
    public  final int RIDER_MAX_HP = 10;
    public  final int RIDER_RANGE = 1;
    public  final int RIDER_COST = 3;
    final int RIDER_POINTS = 15;

    // Boat
    public  final int BOAT_ATTACK = 1;
    public  final int BOAT_DEFENCE = 1;
    public  final int BOAT_MOVEMENT = 2;
    public  final int BOAT_RANGE = 2;
    public  final int BOAT_COST = 0;
    final int BOAT_POINTS = 0;

    // Ship
    public  final int SHIP_ATTACK = 2;
    public  final int SHIP_DEFENCE = 2;
    public  final int SHIP_MOVEMENT = 3;
    public  final int SHIP_RANGE = 2;
    public  final int SHIP_COST = 5;
    final int SHIP_POINTS = 0;

    // Battleship
    public  final int BATTLESHIP_ATTACK = 4;
    public  final int BATTLESHIP_DEFENCE = 3;
    public  final int BATTLESHIP_MOVEMENT = 3;
    public  final int BATTLESHIP_RANGE = 2;
    public  final int BATTLESHIP_COST = 15;
    final int BATTLESHIP_POINTS = 0;

    //Superunit
    public  final int SUPERUNIT_ATTACK = 5;
    public  final int SUPERUNIT_DEFENCE = 4;
    public  final int SUPERUNIT_MOVEMENT = 1;
    public  final int SUPERUNIT_MAX_HP = 40;
    public  final int SUPERUNIT_RANGE = 1;
    public  final int SUPERUNIT_COST = 10; //Useful for when unit is disbanded.
    final int SUPERUNIT_POINTS = 50;

    //Explorer
    public  final int NUM_STEPS = 15;

    //General Unit constants
    public  final double ATTACK_MODIFIER = 4.5;
    public  final double DEFENCE = 1.5;
    public  final double DEFENCE_IN_WALLS = 4.0;
    public  final int VETERAN_KILLS = 3;
    public  final int VETERAN_PLUS_HP = 5;
    public  final int RECOVER_PLUS_HP = 2;
    public  final int RECOVER_IN_BORDERS_PLUS_HP = 2;

    /* BUILDINGS */

    // Farm
    final int FARM_COST = 5;
    final int FARM_BONUS = 2;
    final Types.RESOURCE FARM_RES_CONSTRAINT = Types.RESOURCE.CROPS;

    //WindMill
    final int WIND_MILL_COST = 5;
    final int WIND_MILL_BONUS = 1;

    // LumberHut
    final int LUMBER_HUT_COST = 2;
    final int LUMBER_HUT_BONUS = 1;

    // SawMill
    final int SAW_MILL_COST = 5;
    final int SAW_MILL_BONUS = 1;

    //Mine
    final int MINE_COST = 5;
    final int MINE_BONUS = 2;
    final Types.RESOURCE MINE_RES_CONSTRAINT = Types.RESOURCE.ORE;

    // Forge
    final int FORGE_COST = 5;
    final int FORGE_BONUS = 2;

    // Port
    final int PORT_COST = 10;
    final int PORT_BONUS = 2;
    public  final int PORT_TRADE_DISTANCE = 4; //Count includes destination port.

    // Customs House
    final int CUSTOMS_COST = 5;
    final int CUSTOMS_BONUS = 2;

    //Monuments
    final int MONUMENT_BONUS = 3;
    public  final int MONUMENT_POINTS = 400;
    public  final int EMPERORS_TOMB_STARS = 100;
    public  final int GATE_OF_POWER_KILLS = 10;
    public  final int GRAND_BAZAR_CITIES = 5;
    public  final int ALTAR_OF_PEACE_TURNS = 5;
    public  final int PARK_OF_FORTUNE_LEVEL = 5;


    // Temple
    final int TEMPLE_COST = 20;
    final int TEMPLE_FOREST_COST = 15;
    final int TEMPLE_BONUS = 1;
    public  final int TEMPLE_TURNS_TO_SCORE = 3;
    public  final int[] TEMPLE_POINTS = new int[]{100, 50, 50, 50, 150};

    //Resources
    final int ANIMAL_COST = 2;
    final int FISH_COST = 2;
    final int WHALES_COST = 0;
    final int FRUIT_COST = 2;
    final int ANIMAL_POP = 1;
    final int FISH_POP = 1;
    final int WHALES_STARS = 10;
    final int FRUIT_POP = 1;

    // ROAD
    public  final int ROAD_COST = 2;

    // City
    public  final int CITY_LEVEL_UP_WORKSHOP_PROD = 1;
    public  final int CITY_LEVEL_UP_RESOURCES = 5;
    public  final int CITY_LEVEL_UP_POP_GROWTH = 3;
    public  final int CITY_LEVEL_UP_PARK = 250;
    public  final int CITY_BORDER_POINTS = 20;
    public  final int PROD_CAPITAL_BONUS = 1;
    public  final int EXPLORER_CLEAR_RANGE = 1;
    public  final int FIRST_CITY_CLEAR_RANGE = 2;
    public  final int NEW_CITY_CLEAR_RANGE = 1;
    public  final int CITY_EXPANSION_TILES = 1;
    public  final int POINTS_PER_POPULATION = 5;

    // Research
    public  final int TECH_BASE_COST = 4;
    public  final Types.TECHNOLOGY TECH_DISCOUNT = Types.TECHNOLOGY.PHILOSOPHY;
    public  final double TECH_DISCOUNT_VALUE = 0.2;

    /* TRIBES */
    public  final int INITIAL_STARS = 5;//1000;

    /* ACTIONS */
    public  final int CLEAR_FOREST_STAR = 2;
    public  final int GROW_FOREST_COST = 5;
    public  final int BURN_FOREST_COST = 5;
    public  final int CLEAR_VIEW_POINTS = 5;

    public TribesConfig(){

    }



}
