package core;

import core.game.Board;

import static core.TribesConfig.ALLEGIANCE_MAX;

public class Diplomacy {

    // stores the allegiances of the tribes
    private int[][] allegianceStatus;

    /**
     * Creates allegiances for each of the existing tribes
     *
     * @param size number of tribes in-game
     */
    public Diplomacy(int size) {
        this.allegianceStatus = new int[size][size];
    }

    public int[][] getAllegianceStatus() {
        return allegianceStatus;
    }

    public void setAllegianceStatus(int x, int y, int val) {
        allegianceStatus[x][y] = val;}

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
        if ((this.allegianceStatus[initTribeID][targetTribeID] + value < -ALLEGIANCE_MAX) || (this.allegianceStatus[initTribeID][targetTribeID] + value > ALLEGIANCE_MAX)) {
            value = Integer.signum(value) * (ALLEGIANCE_MAX - Math.abs(this.allegianceStatus[initTribeID][targetTribeID]));
        }
        // adds the value to the allegiance
        this.allegianceStatus[initTribeID][targetTribeID] = this.allegianceStatus[initTribeID][targetTribeID] + value;
        this.allegianceStatus[targetTribeID][initTribeID] = this.allegianceStatus[targetTribeID][initTribeID] + value;
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
        for (int i = 0; i < allegianceStatus.length; i++) {
            // if the target tribe has a negative relationship and it is not with the initiating tribe
            if ((allegianceStatus[i][targetTribeID]) < 0 && (i != initTribeID)) {
                // call update allegiance for the target tribe
                updateAllegiance(value, i, initTribeID);
            }
        }
    }

    /**
     * Creates a copy of this Diplomacy object
     * @return another instance, copy of this.
     */
    public Diplomacy copy()
    {
        Diplomacy copyD = new Diplomacy(this.allegianceStatus.length);
        for(int i =0; i < getAllegianceStatus().length; ++i)
        {
            for (int j=0; j < getAllegianceStatus()[i].length; ++j)
                copyD.setAllegianceStatus(i, j, getAllegianceStatus()[i][j]);
        }
        return copyD;
    }

    //Method to test the diplomacy is working
    public void logAllegiance(Board b) {
        System.out.println("NEW LOG");
        for (int i = 0; i < allegianceStatus.length; i++) {
            System.out.print(b.getTribes()[i].getName() + ": ");
            for (int j = 0; j < allegianceStatus.length; j++) {
                System.out.print(this.allegianceStatus[i][j] + ", ");
            }
            System.out.println();
        }
    }
}