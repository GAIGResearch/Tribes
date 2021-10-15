package core;

import core.game.Board;

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

    public int[][] getAllegianceStatus() {
        return AllegianceStatus;
    }

    /**
     * Creates allegiances for each of the existing tribes
     *
     * @param value         value the allegiance will change by, will be negative if called by an action, positive if called by checkConsequences()
     * @param initTribeID   ID for the tribe initiating the action
     * @param targetTribeID ID for the target tribe of the action
     */
    public void updateAllegiance(int value, int initTribeID, int targetTribeID) {
        // checks if increase or decreasing the allegiance will go over the limit
        // if so, set the value to the the difference of the absoluteMax and the current allegiance value
        if ((this.AllegianceStatus[initTribeID][targetTribeID] + value < -absoluteMax) || (this.AllegianceStatus[initTribeID][targetTribeID] + value > absoluteMax)) {
            value = Integer.signum(value) * (absoluteMax - Math.abs(this.AllegianceStatus[initTribeID][targetTribeID]));
        }
        // adds the value to the allegiance
        this.AllegianceStatus[initTribeID][targetTribeID] = this.AllegianceStatus[initTribeID][targetTribeID] + value;
        this.AllegianceStatus[targetTribeID][initTribeID] = this.AllegianceStatus[targetTribeID][initTribeID] + value;
    }

    /**
     * Checks the consequences of the change in relationship, to see if any more allegiances need to be updated
     *
     * @param value         value the allegiance will change by
     * @param initTribeID   ID for the tribe initiating the action
     * @param targetTribeID ID for the target tribe of the action
     */
    public void checkConsequences(int value, int initTribeID, int targetTribeID) {
        // inverting and halving the value
        value = value / -2;
        // checks all relationships for the target tribe
        for (int i = 0; i < AllegianceStatus.length; i++) {
            // if the target tribe has a negative relationship and it is not with the initiating tribe
            if ((AllegianceStatus[i][targetTribeID]) < 0 && (i != initTribeID)) {
                // call update allegiance for the target tribe
                updateAllegiance(value, i, initTribeID);
            }
        }
    }

    //Method to test the diplomacy is working
    public void logAllegiance(Board b) {
        System.out.println("NEW LOG");
        for (int i = 0; i < AllegianceStatus.length; i++) {
            System.out.print(b.getTribes()[i].getName() + ": ");
            for (int j = 0; j < AllegianceStatus.length; j++) {
                System.out.print(this.AllegianceStatus[i][j] + ", ");
            }
            System.out.println();
        }
    }
}