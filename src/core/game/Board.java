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

    //Actors in the game
    private HashMap<Integer, Actor> gameActors;

    //variable to declare size of board
    private int size;

    // Player currently making a move.
    private int activeTribeID = -1;

    //Actor ID counter
    private int actorIDcounter;

    //Trade Network of this board
    private TradeNetwork tradeNetwork;

    //Indicate if this model is native (not a copy of the game one) or not.
    private boolean isNative;

    // Constructor for board
    public Board() {
        this.gameActors = new HashMap<>();
    }

    /**
     * Inits the board given its size and array of playing tribes. Initializes all the data structures for the board
     * @param size size of the board (MUST be square)
     * @param tribes tribes to play this game.
     */
    void init(int size, Tribe[] tribes) {

        this.size = size;
        terrains = new Types.TERRAIN[size][size];
        resources = new Types.RESOURCE[size][size];
        buildings = new Types.BUILDING[size][size];
        units = new int[size][size];
        tileCityId = new int[size][size];
        tradeNetwork = new TradeNetwork(size);
        isNative = true;

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

    /**
     * Deep copies the board and returns the copy. It's copied as full not hiding any information
     * @return copy of the current board.
     */
    public Board copy() {
        return copy(false, -1);
    }

    /**
     * Returns a copy of the board.
     * @param partialObs indicates if the board should be copied at full or some information needs to be hid
     * @param playerId if partialObs is true, id of the player who will receive this copy.
     * @return a copy of the board
     */
    public Board copy(boolean partialObs, int playerId) {
        Board copyBoard = new Board();
        copyBoard.size = this.size;
        copyBoard.tribes = new Tribe[this.tribes.length];
        copyBoard.terrains = new Types.TERRAIN[size][size];
        copyBoard.resources = new Types.RESOURCE[size][size];
        copyBoard.buildings = new Types.BUILDING[size][size];
        copyBoard.units = new int[size][size];
        copyBoard.tileCityId = new int[size][size];
        copyBoard.activeTribeID = activeTribeID;
        copyBoard.actorIDcounter = actorIDcounter;
        copyBoard.tradeNetwork = new TradeNetwork(size);
        copyBoard.isNative = false;

        // Copy board objects (they are all ids)
        for (int x = 0; x < this.size; x++) {
            for (int y = 0; y < this.size; y++) {

                if(!partialObs || tribes[playerId].isVisible(x,y))
                {
                    copyBoard.units[x][y] = units[x][y];
                    copyBoard.setTerrainAt(x, y, terrains[x][y]);
                    copyBoard.setResourceAt(x, y, maskResource(playerId, x, y));
                    copyBoard.setBuildingAt(x, y, buildings[x][y]);
                    copyBoard.tileCityId[x][y] = tileCityId[x][y];
                    copyBoard.tradeNetwork.setTradeNetworkValue(x,y,tradeNetwork.getTradeNetworkValue(x,y));
                }
            }
        }

        // Copy tribes
        for (int i = 0; i < tribes.length; i++) {
            boolean hideInfo = (i != playerId) && partialObs;
            copyBoard.tribes[i] = tribes[i].copy(hideInfo);
        }

        //Deep copy of all actors in the board
        copyBoard.gameActors = new HashMap<>();
        for (Actor act : gameActors.values()) {
            int id = act.getActorId();
            int actTribeId = act.getTribeId();

            //When do we copy? if it's the tribe (id==playerId), full observable or actor visible if part. obs.
            if(actTribeId == playerId || !partialObs || tribes[playerId].isVisible(act.getPosition().x, act.getPosition().y))
            {
                boolean hideInfo = (actTribeId != playerId) && partialObs;
                copyBoard.gameActors.put(id, act.copy(hideInfo));
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
    boolean pushUnit(Tribe tribe, Unit toPush, int startX, int startY, Random r) {
        //xPush and yPush encode the order of tiles where the push may be tried.
        int[] xPush = {0, -1, 0, 1, -1, -1, 1, 1};
        int[] yPush = {1, 0, -1, 0, 1, -1, -1, 1};
        int idx = 0;
        boolean pushed = false;

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

    /**
     * Attampts to push a unitt from one position to another.
     * @param tribe tribe of the unit that is being pushed.
     * @param toPush unit being pushed
     * @param startX x coordinate of the position where the unit is being pushed from
     * @param startY y coordinate of the position where the unit is being pushed from
     * @param x x coordinate of the position where the unit is being pushed to
     * @param y y coordinate of the position where the unit is being pushed to
     * @param r random number generator, necessary to clear view if the pushed unit discovers new tiles.
     * @return true if the unit could be pushed.
     */
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

    /**
     * Embarks a unit at position (x,y)
     * @param unit unit to transform into a boat
     * @param tribe tribe that the unit belongs to
     * @param x x coordinate of the position where the unit is embarking
     * @param y y coordinate of the position where the unit is embarking
     */
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
    }

    /**
     * Disembarks a unit at position (x,y)
     * @param unit unit to transform into a land unit, of the type defined in the unit's baseLandUnit
     * @param tribe tribe that the unit belongs to
     * @param x x coordinate of the position where the unit is disembarking
     * @param y y coordinate of the position where the unit is disembarking
     */
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
    }

    /**
     * Moves a unit from x0,y0 to xF,yF
     * @param unit unit to move.
     * @param x0 x coordinate of the starting position
     * @param y0 y coordinate of the starting position
     * @param xF x coordinate of the ending position
     * @param yF y coordinate of the ending position
     * @param r random generator
     */
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

    /**
     * Launches a explorer to clear view in the game
     * @param x0 x coordinate of the starting position
     * @param y0 y coordinate of the starting position
     * @param tribeId id of the tribe that launches the explorer.
     * @param rnd random generator for the explorer's moves.
     */
    public void launchExplorer(int x0, int y0, int tribeId, Random rnd) {

        Vector2d currentPos = new Vector2d(x0, y0);
        for (int i = 0; i < TribesConfig.NUM_STEPS; ++i) {
            int j = 0;
            boolean moved = false;

            while (!moved && j < TribesConfig.NUM_STEPS * 3) {
                //Pick a neighbour tile at random
                LinkedList<Vector2d> neighs = currentPos.neighborhood(1,0, size);
                Vector2d next = neighs.get(rnd.nextInt(neighs.size()));

                if (traversable(next.x, next.y, tribeId)) {
                    moved = true;
                    currentPos.x = next.x;
                    currentPos.y = next.y;
                    tribes[tribeId].clearView(currentPos.x, currentPos.y, TribesConfig.EXPLORER_CLEAR_RANGE, rnd, this.copy());
                }

                j++;
            }

            if (!moved) {
                //couldn't move in many steps. Let's just warn and progress from now.
                System.out.println("WARNING: explorer stuck, " + j + " steps without moving.");
            }

        }

    }

    /**
     * Checks if the position x, y is traversable for the given tribe.
     * @param x x coordinate of the position to check
     * @param y y coordinate of the position to check
     * @param tribeId id of the tribe this tile may be or not traversable.
     * @return true if a unit from tribeId can occupy position x,y in the board.
     */
    public boolean traversable(int x, int y, int tribeId) {

        //we rule out places we can't be.
        TechnologyTree tt = tribes[tribeId].getTechTree();

        //if mountain and climbing not researched
        if (terrains[x][y] == Types.TERRAIN.MOUNTAIN && !tt.isResearched(Types.TECHNOLOGY.CLIMBING))
            return false;

        //Shallow water and no sailing
        if (terrains[x][y] == SHALLOW_WATER && !tt.isResearched(Types.TECHNOLOGY.SAILING))
            return false;

        //Deep water and no navigation
        return terrains[x][y] != DEEP_WATER || tt.isResearched(Types.TECHNOLOGY.NAVIGATION);
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

    /**
     * Gets the unit at location x,y
     * @param x x coordinate of the tile to check
     * @param y y coordinate of the tile to check
     * @return The unit at x,y, null if there isn't any unit there.
     */
    public Unit getUnitAt(int x, int y){

        Actor act = gameActors.get(units[x][y]);
        if(act != null)
            return (Unit) act;
        return null;
    }

    /**
     * Returns the city that owns the tile x,y
     * @param x x coordinate of the tile to check
     * @param y y coordinate of the tile to check
     * @return the city with a tile within its borders. Null if x,y doesn't belong to any city.
     */
    public City getCityInBorders(int x, int y){
        if(tileCityId[x][y] == -1)
            return null;
        else
            return (City) gameActors.get(tileCityId[x][y]);
    }

    /**
     * Assigns board tiles to a given city, adding score to the tribe who adquired this terrain.
     * @param c city that has tiles
     * @param radius maximum distance from the city center where the city can adquire tiles.
     */
    void assignCityTiles(City c, int radius){
        Vector2d cityPos = c.getPosition();
        Tribe t = getTribe(c.getTribeId());
        LinkedList<Vector2d> tiles = cityPos.neighborhood(radius, 0, size);
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

    /**
     * Expands the borders of a given city
     * @param city city whose borders to expand.
     */
    public void expandBorder(City city){
        city.setBound(city.getBound()+TribesConfig.CITY_EXPANSION_TILES);
        assignCityTiles(city,city.getBound());
    }

    /**
     * Indicates if there's a road in position x,y
     * @param x x coordinate of the position to check
     * @param y y coordinate of the position to check
     * @return if there's a road in that position.
     */
    public boolean isRoad(int x, int y) {
        return tradeNetwork.getTradeNetworkValue(x,y) && terrains[x][y] != SHALLOW_WATER && terrains[x][y] != DEEP_WATER && terrains[x][y] != CITY;
    }


    /**
     * Gets all the tiles that belong to a city
     * @param cityID id of the city being queried.
     * @return the list of positions of tiles belonging to this city.
     */
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
            assignCityTiles(newCity, newCity.getBound());

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

        tradeNetwork.setTradeNetwork(this, x, y, true);
        return true;
    }

    /**
     * Moves one unit to destCity from another one in the same tribe.
     * @param destCity destination city.
     * @param tribe tribe this unit belongs to.
     * @param rnd random generator to determine which city to take the unit from if the capital has no
     *            associated units or is not in control of this tribe.
     */
    private void moveOneToNewCity(City destCity, Tribe tribe, Random rnd)
    {
        //Capital is special, we start taking unnits from there.
        boolean ownsCapital = tribe.controlsCapital();
        City capital = (City) getActor(tribe.getCapitalID());
        LinkedList<Integer> cities = new LinkedList<>(tribe.getCitiesID());
        cities.remove((Integer)tribe.getCapitalID());

        // Move the unit from one city to village. Rank: capital -> cities -> None
        if(ownsCapital && capital.getUnitsID().size() > 0){
            moveLastUnitFromCity(capital, destCity);
        }else{
            boolean moved = false;
            //Capital is empty or not owned. Check the other cities at random
            Collections.shuffle(cities, rnd);
            while (!moved && cities.size() > 0){
                City originalCity = (City)getActor(cities.removeFirst());
                if (originalCity.getUnitsID().size() > 0){
                    moveLastUnitFromCity(originalCity, destCity);
                    moved = true;
                }
            }
        }
    }


    /**
     * Moves all the units from a given city to other cities in the tribe
     * @param fromCity original city that the units belong to
     * @param tribe of the units being moved.
     * @param rnd random generator to choose destination cities if required
     */
    private void moveAllFromCity(City fromCity, Tribe tribe, Random rnd)
    {
        //Capital is special, we start taking units from there.
        boolean ownsCapital = tribe.controlsCapital();
        City capital = (City) getActor(tribe.getCapitalID());

        //First to capital
        if(ownsCapital) while (capital.canAddUnit() && fromCity.getNumUnits() > 0){
            moveLastUnitFromCity(fromCity, capital);
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
                    moveLastUnitFromCity(fromCity, destCity);
                }
            }

            //If there are still units, they go to the tribe.
            if (fromCity.getNumUnits() > 0){
                for(Integer unitId: fromCity.getUnitsID())
                {
                    Unit removedUnit = (Unit) gameActors.get(unitId);
                    if(removedUnit != null)
                        tribe.addExtraUnit(removedUnit);
                }
            }
        }
    }

    /**
     * Moves the last unit from a given city to another.
     * @param originalCity original city that the units belong to
     * @param targetCity new city
     */
    private void moveLastUnitFromCity(City originalCity, City targetCity){
        //Move the unit in the citys' unit lists.
        int index = originalCity.getUnitsID().size()-1;
        int actorID = originalCity.removeUnitByIndex(index);
        targetCity.addUnit(actorID);

        //Assign new city to unit
        Unit removedUnit = (Unit) gameActors.get(actorID);
        removedUnit.setCityId(targetCity.getActorId());
    }


    /**
     * Adds a city to a tribe
     * @param c city to add
     */
    void addCityToTribe(City c, Random r)
    {
        addActor(c);
        if (c.isCapital()){
            tribes[c.getTribeId()].setCapitalID(c.getActorId());
        }
        tribes[c.getTribeId()].addCity(c.getActorId());

        //cities provide visibility, which needs updating
        tribes[c.getTribeId()].clearView(c.getPosition().x, c.getPosition().y, TribesConfig.NEW_CITY_CLEAR_RANGE, r, this.copy());

        //By default, cities are considered to be roads for trade network purposes.
        tradeNetwork.setTradeNetwork(this, c.getPosition().x, c.getPosition().y, true);
    }

    /**
     * Removes a unit from the board.
     * @param u unit to remove.
     */
    public void removeUnitFromBoard(Unit u)
    {
        Vector2d pos = u.getPosition();
        units[pos.x][pos.y] = 0;
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
        units[pos.x][pos.y] = u.getActorId();

        //Finally, add the unit to the city that created it, unless it belongs to the tribe.
        if(u.getCityId() != -1)
            c.addUnit(u.getActorId());
    }

    /**
     * Removes a unit from the city that has it assigned. If no city has it assigned, it removes it
     * for the Tribe's control.
     * @param u unit to remove
     * @param city City to remove the unit from
     * @param tribe Tribe this unit belongs to.
     */
    public void removeUnitFromCity(Unit u, City city, Tribe tribe)
    {
        if(u.getCityId() != -1 && city != null) { //This happens when the unit belongs to the tribe
            city.removeUnit(u.getActorId());
        }else{
            tribe.removeExtraUnit(u);
        }
    }

    /**
     * Adds a new actor to the list of game actors
     * @param actor the actor to add
     */
    private void addActor(core.actors.Actor actor)
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
    public Actor getActor(int actorId)
    {
        return gameActors.get(actorId);
    }

    /**
     * Removes an actor from the list of actor
     * @param actorId tileCityId of the actor to remove
     * @return true if the actor was removed (false may indicate that it didn't exist).
     */
    private boolean removeActor(int actorId)
    {
        return gameActors.remove(actorId) != null;
    }


    /**
     * Indicates if there's an enemy of tribeId unnit at x,y
     * @param tribeId tribe which the unit at x,y could be an enemy of
     * @param x x coordinate to check
     * @param y y coordinate to check
     * @return true if there's an enemy at x,y
     */
    private boolean enemyUnitAt(int tribeId, int x, int y)
    {
        //It may be that there's no unit here
        if(units[x][y] == 0)
            return false;
        else
        {
            //Or it is from my tribe.
            Unit u = (Unit) gameActors.get(units[x][y]);
            return u.getTribeId() != tribeId;
        }
    }

    /**
     * Adds a road to the board at position x,y. It recalculates the trade network with
     * this new road.
     * @param x x coordinate of the road position
     * @param y y coordinate of the road position
     */
    public void addRoad(int x, int y)
    {
        tradeNetwork.setTradeNetwork(this, x, y, true);
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
                    if(!tradeNetwork.getTradeNetworkValue(x,y))
                    {
                        //Finally, there should be no enemy unit at this position
                        return !enemyUnitAt(tribeId, x, y);
                    }
                }
            }
        }
        return false;
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
     * Removes a port from the network at position x, y
     * @param x x coordinate of the port destroyed.
     * @param y y coordinate of the port destroyed.
     */
    public void destroyPort(int x, int y)
    {
        this.tradeNetwork.setTradeNetwork(this, x, y,false);
    }

    /**
     * Adds a port to the network at position x, y
     * @param x x coordinate of the port added.
     * @param y y coordinate of the port added.
     */
    public void buildPort(int x, int y)
    {
        this.tradeNetwork.setTradeNetwork(this, x, y,true);
    }

    // Simple getters and setters
    public Tribe[] getTribes() { return tribes; }
    public int getSize() { return size; }
    public Tribe getTribe(int tribeId) { return tribes[tribeId]; }
    public int getActiveTribeID() { return activeTribeID; }
    public void setActiveTribeID(int activeTribeID) { this.activeTribeID = activeTribeID; }
    public void setTribes(Tribe[] t){ this.tribes = t; }
    boolean getNetworkTilesAt(int x, int y) { return this.tradeNetwork.getTradeNetworkValue(x,y); }
    public int[][] getUnits(){ return this.units; }
    public Types.TERRAIN getTerrainAt(int x, int y){ return terrains[x][y]; }
    int getUnitIDAt(int x, int y){ return units[x][y]; }
    public void setResourceAt(int x, int y, Types.RESOURCE r){ resources[x][y] =  r; }
    public void setTerrainAt(int x, int y, Types.TERRAIN t){ terrains[x][y] =  t; }
    public void setBuildingAt(int x, int y, Types.BUILDING b){ buildings[x][y] = b; }
    public Types.RESOURCE getResourceAt(int x, int y){ return resources[x][y]; }
    public Types.BUILDING getBuildingAt(int x, int y){ return buildings[x][y]; }
    public void setUnits(int[][] u){ this.units = u; }
    public int getCityIdAt(int x, int y) { return tileCityId[x][y]; }
    boolean isNative() { return isNative; }
}
