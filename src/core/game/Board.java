package core.game;

import core.TechnologyTree;
import core.TribesConfig;
import core.Types;
import core.actors.Actor;
import core.actors.City;
import core.actors.Tribe;
import core.actors.units.*;
import utils.Vector2d;

import java.util.*;

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
    private boolean[][] roads;

    //Actors in the game
    private HashMap<Integer, Actor> gameActors;

    //variable to declare size of board
    private int size;

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
        this.roads = new boolean[size][size];


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
        Board copyBoard = new Board();
        copyBoard.size = this.size;
        copyBoard.tribes = new Tribe[this.tribes.length];

        copyBoard.terrains = new Types.TERRAIN[size][size];
        copyBoard.resources = new Types.RESOURCE[size][size];
        copyBoard.buildings = new Types.BUILDING[size][size];
        copyBoard.units = new int[size][size];
        copyBoard.tileCityId = new int[size][size];
        copyBoard.roads = new boolean[size][size];

        // Copy board objects (they are all ids)
        for (int x = 0; x < this.size; x++) {
            for (int y = 0; y < this.size; y++) {
                copyBoard.setUnitIdAt(x, y, units[x][y]);
                copyBoard.setTerrainAt(x, y, terrains[x][y]);
                copyBoard.setResourceAt(x, y, resources[x][y]);
                copyBoard.setBuildingAt(x, y, buildings[x][y]);
                copyBoard.tileCityId[x][y] = tileCityId[x][y];
                copyBoard.roads[x][y] = roads[x][y];
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
            copyBoard.gameActors.put(id, act.copy());
        }

        return copyBoard;
    }

    /**
     * Pushes a unit out of a city (x,y). The order in which tiles are tried for the new
     * destination are: S, W, N, E, SW, NW, NE, NW. If none of those positions are available, the
     * unit disappears.
     * See Push Grid at: https://polytopia.fandom.com/wiki/Giant
     *
     * @param tribeId: Id of the tribe the unit belongs to
     * @param toPush   Unit to be pushed
     * @param startX   x coordinate of the starting position of the unit to push
     * @param startY   y coordinate of the starting position of the unit to push
     */

    public void pushUnit(int tribeId, Unit toPush, int startX, int startY) {
        int xPush[] = {0, -1, 0, 1, -1, -1, 1, 1};
        int yPush[] = {1, 0, -1, 0, 1, -1, -1, 1};
        int idx = 0;
        boolean pushed = false;

        while (!pushed && idx < xPush.length) {
            int x = startX + xPush[idx];
            int y = startY + yPush[idx];

            if (x >= 0 && y >= 0 && x < size && y < size) {
                pushed = tryPush(tribeId, toPush, startX, startY, x, y);
            }
            idx++;
        }

        if (!pushed) {
            //it can't be pushed, unit must disappear
        }

    }

    private boolean tryPush(int tribeId, Unit toPush, int startX, int startY, int x, int y) {
        //there's no unit?
        if (units[x][y] > 0)
            return false;

        //climbable mountain?
        Types.TERRAIN terrain = terrains[x][y];
        if (terrain == Types.TERRAIN.MOUNTAIN) {
            if (tribes[tribeId].getTechTree().isResearched(Types.TECHNOLOGY.CLIMBING)) {
                moveUnit(toPush, startX, startY, x, y);
                return true;
            } else return false; //Can't be pushed if it's a mountain and climbing is not researched.
        }


        //Water with a port this tribe owns?
        Types.BUILDING b = buildings[x][y];
        if (terrain == Types.TERRAIN.SHALLOW_WATER) {
            if (b == Types.BUILDING.PORT) {
                City c = getCityInBorders(x, y);
                if (c != null && c.getTribeId() == tribeId) {
                    embark(toPush, startX, startY, x, y);
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
        moveUnit(toPush, startX, startY, x, y);
        return true;
    }


    private void embark(Unit unit, int x0, int y0, int xF, int yF) {
        City city = (City) gameActors.get(unit.getCityID());
        removeUnitFromBoard(unit);
        removeUnitFromCity(unit, city);

        //We're actually creating a new unit
        Vector2d newPos = new Vector2d(xF, yF);
        Unit boat = Types.UNIT.createUnit(newPos, unit.getKills(), unit.isVeteran(), unit.getCityID(), unit.getTribeId(), Types.UNIT.BOAT);
        addUnit(city, boat);
    }

    private void moveUnit(Unit unit, int x0, int y0, int xF, int yF) {
        units[x0][y0] = 0;
        units[xF][yF] = unit.getActorId();
        unit.setCurrentPosition(new Vector2d(xF, yF));
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
                    tribes[tribeId].clearView(x, y);
                }

                j++;
            }

            if (!moved) {
                //couldn't move in many steps. Let's just warn and progress from now.
                System.out.println("WARNING: explorer stuck, " + j + " steps without moving.");
            }

        }

    }


    private boolean traversable(int x, int y, int tribeId) {
        if (x >= 0 && y >= 0 && x < size && y < size) {
            //we rule out places we can't be.
            TechnologyTree tt = tribes[tribeId].getTechTree();

            //if mountain and climbing not researched
            if (terrains[x][y] == Types.TERRAIN.MOUNTAIN && !tt.isResearched(Types.TECHNOLOGY.CLIMBING))
                return false;

            //Shallow water and no sailing
            if (terrains[x][y] == Types.TERRAIN.SHALLOW_WATER && !tt.isResearched(Types.TECHNOLOGY.SAILING))
                return false;

            //Deep water and no navigation
            if (terrains[x][y] == Types.TERRAIN.DEEP_WATER && !tt.isResearched(Types.TECHNOLOGY.NAVIGATION))
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
        }
    }

    // Get size of board
    public int getSize() {
        return size;
    }


    public void setTradeNetwork(int x, int y, boolean trade)
    {
        roads[x][y] = trade;
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
    public void setCityBorders(){

        for(int i = 0; i<tribes.length; i++) {
            ArrayList<Integer> tribe1Cities = tribes[i].getCitiesID();

            for (int cityId : tribe1Cities) {
                City c = (City) gameActors.get(cityId);
                setBorderHelper(c, c.getBound());
            }

        }
    }

    // Set border helper method to set city bounds
    public void setBorderHelper(City c, int bound){
        Vector2d cityPos = c.getPosition();
        for (int i = cityPos.x-bound; i <= cityPos.x+bound; i++){
            for(int j = cityPos.y-bound; j <= cityPos.y+bound; j++) {
                if(tileCityId[i][j] == -1){
                    tileCityId[i][j] = c.getActorId();
                }
            }
        }
    }

    // Method to expand city borders, take city as param
    public void expandBorder(City city){
        city.setBound(city.getBound()+1);
        setBorderHelper(city,city.getBound());

    }

    public int getCityIdAt(int x, int y)
    {
        return tileCityId[x][y];
    }

    // Get all of tiles belong to the city
    public LinkedList<Vector2d> getCityTiles(int cityId){
        LinkedList<Vector2d> tiles = new LinkedList<>();
        for (int i=0; i<size; i++){
            for (int j=0; j<size; j++){
                if (tileCityId[i][j] == cityId){
                    tiles.add(new Vector2d(i, j));
                }
            }
        }
        return tiles;
    }

    /**
     * Captures a city or village for tribe t
     * @param t tribe that captures
     * @param x position of the city to capture
     * @param y position of the city to capture
     * @return true if city was captured.
     */
    public boolean capture(Tribe t, int x, int y){

        Types.TERRAIN ter = terrains[x][y];
        City c;
        if(ter == Types.TERRAIN.VILLAGE)
        {
            //Not a city. Needs to be created, assigned and its border calculated.
            c = new City(x, y, t.getTribeId());
            addCityToTribe(c);
            setBorderHelper(c, c.getBound());

        }else if(ter == Types.TERRAIN.CITY)
        {
            //The city exists, needs to change owner.
            c = (City) gameActors.get(tileCityId[x][y]);
            int cityId = c.getActorId();
            int prevTribeId = c.getTribeId();
            c.setTribeId(t.getTribeId());
            tribes[t.getTribeId()].addCity(cityId);
            tribes[prevTribeId].removeCity(cityId);

        }else
        {
            System.out.println("Warning: Tribe " + t.getTribeId() + " trying to caputre a non-city.");
            return false;
        }

        this.recomputeTradeNetwork();
        return true;
    }

    /**
     * Recomputes the trade network for the game..
     */
    private void recomputeTradeNetwork() {
        for(Tribe t : tribes)
        {
            t.updateNetwork(roads, tileCityId, buildings);
        }
    }

    // Setter method for tribes array
    public void setTribes(Tribe[] t){
        this.tribes = t;
    }

    /**
     * Adds a city to a tribe
     * @param c city to add
     */
    public void addCityToTribe(City c)
    {
        addActor(c);
        if (c.isCapital()){
            tribes[c.getTribeId()].setCapitalID(c.getActorId());
        }
        tribes[c.getTribeId()].addCity(c.getActorId());

        //By default, cities are considered to be roads for trade network purposes.
        roads[c.getPosition().x][c.getPosition().y] = true;
    }


    public void removeUnitFromBoard(Unit u)
    {
        Vector2d pos = u.getCurrentPosition();
        setUnitIdAt(pos.x, pos.y, 0);
        removeActor(u.getActorId());
    }

    /**
     * Adds a unit to a city, which created it.
     * @param c citi that created the unit
     * @param u unit to add
     * @return false if the unit coulnd't be added. That should not happen, so it prints a warning.
     */
    public boolean addUnit(City c, Unit u)
    {
        //First, add the actor to the list of game state actors
        addActor(u);

        //Place it in the board
        Vector2d pos = u.getCurrentPosition();
        setUnitIdAt(pos.x, pos.y, u);

        //Finally, add the unit to the city that created it
        boolean added = c.addUnit(u.getActorId());
        if(!added){
            System.out.println("ERROR: Unit failed to be added to city: u_id: " + u.getActorId() + ", c_id: " + c.getActorId());
        }

        return added;
    }

    public void removeUnitFromCity(Unit u, City city)
    {
        city.removeUnit(u.getActorId());
    }

    /**
     * Adds a new actor to the list of game actors
     * @param actor the actor to add
     * @return the unique identifier of this actor for the rest of the game.
     */
    public int addActor(core.actors.Actor actor)
    {
        int nActors = gameActors.size() + 1;
        gameActors.put(nActors, actor);
        actor.setActorId(nActors);
        return nActors;
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
                if(cityId == -1 || tribes[tribeId].hasCity(cityId))
                {
                    //There should be no road already here
                    if(!roads[x][y])
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
}
