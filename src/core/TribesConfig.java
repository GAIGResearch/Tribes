package core;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

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
    public int WARRIOR_POINTS = 10;

    //Archer
    public int ARCHER_ATTACK = 2;
    public int ARCHER_DEFENCE = 1;
    public int ARCHER_MOVEMENT = 1;
    public int ARCHER_MAX_HP = 10;
    public int ARCHER_RANGE = 2;
    public int ARCHER_COST = 3;
    public int ARCHER_POINTS = 15;

    //Catapult
    public int CATAPULT_ATTACK = 4;
    public int CATAPULT_DEFENCE = 0;
    public int CATAPULT_MOVEMENT = 1;
    public int CATAPULT_MAX_HP = 10;
    public int CATAPULT_RANGE = 3;
    public int CATAPULT_COST = 8;
    public int CATAPULT_POINTS = 40;

    //Swordsman
    public int SWORDMAN_ATTACK = 3;
    public int SWORDMAN_DEFENCE = 3;
    public int SWORDMAN_MOVEMENT = 1;
    public int SWORDMAN_MAX_HP = 15;
    public int SWORDMAN_RANGE = 1;
    public int SWORDMAN_COST = 5;
    public int SWORDMAN_POINTS = 25;

    //MindBender
    public int MINDBENDER_ATTACK = 0;
    public int MINDBENDER_DEFENCE = 1;
    public int MINDBENDER_MOVEMENT = 1;
    public int MINDBENDER_MAX_HP = 10;
    public int MINDBENDER_RANGE = 1;
    public int MINDBENDER_COST = 5;
    public int MINDBENDER_HEAL = 4;
    public int MINDBENDER_POINTS = 25;

    //Defender
    public int DEFENDER_ATTACK = 1;
    public int DEFENDER_DEFENCE = 3;
    public int DEFENDER_MOVEMENT = 1;
    public int DEFENDER_MAX_HP = 15;
    public int DEFENDER_RANGE = 1;
    public int DEFENDER_COST = 3;
    public int DEFENDER_POINTS = 15;

    //Knight
    public int KNIGHT_ATTACK = 4;
    public int KNIGHT_DEFENCE = 1;
    public int KNIGHT_MOVEMENT = 3;
    public int KNIGHT_MAX_HP = 15;
    public int KNIGHT_RANGE = 1;
    public int KNIGHT_COST = 8;
    public int KNIGHT_POINTS = 40;

    //Rider
    public int RIDER_ATTACK = 2;
    public int RIDER_DEFENCE = 1;
    public int RIDER_MOVEMENT = 2;
    public int RIDER_MAX_HP = 10;
    public int RIDER_RANGE = 1;
    public int RIDER_COST = 3;
    public int RIDER_POINTS = 15;

    // Boat
    public int BOAT_ATTACK = 1;
    public int BOAT_DEFENCE = 1;
    public int BOAT_MOVEMENT = 2;
    public int BOAT_RANGE = 2;
    public int BOAT_COST = 0;
    public int BOAT_POINTS = 0;

    // Ship
    public int SHIP_ATTACK = 2;
    public int SHIP_DEFENCE = 2;
    public int SHIP_MOVEMENT = 3;
    public int SHIP_RANGE = 2;
    public int SHIP_COST = 5;
    public int SHIP_POINTS = 0;

    // Battleship
    public int BATTLESHIP_ATTACK = 4;
    public int BATTLESHIP_DEFENCE = 3;
    public int BATTLESHIP_MOVEMENT = 3;
    public int BATTLESHIP_RANGE = 2;
    public int BATTLESHIP_COST = 15;
    public int BATTLESHIP_POINTS = 0;

    //Superunit
    public int SUPERUNIT_ATTACK = 5;
    public int SUPERUNIT_DEFENCE = 4;
    public int SUPERUNIT_MOVEMENT = 1;
    public int SUPERUNIT_MAX_HP = 40;
    public int SUPERUNIT_RANGE = 1;
    public int SUPERUNIT_COST = 10; //Useful for when unit is disbanded.
    public int SUPERUNIT_POINTS = 50;

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
    public int FARM_COST = 5;
    public int FARM_BONUS = 2;
   Types.RESOURCE FARM_RES_CONSTRAINT = Types.RESOURCE.CROPS;

    //WindMill
    public int WIND_MILL_COST = 5;
    public int WIND_MILL_BONUS = 1;

    // LumberHut
    public int LUMBER_HUT_COST = 2;
    public int LUMBER_HUT_BONUS = 1;

    // SawMill
    public int SAW_MILL_COST = 5;
    public int SAW_MILL_BONUS = 1;

    //Mine
    public int MINE_COST = 5;
    public int MINE_BONUS = 2;
   Types.RESOURCE MINE_RES_CONSTRAINT = Types.RESOURCE.ORE;

    // Forge
    public int FORGE_COST = 5;
    public int FORGE_BONUS = 2;

    // Port
    public int PORT_COST = 10;
    public int PORT_BONUS = 2;
    public int PORT_TRADE_DISTANCE = 4; //Count includes destination port.

    // Customs House
    public int CUSTOMS_COST = 5;
    public int CUSTOMS_BONUS = 2;

    //Monuments
    public int MONUMENT_BONUS = 3;
    public int MONUMENT_POINTS = 400;
    public int EMPERORS_TOMB_STARS = 100;
    public int GATE_OF_POWER_KILLS = 10;
    public int GRAND_BAZAR_CITIES = 5;
    public int ALTAR_OF_PEACE_TURNS = 5;
    public int PARK_OF_FORTUNE_LEVEL = 5;


    // Temple
    public int TEMPLE_COST = 20;
    public int TEMPLE_FOREST_COST = 15;
    public int TEMPLE_BONUS = 1;
    public int TEMPLE_TURNS_TO_SCORE = 3;
    public int[] TEMPLE_POINTS = new int[]{100, 50, 50, 50, 150};

    //Resources
    public int ANIMAL_COST = 2;
    public int FISH_COST = 2;
    public  int WHALES_COST = 0;
    public int FRUIT_COST = 2;
    public int ANIMAL_POP = 1;
    public int FISH_POP = 1;
    public int WHALES_STARS = 10;
    public int FRUIT_POP = 1;

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

    public TribesConfig(File file){
        readInFile(file);
    }

    private void readInFile(File file){
        try{
            Scanner input = new Scanner(file);
            WARRIOR_ATTACK = Integer.parseInt(input.next());
            WARRIOR_DEFENCE =  Integer.parseInt(input.next());
            WARRIOR_MOVEMENT =  Integer.parseInt(input.next());
            WARRIOR_MAX_HP =  Integer.parseInt(input.next());
            WARRIOR_RANGE =  Integer.parseInt(input.next());
            WARRIOR_COST =  Integer.parseInt(input.next());
            WARRIOR_POINTS =  Integer.parseInt(input.next());

            //Archer
            ARCHER_ATTACK =  Integer.parseInt(input.next());
            ARCHER_DEFENCE =  Integer.parseInt(input.next());
            ARCHER_MOVEMENT =  Integer.parseInt(input.next());
            ARCHER_MAX_HP =  Integer.parseInt(input.next());
            ARCHER_RANGE =  Integer.parseInt(input.next());
            ARCHER_COST = Integer.parseInt(input.next());
            ARCHER_POINTS =  Integer.parseInt(input.next()) ;

            //Catapult
            CATAPULT_ATTACK = Integer.parseInt(input.next());
            CATAPULT_DEFENCE = Integer.parseInt(input.next());
            CATAPULT_MOVEMENT =  Integer.parseInt(input.next());
            CATAPULT_MAX_HP =  Integer.parseInt(input.next());
            CATAPULT_RANGE = Integer.parseInt(input.next());
            CATAPULT_COST = Integer.parseInt(input.next());
            CATAPULT_POINTS = Integer.parseInt(input.next());

            //Swordsman
            SWORDMAN_ATTACK = Integer.parseInt(input.next());
            SWORDMAN_DEFENCE = Integer.parseInt(input.next());
            SWORDMAN_MOVEMENT =  Integer.parseInt(input.next());
            SWORDMAN_MAX_HP =  Integer.parseInt(input.next());
            SWORDMAN_RANGE =  Integer.parseInt(input.next());
            SWORDMAN_COST =  Integer.parseInt(input.next());
            SWORDMAN_POINTS =  Integer.parseInt(input.next());

            //MindBender
            MINDBENDER_ATTACK = Integer.parseInt(input.next());
            MINDBENDER_DEFENCE =  Integer.parseInt(input.next());
            MINDBENDER_MOVEMENT =  Integer.parseInt(input.next());
            MINDBENDER_MAX_HP =  Integer.parseInt(input.next());
            MINDBENDER_RANGE =  Integer.parseInt(input.next());
            MINDBENDER_COST =  Integer.parseInt(input.next());
            MINDBENDER_HEAL = Integer.parseInt(input.next());
            MINDBENDER_POINTS =  Integer.parseInt(input.next());

            //Defender
            DEFENDER_ATTACK =  Integer.parseInt(input.next());
            DEFENDER_DEFENCE = Integer.parseInt(input.next());
            DEFENDER_MOVEMENT =  Integer.parseInt(input.next());
            DEFENDER_MAX_HP =  Integer.parseInt(input.next());
            DEFENDER_RANGE =  Integer.parseInt(input.next());
            DEFENDER_COST = Integer.parseInt(input.next());
            DEFENDER_POINTS =  Integer.parseInt(input.next());

            //Knight
            KNIGHT_ATTACK = Integer.parseInt(input.next());
            KNIGHT_DEFENCE =  Integer.parseInt(input.next());
            KNIGHT_MOVEMENT = Integer.parseInt(input.next());
            KNIGHT_MAX_HP =  Integer.parseInt(input.next());
            KNIGHT_RANGE =  Integer.parseInt(input.next());
            KNIGHT_COST = Integer.parseInt(input.next());
            KNIGHT_POINTS = Integer.parseInt(input.next());

            //Rider
            RIDER_ATTACK =  Integer.parseInt(input.next());
            RIDER_DEFENCE =  Integer.parseInt(input.next());
            RIDER_MOVEMENT =  Integer.parseInt(input.next());
            RIDER_MAX_HP =  Integer.parseInt(input.next());
            RIDER_RANGE =  Integer.parseInt(input.next());
            RIDER_COST = Integer.parseInt(input.next());
            RIDER_POINTS =  Integer.parseInt(input.next());

            // Boat
            BOAT_ATTACK =  Integer.parseInt(input.next());
            BOAT_DEFENCE =  Integer.parseInt(input.next());
            BOAT_MOVEMENT =  Integer.parseInt(input.next());
            BOAT_RANGE =  Integer.parseInt(input.next());
            BOAT_COST = Integer.parseInt(input.next());
            BOAT_POINTS = Integer.parseInt(input.next());

            // Ship
            SHIP_ATTACK =  Integer.parseInt(input.next());
            SHIP_DEFENCE =  Integer.parseInt(input.next());
            SHIP_MOVEMENT = Integer.parseInt(input.next());
            SHIP_RANGE =  Integer.parseInt(input.next());
            SHIP_COST =  Integer.parseInt(input.next());
            SHIP_POINTS = Integer.parseInt(input.next());

            // Battleship
            BATTLESHIP_ATTACK = Integer.parseInt(input.next());
            BATTLESHIP_DEFENCE = Integer.parseInt(input.next());
            BATTLESHIP_MOVEMENT = Integer.parseInt(input.next());
            BATTLESHIP_RANGE =  Integer.parseInt(input.next());
            BATTLESHIP_COST =  Integer.parseInt(input.next());
            BATTLESHIP_POINTS = Integer.parseInt(input.next());

            //Superunit
            SUPERUNIT_ATTACK =  Integer.parseInt(input.next());
            SUPERUNIT_DEFENCE = Integer.parseInt(input.next());
            SUPERUNIT_MOVEMENT =  Integer.parseInt(input.next());
            SUPERUNIT_MAX_HP = Integer.parseInt(input.next());
            SUPERUNIT_RANGE =  Integer.parseInt(input.next());
            SUPERUNIT_COST =  Integer.parseInt(input.next()); //Useful for when unit is disbanded.
            SUPERUNIT_POINTS =  Integer.parseInt(input.next());

            //Explorer
            NUM_STEPS =  Integer.parseInt(input.next());
            ATTACK_MODIFIER = Double.parseDouble(input.next());
            DEFENCE =  Double.parseDouble(input.next());
            DEFENCE_IN_WALLS = Double.parseDouble(input.next());
            VETERAN_KILLS = Integer.parseInt(input.next());
            VETERAN_PLUS_HP =  Integer.parseInt(input.next());
            RECOVER_PLUS_HP =  Integer.parseInt(input.next());
            RECOVER_IN_BORDERS_PLUS_HP =  Integer.parseInt(input.next());

            /* BUILDINGS */

            // Farm
            FARM_COST =  Integer.parseInt(input.next());
            FARM_BONUS =  Integer.parseInt(input.next());

            //WindMill
            WIND_MILL_COST =  Integer.parseInt(input.next());
            WIND_MILL_BONUS =  Integer.parseInt(input.next());

            // LumberHut
            LUMBER_HUT_COST =  Integer.parseInt(input.next());
            LUMBER_HUT_BONUS =  Integer.parseInt(input.next());

            // SawMill
            SAW_MILL_COST =  Integer.parseInt(input.next());
            SAW_MILL_BONUS =  Integer.parseInt(input.next());

            //Mine
            MINE_COST =  Integer.parseInt(input.next());
            MINE_BONUS =  Integer.parseInt(input.next());

            // Forge
            FORGE_COST =  Integer.parseInt(input.next());
            FORGE_BONUS =  Integer.parseInt(input.next());

            // Port
            PORT_COST =  Integer.parseInt(input.next());
            PORT_BONUS =  Integer.parseInt(input.next());
            PORT_TRADE_DISTANCE = Integer.parseInt(input.next()); //Count includes destination port.

            // Customs House
            CUSTOMS_COST =  Integer.parseInt(input.next());
            CUSTOMS_BONUS =  Integer.parseInt(input.next());

            //Monuments
            MONUMENT_BONUS = Integer.parseInt(input.next());;
            MONUMENT_POINTS = Integer.parseInt(input.next());;
            EMPERORS_TOMB_STARS =  Integer.parseInt(input.next());
            GATE_OF_POWER_KILLS =  Integer.parseInt(input.next());
            GRAND_BAZAR_CITIES =  Integer.parseInt(input.next());
            ALTAR_OF_PEACE_TURNS =  Integer.parseInt(input.next());
            PARK_OF_FORTUNE_LEVEL =  Integer.parseInt(input.next());


            // Temple
            TEMPLE_COST =  Integer.parseInt(input.next());
            TEMPLE_FOREST_COST =  Integer.parseInt(input.next());
            TEMPLE_BONUS =  Integer.parseInt(input.next());
            TEMPLE_TURNS_TO_SCORE = Integer.parseInt(input.next());
            TEMPLE_POINTS = new int[]{ Integer.parseInt(input.next()),  Integer.parseInt(input.next()),  Integer.parseInt(input.next()),  Integer.parseInt(input.next()),  Integer.parseInt(input.next()),Integer.parseInt(input.next())};

            //Resources
            ANIMAL_COST =  Integer.parseInt(input.next());
            FISH_COST =  Integer.parseInt(input.next());
            WHALES_COST = Integer.parseInt(input.next());
            FRUIT_COST =  Integer.parseInt(input.next());
            ANIMAL_POP =  Integer.parseInt(input.next());
            FISH_POP =  Integer.parseInt(input.next());
            WHALES_STARS =  Integer.parseInt(input.next());
            FRUIT_POP =  Integer.parseInt(input.next());

            // ROAD
            ROAD_COST =  Integer.parseInt(input.next());

            // City
            CITY_LEVEL_UP_WORKSHOP_PROD =  Integer.parseInt(input.next());
            CITY_LEVEL_UP_RESOURCES =  Integer.parseInt(input.next());
            CITY_LEVEL_UP_POP_GROWTH = Integer.parseInt(input.next());
            CITY_LEVEL_UP_PARK =  Integer.parseInt(input.next());
            CITY_BORDER_POINTS =  Integer.parseInt(input.next());
            PROD_CAPITAL_BONUS =  Integer.parseInt(input.next());
            EXPLORER_CLEAR_RANGE =  Integer.parseInt(input.next());
            FIRST_CITY_CLEAR_RANGE =  Integer.parseInt(input.next());
            NEW_CITY_CLEAR_RANGE =  Integer.parseInt(input.next());
            CITY_EXPANSION_TILES =  Integer.parseInt(input.next());
            POINTS_PER_POPULATION =  Integer.parseInt(input.next());

            // Research
            TECH_BASE_COST = 4;
            TECH_DISCOUNT_VALUE = Double.parseDouble(input.next());

            /* TRIBES */
            INITIAL_STARS =  Integer.parseInt(input.next());

            /* ACTIONS */
            CLEAR_FOREST_STAR =  Integer.parseInt(input.next());
            GROW_FOREST_COST =  Integer.parseInt(input.next());
            BURN_FOREST_COST =  Integer.parseInt(input.next());
            CLEAR_VIEW_POINTS =  Integer.parseInt(input.next());
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }


    // MAP
    public final int[] DEFAULT_MAP_SIZE = new int[]{-1, 11, 14, 16};

}
