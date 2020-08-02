package core.levelgen;

import static core.Types.RESOURCE.*;
import static core.Types.TERRAIN.*;
import static core.Types.TRIBE.*;
//import static core.Types.RESOURCE.*;
import core.Types;
import org.json.JSONObject;
import utils.IO;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

/**
 * This is a Java port of the level generator created for the game Polytopia adapted to our format.
 * Original source: https://github.com/QuasiStellar/Polytopia-Map-Generator.
 */

//TODO: Clean up and write some comments.

public class LevelGenerator {

    //Level parameters, can be changed using init().
    private int mapSize;
    private int smoothing;
    private int relief;
    private double initialLand;
    private double landCoefficient;
    private String[] level;
    private Types.TRIBE[] tribes;
    private double BORDER_EXPANSION = 1/3.0;
    private long seed;
    private Random rnd;

    //JSON that contains all the probability values for all the tribes.
    private JSONObject data;

    private boolean LEVELGEN_VERBOSE = false;

    /**
     * Constructor of the generator
     */
    public LevelGenerator(long seed) {

        this.seed = seed;
        this.rnd = new Random(seed);

        //Initialize with default values.
        init(11, 3, 4, 0.5, new Types.TRIBE[]{XIN_XI, OUMAJI});

        //Read the JSON that contains all the probability values for all the tribes.
        try {
            this.data =  new IO().readJSON("terrainProbs.json");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes the map generation parameters.
     */
    public void init(int mapSize, int smoothing, int relief, double initialLand, Types.TRIBE[] tribes) {
        this.mapSize = mapSize;
        this.smoothing = smoothing;
        this.relief = relief;
        this.initialLand = initialLand;
        this.level = new String[mapSize*mapSize];
        this.tribes = tribes;
        this.landCoefficient = (0.5 + relief) / 9;

        //Initialize the level with deep water.
        for(int i = 0; i < mapSize*mapSize; i++){ level[i] = "d: "; };
    }

    /**
     * Generates the level.
     */
    public void generate() {

        if (LEVELGEN_VERBOSE) System.out.println("Generating level with seed: " + this.seed);

        //Randomly replace half of the tiles with ground.
        if (LEVELGEN_VERBOSE) System.out.println("Randomly replace half of the tiles with ground.");
        int i = 0;
        while(i < mapSize*mapSize*initialLand) {
            int index = randomInt(0, mapSize*mapSize);
            if(getTerrain(index) == DEEP_WATER.getMapChar()) {
                i++;
                writeTile(index, ""+PLAIN.getMapChar(), null);
            }
        }

        //Turning random water/ground grid into something smooth.
        if (LEVELGEN_VERBOSE) System.out.println("Turning random water/ground grid into something smooth.");
        ArrayList<Integer> toBeGround = new ArrayList<>();

        for (i = 0; i < smoothing; i++) {
            for (int cell = 0; cell < mapSize * mapSize; cell++) {

                int water_count = 0;
                int tile_count = 0;
                for (int n : disk(cell, 1)) {
                    if (getTerrain(n) == DEEP_WATER.getMapChar()) {
                        water_count++;
                    }
                    tile_count++;
                }

                if (water_count / (double) tile_count <= landCoefficient)
                    toBeGround.add(cell);
            }

            for (int cell = 0; cell < mapSize * mapSize; cell++) {
                if(toBeGround.contains(cell))
                {
                    writeTile(cell, ""+PLAIN.getMapChar(), null);
                }else {
                    writeTile(cell, "" + DEEP_WATER.getMapChar(), null);
                }
            }
        }

        // Capital distribution
        if (LEVELGEN_VERBOSE) System.out.println("Capital distribution");
        ArrayList<Integer> capitalCells = new ArrayList<>();
        HashMap<Integer, Integer> capitalMap = new HashMap<>();
        // make a map of potential (ground) tiles associated with numbers (0 by default)
        for (Types.TRIBE tribe : tribes) {
            for (int row = 2; row < mapSize - 2; row++) {
                for (int column = 2; column < mapSize - 2; column++) {
                    if (getTerrain(row * mapSize + column) == PLAIN.getMapChar()) {
                        capitalMap.put(row * mapSize + column, 0);
                    }
                }
            }
        }
        for (Types.TRIBE tribe : tribes) {
            int max = 0;
            // this number is a sum of distances between the tile and all capitals
            Iterator capitalIterator = capitalMap.entrySet().iterator();
            while (capitalIterator.hasNext()) {
                Map.Entry cell = (Map.Entry)capitalIterator.next();
                cell.setValue(mapSize);
                for (int capital_cell : capitalCells) {
                    cell.setValue(Math.min((int)cell.getValue(), distance((int)cell.getKey(), capital_cell, mapSize)));
                }
                max = Math.max(max, (int)cell.getValue());
            }
            //Count how many potential capital positions are at a maximum distance
            int len = 0;
            capitalIterator = capitalMap.entrySet().iterator();
            while (capitalIterator.hasNext()) {
                Map.Entry cell = (Map.Entry)capitalIterator.next();
                if ((int)cell.getValue() == max) {
                    len++;
                }
            }

            // we want to find a tile with a maximum sum
            int randCell = randomInt(0, len);
            capitalIterator = capitalMap.entrySet().iterator();
            while (capitalIterator.hasNext()) {
                Map.Entry cell = (Map.Entry) capitalIterator.next();
                if ((int)cell.getValue() == max) {
                    if (randCell == 0) {
                        capitalCells.add((int)cell.getKey());
                        if(LEVELGEN_VERBOSE) System.out.println("Adding a capital for tribe " + tribe + " at tile " + (int)cell.getKey() + " with a max distance of " + cell.getValue());
                    }
                    randCell--;
                }
            }
        }
        for (i = 0; i < capitalCells.size(); i++) {
            writeTile((capitalCells.get(i) / mapSize) * mapSize + (capitalCells.get(i) % mapSize), ""+CITY.getMapChar(), String.valueOf(tribes[i].getKey()));
        }

        // Terrain distribution
        if (LEVELGEN_VERBOSE) System.out.println("Terrain distribution");
        ArrayList<Integer> doneTiles = new ArrayList<>();
        ArrayList<ArrayList<Integer>> activeTiles = new ArrayList<>(); // done tiles that generate terrain around them
        Types.TRIBE[] tileOwner = new Types.TRIBE[mapSize*mapSize];

        for (i = 0; i < capitalCells.size(); i++) {
            doneTiles.add(i, capitalCells.get(i));
            ArrayList<Integer> cap = new ArrayList<>();
            cap.add(capitalCells.get(i));
            activeTiles.add(i, cap);
        }
        // We will start from capital tiles and evenly expand until the whole map is covered
        while (doneTiles.size() != mapSize*mapSize) {
            for (i = 0; i < tribes.length; i++) {
                if (activeTiles.get(i).size() != 0) {
                    int randNumber = randomInt(0, activeTiles.get(i).size());
                    int randCell = activeTiles.get(i).get(randNumber);

                    ArrayList<Integer> neighbours = circle(randCell, 1);

                    ArrayList<Integer> validNeighbours = new ArrayList<>();
                    for(int n : neighbours){
                        if(!doneTiles.contains(n) && getTerrain(n) != DEEP_WATER.getMapChar()){
                            validNeighbours.add(n);
                        }
                    }
                    // If there are no land tiles around, accept water tiles
                    if (validNeighbours.size() == 0) {
                        for(int n : neighbours){
                            if(!doneTiles.contains(n)){
                                validNeighbours.add(n);
                            }
                        }
                    }
                    if (validNeighbours.size() != 0) {
                        int new_rand_number = randomInt(0, validNeighbours.size());
                        int new_rand_cell = validNeighbours.get(new_rand_number);
                        tileOwner[new_rand_cell] = tribes[i];
                        activeTiles.get(i).add(new_rand_cell);
                        doneTiles.add(new_rand_cell);
                    } else {
                        activeTiles.get(i).remove(randNumber); // deactivate tiles surrounded with done tiles
                    }
                }
            }
        }

        // Generate forest, mountains.
        if (LEVELGEN_VERBOSE) System.out.println("Generate forest, mountains");
        for (int cell = 0; cell < mapSize*mapSize; cell++) {
            if (getTerrain(cell) == PLAIN.getMapChar()) {
                double rand = rnd.nextDouble(); // 0 (---forest---)--nothing--(-mountain-) 1
                if (rand < getBaseProb("FOREST") * getTribeProb("FOREST", tileOwner[cell])) {
                    writeTile(cell,""+FOREST.getMapChar(), null);
                } else if (rand > 1 - getBaseProb("MOUNTAIN") * getTribeProb("MOUNTAIN", tileOwner[cell])) {
                    writeTile(cell,""+MOUNTAIN.getMapChar(), null);
                }
            }
        }

        ArrayList<Integer> villageMap = new ArrayList<Integer>(mapSize*mapSize);

        // Initialize with zeros.
        for(i = 0; i < mapSize*mapSize; i++) {
            villageMap.add(0);
        }

        // -1 - water far away
        // 0 - far away
        // 1 - border expansion
        // 2 - initial territory
        // 3 - village
        for (int cell = 0; cell < mapSize*mapSize; cell++) {
            int row = cell / mapSize;
            int column = cell % mapSize;
            if (getTerrain(cell) == DEEP_WATER.getMapChar() || getTerrain(cell) == MOUNTAIN.getMapChar()) {
                villageMap.set(cell, -1);
            } else if (row == 0 || row == mapSize - 1 || column == 0 || column == mapSize - 1) {
                villageMap.set(cell, -1); // villages don't spawn next to the map border
            } else {
                villageMap.set(cell, 0);
            }
        }

        // Replace some ocean with shallow water
        if (LEVELGEN_VERBOSE) System.out.println("Replace some ocean with shallow water");
        for (int cell = 0; cell < mapSize*mapSize; cell++) {
            if (getTerrain(cell) == DEEP_WATER.getMapChar()) {
                for (int neighbour : crossNeighbors(cell)) {
                    char terrainN = getTerrain(neighbour);
                    if(terrainN == PLAIN.getMapChar() || terrainN == FOREST.getMapChar() || terrainN == MOUNTAIN.getMapChar()){
                        writeTile(neighbour, ""+SHALLOW_WATER.getMapChar(), null);
                        break;
                    }
                }
            }
        }

        // Mark tiles next to capitals according to the notation
        int villageCount = 0;
        for (int capital : capitalCells){
            villageMap.set(capital, 3);
            for (int cell : circle(capital, 1)){
                villageMap.set(cell, Math.max(villageMap.get(cell), 2));
            }
            for (int cell : circle(capital, 2)){
                villageMap.set(cell, Math.max(villageMap.get(cell), 1));
            }
        }

        // Generate villages & mark tiles next to them
        // We will place villages until there are none of "far away" (villageMap == 0) tiles.
        while(villageMap.contains(0)) {
            int new_village = villageMap.indexOf(0);
            villageMap.set(new_village, 3);
            for (int cell : circle(new_village, 1)) {
                villageMap.set(cell, Math.max(villageMap.get(cell), 2));
            }
            for (int cell : circle(new_village, 2)) {
                villageMap.set(cell, Math.max(villageMap.get(cell), 1));
            }
        }

        // Generate resources
        if (LEVELGEN_VERBOSE) System.out.println("Generate resources");
        for (int cell = 0; cell < mapSize*mapSize; cell++) {
            if(getTerrain(cell) == PLAIN.getMapChar()) {
                double fruit = getBaseProb("FRUIT") * getTribeProb("FRUIT", tileOwner[cell]);
                double crop = getBaseProb("CROPS") * getTribeProb("CROPS", tileOwner[cell]);
                if (getTerrain(cell) != CITY.getMapChar()) {
                    if (villageMap.get(cell) == 3) {
                        writeTile(cell, ""+VILLAGE.getMapChar(), null);
                    } else if (proc(villageMap, cell, fruit * (1 - crop / 2))) {
                        writeTile(cell, null, ""+FRUIT.getMapChar());
                    } else if (proc(villageMap, cell, crop * (1 - fruit / 2))) {
                        writeTile(cell, null, ""+CROPS.getMapChar());
                    }
                }
            } else if(getTerrain(cell) == FOREST.getMapChar()) {
                if (getTerrain(cell) != CITY.getMapChar()) {
                    if (villageMap.get(cell) == 3) {
                        writeTile(cell, ""+VILLAGE.getMapChar(), " ");
                    } else if (proc(villageMap, cell, getBaseProb("ANIMAL") * getTribeProb("ANIMAL", tileOwner[cell]))) {
                        writeTile(cell, null, ""+ANIMAL.getMapChar());
                    }
                }
            } else if(getTerrain(cell) == SHALLOW_WATER.getMapChar()) {
                if (proc(villageMap, cell, getBaseProb("FISH") * getTribeProb("FISH", tileOwner[cell]))) {
                    writeTile(cell, null, ""+FISH.getMapChar());
                }
            } else if(getTerrain(cell) == DEEP_WATER.getMapChar()) {
                if (proc(villageMap, cell, getBaseProb("WHALES") * getTribeProb("WHALES", tileOwner[cell]))) {
                    writeTile(cell, null, ""+WHALES.getMapChar());
                }
            } else if(getTerrain(cell) == MOUNTAIN.getMapChar()) {
                if (proc(villageMap, cell, getBaseProb("ORE") * getTribeProb("ORE", tileOwner[cell]))) {
                    writeTile(cell, null, ""+ORE.getMapChar());
                }
            }
        }

        // Ruins generation.
        if (LEVELGEN_VERBOSE) System.out.println("Ruins generation");

        int ruins_number = (int) Math.round((mapSize*mapSize)/40.0);
        int water_ruins_number = (int) Math.round(ruins_number/3.0);
        int ruins_count = 0;
        int water_ruins_count = 0;


        while (ruins_count < ruins_number) {

            // We are reusing villageMap even though it is irrelevant in this context but it has useful info for ruin placement.
            ArrayList<Integer> ruinCandidates = new ArrayList<>();
            for(i=0; i < villageMap.size(); i++) {
                int cell = villageMap.get(i);
                if(cell == 0 || cell == 1 || cell == -1) {
                    ruinCandidates.add(i);
                }
            }

            int ruin = ruinCandidates.get(randomInt(0,ruinCandidates.size()));
            if (getTerrain(ruin) != SHALLOW_WATER.getMapChar() && (water_ruins_count < water_ruins_number || getTerrain(ruin) != DEEP_WATER.getMapChar())) {
                writeTile(ruin, null, ""+RUINS.getMapChar());
                if (getTerrain(ruin) == DEEP_WATER.getMapChar()) {
                    water_ruins_count++;
                }

                //This avoids having contiguous ruins and favours dispersion.
                for (int neighbour : circle(villageMap.get(ruin), 1)) {
                    villageMap.set(neighbour, Math.max(villageMap.get(neighbour), 2));
                }

                ruins_count++;
            }
        }

        // Re-adjust starting tiles around capitals
        if (LEVELGEN_VERBOSE) System.out.println("Re-adjust starting tiles around capitals");
        for(int capital : capitalCells) {
            int owner = Integer.parseInt(getResource(capital));

            if(owner == (char)IMPERIUS.getKey()) {
                postGenerate(FRUIT.getMapChar(), PLAIN.getMapChar(), 2, capital);
            } else if(owner == (char)BARDUR.getKey()) {
                postGenerate(ANIMAL.getMapChar(), FOREST.getMapChar(), 2, capital);
            }
        }
    }

    /**
     * Counts the instances of a resource that exists on the starting tiles that surround a capital.
     * @param resource the resource to be counted.
     * @param capital the index of the capital.
     * @return the resource counter.
     */
    public int checkResources(char resource, int capital) {
        int resources = 0;
        for (int neighbour : circle(capital, 1)) {
            String resourceStr = getResource(neighbour);
            if(resourceStr.length() > 0 && resourceStr.charAt(0) == resource){
                resources++;
            }
        }
        return resources;
    }

    /**
     * Adds the required amount of a specific resource on specific type of terrain
     * in the starting tiles that surround a capital.
     * @param resource the resource to be added.
     * @param terrain the terrain on top of which the resource will be added.
     * @param quantity the amount to be tiles that must have this terrain + resource combination.
     */
    public void postGenerate(char resource, char terrain, int quantity, int capital) {
        int resources = checkResources(resource, capital);
        while (resources < quantity) {
            int pos = randomInt(0, 8);
            ArrayList<Integer> territory = circle(capital, 1);
            writeTile(territory.get(pos), ""+terrain, ""+resource);
            for (int neighbour : crossNeighbors(territory.get(pos))) {
                if (getTerrain(neighbour) == DEEP_WATER.getMapChar()) {
                    writeTile(neighbour, ""+SHALLOW_WATER.getMapChar(), null);
                }
            }
            resources = checkResources(resource, capital);
        }
    }
    /**
     * Utility function used in the generator.
     */
    public boolean proc(ArrayList<Integer> villageMap, int cell, double probability) {
        return (villageMap.get(cell) == 2 && rnd.nextDouble() < probability) || (villageMap.get(cell) == 1 && rnd.nextDouble() < probability * BORDER_EXPANSION);
    }

    /**
     * Reads the JSON configuration file and returns the probability of a terrain or a resource for a specific tribe.
     * @param name the name of the terrain or resource.
     * @param tribe the name of the tribe.
     * @return the probability.
     */
    public double getTribeProb(String name, Types.TRIBE tribe) {
        if(tribe == null) {
            return 1.0;
        } else {
            return data.getJSONObject(name.toString()).getDouble(tribe.toString());
        }
    }

    /**
     * Reads the JSON configuration file and returns the base probability of a specific terrain or resource.
     * @param name the name of the terrain or resource.
     * @return the base probability.
     */
    public double getBaseProb(String name) {
        return data.getJSONObject(name.toString()).getDouble("BASE");
    }

    /**
     * Writes a level tile at a specified position (consult the TERRAIN and RESOURCE enums).
     * @param index the index of the tile that needs to be written.
     * @param terrain the desired type of terrain.
     * @param resource the desired type of resource.
     */
    public void writeTile(int index, String terrain, String resource) {
        if(terrain == null) {
            level[index] = "" + getTerrain(index) + ':' + resource;
        }else if(resource == null) {
            level[index] = "" + terrain + ':' + getResource(index);
        }else {
            level[index] = "" + terrain + ':' + resource;
        }
    }

    /**
     * Returns a tile's terrain at a specified position.
     * @param index the desired position.
     * @return the character that represents the specific terrain (consult TERRAIN enum).
     */
    public char getTerrain(int index) {
        return level[index].split(":")[0].charAt(0);
    }


//    public char getResource(int index) {
//        return level[index].split(":")[1].charAt(0);
////        try {
////            return level[index].split(":")[1].charAt(0);
////        } catch(Exception e) {
////            return '';
////        }
//    }
    /**
     * Returns a tile's resource at a specified position.
     * @param index the desired position.
     * @return the character that represents the specific resource (consult RESOURCE enum).
     */
    public String getResource(int index)
    {
        String[] pieces = level[index].split(":");
        if(pieces.length > 1)
            if(pieces[1].charAt(0) == ' ')
                return "";
            else return pieces[1];
        else return "";
    }

    /**
     * Returns a random int in the range [min, max).
     * @param min lower bound (inclusive).
     * @param max upper bound (exclusive).
     * @return a random int.
     */
    public int randomInt(int min, int max) {
        return (int) Math.floor(min + rnd.nextDouble() * (max - min));
    }

    /**
     * Returns the indices of the map that lie on a circle.
     * @param center center of the circle.
     * @param radius radius of the circle.
     * @return an ArrayList of indices.
     */
    public ArrayList<Integer> circle(int center, int radius) {
        ArrayList<Integer> circle = new ArrayList<>();
        int row = center / mapSize;
        int column = center % mapSize;
        int i = row - radius;
        if (i >= 0 && i < mapSize) {
            for (int j = column - radius; j < column + radius; j++) {
                if (j >= 0 && j < mapSize) {
                    circle.add(i * mapSize + j);
                }
            }
        }
        i = row + radius;
        if (i >= 0 && i < mapSize) {
            for (int j = column + radius; j > column - radius; j--) {
                if (j >= 0 && j < mapSize) {
                    circle.add(i * mapSize + j);
                }
            }
        }
        int j = column - radius;
        if (j >= 0 && j < mapSize) {
            for (i = row + radius; i > row - radius; i--) {
                if (i >= 0 && i < mapSize) {
                    circle.add(i * mapSize + j);
                }
            }
        }
        j = column + radius;
        if (j >= 0 && j < mapSize) {
            for (i = row - radius; i < row + radius; i++) {
                if (i >= 0 && i < mapSize) {
                    circle.add(i * mapSize + j);
                }
            }
        }
        return circle;
    }

    /**
     * Returns the indices of the map that lie on and inside a circle including the center.
     * @param center center of the circle.
     * @param radius radius of the circle.
     * @return an ArrayList of indices.
     */
    public ArrayList<Integer> disk(int center, int radius) {
        ArrayList<Integer> round = new ArrayList<>();
        for (int r = 1; r <= radius; r++) {
            round.addAll(circle(center, r));
        }
        round.add(center);
        return round;
    }

    /**
     * Returns the indices of the map that lie on the cross pattern.
     * @param center center of the cross.
     * @return an ArrayList of indices.
     */
    public ArrayList<Integer> crossNeighbors(int center) {
        ArrayList<Integer> plus_sign = new ArrayList<>();
        int row = center / mapSize;
        int column = center % mapSize;
        if (column > 0) {
            plus_sign.add(center - 1);
        }
        if (column < mapSize - 1) {
            plus_sign.add(center + 1);
        }
        if (row > 0) {
            plus_sign.add(center - mapSize);
        }
        if (row < mapSize - 1) {
            plus_sign.add(center + mapSize);
        }
        return plus_sign;
    }

    // we use pythagorean distances
    public int distance(int a, int b, int size) {
        int ax = a % size;
        int ay = a / size;
        int bx = b % size;
        int by = b / size;
        return Math.max(Math.abs(ax - bx), Math.abs(ay - by));
    }

    /**
     * Saves the generated level into a .csv format readable by the Tribes framework.
     * @param filename path to save the level.
     */
    public void toCSV(String filename) {
        try {
            FileWriter writer = new FileWriter(filename);
            writer.append(level[0]);
            writer.append(',');
            for(int i = 1; i < mapSize*mapSize; i++) {
                if(i % mapSize == 0) {
                    writer.append('\n');
                    writer.append(level[i]);
                    writer.append(',');
                } else if(i % mapSize == mapSize - 1) {
                    writer.append(level[i]);
                }else {
                    writer.append(level[i]);
                    writer.append(',');
                }
            }
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Prints the generated level in console.
     */
    public void print() {
        StringBuffer writer = new StringBuffer();
        writer.append(level[0]);
        writer.append(',');
        for (int i = 1; i < mapSize * mapSize; i++) {
            if (i % mapSize == 0) {
                writer.append('\n');
                writer.append(level[i]);
                writer.append(',');
            } else if (i % mapSize == mapSize - 1) {
                writer.append(level[i]);
            } else {
                writer.append(level[i]);
                writer.append(',');
            }
        }
        System.out.println(writer.toString());
    }

    /**
     * Returns the generated level into a format readable by the Tribes framework.
     */
    public String[] gelLevelLines()
    {
        String[] allLines = new String[mapSize];
        int lineCounter = 0;

        StringBuffer line = new StringBuffer();
        line.append(level[0]);
        line.append(',');
        for (int i = 1; i < mapSize * mapSize; i++) {
            if (i % mapSize == mapSize - 1) {
                line.append(level[i]);
                allLines[lineCounter] = line.toString();
                lineCounter++;
                line = new StringBuffer();
            } else {
                line.append(level[i]);
                line.append(',');
            }
        }
        return allLines;
    }

    public static void main(String[] args) {

        long genSeed = System.currentTimeMillis();
        LevelGenerator gen = new LevelGenerator(genSeed);
        gen.init(11, 3, 4, 0.5, new Types.TRIBE[]{XIN_XI, OUMAJI});
        gen.generate();
        gen.toCSV("levels/levelgen_test.csv");
        gen.print();
        
    }
}
