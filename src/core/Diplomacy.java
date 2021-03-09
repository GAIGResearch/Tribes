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
     * @param b             board for the game
     * @param value         value the allegiance will change by
     * @param initTribeID   ID for the tribe initiating the action
     * @param targetTribeID ID for the target tribe of the action
     */
    public void UpdateAllegiance(Board b, int value, int initTribeID, int targetTribeID) {

        //TODO check new value against absolute max, if new value is above, set to +-absolute max

        this.AllegianceStatus[initTribeID][targetTribeID] = this.AllegianceStatus[initTribeID][targetTribeID] + value;
        this.AllegianceStatus[targetTribeID][initTribeID] = this.AllegianceStatus[targetTribeID][initTribeID] + value;
    }

    //Method to test the diplomacy is working
    public void LogAllegiance() {
        System.out.println(Arrays.deepToString(this.AllegianceStatus));
    }

}

