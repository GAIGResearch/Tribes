package core.levelgen;

import static core.Types.RESOURCE.FRUIT;
import static core.Types.TERRAIN.*;
import static core.Types.TRIBE.*;

import core.Types;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class LevelGenerator {

    //Level parameters, can be changed using init().
    private int mapSize;
    private int smoothing;
    private int relief;
    private double initialLand;
    private double landCoefficient;
    private String[] level;

    //JSON that contains all the probability values for all the tribes.
    private JSONObject data;

    /**
     * Constructor of the generator
     */
    public LevelGenerator() {

        //Initialize with default values.
        init(11, 3, 4, 0.5);

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
    public void init(int mapSize, int smoothing, int relief, double initialLand) {
        this.mapSize = mapSize;
        this.smoothing = smoothing;
        this.relief = relief;
        this.initialLand = initialLand;
        this.level = new String[mapSize*mapSize];
        this.landCoefficient = (0.5 + relief) / 9;

        //Initialize the level with deep water.
        for(int i = 0; i < mapSize*mapSize; i++){ level[i] = "d:"; };
    }

    /**
     * Generates the level.
     */
    public void generate() {

        //Randomly replace half of the tiles with ground.
        int i = 0;
        while(i < mapSize*mapSize*initialLand) {
            int index = randomInt(0, mapSize*mapSize);
            if(getTerrain(index) == DEEP_WATER.getMapChar()) {
                i++;
                writeTile(index, PLAIN, null);
            }
        }

        //Turning random water/ground grid into something smooth.
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
                    writeTile(cell, PLAIN, null);
                } else {
                    writeTile(cell, DEEP_WATER, null);
                }
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
     * Returns probability of a specific tile type for a specific tribe.
     */
    public double getTribeProb(String name, Types.TRIBE tribe) {
        return data.getJSONObject(name.toString()).getDouble(tribe.toString());
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
    public void writeTile(int index, Types.TERRAIN terrain, Types.RESOURCE resource) {

        level[index] = (resource == null) ? ""+ terrain.getMapChar() + ':' : ""+ terrain.getMapChar() + ':' + resource.getMapChar();
    }

    /**
     * Returns a tile's terrain at index
     */
    public char getTerrain(int index) {
        return level[index].split(":")[0].charAt(0);
    }

    /**
     * Returns a random int in the range [min, max)
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

    public static void main(String[] args) {

        LevelGenerator gen = new LevelGenerator();
        gen.writeTile(55, MOUNTAIN, FRUIT);
        System.out.println(gen.getTerrain(55));
    }
}
