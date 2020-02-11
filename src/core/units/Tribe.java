package core.units;

import core.Types;
import core.actors.City;

import java.util.ArrayList;

public class Tribe extends Actor{

    private Types.RESULT winner = Types.RESULT.INCOMPLETE;
    private int score = 0;
    private ArrayList<City> cities;

    //ID of this tribe. It corresponds with the ID of the player who controls it.
    private int tribeID;


    public Types.RESULT getWinner() {return winner;}
    public int getScore() {return score;}

    public void setTribeID(int tribeID) {
        this.tribeID = tribeID;
    }

    public int getTribeID() {
        return tribeID;
    }

    public ArrayList<City> getCities(){
        return cities;
    }

    public void setScore(int s) {this.score = s;}

    public void setCities(ArrayList<City> c){
        this.cities = c;
    }


}
