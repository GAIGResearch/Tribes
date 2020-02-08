package core.actors;

import core.Types;

import java.util.LinkedList;

public class Tribe extends Actor{

    private Types.RESULT winner = Types.RESULT.INCOMPLETE;
    private int score = 0;
    private LinkedList<Integer> citiesID = new LinkedList<>();
    private Types.TRIBE tribe;

    //ID of this tribe. It corresponds with the ID of the player who controls it.
    private int tribeID;

    public Tribe(int tribeID, int cityID, Types.TRIBE tribe) {
        this.tribeID = tribeID;
        citiesID.add(cityID);
        this.tribe = tribe;
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

    public Types.TECHNOLOGY getInitialTechnology(){
        return tribe.getInitialTech();
    }

    public void addScore(int score){
        this.score += score;
    }

    public LinkedList<Integer> getCitiesID() {
        LinkedList<Integer> copyCities = new LinkedList<>();
        for (Integer integer : citiesID) {
            copyCities.add(integer);
        }
        return copyCities;
    }

    public String getName(){return tribe.getName();}

    public Types.TRIBE getType(){return tribe;}

    public Types.RESULT getWinner() {return winner;}
    public int getScore() {return score;}

    public void setTribeID(int tribeID) {
        this.tribeID = tribeID;
    }

    public int getTribeID() {
        return tribeID;
    }


}
