package core.actors;

import core.TechnologyTree;
import core.TribesConfig;
import core.Types;
import core.actors.units.Unit;
import core.game.Board;
import core.game.Game;
import core.game.GameState;
import org.json.JSONArray;
import org.json.JSONObject;
import utils.Vector2d;
import utils.graph.PathNode;
import utils.graph.Pathfinder;

import java.util.*;

import static core.Types.BUILDING.*;


public class Tribe extends Actor {

    //Cities this tribe owns.
    private ArrayList<Integer> citiesID;

    //Capital City ID
    private int capitalID;

    //Type of the tribe
    private Types.TRIBE tribe;

    //Technology progress of this tribe
    private TechnologyTree techTree;

    //Current number of stars (resources) of this tribe.
    private int stars;

    //Game result for this player.
    private Types.RESULT winner = Types.RESULT.INCOMPLETE;

    //Score for the tribe.
    private int score = 0;

    //Indicates if the position in the board is visible
    private boolean[][] obsGrid;

    //List of city ids connected to the capital (capital not included)
    private ArrayList<Integer> connectedCities = new ArrayList<>();

    //Monument availability
    private HashMap<Types.BUILDING, MONUMENT_STATUS> monuments;

    //Tribes met by this tribe.
    private ArrayList<Integer> tribesMet;

    //Units that don't belong to a city (either converted of shifted).
    private ArrayList<Integer> extraUnits;

    //Kills by this tribe
    private int nKills;

    //Turns since the last attack of this tribe if Meditation is reseached.
    private int nPacifistCount;

    public Tribe(Types.TRIBE tribe) {
        this.tribe = tribe;
        init();
    }

    public Tribe(int tribeID, int cityID, Types.TRIBE tribe) {
        this.tribeId = tribeID;
        citiesID = new ArrayList<>();
        citiesID.add(cityID);
        this.tribe = tribe;
        init();
    }

    public Tribe(int id, JSONObject obj){
        tribeId = id;
        citiesID = new ArrayList<>();
        JSONArray JCitiesID = obj.getJSONArray("citiesID");
        for (int i=0; i<JCitiesID.length(); i++){
            citiesID.add(JCitiesID.getInt(i));
        }
        this.tribe = Types.TRIBE.getTypeByKey(obj.getInt("type"));
        this.nKills = obj.getInt("nKills");
        JSONArray JTribesMet = obj.getJSONArray("tribesMet");
        tribesMet = new ArrayList<>();
        for (int i=0; i<JTribesMet.length(); i++){
            tribesMet.add(JTribesMet.getInt(i));
        }
        this.capitalID = obj.getInt("capitalID");
        JSONArray JObsGrids = obj.getJSONArray("obsGrid");
        initObsGrid(JObsGrids.length());
        for (int i=0; i<JObsGrids.length(); i++){
            JSONArray JObsGrid = JObsGrids.getJSONArray(i);
            for (int j=0; j<JObsGrid.length(); j++){
                obsGrid[i][j] = JObsGrid.getBoolean(j);
            }
        }
        stars = obj.getInt("star");
        monuments = Types.BUILDING.initMonuments(obj.getJSONObject("monuments"));
        nPacifistCount = obj.getInt("nPacifistCount");
        techTree = new TechnologyTree(obj.getJSONObject("technology"));
        connectedCities = new ArrayList<>();
        JSONArray JConnectedCities = obj.getJSONArray("connectedCities");
        for (int i=0; i<JConnectedCities.length(); i++){
            connectedCities.add(JConnectedCities.getInt(i));
        }
        score = obj.getInt("score");
        winner = Types.RESULT.getTypeByKey(obj.getInt("winner"));
        extraUnits = new ArrayList<>();
        JSONArray JExtraUnits = obj.getJSONArray("extraUnits");
        for (int i=0; i<JExtraUnits.length(); i++){
            extraUnits.add(JExtraUnits.getInt(i));
        }

    }

    private void init() {
        techTree = new TechnologyTree();
        techTree.doResearch(tribe.getInitialTech());
        citiesID = new ArrayList<>();
        stars = TribesConfig.INITIAL_STARS;
        score = tribe.getInitialScore();
        tribesMet = new ArrayList<>();
        extraUnits = new ArrayList<>();
        connectedCities = new ArrayList<>();
        monuments = Types.BUILDING.initMonuments();
        nKills = 0;
        nPacifistCount = 0;
    }

    public void initObsGrid(int size) {
        obsGrid = new boolean[size][size];
    }


    public Tribe copy(boolean hideInfo) {
        Tribe tribeCopy = new Tribe(this.tribe);
        tribeCopy.actorId = this.actorId;
        tribeCopy.tribeId = this.tribeId;
        tribeCopy.stars = hideInfo ? 0 : this.stars;
        tribeCopy.winner = this.winner;
        tribeCopy.score = this.score;
        tribeCopy.capitalID = this.capitalID;
        tribeCopy.nKills = hideInfo ? 0 : this.nKills;
        tribeCopy.nPacifistCount = hideInfo ? 0 : this.nPacifistCount;

        tribeCopy.techTree = hideInfo ? new TechnologyTree() : this.techTree.copy();

        tribeCopy.obsGrid = new boolean[obsGrid.length][obsGrid.length];
        if(!hideInfo) for (int i = 0; i < obsGrid.length; ++i)
            System.arraycopy(obsGrid[i], 0, tribeCopy.obsGrid[i], 0, obsGrid.length);

        tribeCopy.citiesID = new ArrayList<>();
        if(!hideInfo) tribeCopy.citiesID.addAll(citiesID);

        tribeCopy.connectedCities = new ArrayList<>();
        if(!hideInfo) tribeCopy.connectedCities.addAll(connectedCities);

        tribeCopy.tribesMet = new ArrayList<>();
        if(!hideInfo) tribeCopy.tribesMet.addAll(tribesMet);

        tribeCopy.extraUnits = new ArrayList<>();
        if(!hideInfo) tribeCopy.extraUnits.addAll(extraUnits);

        tribeCopy.monuments = new HashMap<>();
        if(!hideInfo) for(Types.BUILDING b : monuments.keySet())
        {
            tribeCopy.monuments.put(b, monuments.get(b));
        }

        return tribeCopy;
    }

    public boolean clearView(int x, int y, int range, Random r, Board b) {
        int size = obsGrid.length;
        Vector2d center = new Vector2d(x, y);
        boolean requiresNetworkUpdate = false;

        LinkedList<Vector2d> tiles = center.neighborhood(range, 0, size);
        tiles.add(center);

        for(Vector2d tile : tiles)
        {
            if (!obsGrid[tile.x][tile.y]) {
                //Points and visibility.
                obsGrid[tile.x][tile.y] = true;
                this.score += TribesConfig.CLEAR_VIEW_POINTS;

                //Network updates for this tribe, only if a road or a water tile has been revealed.
                Types.TERRAIN terr = b.getTerrainAt(tile.x, tile.y);
                if(b.isRoad(tile.x, tile.y) || ((terr != null) && terr.isWater()))
                    requiresNetworkUpdate = true;
            }

            //Meeting other tribes
            Unit u = b.getUnitAt(tile.x,tile.y);
            City c = b.getCityInBorders(tile.x,tile.y);

            //This tribe meets other tribe when clearing view if other tribe's unit or city is visible
            if( u !=null){
                meetTribe(r,b.getTribes(),u.getTribeId());
                //other tribe meets this tribe if moving into visible area
                if(b.getTribe(u.tribeId).obsGrid[tile.x][tile.y]){
                    meetTribe(r,b.getTribes(),this.tribeId);
                }
            }
            if(c !=null){
                meetTribe(r,b.getTribes(),c.getTribeId());
                //other tribe meets this tribe if moving into visible area
                if(b.getTribe(c.tribeId).obsGrid[tile.x][tile.y]){
                    meetTribe(r,b.getTribes(),this.tribeId);
                }
            }
        }

        //We may be clearing the last tiles of the board, which grants a monument
        if(monuments.get(EYE_OF_GOD) == MONUMENT_STATUS.UNAVAILABLE)
        {
            for (boolean[] booleans : obsGrid)
                for (int j = 0; j < obsGrid[0].length; ++j) {
                    if (!booleans[j]) //end and out
                        return requiresNetworkUpdate;
                }

            //All clear and we couldn't buy monument before. Now we can.
            monuments.put(EYE_OF_GOD, MONUMENT_STATUS.AVAILABLE);
        }

        return requiresNetworkUpdate;
    }


    public void addCity(int id) {
        citiesID.add(id);
    }

    private void removeCity(int id) {
        for (int i = 0; i < citiesID.size(); i++) {
            if (citiesID.get(i) == id) {
                citiesID.remove(i);
                return;
            }
        }
        //System.out.println("Error!! city ID " + id + " does not belong to this tribe"); //This is only a problem if it happens in the real game
    }

    public void setTechTree(TechnologyTree techTree) {
        this.techTree = techTree;
    }

    public TechnologyTree getTechTree() {
        return techTree;
    }

    public Types.TECHNOLOGY getInitialTechnology() {
        return tribe.getInitialTech();
    }

    public void addScore(int score) {
        this.score += score;
    }

    public void subtractScore(int score) {
        this.score -= score;
    }

    public ArrayList<Integer> getCitiesID() {
        return citiesID;
    }

    public String getName() {
        return tribe.getName();
    }

    public boolean[][] getObsGrid() {
        return obsGrid;
    }

    public boolean isVisible(int x, int y) {
        return obsGrid[x][y];
    }

    public Types.TRIBE getType() {
        return tribe;
    }

    public Types.RESULT getWinner() {
        return winner;
    }

    public int getScore() {
        return score;
    }

    public int getReverseScore() {
        return -score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public void addStars(int stars) {
        this.stars += stars;

        if(this.stars >= TribesConfig.EMPERORS_TOMB_STARS && monuments.get(Types.BUILDING.EMPERORS_TOMB) == MONUMENT_STATUS.UNAVAILABLE)
            monuments.put(EMPERORS_TOMB, MONUMENT_STATUS.AVAILABLE);
    }

    public void subtractStars(int stars) {
        this.stars -= stars;
    }

    public void setCapitalID(int capitalID) {
        this.capitalID = capitalID;
    }

    public int getCapitalID() {
        return capitalID;
    }

    public void setPosition(int x, int y) {
        position = null;
    } //this doesn't make sense

    public Vector2d getPosition() {
        return null;
    }

    public void moveAllUnits(ArrayList<Integer> units){
        extraUnits.addAll(units);
    }

    public boolean isMonumentBuildable(Types.BUILDING building)
    {
        return monuments.get(building) == MONUMENT_STATUS.AVAILABLE;
    }

    public void monumentIsBuilt(Types.BUILDING building)

    {
        monuments.put(building, MONUMENT_STATUS.BUILT);
    }

    public int getnumKills() {
        return nKills;
    }

    public void addKill() {
        this.nKills++;

        //we may have a new monument availability here
        if(this.nKills >= TribesConfig.GATE_OF_POWER_KILLS && monuments.get(GATE_OF_POWER) == MONUMENT_STATUS.UNAVAILABLE)
            monuments.put(GATE_OF_POWER, MONUMENT_STATUS.AVAILABLE);
    }

    public ArrayList<Integer> getTribesMet() {
        return tribesMet;
    }

    private void meetTribe(Random r, Tribe[] tribes, int tribeID) {

        for (Integer tribeMetId : tribesMet) {
            // if tribes not in tribes met or tribe is itself then do nothing else add to tribesmet arraylist
            if (tribeID == tribeMetId || tribeID == this.getTribeId()) {
                return;

            }
        }

        tribesMet.add(tribeID); // add to this tribe

        //Pick a technology at random from the tribe to learn
        TechnologyTree thisTribeTree = getTechTree();
        TechnologyTree metTribeTree = tribes[tribeID].getTechTree();
        ArrayList<Types.TECHNOLOGY> potentialTechForThisTribe = new ArrayList<>();

        for (Types.TECHNOLOGY tech : Types.TECHNOLOGY.values())
        {
            if (metTribeTree.isResearched(tech) && !thisTribeTree.isResearched(tech))
            {
                potentialTechForThisTribe.add(tech);
            }
        }

        if (potentialTechForThisTribe.size() == 0)
            return;

        Types.TECHNOLOGY techToGet = potentialTechForThisTribe.get(r.nextInt(potentialTechForThisTribe.size()));
        thisTribeTree.doResearch(techToGet);
    }



    public void updateNetwork(Pathfinder tp, Board b, boolean thisTribesTurn) {
        ArrayList<Integer> lostCities = new ArrayList<>();
        ArrayList<Integer> addedCities = new ArrayList<>();

        //We need to start from the capital. If capital is not owned, there's no trade network
        if (!controlsCapital()) {

            lostCities.addAll(connectedCities);
            connectedCities.clear();

        } else if (tp != null) {

            City capital = (City) b.getActor(capitalID);

            for (int cityId : citiesID) {
                if (cityId != capitalID) {

                    //Check if the city is connected to the capital
                    City nonCapitalCity = (City) b.getActor(cityId);
                    Vector2d nonCapitalPos = nonCapitalCity.getPosition();
                    ArrayList<PathNode> pathToCity = tp.findPathTo(nonCapitalPos);

                    boolean connectedNow = (pathToCity != null) && (pathToCity.size() > 0);

                    //This was previously connected
                    if (connectedCities.contains(cityId)) {
                        if (!connectedNow) {
                            //drops from the network
                            dropCityFromNetwork(nonCapitalCity);
                            lostCities.add(cityId);
                        }
                    } else if (connectedNow) {
                        //Wasn't connected, but it is now
                        connectedCities.add(cityId);
                        addedCities.add(cityId);
                    }
                }
            }

            //There may be some connected cities that we don't longer own
            // (i.e. we're here because an enemy captured one of our cities in the network)
            ArrayList<Integer> connCities = new ArrayList<>(connectedCities);
            for(Integer cityId : connCities)
            {
                if(!this.controlsCity(cityId))
                {
                    dropCityFromNetwork((City) b.getActor(cityId));
                    lostCities.add(cityId);
                }
            }

            //The capital gains 1 population for each city connected, -1 for each city disconnected
            int capitalGain = addedCities.size() - lostCities.size();
            capital.addPopulation(this, capitalGain);

            //We may be adding a new monument to the pool!
            if(connectedCities.size() >= TribesConfig.GRAND_BAZAR_CITIES && monuments.get(GRAND_BAZAR) == MONUMENT_STATUS.UNAVAILABLE) {
                monuments.put(GRAND_BAZAR, MONUMENT_STATUS.AVAILABLE);
            }
        }


        //Population adjustments: they only happen if it's this tribe's turn
        if (thisTribesTurn) {

            //All cities that gained connection with the capital gain 1 population.
            for (int cityId : addedCities) {
                City nonCapitalCity = (City) b.getActor(cityId);
                nonCapitalCity.addPopulation(this, 1);
            }
        }
    }

    /**
     * Drops a city from the network. Removes the associated population required to that city.
     * @param lostCity city to remove from network
     */
    private void dropCityFromNetwork(City lostCity)
    {
        int cityId = lostCity.getActorId();
        int cityIdx = connectedCities.indexOf(cityId);
        connectedCities.remove(cityIdx);

        //this city loses 1 population
        lostCity.addPopulation(this, -1);
    }

    public int getMaxProduction(GameState gs)
    {
        int acumProd = 0;
        for (int cityId : citiesID) {
            City city = (City) gs.getActor(cityId);
            acumProd += city.getProduction();
        }
        return acumProd;
    }


    public boolean controlsCapital() {
        return citiesID.contains(capitalID);
    }

    public boolean controlsCity(int cityId)
    {
        return citiesID.contains(cityId);
    }

    public int getNumCities()
    {
        return citiesID.size();
    }

    public void cityMaxedUp() {
        if(monuments.get(PARK_OF_FORTUNE) == MONUMENT_STATUS.UNAVAILABLE)
            monuments.put(PARK_OF_FORTUNE, MONUMENT_STATUS.AVAILABLE);
    }

    public void allResearched() {
        if(monuments.get(TOWER_OF_WISDOM) == MONUMENT_STATUS.UNAVAILABLE)
            monuments.put(TOWER_OF_WISDOM, MONUMENT_STATUS.AVAILABLE);
    }

    public void addExtraUnit(Unit target)
    {
        extraUnits.add(target.getActorId());
        target.setCityId(-1);
    }

    public void removeExtraUnit(Unit target)
    {
        int index = extraUnits.indexOf(target.getActorId());
        if(index != -1)
            extraUnits.remove(index);
    }

    /**
     * Checks if the tribe can build roads
     * @return if tribe can build roads
     */
    public boolean canBuildRoads() {
        //Factors for tree building in general: tech and enough stars.
        boolean canBuildRoad = techTree.isResearched(Types.TECHNOLOGY.ROADS);
        boolean hasMoney = stars >= TribesConfig.ROAD_COST;
        return canBuildRoad && hasMoney;
    }

    public void capturedCity(GameState gameState, City captured)
    {
        this.addCity(captured.getActorId());
        captured.setTribeId(this.tribeId);

        //manage production and population of this new city (and others!)
        for(Building building : captured.getBuildings())
        {
            captured.updateBuildingEffects(gameState, building, false, true);
        }
    }

    public void lostCity(GameState gameState, City lostCity)
    {
        this.removeCity(lostCity.getActorId());
        //manage the effect of losing this in the production and population of other cities.

        //manage production and population of this new city (and others!)
        for(Building building : lostCity.getBuildings())
        {
            if(building.type.isBase() || building.type == Types.BUILDING.PORT)
            {
                lostCity.updateBuildingEffects(gameState, building, true, true);
            }
        }

    }

    public ArrayList<Integer> getExtraUnits() {
        return extraUnits;
    }

    public void addPacifistCount() {
        if(techTree.isResearched(Types.TECHNOLOGY.MEDITATION))
        {
            nPacifistCount++;
            if(nPacifistCount == TribesConfig.ALTAR_OF_PEACE_TURNS)
            {
                monuments.put(ALTAR_OF_PEACE, MONUMENT_STATUS.AVAILABLE);
            }
        }
    }

    public void resetPacifistCount() {nPacifistCount = 0;}

    public ArrayList<Integer> getConnectedCities() {
        return connectedCities;
    }

    public HashMap<Types.BUILDING, MONUMENT_STATUS> getMonuments() {
        return monuments;
    }

    public int getnKills() {
        return nKills;
    }

    public int getnPacifistCount() {
        return nPacifistCount;
    }

}
