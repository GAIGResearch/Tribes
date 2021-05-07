/**
 * Author: Cristina Guerrero
 * Date: 5th February 2021
 */

package utils.mapelites;

import core.Constants;
import utils.stats.GameplayStats;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Contains the definition of the MAP-Elites as well as the methods required to
 * obtain the performance and features of a certain agent-heuristics
 * combination.
 * 
 * For the MAP-Elites algorithm adaptation for automatic gameplay agents, the
 * performance and features information is retrieved from the stats of the game.
 * The specifics of the elements to consider for these will depend on the
 * configuration set when running the algorithm
 */
public class MapElites {

    private Feature[] features;
    private ArrayList<EliteIdx> occupiedCellsIdx;
    private Map mapElites;
    private MapRecord mapElitesRecord;
    private int nWeights;
    boolean ELITE_VERBOSE = true;
    boolean master = false;
    boolean fileBased = false;
    Path path;

    private class EliteIdx {
        int[] coordinates;

        EliteIdx(int[] coord) {
            coordinates = new int[coord.length];
            System.arraycopy(coord, 0, coordinates, 0, coord.length);
        }
    }

    public MapElites(Feature[] features, int nWeights, boolean master, Path path, boolean fileBased) {
        mapElites = new Map(features);
        mapElitesRecord = new MapRecord(features);
        occupiedCellsIdx = new ArrayList<>();
        this.features = features;
        this.nWeights = nWeights;
        this.master = master;
        this.path = path;
        this.fileBased = true;
    }

    /**
     * Initialise map. Randomise nRandomInitialisations configurations (weights) and assign the resulting
     * agents to the correspondent cell in the MAP
     * @param nRandomInitialisations number of random initialisations requested
     */
    private void initialiseMap(int nRandomInitialisations, Runner runner) {
        // Create an elite with only one of each of the behaviours, set to 1.0 and the others to 0.0
        double genome[] = new double[nWeights];

        //Master runs initialize weights only:
        if(master) {
            for (int i = 0; i < nWeights; i++) {
                if (ELITE_VERBOSE) System.out.println("MAPELites initialisation with behaviour for weight in " + i);
                // Set all the weights to 0.0 and the current one to 1.0
                Arrays.fill(genome, 0.0);
                genome[i] = 1.0;

                // Create elite for the current behaviour
                createGameplayElite(genome, runner);

            }

        //Non master runs initalize at random
        }else {
            // Random initialisations
            for (int i = 1; i < (nRandomInitialisations + 1); i++) {
                if (ELITE_VERBOSE) System.out.println("MAPELites initialisation iteration " + i);
                Generator.setRandomWeights(genome, 0.0, 0.25, 1.0);

                // Create elite from random values and add to map
                createGameplayElite(genome, runner);
            }
        }

        this.refreshMapFromPath();
        if(ELITE_VERBOSE) System.out.println("MAPElites initialised: " + getNCellsOccupied() + " cells occupied\n");
    }

    /**
     * MAP elites algorithm - iterate nIterations times
     * 1) get random elite from map
     * 2) evolve weights taking random elite as base
     * 3) create new elite (and get its stats to be able to get features and performance)
     * 4) add elite to map (assign to cell and replace elite in assigned cell if the new performance is better)
     * @param nTotalIterations number of iterations of the map elites algorithm
     */
    public void runAlgorithm(int nTotalIterations, int nRandomInitialisations, Runner runner, String runStr) {

        this.initialiseMap(nRandomInitialisations, runner);
        printMapElitesInfo("starting_map_" + runStr + ".txt");

        int nIteration = 0;
        double[] genome = new double[nWeights];
        while(nIteration < nTotalIterations) {
            if(ELITE_VERBOSE) System.out.println("MAPELites algorithm iteration " + (nIteration + 1) + "/" + nTotalIterations);
            
            // get random cell elite and a copy of its weights
            EliteIdx eliteIdx = (EliteIdx) Generator.getRandomElementFromArray(occupiedCellsIdx);
            if(fileBased)
            {
                EliteRecord eliteR = mapElitesRecord.getCell(eliteIdx.coordinates);
                System.arraycopy(eliteR.weights, 0, genome, 0, eliteR.weights.length);

            }else
            {
                Elite elite = mapElites.getCell(eliteIdx.coordinates);
                System.arraycopy(elite.genome, 0, genome, 0, elite.genome.length);
            }

            // evol weights
            Generator.stochasticHillClimberMutation(genome, 0.0, 0.25, 1.0);

            // Create new possible elite and add to map
            createGameplayElite(genome, runner);

            nIteration++;

            this.refreshMapFromPath();
            printMapElitesInfo("map_" + runStr + "_" + nIteration + ".txt");
        }

    }

    /**
     * Make all the data of the elites available: calculate all stats and set the data needed for serialisation.
     * While the algorithm is running, not all gameStats data is being calculated so it is needed to got through
     * all the occupied cells in the map to process all the data of the elite so it is available when their
     * information is printed or serialised.
     */
    public void processMapElitesData() {
        for (EliteIdx eliteIdx : occupiedCellsIdx) {
            Elite elite = mapElites.getCell(eliteIdx.coordinates);
            elite.calculateAllStats();
        }
    }

    private int getNCellsOccupied() {
        return occupiedCellsIdx.size();
    }

    private void createGameplayElite(double[] genome, Runner runner) {

        ArrayList<GameplayStats> allStats = runner.run(genome);

        // Create elite with information and results
        Elite elite = new Elite(genome, allStats);

        for (Feature feature : features) {
            elite.setFeatureValue(feature);
        }

        if(fileBased)  elite.saveToFile(path);
        else           addEliteToMap(elite);
    }

    private void refreshMapFromPath()
    {
        File f = new File(path.toString());
        HashMap<String, Integer> filenamesInMap = new HashMap<>();
        mapElitesRecord = new MapRecord(features);
        occupiedCellsIdx = new ArrayList<>();

        int num = 0;
        for(File file : f.listFiles())
        {
            String fileName = file.getName();
            String name = fileName.substring(0, fileName.length()-4); //extension out.
            int lastHyp = name.lastIndexOf("-");
            int idx = Integer.parseInt(name.substring(lastHyp+1));
            String mapCell = name.substring(0, lastHyp);

            if(filenamesInMap.containsKey(mapCell))
            {
                int inMapIdx = filenamesInMap.get(mapCell);
                if(idx > inMapIdx)
                    filenamesInMap.put(mapCell, idx);
            }else{
                filenamesInMap.put(mapCell, idx);
            }
        }

        //We have the elites, let's process them:
        for(String key : filenamesInMap.keySet())
        {
            String filename = path.toString() + "/" + key + "-" + filenamesInMap.get(key) + ".txt";
            EliteRecord eliteInFile = new EliteRecord();
            eliteInFile.readFromFile(filename);

            String[] coordStr = key.split("-");
            int[] coord = new int[coordStr.length];
            for(int i = 0; i < coord.length; i++) coord[i] = Integer.parseInt(coordStr[i]);

            mapElitesRecord.setCell(coord, eliteInFile);
            occupiedCellsIdx.add(new EliteIdx(coord));
        }

    }

    private void addEliteToMap(Elite elite) {

        int[] featuresIdx = new int[features.length];
        for(int i = 0; i < features.length; ++i)
        {
            featuresIdx[i] = elite.getFeatureIdx(features[i]);
        }

        Elite currentElite = mapElites.getCell(featuresIdx);

        if(ELITE_VERBOSE) {
            System.out.print("New elite w/ weights: " + elite.printWeights() + " --> Cell (");
            for (int i : featuresIdx) System.out.print(i + ", ");
            System.out.println(")");
            elite.printInfoConsole();
        }

        if (currentElite == null) {
            mapElites.setCell(featuresIdx, elite);
            occupiedCellsIdx.add(new EliteIdx(featuresIdx));
        } else {
            if(ELITE_VERBOSE)  System.out.println("Cell occupied by elite w/ weights: " + currentElite.printWeights()+ ". Performances: New: (" + elite.getPerformance() + ") vs OLD (" + currentElite.getPerformance() + ")");
            // substitute the current elite only if thew new one has better performance
            if(elite.isBetterThan(currentElite))
            {
                if(ELITE_VERBOSE) System.out.println("New elite has better performance; replace");
                mapElites.setCell(featuresIdx, elite);
            }
        }
        System.out.println("\n");
    }

    public void printMapElitesInfo(String statsResultsFileName) {
        System.out.print("MAP Elites cells: ");
        for(Feature f : features) System.out.print(f.name() + " ");
        System.out.println();

        if(fileBased)
            mapElitesRecord.printData(statsResultsFileName);
        else
            mapElites.printData(statsResultsFileName);
    }
}
