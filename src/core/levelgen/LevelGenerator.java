package core.levelgen;

import core.Types;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;

public class LevelGenerator {

    enum BASE_PROB {

        WATER(0.0),
        MOUNTAIN(0.15),
        FOREST(0.4),
        FRUIT(0.5),
        CROP(0.5),
        FISH(0.5),
        ANIMAL(0.5),
        WHALE(0.4),
        ORE(0.5);

        double baseProb;

        BASE_PROB(double baseProb) {
            this.baseProb = baseProb;
        }
    }

    //Level parameters, can be changed using init().
    private int mapSize;
    private int smoothing;
    private int relief;
    private double initialLand;
    private String[][] level;

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
        this.level = new String[mapSize][mapSize];

        //Initialize level with the colon tile separator.
        for(int i = 0; i < mapSize*mapSize; i++){ level[i/mapSize][i%mapSize] = ":"; };
    }

    /**
     * Generates the level.
     */
    public void generate() {

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
    public double _getTribeProb(BASE_PROB b, Types.TRIBE tribe) {
        return data.getJSONObject(b.toString()).getDouble(tribe.toString());
    }

    /**
     * Writes a level tile at position x, y.
     */
    public void writeTile(int x, int y, Types.TERRAIN terrain, Types.RESOURCE resource) {

        level[x][y] = (resource == null) ? ""+ terrain.getMapChar() + ':' : ""+ terrain.getMapChar() + ':' + resource.getMapChar();
    }

    public static void main(String[] args) {

        LevelGenerator gen = new LevelGenerator();

    }
}
