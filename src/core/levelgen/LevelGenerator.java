package core.levelgen;

import static core.Types.TERRAIN.*;
import static core.Types.TRIBE.*;

import core.Types;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class LevelGenerator {

    //Level parameters, can be changed using init().
    private int mapSize;
    private int smoothing;
    private int relief;
    private double initialLand;
    private double landCoefficient;
    private String[] level;
    private Types.TRIBE[] tribes;

    //JSON that contains all the probability values for all the tribes.
    private JSONObject data;

    /**
     * Constructor of the generator
     */
    public LevelGenerator() {

        //Initialize with default values.
        init(11, 3, 4, 0.5, new Types.TRIBE[]{XIN_XI, OUMAJI});

        //Read the JSON that contains all the probability values for all the tribes.
        try {
            this.data = new JSONObject(_readFile("src/core/levelgen/terrainProbs.json"));
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

        //Randomly replace half of the tiles with ground.
        System.out.println("Randomly replace half of the tiles with ground.");
        int i = 0;
        while(i < mapSize*mapSize*initialLand) {
            int index = randomInt(0, mapSize*mapSize);
            if(getTerrain(index) == DEEP_WATER.getMapChar()) {
                i++;
                writeTile(index, ""+PLAIN.getMapChar(), null);
            }
        }

        //Turning random water/ground grid into something smooth.
        System.out.println("Turning random water/ground grid into something smooth.");
        for (i = 0; i < smoothing; i++) {
            for (int cell = 0; cell < mapSize * mapSize; cell++) {

                int water_count = 0;
                int tile_count = 0;
                for (int n : round(cell, 1)) {
                    if (getTerrain(n) == DEEP_WATER.getMapChar()) {
                        water_count++;
                    }
                    tile_count++;
                }
                if (water_count / (double) tile_count <= landCoefficient) {
                    writeTile(cell, ""+PLAIN.getMapChar(), null);
                } else {
                    writeTile(cell, ""+DEEP_WATER.getMapChar(), null);
                }
            }
        }

        // Capital distribution
        System.out.println("Capital distribution");
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
                    cell.setValue(Math.min((int)cell.getValue(), distance((int)cell.getValue(), capital_cell, mapSize)));
                }
                max = Math.max(max, (int)cell.getValue());
            }

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
                    }
                    randCell--;
                }
            }
        }
        for (i = 0; i < capitalCells.size(); i++) {
            writeTile((capitalCells.get(i) / mapSize) * mapSize + (capitalCells.get(i) % mapSize), ""+CITY.getMapChar(), null);
        }

        // Terrain distribution
        System.out.println("Terrain distribution");
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
        System.out.println("Generate forest, mountains");
        for (int cell = 0; cell < mapSize*mapSize; cell++) {
            if (getTerrain(cell) == PLAIN.getMapChar()) {
                double rand = Math.random(); // 0 (---forest---)--nothing--(-mountain-) 1
                if (rand < getBaseProb("FOREST") * getTribeProb("FOREST", tileOwner[cell])) {
                    writeTile(cell, null, ""+FOREST.getMapChar());
                } else if (rand > 1 - getBaseProb("MOUNTAIN") * getTribeProb("MOUNTAIN", tileOwner[cell])) {
                    writeTile(cell, null, ""+MOUNTAIN.getMapChar());
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
        System.out.println("Replace some ocean with shallow water");
        for (int cell = 0; cell < mapSize*mapSize; cell++) {
            if (getTerrain(cell) == DEEP_WATER.getMapChar()) {
                for (int neighbour : plusSign(cell)) {
                    if (getTerrain(neighbour) == DEEP_WATER.getMapChar()) {
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
        // We will place villages until there are none of 'far away'(villageMap == 0) tiles
        System.out.println("lele");
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
        
    }

    /**
     * Reads 'filename' and returns it as a String.
     * @param filename path to the file.
     * @return the file as a String.
     */
    private String _readFile(String filename) {
        String result = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                line = br.readLine();
            }
            result = sb.toString();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Returns the probability of a specific tile type for a specific tribe.
     */
    public double getTribeProb(String name, Types.TRIBE tribe) {
        if(tribe == null) {
            return 1.0;
        } else {
            return data.getJSONObject(name.toString()).getDouble(tribe.toString());
        }
    }

    /**
     * Returns the base probability of a specific tile type.
     */
    public double getBaseProb(String name) {
        return data.getJSONObject(name.toString()).getDouble("BASE");
    }

    /**
     * Writes a level tile at position index.
     */
    public void writeTile(int index, String type1, String type2) {
        if(type1 == null) {
            level[index] = "" + getTerrain(index) + ':' + type2;
        }else if(type2 == null) {
            level[index] = "" + type1 + ':' + getResource(index);
        }else {
            level[index] = "" + type1 + ':' + type2;
        }
    }

    /**
     * Returns a tile's terrain at index
     */
    public char getTerrain(int index) {
        return level[index].split(":")[0].charAt(0);
    }

    /**
     * Returns a tile's resource at index
     */
    public char getResource(int index) {
        return level[index].split(":")[1].charAt(0);
//        try {
//            return level[index].split(":")[1].charAt(0);
//        } catch(Exception e) {
//            return '';
//        }
    }

    /**
     * Returns a random int in the range [min, max).
     * @param min lower bound (inclusive).
     * @param max upper bound (exclusive).
     * @return a random int.
     */
    public int randomInt(int min, int max) {
        return (int) Math.floor(min + Math.random() * (max - min));
    }

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

    public ArrayList<Integer> round(int center, int radius) {
        ArrayList<Integer> round = new ArrayList<>();
        for (int r = 1; r <= radius; r++) {
            round.addAll(circle(center, r));
        }
        round.add(center);
        return round;
    }

    public ArrayList<Integer> plusSign(int center) {
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

    public void toCSV() {
        try {
            FileWriter writer = new FileWriter("src/core/levelgen/test.csv");
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

    public static void main(String[] args) {

        LevelGenerator gen = new LevelGenerator();
        gen.init(12, 3, 4, 0.5, new Types.TRIBE[]{XIN_XI, OUMAJI});
        gen.generate();
        gen.toCSV();

    }
}
