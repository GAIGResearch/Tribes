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

    // MAP
    public int[] DEFAULT_MAP_SIZE = new int[]{-1, 11, 14, 16};

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

    //TODO: Copy method

    public TribesConfig copy(){
        TribesConfig tc = new TribesConfig();
        /* UNITS */

        // Warrior
        tc.WARRIOR_ATTACK = this.WARRIOR_ATTACK;
        tc.WARRIOR_DEFENCE = this.WARRIOR_DEFENCE;
        tc.WARRIOR_MOVEMENT = this.WARRIOR_MOVEMENT;
        tc.WARRIOR_MAX_HP = this.WARRIOR_MAX_HP;
        tc.WARRIOR_RANGE = this.WARRIOR_RANGE;
        tc.WARRIOR_COST = this.WARRIOR_COST;
        tc.WARRIOR_POINTS = this.WARRIOR_POINTS;

        //Archer
        tc.ARCHER_ATTACK = this.ARCHER_ATTACK;
        tc.ARCHER_DEFENCE = this.ARCHER_DEFENCE;
        tc.ARCHER_MOVEMENT = this.ARCHER_MOVEMENT;
        tc.ARCHER_MAX_HP = this.ARCHER_MAX_HP;
        tc.ARCHER_RANGE = this.ARCHER_RANGE;
        tc.ARCHER_COST = this.ARCHER_COST;
        tc.ARCHER_POINTS = this.ARCHER_POINTS;

        //Catapult
        tc.CATAPULT_ATTACK = this.CATAPULT_ATTACK;
        tc.CATAPULT_DEFENCE = this.CATAPULT_DEFENCE;
        tc.CATAPULT_MOVEMENT = this.CATAPULT_MOVEMENT;
        tc.CATAPULT_MAX_HP = this.CATAPULT_MAX_HP;
        tc.CATAPULT_RANGE = this.CATAPULT_RANGE;
        tc.CATAPULT_COST = this.CATAPULT_COST;
        tc.CATAPULT_POINTS = this.CATAPULT_POINTS;

        //Swordsman
        tc.SWORDMAN_ATTACK = this.SWORDMAN_ATTACK;
        tc.SWORDMAN_DEFENCE = this.SWORDMAN_DEFENCE;
        tc.SWORDMAN_MOVEMENT = this.SWORDMAN_MOVEMENT;
        tc.SWORDMAN_MAX_HP = this.SWORDMAN_MAX_HP;
        tc.SWORDMAN_RANGE = this.SWORDMAN_RANGE;
        tc.SWORDMAN_COST = this.SWORDMAN_COST;
        tc.SWORDMAN_POINTS = this.SWORDMAN_POINTS;

        //MindBender
        tc.MINDBENDER_ATTACK = this.MINDBENDER_ATTACK;
        tc.MINDBENDER_DEFENCE = this.MINDBENDER_DEFENCE;
        tc.MINDBENDER_MOVEMENT = this.MINDBENDER_MOVEMENT;
        tc.MINDBENDER_MAX_HP = this.MINDBENDER_MAX_HP;
        tc.MINDBENDER_RANGE = this.MINDBENDER_RANGE;
        tc.MINDBENDER_COST = this.MINDBENDER_COST;
        tc.MINDBENDER_HEAL = this.MINDBENDER_HEAL;
        tc.MINDBENDER_POINTS = this.MINDBENDER_POINTS;

        //Defender
        tc.DEFENDER_ATTACK = this.DEFENDER_ATTACK;
        tc.DEFENDER_DEFENCE = this.DEFENDER_DEFENCE;
        tc.DEFENDER_MOVEMENT = this.DEFENDER_MOVEMENT;
        tc.DEFENDER_MAX_HP = this.DEFENDER_MAX_HP;
        tc.DEFENDER_RANGE = this.DEFENDER_RANGE;
        tc.DEFENDER_COST = this.DEFENDER_COST;
        tc.DEFENDER_POINTS = this.DEFENDER_POINTS;

        //Knight
        tc.KNIGHT_ATTACK = this.KNIGHT_ATTACK;
        tc.KNIGHT_DEFENCE = this.KNIGHT_DEFENCE;
        tc.KNIGHT_MOVEMENT = this.KNIGHT_MOVEMENT;
        tc.KNIGHT_MAX_HP = this.KNIGHT_MAX_HP;
        tc.KNIGHT_RANGE = this.KNIGHT_RANGE;
        tc.KNIGHT_COST = this.KNIGHT_COST;
        tc.KNIGHT_POINTS = this.KNIGHT_POINTS;

        //Rider
        tc.RIDER_ATTACK = this.RIDER_ATTACK;
        tc.RIDER_DEFENCE = this.RIDER_DEFENCE;
        tc.RIDER_MOVEMENT = this.RIDER_MOVEMENT;
        tc.RIDER_MAX_HP = this.RIDER_MAX_HP;
        tc.RIDER_RANGE = this.RIDER_RANGE;
        tc.RIDER_COST = this.RIDER_COST;
        tc.RIDER_POINTS = this.RIDER_POINTS;

        // Boat
        tc.BOAT_ATTACK = this.BOAT_ATTACK;
        tc.BOAT_DEFENCE = this.BOAT_DEFENCE;
        tc.BOAT_MOVEMENT = this.BOAT_MOVEMENT;
        tc.BOAT_RANGE = this.BOAT_RANGE;
        tc.BOAT_COST = this.BOAT_COST;
        tc.BOAT_POINTS = this.BOAT_POINTS;

        // Ship
        tc.SHIP_ATTACK = this.SHIP_ATTACK;
        tc.SHIP_DEFENCE = this.SHIP_DEFENCE;
        tc.SHIP_MOVEMENT = this.SHIP_MOVEMENT;
        tc.SHIP_RANGE = this.SHIP_RANGE;
        tc.SHIP_COST = this.SHIP_COST;
        tc.SHIP_POINTS = this.SHIP_POINTS;

        // Battleship
        tc.BATTLESHIP_ATTACK = this.BATTLESHIP_ATTACK;
        tc.BATTLESHIP_DEFENCE = this.BATTLESHIP_DEFENCE;
        tc.BATTLESHIP_MOVEMENT = this.BATTLESHIP_MOVEMENT;
        tc.BATTLESHIP_RANGE = this.BATTLESHIP_RANGE;
        tc.BATTLESHIP_COST = this.BATTLESHIP_COST;
        tc.BATTLESHIP_POINTS = this.BATTLESHIP_POINTS;

        //Superunit
        tc.SUPERUNIT_ATTACK = this.SUPERUNIT_ATTACK;
        tc.SUPERUNIT_DEFENCE = this.SUPERUNIT_DEFENCE;
        tc.SUPERUNIT_MOVEMENT = this.SUPERUNIT_MOVEMENT;
        tc.SUPERUNIT_MAX_HP = this.SUPERUNIT_MAX_HP;
        tc.SUPERUNIT_RANGE = this.SUPERUNIT_RANGE;
        tc.SUPERUNIT_COST = this.SUPERUNIT_COST; //Useful for when unit is disbanded.
        tc.SUPERUNIT_POINTS = this.SUPERUNIT_POINTS;

        //Explorer
        tc.NUM_STEPS = this.NUM_STEPS;

        //General Unit constants
        tc.ATTACK_MODIFIER = this.ATTACK_MODIFIER;
        tc.DEFENCE = this.DEFENCE;
        tc.DEFENCE_IN_WALLS = this.DEFENCE_IN_WALLS;
        tc.VETERAN_KILLS = this.VETERAN_KILLS;
        tc.VETERAN_PLUS_HP = this.VETERAN_PLUS_HP;
        tc.RECOVER_PLUS_HP = this.RECOVER_PLUS_HP;
        tc.RECOVER_IN_BORDERS_PLUS_HP = this.RECOVER_IN_BORDERS_PLUS_HP;

        /* BUILDINGS */

        // Farm
        tc.FARM_COST = this.FARM_COST;
        tc.FARM_BONUS = this.FARM_BONUS;
        tc.FARM_RES_CONSTRAINT = this.FARM_RES_CONSTRAINT;

        //WindMill
        tc.WIND_MILL_COST = this.WIND_MILL_COST;
        tc.WIND_MILL_BONUS = this.WIND_MILL_BONUS;

        // LumberHut
        tc.LUMBER_HUT_COST = this.LUMBER_HUT_COST;
        tc.LUMBER_HUT_BONUS = this.LUMBER_HUT_BONUS;

        // SawMill
        tc.SAW_MILL_COST = this.SAW_MILL_COST;
        tc.SAW_MILL_BONUS = this.SAW_MILL_BONUS;

        //Mine
        tc.MINE_COST = this.MINE_COST;
        tc.MINE_BONUS = this.MINE_BONUS;
        tc.MINE_RES_CONSTRAINT = this.MINE_RES_CONSTRAINT;

        // Forge
        tc.FORGE_COST = this.FORGE_COST;
        tc.FORGE_BONUS = this.FORGE_BONUS;

        // Port
        tc.PORT_COST = this.PORT_COST;
        tc.PORT_BONUS = this.PORT_BONUS;
        tc.PORT_TRADE_DISTANCE = this.PORT_TRADE_DISTANCE; //Count includes destination port.

        // Customs House
        tc.CUSTOMS_COST = this.CUSTOMS_COST;
        tc.CUSTOMS_BONUS = this.CUSTOMS_BONUS;

        //Monuments
        tc.MONUMENT_BONUS = this.MONUMENT_BONUS;
        tc.MONUMENT_POINTS = this.MONUMENT_POINTS;
        tc.EMPERORS_TOMB_STARS = this.EMPERORS_TOMB_STARS;
        tc.GATE_OF_POWER_KILLS = this.GATE_OF_POWER_KILLS;
        tc.GRAND_BAZAR_CITIES = this.GRAND_BAZAR_CITIES;
        tc.ALTAR_OF_PEACE_TURNS = this.ALTAR_OF_PEACE_TURNS;
        tc.PARK_OF_FORTUNE_LEVEL = this.PARK_OF_FORTUNE_LEVEL;


        // Temple
        tc.TEMPLE_COST = this.TEMPLE_COST;
        tc.TEMPLE_FOREST_COST = this.TEMPLE_FOREST_COST;
        tc.TEMPLE_BONUS = this.TEMPLE_BONUS;
        tc.TEMPLE_TURNS_TO_SCORE = this.TEMPLE_TURNS_TO_SCORE;
        tc.TEMPLE_POINTS = this.TEMPLE_POINTS;

        //Resources
        tc.ANIMAL_COST = this.ANIMAL_COST;
        tc.FISH_COST = this.FISH_COST;
        tc.WHALES_COST = this.WHALES_COST;
        tc.FRUIT_COST = this.FRUIT_COST;
        tc.ANIMAL_POP = this.ANIMAL_POP;
        tc.FISH_POP = this.FISH_POP;
        tc.WHALES_STARS = this.WHALES_STARS;
        tc.FRUIT_POP = this.FRUIT_POP;

        // ROAD
        tc.ROAD_COST = this.ROAD_COST;

        // City
        tc.CITY_LEVEL_UP_WORKSHOP_PROD = this.CITY_LEVEL_UP_WORKSHOP_PROD;
        tc.CITY_LEVEL_UP_RESOURCES = this.CITY_LEVEL_UP_RESOURCES;
        tc.CITY_LEVEL_UP_POP_GROWTH = this.CITY_LEVEL_UP_POP_GROWTH;
        tc.CITY_LEVEL_UP_PARK = this.CITY_LEVEL_UP_PARK;
        tc.CITY_BORDER_POINTS = this.CITY_BORDER_POINTS;
        tc.PROD_CAPITAL_BONUS = this.PROD_CAPITAL_BONUS;
        tc.EXPLORER_CLEAR_RANGE = this.EXPLORER_CLEAR_RANGE;
        tc.FIRST_CITY_CLEAR_RANGE = this.FIRST_CITY_CLEAR_RANGE;
        tc.NEW_CITY_CLEAR_RANGE = this.NEW_CITY_CLEAR_RANGE;
        tc.CITY_EXPANSION_TILES = this.CITY_EXPANSION_TILES;
        tc.POINTS_PER_POPULATION = this.POINTS_PER_POPULATION;

        // Research
        tc.TECH_BASE_COST = this.TECH_BASE_COST;
        tc.TECH_DISCOUNT = this.TECH_DISCOUNT;
        tc.TECH_DISCOUNT_VALUE = this.TECH_DISCOUNT_VALUE;

        /* TRIBES */
        tc.INITIAL_STARS = this.INITIAL_STARS;//1000;

        /* ACTIONS */
        tc.CLEAR_FOREST_STAR = this.CLEAR_FOREST_STAR;
        tc.GROW_FOREST_COST = this.GROW_FOREST_COST;
        tc.BURN_FOREST_COST = this.BURN_FOREST_COST;
        tc.CLEAR_VIEW_POINTS = this.CLEAR_VIEW_POINTS;

        //MAP
        tc.DEFAULT_MAP_SIZE = this.DEFAULT_MAP_SIZE;

        return tc;


    }



}
