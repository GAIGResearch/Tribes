package core;

import core.actors.Tribe;
import core.game.Board;
import core.game.GameState;

public class Diplomacy {

    // stores the allegiances of the tribes
    private int[][] AllegianceStatus;

    // the absolute of the maximum positive and negative values
    private int absoluteMax = 60;

    /**
     * Creates allegiances for each of the existing tribes
     * @param size number of tribes in-game
     */

    public Diplomacy(int size){
        this.AllegianceStatus = new int[size][size];
        for (int i=0; i<size;i++){
            this.AllegianceStatus[i][i] =  100;
        }
    }

    /**
     * Creates allegiances for each of the existing tribes
     * @param b number of tribes in-game
     * @param value number of tribes in-game
     * @param attackerTribeID number of tribes in-game
     * @param targetTribeID number of tribes in-game
     */

    public void UpdateAllegiance(Board b, int value, int attackerTribeID, int targetTribeID){
        int attackerTribePos = 0;
        int targetTribePos = 0;
        for (int i=0; i<b.getTribes().length;i++){
            if (b.getTribes()[i].getTribeId() == attackerTribeID){
                attackerTribePos = i;

            } else if(b.getTribes()[i].getTribeId() == targetTribeID){
                targetTribePos = i;
            }
        }

        this.AllegianceStatus[attackerTribePos][targetTribePos] = this.AllegianceStatus[attackerTribePos][targetTribePos] + value;
        this.AllegianceStatus[targetTribePos][attackerTribePos] = this.AllegianceStatus[targetTribePos][attackerTribePos] + value;
        System.out.println(b.getTribes()[attackerTribePos].getName() + " " + b.getTribes()[targetTribePos].getName() +
                " " + this.AllegianceStatus[attackerTribePos][targetTribePos]);
    }

}

