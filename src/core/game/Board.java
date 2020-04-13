package core.game;

import core.TechnologyTree;
import core.TribesConfig;
import core.Types;
import core.actors.Actor;
import core.actors.City;
import core.actors.Tribe;
import core.actors.units.*;
import utils.Vector2d;
import utils.graph.*;

import java.util.*;

import static core.Types.TERRAIN.*;

public class Board {

    // Array for the type of terrain that each tile of board will have
    private Types.TERRAIN[][] terrains;

    // Array for resource each tile of the board will have
    private Types.RESOURCE[][] resources;

    // Array for buildings each tile of the board will have
    private Types.BUILDING[][] buildings;

    // Array for units each tile of the board will have
    private int[][] units;

    // Array for tribes
    private Tribe[] tribes;

    // Array for id of the city that owns each tile. -1 if no city owns the tile.
    private int[][] tileCityId;

    // Array that indicates presence of roads, cities, ports or naval links
    private boolean[][] networkTiles;

    //Actors in the game
    private HashMap<Integer, Actor> gameActors;

    //variable to declare size of board
    private int size;

    // Player currently making a move.
    private int activeTribeID = -1;

    //Actor ID counter
    private int actorIDcounter;

    // Constructor for board
    public Board() {
        this.gameActors = new HashMap<>();
    }

    public void init(int size, Tribe[] tribes) {

        this.size = size;

        terrains = new Types.TERRAIN[size][size];
        resources = new Types.RESOURCE[size][size];
        buildings = new Types.BUILDING[size][size];
        units = new int[size][size];
        this.tileCityId = new int[size][size];
        this.networkTiles = new boolean[size][size];

        for(Tribe t : tribes)
            t.initObsGrid(size);

        //Initialise tile IDs
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                tileCityId[x][y] = -1;
            }
        }

        this.assignTribes(tribes);
    }

    // Return deep copy of board
    public Board copy() {
        return copy(false, -1);
    }

    // Return deep copy of board
    public Board copy(boolean partialObs, int playerId) {
        Board copyBoard = new Board();
        copyBoard.size = this.size;
        copyBoard.tribes = new Tribe[this.tribes.length];
        copyBoard.terrains = new Types.TERRAIN[size][size];
        copyBoard.resources = new Types.RESOURCE[size][size];
        copyBoard.buildings = new Types.BUILDING[size][size];
        copyBoard.units = new int[size][size];
        copyBoard.tileCityId = new int[size][size];
        copyBoard.networkTiles = new boolean[size][size];
        copyBoard.activeTribeID = activeTribeID;
        copyBoard.actorIDcounter = actorIDcounter;

        // Copy board objects (they are all ids)
        for (int x = 0; x < this.size; x++) {
            for (int y = 0; y < this.size; y++) {

                if(!partialObs || tribes[playerId].isVisible(x,y))
                {
                    copyBoard.setUnitIdAt(x, y, units[x][y]);
                    copyBoard.setTerrainAt(x, y, terrains[x][y]);
                    copyBoard.setResourceAt(x, y, maskResource(playerId, x, y));
                    copyBoard.setBuildingAt(x, y, buildings[x][y]);
                    copyBoard.tileCityId[x][y] = tileCityId[x][y];
                    copyBoard.networkTiles[x][y] = networkTiles[x][y];
                }
            }
        }

        // Copy tribes
        for (int i = 0; i < tribes.length; i++) {
            copyBoard.tribes[i] = tribes[i].copy();
        }

        //Deep copy of all actors in the board
        copyBoard.gameActors = new HashMap<>();
        for (Actor act : gameActors.values()) {
            int id = act.getActorId();

            //When do we copy? if it's the tribe (id==playerId), full observable or actor visible if part. obs.
            if(id == playerId || !partialObs || tribes[playerId].isVisible(act.getPosition().x, act.getPosition().y))
            {
                copyBoard.gameActors.put(id, act.copy());
            }
        }

        return copyBoard;
    }

    /**
     * Masks a resource that can only be revealed after researching a specific technology.
     * @param playerID if -1 we don not mask any resources.
     * @param x x coordinate of the resource.
     * @param y y coordinate of the resource.
     * @return Returns the resource at x,y or null if there is no resource, or the resource is hidden.
     */
    private Types.RESOURCE maskResource(int playerID, int x, int y) {
        if(playerID == -1) { return resources[x][y]; }
        else {
            TechnologyTree t = tribes[playerID].getTechTree();

            try {
                switch (resources[x][y]) {
                    case CROPS:
                        if (!t.isResearched(Types.TECHNOLOGY.ORGANIZATION)) {
                            return null;
                        }
                        break;
                    case ORE:
                        if (!t.isResearched(Types.TECHNOLOGY.CLIMBING)) {
                            return null;
                        }
                        break;
                    case WHALES:
                        if (!t.isResearched(Types.TECHNOLOGY.FISHING)) {
                            return null;
                        }
                        break;
                }
                return resources[x][y];
            } catch (Exception e) {
                return null;
            }
        }
    }

    /**
     * Pushes a unit out of a city (x,y). The order in which tiles are tried for the new
     * destination are: S, W, N, E, SW, NW, NE, NW. If none of those positions are available, the
     * unit disappears.
     * See Push Grid at: https://polytopia.fandom.com/wiki/Giant
     *
     * @param tribe:   Tribe the unit belongs to
     * @param toPush   Unit to be pushed
     * @param startX   x coordinate of the starting position of the unit to push
     * @param startY   y coordinate of the starting position of the unit to push
     * @return true if the unit could be pushed.
     */

    public boolean pushUnit(Tribe tribe, Unit toPush, int startX, int startY, Random r) {
        int[] xPush = {0, -1, 0, 1, -1, -1, 1, 1};
        int[] yPush = {1, 0, -1, 0, 1, -1, -1, 1};
        int idx = 0;
        boolean pushed = false;
        int tribeId = tribe.getTribeId();

        while (!pushed && idx < xPush.length) {
            int x = startX + xPush[idx];
            int y = startY + yPush[idx];

            if (x >= 0 && y >= 0 && x < size && y < size) {
                pushed = tryPush(tribe, toPush, startX, startY, x, y, r);
            }
            idx++;
        }

        //A pushed unit moves to PUSHED status - essentially its turn is over.
        toPush.setStatus(Types.TURN_STATUS.PUSHED);

        return pushed;
    }

    public boolean tryPush(Tribe tribe, Unit toPush, int startX, int startY, int x, int y, Random r) {
        //there's no unit? (or killed)
        Unit u = getUnitAt(x, y);
        if (u != null && !u.isKilled())
        {
            return false;
        }
        int tribeId = tribe.getTribeId();

        //climbable mountain?
        Types.TERRAIN terrain = terrains[x][y];
        if (terrain == Types.TERRAIN.MOUNTAIN) {
            if (tribes[tribeId].getTechTree().isResearched(Types.TECHNOLOGY.CLIMBING)) {
                moveUnit(toPush, startX, startY, x, y, r);
                return true;
            } else return false; //Can't be pushed if it's a mountain and climbing is not researched.
        }


        //Water with a port this tribe owns?
        Types.BUILDING b = buildings[x][y];
        if (terrain == SHALLOW_WATER) {
            if (b == Types.BUILDING.PORT) {
                City c = getCityInBorders(x, y);
                if (c != null && c.getTribeId() == tribeId) {
                    embark(toPush, tribe, x, y);
                    return true;
                }

                if (c == null) {
                    System.out.println("WARNING: This shouldn't happen. Trying to push an unit to a location outside all borders.");
                }
            }

            //Not in any city (shouldn't happen), in an enemy port, or in water but no port.
            return false;
        }

        //Otherwise, no problem
        moveUnit  (toPush, startX, startY, x, y, r);
        return true;
    }


    public void embark(Unit unit, Tribe tribe, int x, int y) {
        City city = (City) gameActors.get(unit.getCityId());
        removeUnitFromBoard(unit);
        removeUnitFromCity(unit, city, tribe);

        //We're actually creating a new unit
        Vector2d newPos = new Vector2d(x, y);
        Unit boat = Types.UNIT.createUnit(newPos, unit.getKills(), unit.isVeteran(), unit.getCityId(), unit.getTribeId(), Types.UNIT.BOAT);
        boat.setCurrentHP(unit.getCurrentHP());
        ((Boat)boat).setBaseLandUnit(unit.getType());
        addUnit(city, boat);

//        System.out.println("Embarking unit. From id: " + unit.getActorId() + " to " + boat.getActorId() + ". City " + unit.getCityId() + "/" + boat.getCityId() +
//                 " has associated units: " + city.getUnitsID().toString());
    }

    public void disembark(Unit unit, Tribe tribe, int x, int y) {
        City city = (City) gameActors.get(unit.getCityId());
        removeUnitFromBoard(unit);
        removeUnitFromCity(unit, city, tribe);
        
        Types.UNIT baseLandUnit;
        switch (unit.getType())
        {
            case BOAT:
                Boat boat = (Boat) unit; 
                baseLandUnit = boat.getBaseLandUnit();
                break;
            case SHIP:
                Ship ship = (Ship) unit;
                baseLandUnit = ship.getBaseLandUnit();  
                break;
            case BATTLESHIP:
                Battleship battleship = (Battleship) unit;
                baseLandUnit = battleship.getBaseLandUnit();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + unit.getType());
        }
        //We're actually creating a new unit
        Vector2d newPos = new Vector2d(x, y);
        Unit newUnit = Types.UNIT.createUnit(newPos, unit.getKills(), unit.isVeteran(), unit.getCityId(), unit.getTribeId(), baseLandUnit);
        newUnit.setCurrentHP(unit.getCurrentHP());
        addUnit(city, newUnit);


//        System.out.println("Disembarking unit. From id: " + unit.getActorId() + " to " + newUnit.getActorId() + ". City " + unit.getCityId() + "/" + newUnit.getCityId() +
//                " has associated units: " + city.getUnitsID().toString());
    }

    public void moveUnit(Unit unit, int x0, int y0, int xF, int yF, Random r) {
        units[x0][y0] = 0;
        units[xF][yF] = unit.getActorId();
        unit.setPosition(xF, yF);
        Tribe t = tribes[unit.getTribeId()];

        int partialObsRangeClear = 1;
        if (getTerrainAt(xF, yF) == Types.TERRAIN.MOUNTAIN) {
            partialObsRangeClear += 1;
        }
        t.clearView(xF, yF, partialObsRangeClear, r, this);
    }

    public void launchExplorer(int x0, int y0, int tribeId, Random rnd) {
        int xMove[] = {0, -1, 0, 1, -1, -1, 1, 1};
        int yMove[] = {1, 0, -1, 0, 1, -1, -1, 1};

        int curX = x0;
        int curY = y0;

        for (int i = 0; i < TribesConfig.NUM_STEPS; ++i) {
            int j = 0;
            boolean moved = false;

            while (!moved && j < TribesConfig.NUM_STEPS * 3) {
                //Pick a direction at random
                int idx = rnd.nextInt(xMove.length);
                int x = curX + xMove[idx];
                int y = curY + yMove[idx];

                if (traversable(x, y, tribeId)) {
                    moved = true;
                    curX = x;
                    curY = y;
                    tribes[tribeId].clearView(x, y, TribesConfig.EXPLORER_CLEAR_RANGE, rnd, this.copy());
                }

                j++;
            }

            if (!moved) {
                //couldn't move in many steps. Let's just warn and progress from now.
                System.out.println("WARNING: explorer stuck, " + j + " steps without moving.");
            }

        }

    }


    public boolean traversable(int x, int y, int tribeId) {
        if (x >= 0 && y >= 0 && x < size && y < size) {
            //we rule out places we can't be.
            TechnologyTree tt = tribes[tribeId].getTechTree();

            //if mountain and climbing not researched
            if (terrains[x][y] == Types.TERRAIN.MOUNTAIN && !tt.isResearched(Types.TECHNOLOGY.CLIMBING))
                return false;

            //Shallow water and no sailing
            if (terrains[x][y] == SHALLOW_WATER && !tt.isResearched(Types.TECHNOLOGY.SAILING))
                return false;

            //Deep water and no navigation
            if (terrains[x][y] == DEEP_WATER && !tt.isResearched(Types.TECHNOLOGY.NAVIGATION))
                return false;


        } else return false; //Outside board bounds.

        return true;
    }


    public Tribe[] getTribes() {
        return tribes;
    }

    public Tribe getTribe(int tribeId) {
        return tribes[tribeId];
    }

    /**
     * Sets the tribes that will play the game. The number of tribes must equal the number of players in Game.
     *
     * @param tribes to play with
     */
    private void assignTribes(Tribe[] tribes) {
        int numTribes = tribes.length;
        this.tribes = new Tribe[numTribes];
        for (int i = 0; i < numTribes; ++i) {
            this.tribes[i] = tribes[i];
            this.tribes[i].setTribeId(i);
            this.tribes[i].setActorId(i);
        }
    }

    // Get size of board
    public int getSize() {
        return size;
    }

    public void setTradeNetwork(int x, int y, boolean trade)
    {
        networkTiles[x][y] = trade;
        computeTradeNetwork();
    }

    // Get units array
    public int[][] getUnits(){
        return this.units;
    }

    // Get Terrain at pos x,y
    public Types.TERRAIN getTerrainAt(int x, int y){
        return terrains[x][y];
    }

    // Get Unit at pos x,y
    public int getUnitIDAt(int x, int y){
        return units[x][y];
    }

    // Get Unit at pos x,y
    public Unit getUnitAt(int x, int y){

        Actor act = gameActors.get(units[x][y]);
        if(act != null)
            return (Unit) act;
        return null;
    }

    // Get CityID at pos x,y
    public City getCityInBorders(int x, int y){
        if(tileCityId[x][y] == -1)
            return null;
        else
            return (City) gameActors.get(tileCityId[x][y]);
    }

    // Set Resource at pos x,y
    public void setResourceAt(int x, int y, Types.RESOURCE r){
        resources[x][y] =  r;
    }

    // Set Terrain at pos x,y
    public void setTerrainAt(int x, int y, Types.TERRAIN t){
        terrains[x][y] =  t;
    }

    // Set unit id at pos x,y
    public void setUnitIdAt(int x, int y, int unitId){
        units[x][y] = unitId;
    }

    // Set Terrain at pos x,y
    public void setUnitIdAt(int x, int y, Unit unit){
        units[x][y] = unit.getActorId();
    }


    // Set Building at pos x,y
    public void setBuildingAt(int x, int y, Types.BUILDING b){
        buildings[x][y] = b;
    }

    // Get Resource at pos x,y
    public Types.RESOURCE getResourceAt(int x, int y){
        return resources[x][y];
    }

    // Get Resource at pos x,y
    public Types.BUILDING getBuildingAt(int x, int y){
        return buildings[x][y];
    }

    // Setter method for units array
    public void setUnits(int[][] u){
        this.units = u;
    }


    // Method to determine city borders, take city and x and y pos of city as params
    void setCityBorders(){

        for (Tribe tribe : tribes) {
            ArrayList<Integer> tribe1Cities = tribe.getCitiesID();

            for (int cityId : tribe1Cities) {
                City c = (City) gameActors.get(cityId);
                setBorderHelper(c, c.getBound());
            }

        }
    }

    // Set border helper method to set city bounds
    private void setBorderHelper(City c, int bound){
        Vector2d cityPos = c.getPosition();
        Tribe t = getTribe(c.getTribeId());
        LinkedList<Vector2d> tiles = cityPos.neighborhood(bound, 0, size);
        tiles.add(new Vector2d(cityPos));
        for(Vector2d tile : tiles)
        {
            if(tileCityId[tile.x][tile.y] == -1){
                tileCityId[tile.x][tile.y] = c.getActorId();
                t.addScore(TribesConfig.CITY_BORDER_POINTS); // Add score to tribe on border creation
                c.addPointsWorth(TribesConfig.CITY_BORDER_POINTS);
            }
        }
    }

//    //Set extra points for tribe on border expansion
//    private void setPointsForBorderExpansion(City c){
//        Tribe t = getTribe(c.getTribeId());
//        Vector2d cityPos = c.getPosition();
//        for(Vector2d tile : cityPos.neighborhood(TribesConfig.CITY_EXPANSION_TILES, 0, size))
//        {
//            if(tileCityId[tile.x][tile.y] == c.getActorId())
//            {
//                t.addScore(TribesConfig.CITY_BORDER_POINTS);
//                c.addPointsWorth(TribesConfig.CITY_BORDER_POINTS);
//            }
//        }
//    }

    // Method to expand city borders, take city as param
    public void expandBorder(City city){
        city.setBound(city.getBound()+TribesConfig.CITY_EXPANSION_TILES);
        setBorderHelper(city,city.getBound());
//        setPointsForBorderExpansion(city);
    }

    public boolean isRoad(int x, int y) {
        return networkTiles[x][y] && terrains[x][y] != SHALLOW_WATER && terrains[x][y] != DEEP_WATER && terrains[x][y] != CITY;
    }

    public int getCityIdAt(int x, int y)
    {
        return tileCityId[x][y];
    }

    // Get all the tiles that belong to a city
    public LinkedList<Vector2d> getCityTiles(int cityID){
        LinkedList<Vector2d> tiles = new LinkedList<>();
        City targetCity = (City) gameActors.get(cityID);
        Vector2d targetCityPos = targetCity.getPosition();
        int radius = 0;

        if(targetCity.getLevel() < 4){ radius = 1; } else{ radius = 2; }

        for(int i = targetCityPos.x - radius; i <= targetCityPos.x + radius; i++) {
            for(int j = targetCityPos.y - radius; j <= targetCityPos.y + radius; j++) {
                if(i >= 0 && j >= 0 && i < size && j < size) {
                    if (tileCityId[i][j] == cityID){
                        tiles.add(new Vector2d(i, j));
                    }
                }
            }
        }

        return tiles;
    }

    /**
     * Captures a city or village for tribe t
     * @param capturingTribe tribe that captures
     * @param x position of the city to capture
     * @param y position of the city to capture
     * @return true if city was captured.
     */
    public boolean capture(GameState gameState, Tribe capturingTribe, int x, int y){

        Random rnd = gameState.getRandomGenerator();
        Types.TERRAIN ter = terrains[x][y];

        if(ter == Types.TERRAIN.VILLAGE)
        {
            //Not a city. Needs to be created, assigned and its border calculated.
            City newCity = new City(x, y, capturingTribe.getTribeId());

            //Add city to board and set its borders
            addCityToTribe(newCity,gameState.getRandomGenerator());
            setBorderHelper(newCity, newCity.getBound());

            //This becomes a city.
            setTerrainAt(x, y, CITY);

            // Move the unit from one city to village. Rank: capital -> cities -> None
            moveOneToNewCity(newCity, capturingTribe, rnd);

        }else if(ter == CITY)
        {
            City capturedCity = (City) gameActors.get(tileCityId[x][y]);
            Tribe previousOwner = tribes[capturedCity.getTribeId()];

            //The city exists, needs to change owner, tribes notified and production & population updated
            capturingTribe.capturedCity(gameState, capturedCity);
            previousOwner.lostCity(gameState, capturedCity);

            // TRIBE that lost the city: Move units to other cities (before the capturing tribe moves their unit)
            moveAllFromCity(capturedCity, previousOwner, rnd);

            // TRIBE that captured this city. One unit moves there.
            moveOneToNewCity(capturedCity, capturingTribe, rnd);

        }else
        {
            System.out.println("Warning: Tribe " + capturingTribe.getTribeId() + " trying to caputre a non-city.");
            return false;
        }

        this.setTradeNetwork(x, y, true);
        return true;
    }

    private void moveOneToNewCity(City destCity, Tribe tribe, Random rnd)
    {
        //Capital is special, we start taking unnits from there.
        boolean ownsCapital = tribe.controlsCapital();
        City capital = (City) getActor(tribe.getCapitalID());
        LinkedList<Integer> cities = new LinkedList<>(tribe.getCitiesID());
        cities.remove((Integer)tribe.getCapitalID());

        // Move the unit from one city to village. Rank: capital -> cities -> None
        if(ownsCapital && capital.getUnitsID().size() > 0){
            moveBelongingCity(capital, destCity);
        }else{
            boolean moved = false;
            //Capital is empty or not owned. Check the other cities at random
            Collections.shuffle(cities, rnd);
            while (!moved && cities.size() > 0){
                City originalCity = (City)getActor(cities.removeFirst());
                if (originalCity.getUnitsID().size() > 0){
                    moveBelongingCity(originalCity, destCity);
                    moved = true;
                }
            }
        }
    }

    private void moveAllFromCity(City fromCity, Tribe tribe, Random rnd)
    {
        //Capital is special, we start taking unnits from there.
        boolean ownsCapital = tribe.controlsCapital();
        City capital = (City) getActor(tribe.getCapitalID());

        //First to capital
        if(ownsCapital) while (capital.canAddUnit() && fromCity.getNumUnits() > 0){
            moveBelongingCity(fromCity, capital);
        }

        //Then, to all the other cities, picked at random.
        if(fromCity.getNumUnits() > 0)
        {
            LinkedList<Integer> cities = new LinkedList<>(tribe.getCitiesID());
            cities.remove((Integer)tribe.getCapitalID());
            Collections.shuffle(cities, rnd);
            while (cities.size() > 0 && fromCity.getNumUnits() > 0){
                City destCity = (City)getActor(cities.removeFirst());
                while (destCity.canAddUnit() && fromCity.getNumUnits() > 0){
                    moveBelongingCity(fromCity, destCity);
                }
            }

            //If there are still units, they go to the tribe.
            if (fromCity.getNumUnits() > 0){
                for(Integer unitId: fromCity.getUnitsID())
                {
                    Unit removedUnit = (Unit) gameActors.get(unitId);
                    tribe.addExtraUnit(removedUnit);
                }
            }
        }
    }

    private void moveBelongingCity(City originalCity, City targetCity){
        //Move the unit in the citys' unit lists.
        int index = originalCity.getUnitsID().size()-1;
        int actorID = originalCity.removeUnitByIndex(index);
        targetCity.addUnit(actorID);

        //Assign new city to unit
        Unit removedUnit = (Unit) gameActors.get(actorID);
        removedUnit.setCityId(targetCity.getActorId());
    }
  
    private void computeTradeNetwork()
    {
        for(Tribe t : tribes) {

            if (t.controlsCapital()) {

                boolean[][] connectedTiles = new boolean[networkTiles.length][networkTiles[0].length];
                boolean[][] navigable = new boolean[networkTiles.length][networkTiles[0].length];

                ArrayList<Vector2d> ports = new ArrayList<>();

                //First, set up the graph. Including all tiles that correspond to active trade points (roads, cities, ports)
                for (int i = 0; i < networkTiles.length; ++i) {
                    for (int j = 0; j < networkTiles[0].length; ++j) {
                        //Only for this tribe
                        int cityId = tileCityId[i][j];
                        if (t.controlsCity(cityId)) {
                            // Map cities, roads and ports
                            connectedTiles[i][j] = networkTiles[i][j];

                            //Keep a list of my ports
                            if (buildings[i][j] == Types.BUILDING.PORT)
                                ports.add(new Vector2d(i, j));

                            //And navigable tiles
                            if ((terrains[i][j] == SHALLOW_WATER || terrains[i][j] == DEEP_WATER) //WATER
                                    && t.isVisible(i, j) && tileCityId[i][j] != -1) //VISIBLE AND NOT ENEMY
                            {
                                navigable[i][j] = true;
                            }

                        }
                    }
                }

                TradeNetworkStep tns = new TradeNetworkStep(connectedTiles);

                //Now, we need to add jump links. In this case, two ports are connected if
                // separated by [0,TribesConfig.PORT_TRADE_DISTANCE] WATER, VISIBLE, NON-ENEMY tiles
                for (Vector2d portFrom : ports) {
                    for (Vector2d portTo : ports) {
                        if (!portFrom.equals(portTo)) {

                            Vector2d originPortPos = new Vector2d(portFrom.x, portFrom.y);
                            Pathfinder tp = new Pathfinder(originPortPos, new TradeWaterStep(navigable));
                            ArrayList<PathNode> path = tp.findPathTo(new Vector2d(portTo.x, portTo.y));

                            if (path != null) //+1 because path includes destination
                            {
                                //We add this as a link between ports.
                                tns.addJumpLink(portFrom, portTo, true);
                            }
                        }
                    }
                }

                City capital = (City) getActor(t.getCapitalID());
                t.updateNetwork(new Pathfinder(capital.getPosition(), tns), this, t.getTribeId() == this.activeTribeID);
            }else {
                t.updateNetwork(null, this, t.getTribeId() == this.activeTribeID);
            }
        }
    }

    public int getActiveTribeID() {
        return activeTribeID;
    }

    public void setActiveTribeID(int activeTribeID) {
        this.activeTribeID = activeTribeID;
    }

    // Setter method for tribes array
    public void setTribes(Tribe[] t){
        this.tribes = t;
    }

    /**
     * Adds a city to a tribe
     * @param c city to add
     */
    public void addCityToTribe(City c, Random r)
    {
        addActor(c);
        if (c.isCapital()){
            tribes[c.getTribeId()].setCapitalID(c.getActorId());
        }
        tribes[c.getTribeId()].addCity(c.getActorId());

        //cities provide visibility, which needs updating
        tribes[c.getTribeId()].clearView(c.getPosition().x, c.getPosition().y, TribesConfig.NEW_CITY_CLEAR_RANGE, r, this.copy());

        //By default, cities are considered to be roads for trade network purposes.
        networkTiles[c.getPosition().x][c.getPosition().y] = true;
    }


    public void removeUnitFromBoard(Unit u)
    {
        Vector2d pos = u.getPosition();
        setUnitIdAt(pos.x, pos.y, 0);
        removeActor(u.getActorId());
    }

    /**
     * Adds a unit to a city, which created it.
     * @param c city that created the unit
     * @param u unit to add
     */
    public void addUnit(City c, Unit u)
    {
        //First, add the actor to the list of game state actors
        addActor(u);

        //Place it in the board
        Vector2d pos = u.getPosition();
        setUnitIdAt(pos.x, pos.y, u);

        //Finally, add the unit to the city that created it, unless it belongs to the tribe.
        if(u.getCityId() != -1)
            c.addUnit(u.getActorId());
    }

    public void removeUnitFromCity(Unit u, City city, Tribe tribe)
    {
        if(u.getCityId() != -1) { //This happens when the unit belongs to the city
            city.removeUnit(u.getActorId());
        }else{
            tribe.removeExtraUnit(u);
        }
    }

    /**
     * Adds a new actor to the list of game actors
     * @param actor the actor to add
     */
    void addActor(core.actors.Actor actor)
    {
        actorIDcounter++;
        gameActors.put(actorIDcounter, actor);
        actor.setActorId(actorIDcounter);
    }

    /**
     * Gets a game actor from its tileCityId.
     * @param actorId the tileCityId of the actor to retrieve
     * @return the actor, null if the tileCityId doesn't correspond to an actor (note that it may have
     * been deleted if the actor was removed from the game).
     */
    public core.actors.Actor getActor(int actorId)
    {
        return gameActors.get(actorId);
    }

    /**
     * Removes an actor from the list of actor
     * @param actorId tileCityId of the actor to remove
     * @return true if the actor was removed (false may indicate that it didn't exist).
     */
    public boolean removeActor(int actorId)
    {
        return gameActors.remove(actorId) != null;
    }

    /**
     * Returns a list of positions where roads can be built by a certain tribe.
     * @param tribeId id of the tribe that could build roads
     * @return the list of positions where a road could be build
     */
    public ArrayList<Vector2d> getBuildRoadPositions(int tribeId)
    {
        ArrayList<Vector2d> positions = new ArrayList<>();
        for (int i=0; i<size; i++){
            for (int j=0; j<size; j++){
                if(canBuildRoadAt(tribeId, i, j))
                    positions.add(new Vector2d(i,j));
            }
        }
        return positions;
    }

    /**
     * Returns true if tribeId can build a road in (x,y)
     * It does not check for tribe stars or technology, *only* for board features (territory, terrain and visibility)
     * @param tribeId id of the tribe that could build roads
     * @return the list of positions where a road could be build
     */
    public boolean canBuildRoadAt(int tribeId, int x, int y)
    {
        // Visible tile?
        if(tribes[tribeId].isVisible(x, y))
        {
            // Only on certain terrain types.
            if(terrains[x][y] == Types.TERRAIN.VILLAGE || terrains[x][y] == Types.TERRAIN.PLAIN || terrains[x][y] == Types.TERRAIN.FOREST)
            {
                //Only on tiles that are neutral or in my cities
                int cityId = tileCityId[x][y];
                if(cityId == -1 || tribes[tribeId].controlsCity(cityId))
                {
                    //There should be no road already here
                    if(!networkTiles[x][y])
                    {
                        //Finally, there should be no enemy unit at this position
                        if(!enemyUnitAt(tribeId, x, y))
                        {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean enemyUnitAt(int tribeId, int x, int y)
    {
        //It may be that there's no unit here
        if(units[x][y] == 0)
            return false;
        else
        {
            //Or it is from my tribe.
            Unit u = (Unit) gameActors.get(units[x][y]);
            if(u.getTribeId() == tribeId)
            {
                return false;
            }
        }
        return true;
    }

    public void addRoad(int x, int y)
    {
        setTradeNetwork(x, y, true);
    }


    private class TradeWaterStep implements NeighbourHelper
    {
        private boolean [][]navigable;

        TradeWaterStep(boolean [][]navigable)
        {
            this.navigable = navigable;
        }

        @Override
        // from: position from which we need neighbours
        // costFrom: is the total move cost computed up to "from"
        // Using this.board, this.tribe, from and costFrom, gets all the adjacent neighbours to tile in position "from"
        public ArrayList<PathNode> getNeighbours(Vector2d from, double costFrom) {

            ArrayList<PathNode> neighbours = new ArrayList<>();
            double stepCost = 1.0;

            for(Vector2d tile : from.neighborhood(1, 0, size)) {
                int x = tile.x;
                int y = tile.y;
                if(navigable[x][y] && costFrom+stepCost <= TribesConfig.PORT_TRADE_DISTANCE)
                {
                    neighbours.add(new PathNode(new Vector2d(x, y), stepCost));
                }
            }

            return neighbours;
        }

        @Override
        public void addJumpLink(Vector2d from, Vector2d to, boolean reverse) {
            //No jump links
        }
    }


    private class TradeNetworkStep implements NeighbourHelper
    {
        private boolean [][]connected;
        private HashMap<Vector2d, ArrayList<Vector2d>> jumpLinks;

        TradeNetworkStep (boolean [][]connected)
        {
            this.connected = connected;
            this.jumpLinks = new HashMap<>();
        }

        @Override
        // from: position from which we need neighbours
        // costFrom: is the total move cost computed up to "from"
        // Using this.board, this.tribe, from and costFrom, gets all the adjacent neighbours to tile in position "from"
        public ArrayList<PathNode> getNeighbours(Vector2d from, double costFrom) {

            ArrayList<PathNode> neighbours = new ArrayList<>();
            double stepCost = 1.0;

            for(Vector2d tile : from.neighborhood(1, 0, size)) {
                int x = tile.x;
                int y = tile.y;
                if(connected[x][y])
                {
                    neighbours.add(new PathNode(new Vector2d(x, y), stepCost));
                }
            }

            //Now, add the jump link neighbours
            if(jumpLinks.containsKey(from))
            {
                ArrayList<Vector2d> connected = jumpLinks.get(from);
                for(Vector2d to: connected)
                {
                    neighbours.add(new PathNode(to, stepCost));
                }
            }


            return neighbours;
        }

        @Override
        public void addJumpLink(Vector2d from, Vector2d to, boolean reverse) {
            addAtoB(from, to);
            if(reverse) addAtoB(to, from);
        }

        private void addAtoB(Vector2d from, Vector2d to)
        {
            if(!jumpLinks.containsKey(from))
                jumpLinks.put(from, new ArrayList<>());

            ArrayList<Vector2d> connected = jumpLinks.get(from);
            connected.add(to);
        }
    }

}
