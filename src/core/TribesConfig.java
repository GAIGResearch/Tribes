package core;

import static core.Types.RESOURCE.*;

public class TribesConfig
{
    /* UNITS */

    // Warrior
    public static int WARRIOR_ATTACK = 2;
    public static int WARRIOR_DEFENCE = 2;
    public static int WARRIOR_MOVEMENT = 1;
    public static int WARRIOR_MAX_HP = 10;
    public static int WARRIOR_RANGE = 1;
    public static int WARRIOR_COST = 2;
    public static int WARRIOR_POINTS = 10;

    //Archer
    public static int ARCHER_ATTACK = 2;
    public static int ARCHER_DEFENCE = 1;
    public static int ARCHER_MOVEMENT = 1;
    public static int ARCHER_MAX_HP = 10;
    public static int ARCHER_RANGE = 2;
    public static int ARCHER_COST = 3;
    public static int ARCHER_POINTS = 15;

    //Catapult
    public static int CATAPULT_ATTACK = 4;
    public static int CATAPULT_DEFENCE = 0;
    public static int CATAPULT_MOVEMENT = 1;
    public static int CATAPULT_MAX_HP = 10;
    public static int CATAPULT_RANGE = 3;
    public static int CATAPULT_COST = 8;
    public static int CATAPULT_POINTS = 40;

    //Swordman
    public static int SWORDMAN_ATTACK = 3;
    public static int SWORDMAN_DEFENCE = 3;
    public static int SWORDMAN_MOVEMENT = 1;
    public static int SWORDMAN_MAX_HP = 15;
    public static int SWORDMAN_RANGE = 1;
    public static int SWORDMAN_COST = 5;
    public static int SWORDMAN_POINTS = 25;

    //MindBender
    public static int MINDBENDER_ATTACK = 0;
    public static int MINDBENDER_DEFENCE = 1;
    public static int MINDBENDER_MOVEMENT = 1;
    public static int MINDBENDER_MAX_HP = 10;
    public static int MINDBENDER_RANGE = 1;
    public static int MINDBENDER_COST = 5;
    public static int MINDBENDER_POINTS = 25;
    public static int MINDBENDER_HEAL = 4;

    //Defender
    public static int DEFENDER_ATTACK = 1;
    public static int DEFENDER_DEFENCE = 3;
    public static int DEFENDER_MOVEMENT = 1;
    public static int DEFENDER_MAX_HP = 15;
    public static int DEFENDER_RANGE = 1;
    public static int DEFENDER_COST = 3;
    public static int DEFENDER_POINTS = 15;

    //Knight
    public static int KNIGHT_ATTACK = 4;
    public static int KNIGHT_DEFENCE = 1;
    public static int KNIGHT_MOVEMENT = 3;
    public static int KNIGHT_MAX_HP = 15;
    public static int KNIGHT_RANGE = 1;
    public static int KNIGHT_COST = 8;
    public static int KNIGHT_POINTS = 40;

    //Rider
    public static int RIDER_ATTACK = 2;
    public static int RIDER_DEFENCE = 1;
    public static int RIDER_MOVEMENT = 2;
    public static int RIDER_MAX_HP = 10;
    public static int RIDER_RANGE = 1;
    public static int RIDER_COST = 3;
    public static int RIDER_POINTS = 15;

    // Boat
    public static int BOAT_ATTACK = 1;
    public static int BOAT_DEFENCE = 1;
    public static int BOAT_MOVEMENT = 2;
    public static int BOAT_RANGE = 2;
    public static int BOAT_COST = 0;
    public static int BOAT_POINTS = 0;

    // Ship
    public static int SHIP_ATTACK = 2;
    public static int SHIP_DEFENCE = 2;
    public static int SHIP_MOVEMENT = 3;
    public static int SHIP_RANGE = 2;
    public static int SHIP_COST = 5;
    public static int SHIP_POINTS = 0;

    // Battleship
    public static int BATTLESHIP_ATTACK = 4;
    public static int BATTLESHIP_DEFENCE = 3;
    public static int BATTLESHIP_MOVEMENT = 3;
    public static int BATTLESHIP_RANGE = 2;
    public static int BATTLESHIP_COST = 15;
    public static int BATTLESHIP_POINTS = 0;

    //Superunit
    public static int SUPERUNIT_ATTACK = 5;
    public static int SUPERUNIT_DEFENCE = 4;
    public static int SUPERUNIT_MOVEMENT = 1;
    public static int SUPERUNIT_MAX_HP = 40;
    public static int SUPERUNIT_RANGE = 1;
    public static int SUPERUNIT_COST = 10; //Useful for when unit is disbanded.
    public static int SUPERUNIT_POINTS = 50;

    //Explorer
    public static int NUM_STEPS = 15;

    //General Unit constants
    public static double ATTACK_MODIFIER = 4.5;
    public static double DEFENCE = 1.5;
    public static double DEFENCE_IN_WALLS = 4.0;
    public static int VETERAN_KILLS = 3;
    public static int VETERAN_PLUS_HP = 5;
    public static int RECOVER_PLUS_HP = 2;
    public static int RECOVER_IN_CITY_PLUS_HP = 2;

    /* BUILDINGS */

    // Farm
    public static int FARM_COST = 5;
    public static int FARM_BONUS = 2;
    public static int FARM_POINTS = 10;
    public static Types.RESOURCE FARM_RES_CONSTRAINT = CROPS;

    //WindMill
    public static int WIND_MILL_COST = 5;
    public static int WIND_MILL_BONUS = 1;
    public static int WIND_MILL_POINTS = 5;

    // LumberHut
    public static int LUMBER_HUT_COST = 2;
    public static int LUMBER_HUT_BONUS = 1;
    public static int LUMBER_HUT_POINTS = 5;

    // SawMill
    public static int SAW_MILL_COST = 5;
    public static int SAW_MILL_BONUS = 1;
    public static int SAW_MILL_POINTS = 5;

    //Mine
    public static int MINE_COST = 5;
    public static int MINE_BONUS = 2;
    public static int MINE_POINTS = 10;
    public static Types.RESOURCE MINE_RES_CONSTRAINT = ORE;

    // Forge
    public static int FORGE_COST = 5;
    public static int FORGE_BONUS = 2;
    public static int FORGE_POINTS = 10;

    // Port
    public static int PORT_COST = 10;
    public static int PORT_BONUS = 2;
    public static int PORT_TRADE_DISTANCE = 3;
    public static int PORT_POINTS = 10;

    // Custom House
    public static int CUSTOM_COST = 5;
    public static int CUSTOM_BONUS = 2;
    public static int CUSTOM_POINTS = 0;

    //Monuments
    public static int MONUMENT_BONUS = 3;
    public static int MONUMENT_POINTS = 400;
    public static int EMPERORS_TOMB_STARS = 100;
    public static int GATE_OF_POWER_KILLS = 10;
    public static int GRAND_BAZAR_CITIES = 5;

    // Temple
    public static int TEMPLE_COST = 20;
    public static int TEMPLE_FOREST_COST = 15;
    public static int TEMPLE_BONUS = 1;
    public static int TEMPLE_POINTS = 100;


    //Resources
    public static int ANIMAL_COST = 2;
    public static int FISH_COST = 2;
    public static int WHALES_COST = 0;
    public static int FRUIT_COST = 2;
    public static int ANIMAL_POP = 1;
    public static int FISH_POP = 1;
    public static int WHALES_STARS = 10;
    public static int FRUIT_POP = 1;


    // ROAD
    public static int ROAD_COST = 2;

    // City
    public static int CITY_LEVEL_UP_WORKSHOP_PROD = 1;
    public static int CITY_LEVEL_UP_RESOURCES = 5;
    public static int CITY_LEVEL_UP_POP_GROWTH = 3;
    public static int CITY_LEVEL_UP_PARK = 250;
    public static int CITY_BORDER_POINTS = 20;

    /* TRIBES */
    public static int INITIAL_STARS = 5;

    /* ACTIONS */
    public static int CLEAR_FOREST_STAR = 2;
    public static int FOREST_COST = 5;
    public static int CLEAR_VIEW_POINTS = 5;


}
