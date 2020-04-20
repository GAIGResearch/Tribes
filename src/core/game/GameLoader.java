package core.game;

import core.Types;
import core.actors.City;
import core.actors.Tribe;
import core.actors.units.Unit;
import jdk.nashorn.internal.parser.JSONParser;
import org.json.JSONArray;
import org.json.JSONObject;
import utils.Vector2d;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.util.Iterator;
import java.util.Random;

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
    private Unit[] units;
    private City[] cities;

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

    public Unit[] getUnits() {
        return units;
    }

    public City[] getCities() {
        return cities;
    }
}
