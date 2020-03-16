package core.actors;

import core.TechnologyTree;
import core.TribesConfig;
import core.Types;


import core.actors.units.Unit;
import core.game.Board;
import utils.Vector2d;
import utils.graph.Graph;
import utils.graph.Node;
import utils.graph.TreeNode;
import utils.graph.TreePathfinder;

import java.lang.reflect.Array;
import java.util.ArrayList;



public class Tribe extends Actor{

    //Cities this tribe owns.
    private ArrayList<Integer> citiesID;

    //Capital City ID
    private int capitalID;

    //Type of the tribe
    private Types.TRIBE tribe;

    //Technology progress of this tribe
    private TechnologyTree techTree;

    //Current number of stars (resources) of this tribe.
    private int stars; //TODO: compute this amount at the beginning of each turn.


    //Game result for this player.
    private Types.RESULT winner = Types.RESULT.INCOMPLETE;

    //Score for the tribe.
    private int score = 0;


    //Indicates if the position in the board is visible
    private boolean obsGrid[][];


    //List of city ids connected to the capital (capital not included)
    private ArrayList<Integer> connectedCities = new ArrayList<>();

    //Trade network of this tribe
    private Graph tradeNetwork;

    private  ArrayList<Types.TRIBE> tribesMet;

    private ArrayList<Integer> extraUnits;


    public Tribe(Types.TRIBE tribe)
    {
        this.tribe = tribe;
        init();
    }

    public Tribe(int tribeID, int cityID, Types.TRIBE tribe) {
        this.tribeId = tribeID;
        citiesID.add(cityID);
        this.tribe = tribe;
        init();
    }

    private void init()
    {
        techTree = new TechnologyTree();
        techTree.doResearch(tribe.getInitialTech());
        citiesID = new ArrayList<>();
        stars = TribesConfig.INITIAL_STARS;
        this.tradeNetwork = new Graph();
        tribesMet = new ArrayList<>();
        extraUnits = new ArrayList<>();
        connectedCities = new ArrayList<>();
    }

    public void initObsGrid(int size)
    {
        obsGrid = new boolean[size][size];
    }


    public Tribe copy()
    {
        Tribe tribeCopy = new Tribe(this.tribe);
        tribeCopy.tribeId = this.tribeId;
        tribeCopy.stars = this.stars;
        tribeCopy.winner = this.winner;
        tribeCopy.score = this.score;
        tribeCopy.capitalID = this.capitalID;

        tribeCopy.techTree = this.techTree.copy();

        tribeCopy.obsGrid = new boolean[obsGrid.length][obsGrid.length];
        for(int i = 0; i < obsGrid.length; ++i)
            for(int j = 0; j < obsGrid.length; ++j)
                tribeCopy.obsGrid[i][j] = obsGrid[i][j];

        tribeCopy.citiesID = new ArrayList<>();
        for(int cityID : citiesID)
        {
            tribeCopy.citiesID.add(cityID);
        }

        tribeCopy.connectedCities = new ArrayList<>();
        for(int cityID : connectedCities) {
            tribeCopy.connectedCities.add(cityID);
        }

        tribeCopy.tribesMet = new ArrayList<>();
        for (Types.TRIBE t:tribesMet) {
            tribeCopy.tribesMet.add(t);
        }

        tribeCopy.extraUnits = new ArrayList<>();

        for (Integer unitID: extraUnits) {
            tribeCopy.extraUnits.add(unitID);
        }

        return tribeCopy;
    }


    public void clearView(int x, int y)
    {
        clearView(x, y, 1);
    }

    public void clearView(int x, int y, int range)
    {
        int size = obsGrid.length;
        for(int i = x-range; i <= x+range; ++i)
            for(int j = y-range; j <= y+range; ++j)
            {
                //All these positions should be within my view.
                if(i >= 0 && j >= 0 && i < size && j < size)
                {
                    obsGrid[i][j] = true;
                }
            }
    }


    public void addCity(int id) {
        citiesID.add(id);
    }

    public void removeCity(int id){
        for(int i=0; i<citiesID.size(); i++){
            if (citiesID.get(i) == id){
                citiesID.remove(i);
                return;
            }
        }
        System.out.println("Error!! city ID "+ id +" does not belong to this tribe");
    }

    public void setTechTree(TechnologyTree techTree) {this.techTree = techTree;}
    public TechnologyTree getTechTree() {return techTree;}

    public Types.TECHNOLOGY getInitialTechnology(){
        return tribe.getInitialTech();
    }

    public void addScore(int score){
        this.score += score;
    }

    public void subtractScore(int score){
        this.score -= score;
    }

    public ArrayList<Integer> getCitiesID() {
        return citiesID;
    }

    public String getName(){return tribe.getName();}

    public boolean[][] getObsGrid() {return obsGrid;}

    public boolean isVisible(int x, int y)
    {
        return obsGrid[x][y];
    }

    public Types.TRIBE getType(){return tribe;}

    public Types.RESULT getWinner() {return winner;}
    public int getScore() {return score;}

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public void addStars(int stars) {this.stars += stars;}

    public void subtractStars(int stars) {this.stars -= stars;}

    public void setCapitalID(int capitalID) {
        this.capitalID = capitalID;
    }

    public int getCapitalID() {
        return capitalID;
    }

    public boolean hasCity(int cityId) {
        return this.citiesID.contains(cityId);
    }

    public void setPosition(int x, int y) {position = null;} //this doesn't make sense
    public Vector2d getPosition()
    {
        return null;
    }

    /**
     * Updates the cities connected to the capital of this tribe given a graph.
     * @param mainGraph graph with tile connections. Must include roads, ports, capital and links between ports
     * @param b board of the game.
     * @param thisTribesTurn indicates if it is this tribe's turn
     */
    public void updateNetwork(Graph mainGraph, Board b, boolean thisTribesTurn) {
        ArrayList<Integer> lostCities = new ArrayList<>();
        ArrayList<Integer> addedCities = new ArrayList<>();

        //We need to start from the capital. If capital is not owned, there's no trade network
        if (!controlsCapital()) {

            for (int cityId : connectedCities)
                lostCities.add(cityId);

            connectedCities.clear();

        } else {

            //Execute Dijkstra from the capital city to all cities owned by this tribe
            City capital = (City) b.getActor(capitalID);
            Node capitalNode = mainGraph.getNode(capital.getPosition().x, capital.getPosition().y);
            mainGraph.pathfinder.findPath(capitalNode, null);

            for (int cityId : citiesID) {
                if (cityId != capitalID) {

                    //Check if the city is conected to the capital
                    City nonCapitalCity = (City) b.getActor(cityId);
                    Node nonCapitalCityNode = mainGraph.getNode(nonCapitalCity.getPosition().x, nonCapitalCity.getPosition().y);
                    boolean connectedNow = nonCapitalCityNode.isVisited();

                    //This was previously connected
                    if (connectedCities.contains(cityId)) {
                        if (!connectedNow) {
                            //drops from the network
                            connectedCities.remove(cityId);
                            lostCities.add(cityId);
                        }
                    } else if (connectedNow) {
                        //Wasn't connected, but it is now
                        connectedCities.add(cityId);
                        addedCities.add(cityId);
                    }

                }
            }

            //The capital gains 1 population for each city connected, -1 for each city disconnected
            int capitalGain = addedCities.size() - lostCities.size();
            capital.addPopulation(capitalGain);
        }


        //Population adjustments: they only happen if it's this tribe's turn
        if (thisTribesTurn) {

            //All cities that lost connection with the capital lose 1 population
            for (int cityId : lostCities) {
                City nonCapitalCity = (City) b.getActor(cityId);
                nonCapitalCity.addPopulation(-1);
            }

            //All cities that gained connection with the capital gain 1 population.
            for (int cityId : addedCities) {
                City nonCapitalCity = (City) b.getActor(cityId);
                nonCapitalCity.addPopulation(1);
            }
        }
    }

    public void addConvertedUnit (Unit u){
        extraUnits.add(u.getActorId());
    }


    public void updateNetwork2(TreePathfinder tp, Board b, boolean thisTribesTurn)
    {
        ArrayList<Integer> lostCities = new ArrayList<>();
        ArrayList<Integer> addedCities = new ArrayList<>();

        //We need to start from the capital. If capital is not owned, there's no trade network
        if(!controlsCapital()) {

            for(int cityId : connectedCities)
                lostCities.add(cityId);

            connectedCities.clear();

        }else if(tp != null){

            City capital = (City) b.getActor(capitalID);

            for (int cityId : citiesID) {
                if (cityId != capitalID) {

                    //Check if the city is conected to the capital
                    City nonCapitalCity = (City) b.getActor(cityId);
                    Vector2d nonCapitalPos = nonCapitalCity.getPosition();
                    ArrayList<TreeNode> pathToCity = tp.findPathTo(nonCapitalPos);


                    boolean connectedNow = pathToCity.size() > 0;

                    //This was previously connected
                    if (connectedCities.contains(cityId)) {
                        if (!connectedNow) {
                            //drops from the network
                            connectedCities.remove(cityId);
                            lostCities.add(cityId);
                        }
                    } else if (connectedNow) {
                        //Wasn't connected, but it is now
                        connectedCities.add(cityId);
                        addedCities.add(cityId);
                    }

                }
            }

            //The capital gains 1 population for each city connected, -1 for each city disconnected
            int capitalGain = addedCities.size() - lostCities.size();
            capital.addPopulation(capitalGain);
        }


        //Population adjustments: they only happen if it's this tribe's turn
        if(thisTribesTurn) {

            //All cities that lost connection with the capital lose 1 population
            for (int cityId : lostCities) {
                City nonCapitalCity = (City) b.getActor(cityId);
                nonCapitalCity.addPopulation(-1);
            }

            //All cities that gained connection with the capital gain 1 population.
            for (int cityId : addedCities) {
                City nonCapitalCity = (City) b.getActor(cityId);
                nonCapitalCity.addPopulation(1);
            }
        }


    }

    public boolean controlsCapital() {
        return citiesID.contains(capitalID);
    }
}
