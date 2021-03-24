/**
 * Author: Cristina Guerrero
 * Date: 5th February 2021
 */

package utils.mapelites;

import core.Constants;
import utils.stats.GameplayStats;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;

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
    private int nWeights;
    boolean ELITE_VERBOSE = true;
    boolean master = false;
    Path path;

    private class EliteIdx {
        int[] coordinates;

        EliteIdx(int[] coord) {
            coordinates = new int[coord.length];
            System.arraycopy(coord, 0, coordinates, 0, coord.length);
        }
    }

    public MapElites(Feature[] features, int nWeights, boolean master, Path path) {
        mapElites = new Map(features);
        occupiedCellsIdx = new ArrayList<>();
        this.features = features;
        this.nWeights = nWeights;
        this.master = master;
        this.path = path;
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
                Elite elite = createGameplayElite(genome, runner);
                addEliteToMap(elite);
                //elite.saveToFile(path);
            }

        //Non master runs initalize at random
        }else {
            // Random initialisations
            for (int i = 1; i < (nRandomInitialisations + 1); i++) {
                if (ELITE_VERBOSE) System.out.println("MAPELites initialisation iteration " + i);
                Generator.setRandomWeights(genome, 0.0, 0.25, 1.0);

                // Create elite from random values and add to map
                Elite elite = createGameplayElite(genome, runner);
                addEliteToMap(elite);
                //elite.saveToFile(path);
            }
        }

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
            Elite randomElite = getRandomEliteFromMap();
            randomElite.copyWeightsListValues(genome);

            // evol weights
            evolveHeuristicsWeights(genome);

            // Create new possible elite and add to map
            Elite newElite = createGameplayElite(genome, runner);
            addEliteToMap(newElite);

            nIteration++;

            printMapElitesInfo("map_" + runStr + "_" + nIteration + ".txt");
        }

        // When the algorithm is over, we need to make sure the final elites have all the data available
        processMapElitesData();
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

    private Elite getRandomEliteFromMap() {
        EliteIdx eliteIdx = (EliteIdx) Generator.getRandomElementFromArray(occupiedCellsIdx);
        return mapElites.getCell(eliteIdx.coordinates);
    }

    private void evolveHeuristicsWeights(double[] heuristicsWeightList) {
        Generator.evolveWeightList(heuristicsWeightList);
    }
    
    private Elite createGameplayElite(double[] genome, Runner runner) {
        // Get the game stats needed for the calculations for the map for current controller and weights. 
        // During the iteration of the algorithm, only the stats involved in performance and features are needed
        //GameStats gameStats = gameplayFramework.createMapEliteStatsFromGameplay(controller, performanceCriteria, new Features[]{featureInfoX, featureInfoY});

        //Constants.MAX_TURNS_CAPITALS = 10;
        ArrayList<GameplayStats> allStats = runner.run(genome);

        // Create elite with information and results
        return new Elite(genome, allStats);
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
//            elite.setDataForSerialisation(performanceCriteria, featuresInfo);
            mapElites.setCell(featuresIdx, elite);
            occupiedCellsIdx.add(new EliteIdx(featuresIdx));
        } else {
            if(ELITE_VERBOSE)  System.out.println("Cell occupied by elite w/ weights: " + currentElite.printWeights()+ ". Performances: New: (" + elite.getPerformance() + ") vs OLD (" + currentElite.getPerformance() + ")");
            // substitute the current elite only if thew new one has better performance
            if(elite.isBetterThan(currentElite))
            {
                if(ELITE_VERBOSE) System.out.println("New elite has better performance; replace");
//                elite.setDataForSerialisation(performanceCriteria, featuresInfo);
                mapElites.setCell(featuresIdx, elite);
            }
        }
        System.out.println("\n");
    }

    public void printMapElitesInfo(String statsResultsFileName) {
        System.out.print("MAP Elites cells: ");
        for(Feature f : features) System.out.print(f.name() + " ");
        System.out.println();

        mapElites.printData(statsResultsFileName);
    }
}
