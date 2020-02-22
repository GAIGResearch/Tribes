package core.game;

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

    //Actors in the game
    private HashMap<Integer, Actor> gameActors;

    //variable to declare size of board
    private int size;

    // Constructor for board
    public Board()
    {
        this.gameActors = new HashMap<>();
    }

    public void init (int size, Tribe[] tribes){

        terrains = new Types.TERRAIN[size][size];
        resources = new Types.RESOURCE[size][size];
        buildings = new Types.BUILDING[size][size];
        units = new int[size][size];

        this.size = size;
        this.tileCityId = new int[size][size];

        //Initialise tile IDs
        for (int x = 0; x<size; x++){
            for(int y =0; y<size; y++){
                tileCityId[x][y] = -1;
            }
        }

        this.assignTribes(tribes);
    }

    // Return deep copy of board
    public Board copy(){
        Board copyBoard = new Board();
        copyBoard.size = this.size;
        copyBoard.tribes = new Tribe[this.tribes.length];

        copyBoard.terrains = new Types.TERRAIN[size][size];
        copyBoard.resources = new Types.RESOURCE[size][size];
        copyBoard.buildings = new Types.BUILDING[size][size];
        copyBoard.units = new int[size][size];
        copyBoard.tileCityId = new int[size][size];

        // Copy board objects (they are all ids)
        for (int x = 0; x<this.size; x++){
            for(int y = 0; y<this.size; y++){
                copyBoard.setUnitIdAt(x,y,units[x][y]);
                copyBoard.setTerrainAt(x,y,terrains[x][y]);
                copyBoard.setResourceAt(x,y,resources[x][y]);
                copyBoard.setBuildingAt(x,y,buildings[x][y]);
                copyBoard.tileCityId[x][y] = tileCityId[x][y];
            }
        }

        // Copy tribes
        for (int i = 0; i< tribes.length; i++){
            copyBoard.tribes[i] = tribes[i].copy();
        }

        //Deep copy of all actors in the board
        copyBoard.gameActors = new HashMap<>();
        for(Actor act : gameActors.values())
        {
            int id = act.getActorID();
            copyBoard.gameActors.put(id, act.copy());
        }

        return copyBoard;
    }


    public Tribe[] getTribes() {return tribes;}
    public Tribe getTribe(int tribeId) {return tribes[tribeId];}
    public int getNumTribes() {return tribes.length;}

    /**
     * Sets the tribes that will play the game. The number of tribes must equal the number of players in Game.
     * @param tribes to play with
     */
    private void assignTribes(Tribe[] tribes)
    {
        int numTribes = tribes.length;
        this.tribes = new Tribe[numTribes];
        for(int i = 0; i < numTribes; ++i)
        {
            this.tribes[i] = tribes[i];
            this.tribes[i].setTribeID(i);
        }
    }

    // Get size of board
    public int getSize() {
        return size;
    }

    // Get terrains array
    public Types.TERRAIN[][] getTerrains(){
        return this.terrains;
    }

    // Get units array
    public int[][] getUnits(){
        return this.units;
    }

    // Get Resources array
    public Types.RESOURCE[][] getResources(){
        return this.resources;
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
    public int getCityIDAt(int x, int y){
        return tileCityId[x][y];
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
        units[x][y] = unit.getActorID();
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


    // Moves a unit on the board if unit exists on tile
//    void moveUnit(Types.DIRECTIONS direction, int x, int y){
//        int[][] newUnits = new int[size][size];
//
//        if(units[x][y] == -1){
//            System.out.println("Invalid move, there is no unit on this tile");
//            return;
//        }
//
//        // Loop through old units array and update new units array with new position
//        for(int i =0; i<units.length; i++){
//            for(int j =0; x<units.length; x++){
//                if((i !=x && j!=y) && (i != x+direction.x() && j != y+direction.y())){
//                    newUnits[i][j] = checkUnit(i,j);
//                }else if( i == x && j == y){
//                    newUnits[i][j] = null;
//                }else if(i == x+direction.x() && j == y+direction.y()){
//                    Unit oldUnit = checkUnit(x,y);
//                    newUnits[i][j] = oldUnit;
//                }
//            }
//        }
//
//        // Update units array
//        units = newUnits;
//
//    }
//
//    // Helper method to check which unit at which tile for deep copying unit array
//    Unit checkUnit(int x, int y){
//        int unitId = units[x][y];
//        Unit u = (Unit) gameActors.get(unitId);
//        String unitName = u.getClass().getName();
//        switch (unitName){
//            case "Rider":
//                return new Rider(u.getCurrentPosition(), u.getKills(), u.isVeteran(), u.getOwnerID());
//            case "Knight":
//                return new Knight(u.getCurrentPosition(), u.getKills(), u.isVeteran(), u.getOwnerID());
//            case "Archer":
//                return new Archer(u.getCurrentPosition(), u.getKills(), u.isVeteran(), u.getOwnerID());
//            case "Warrior":
//                return new Warrior(u.getCurrentPosition(), u.getKills(), u.isVeteran(), u.getOwnerID());
//            case "Catapult":
//                return new Catapult(u.getCurrentPosition(), u.getKills(), u.isVeteran(), u.getOwnerID());
//            case "Defender":
//                return new Defender(u.getCurrentPosition(), u.getKills(), u.isVeteran(), u.getOwnerID());
//            case "Swordman":
//                return new Swordman(u.getCurrentPosition(), u.getKills(), u.isVeteran(), u.getOwnerID());
//            //case 'MIND_BEARER': //TODO: Need Mind bearer class
//            //    return new Warrior(u.getCurrentPosition(), u.getKills(), u.isVeteran(), u.getOwnerID());
//        }
//        return u;
//    }

    // Helper method to check which terrain at which tile for deep copying unit array
//    Types.TERRAIN checkTerrain(int x, int y){
//        switch (terrains[x][y]){
//            case PLAIN :
//                return Types.TERRAIN.PLAIN;
//            case VILLAGE:
//                return Types.TERRAIN.VILLAGE;
//            case SHALLOW_WATER:
//                return Types.TERRAIN.SHALLOW_WATER;
//            case  DEEP_WATER:
//                return Types.TERRAIN.DEEP_WATER;
//            case CITY:
//                return Types.TERRAIN.CITY;
//            case MOUNTAIN:
//                return Types.TERRAIN.MOUNTAIN;
//            case FOREST:
//                return Types.TERRAIN.FOREST;
//        }
//        return Types.TERRAIN.PLAIN;
//    }
//
//    // Helper method to check which terrain at which tile for deep copying unit array
//    Types.RESOURCE checkResource(int x, int y){
//        Types.RESOURCE r = null;
//        switch (resources[x][y]){
//            case FISH:
//                return Types.RESOURCE.FISH;
//            case FRUIT:
//                return Types.RESOURCE.FRUIT;
//            case ANIMAL:
//                return Types.RESOURCE.ANIMAL;
//            case WHALES:
//                return Types.RESOURCE.WHALES;
//            case ORE:
//                return Types.RESOURCE.ORE;
//            case CROPS:
//                return Types.RESOURCE.CROPS;
//            case RUINS:
//                return Types.RESOURCE.RUINS;
//        }
//        return r;
//    }
//
//    // Helper method to check which Building at which tile for deep copying building array
//    Building checkBuilding(int x, int y){
//        Building b = getBuildingAt(x,y);
//        switch (b.getTYPE()){
//            case TEMPLE:
//                return new Temple(x,y);
//            case  PORT:
//                return new Port(x,y);
//            case MINE:
//                return new Mine(x,y);
//            case  FORGE:
//                return new Forge(x,y);
//            case FARM:
//                return new Farm(x,y);
//            case WINDMILL:
//                return new Windmill(x,y);
//            case ROAD:
//                return null;
//            case CUSTOM_HOUSE:
//                return new CustomHouse(x,y);
//            case LUMBER_HUT:
//                return new LumberHut(x,y);
//            case SAWMILL:
//                return new Sawmill(x,y);
//            case WATER_TEMPLE:
//                return null; // TODO: Need Water Temple class
//            case FOREST_TEMPLE:
//                return new ForestTemple(x,y);
//            case MOUNTAIN_TEMPLE:
//                return null; // TODO: Need Mountain Temple class
//            case ALTAR_OF_PEACE: //TODO: Need Altar of Peace class
//                return null;
//            case EMPERORS_TOMB: //TODO: Need Emperors tomb class
//                return null;
//            case EYE_OF_GOD: //TODO: Need Eye of God class
//                return null;
//            case GATE_OF_POWER: //TODO: Need Gate of power class
//                return null;
//            case GRAND_BAZAR: //TODO: Need Grand Bazar class
//                return null;
//            case PARK_OF_FORTUNE: //TODO: Need Park of fortune class
//                return null;
//            case TOWER_OF_WISDOM: //TODO: Need tower of wisdom class
//                return null;
//        }
//        return b;
//    }

    // Setter method for units array
    public void setUnits(int[][] u){
        this.units = u;
    }

    // Setter method for terrains array
    public void setTerrains(Types.TERRAIN[][] t){
        this.terrains = t;
    }

    // Setter method for resources array
    public void setResources(Types.RESOURCE[][] r){
        this.resources = r;
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
        int x = c.getX();
        int y = c.getY();
        for (int i = x-bound; i <= x+bound; i++){
            for(int j = y-bound; j <= y+bound; j++) {
                if(tileCityId[i][j] == -1){
                    tileCityId[i][j] = c.getActorID();
                }
            }
        }
    }

    // Method to expand city borders, take city as param
    public void expandBorder(City city){
        city.setBound(city.getBound()+1);
        setBorderHelper(city,city.getBound());

    }

    public int getTileCityId(int x, int y)
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


    public void occupy(Tribe t, int x, int y){
        ArrayList<Integer> cities = t.getCitiesID();

        City c = null;
        for (int cityId:cities) {
            City city = (City) gameActors.get(cityId);
            if (city.getX() == x && city.getY() == y){
                c = city;
                break;
            }
        }
        if (c != null && c.getIsValley()){
            c.setIsValley(false);
            // TODO: Assign the owner
            c.levelUp();
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
        if (c.isPrism()){
            tribes[c.getTribeId()].setCapitalID(c.getActorID());
        }
        tribes[c.getTribeId()].addCity(c.getActorID());
    }

    public void addUnitToBoard(Unit u)
    {
        addActor(u);
        Vector2d pos = u.getCurrentPosition();
        setUnitIdAt(pos.x, pos.y, u);
    }


    /**
     * Adds a unit to a city, which created it.
     * @param u unit to add
     * @param c citiy that created the unit
     * @return false if the unit coulnd't be added. That should not happen, so it prints a warning.
     */
    public boolean addUnitToCity(Unit u, City c)
    {
        boolean added = c.addUnits(u.getActorID());
        if(!added){
            System.out.println("ERROR: Unit failed to be added to city: u_id: " + u.getActorID() + ", c_id: " + c.getActorID());
        }
        return added;
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
        actor.setActorID(nActors);
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

}
