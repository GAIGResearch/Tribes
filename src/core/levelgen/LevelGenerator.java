package core.levelgen;

import core.Types;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;

//TODO add BASE_PROB to json
public class LevelGenerator {

    //Level parameters, can be changed using init().
    private int mapSize;
    private int smoothing;
    private int relief;
    private double initialLand;
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
        this.level = new String[mapSize];

        //Initialize level with the colon tile separator.
        for(int i = 0; i < mapSize*mapSize; i++){ level[i] = ":"; };
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
     * Writes a level tile at position x, y.
     */
    public void writeTile(int index, Types.TERRAIN terrain, Types.RESOURCE resource) {

        level[index] = (resource == null) ? ""+ terrain.getMapChar() + ':' : ""+ terrain.getMapChar() + ':' + resource.getMapChar();
    }

    public static void main(String[] args) {

        LevelGenerator gen = new LevelGenerator();

    }
}
