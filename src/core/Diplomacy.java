package core;

import core.actors.Tribe;
import core.game.Board;
import core.game.GameState;

import java.util.Arrays;
import java.util.HashMap;

public class Diplomacy {

    // stores the allegiances of the tribes
    private int[][] AllegianceStatus;

    // the absolute of the maximum positive and negative values
    private int absoluteMax = 60;

    /*private HashMap<Integer,Integer> tribeIDs = new HashMap<>();

    public HashMap<Integer, Integer> getTribeIDs() {
        return tribeIDs;
    }

    public void setTribeIDs(Tribe[] tribes) {
        for (Tribe t: tribes){
            System.out.println(t.getName() + " " + t.getTribeId());
        }
        HashMap<Integer, Integer> tribeIDs = new HashMap<>();
        for (int i=0; i<tribes.length;i++){
            tribeIDs.put(tribes[i].getTribeId(),i);
        }
        this.tribeIDs = tribeIDs;
        for (Integer i: tribeIDs.keySet()) {
            System.out.println("key: " + i + " value: " + tribeIDs.get(i));
        }
    }*/

    /**
     * Creates allegiances for each of the existing tribes
     *
     * @param size number of tribes in-game
     */
    public Diplomacy(int size) {
        this.AllegianceStatus = new int[size][size];
    }

    /**
     * Creates allegiances for each of the existing tribes
     *
     * @param b               number of tribes in-game
     * @param value           number of tribes in-game
     * @param attackerTribeID number of tribes in-game
     * @param targetTribeID   number of tribes in-game
     */
    public void UpdateAllegiance(Board b, int value, int attackerTribeID, int targetTribeID) {
        //int attackerTribePos = this.tribeIDs.get(attackerTribeID);
        //int targetTribePos = this.tribeIDs.get(targetTribeID);

        this.AllegianceStatus[attackerTribeID][targetTribeID] = this.AllegianceStatus[attackerTribeID][targetTribeID] + value;
        this.AllegianceStatus[targetTribeID][attackerTribeID] = this.AllegianceStatus[targetTribeID][attackerTribeID] + value;
        System.out.println(b.getTribes()[attackerTribeID].getName() + " " + b.getTribes()[targetTribeID].getName() +
                " " + this.AllegianceStatus[attackerTribeID][targetTribeID]);
    }

    //Method to test the diplomacy is working
    public void LogAllegiance() {
        System.out.println(Arrays.deepToString(this.AllegianceStatus));
    }

}

