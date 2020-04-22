package core.game;

import core.Types;
import core.actors.City;
import core.actors.Tribe;
import core.actors.units.Battleship;
import core.actors.units.Boat;
import core.actors.units.Ship;
import core.actors.units.Unit;
import org.json.JSONObject;
import utils.Vector2d;

import java.io.*;
import java.util.Iterator;

public class GameLoader
{

    private int tick;
    private double seed;
    private int activeTribeID;

    private JSONObject JBoard;
    private JSONObject JTribe;
    private JSONObject JUnit;
    private JSONObject JCity;

    private Board board;
    private Tribe[] tribes;

    public GameLoader(String fileName) {

        String jsonData = readFile(fileName);
        JSONObject gameINFO = new JSONObject(jsonData);

        this.JBoard = (JSONObject) gameINFO.get("board");
        this.JTribe = gameINFO.getJSONObject("tribes");
        this.tick = gameINFO.getInt("tick");
        this.seed = gameINFO.getDouble("seed");
        this.activeTribeID = gameINFO.getInt("activeTribeID");
        this.JUnit = gameINFO.getJSONObject("unit");
        this.JCity = gameINFO.getJSONObject("city");

        loadTribes();
//        TODO: Testing
//        for (Tribe t: tribes){
//            System.out.println(t);
//        }

        loadBoard();

        loadUnits();

        loadCities();

    }

    private void loadTribes(){
        Iterator<String> keys = JTribe.keys();
        tribes = new Tribe[JTribe.length()];
        int index = 0;
        while (keys.hasNext()){
            String key = keys.next();
            JSONObject tribeINFO = JTribe.getJSONObject(key);
            tribes[index++] = new Tribe(Integer.parseInt(key), tribeINFO);
        }
    }

    private void loadBoard() {
        board = new Board(JBoard);
    }

    private void loadUnits(){
        Iterator<String> keys = JUnit.keys();
        while (keys.hasNext()){
            String key = keys.next();
            JSONObject unitINFO = JUnit.getJSONObject(key);
            Types.UNIT unitType = Types.UNIT.getTypeByKey(unitINFO.getInt("type"));
            Unit unit = Types.UNIT.createUnit(new Vector2d(unitINFO.getInt("x"),unitINFO.getInt("y")),
                    unitINFO.getInt("kill"), unitINFO.getBoolean("isVeteran"),
                    unitINFO.getInt("cityID"), unitINFO.getInt("tribeId"), unitType);
            unit.setCurrentHP(unitINFO.getInt("currentHP"));
            if (unitType == Types.UNIT.BOAT){
                ((Boat)unit).setBaseLandUnit(Types.UNIT.getTypeByKey(unitINFO.getInt("baseLandType")));
            }else if (unitType == Types.UNIT.SHIP){
                ((Ship)unit).setBaseLandUnit(Types.UNIT.getTypeByKey(unitINFO.getInt("baseLandType")));
            }else if (unitType == Types.UNIT.BATTLESHIP){
                ((Battleship)unit).setBaseLandUnit(Types.UNIT.getTypeByKey(unitINFO.getInt("baseLandType")));
            }
            board.addActor(unit, Integer.parseInt(key));
        }
    }

    private void loadCities(){
        Iterator<String> keys = JCity.keys();
        while (keys.hasNext()){
            String key = keys.next();
            JSONObject cityINFO = JCity.getJSONObject(key);
            City city = new City(cityINFO, Integer.parseInt(key));
            board.addActor(city, Integer.parseInt(key));
        }
    }

    public String readFile(String filename) {
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

    public int getTick() {
        return tick;
    }

    public double getSeed() {
        return seed;
    }

    public int getActiveTribeID() {
        return activeTribeID;
    }

    public Board getBoard() {
        return board;
    }

    public Tribe[] getTribes() {
        return tribes;
    }

}
