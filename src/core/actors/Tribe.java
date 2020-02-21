package core.actors;

import core.TechnologyTree;
import core.TribesConfig;
import core.Types;
import core.game.ForwardModel;

import java.util.ArrayList;

public class Tribe extends Actor{

    //Cities this tribe owns.
    private ArrayList<Integer> citiesID;

    //ID of this tribe. It corresponds with the ID of the player who controls it.
    private int tribeID;

    //Type of the tribe
    private Types.TRIBE tribe;

    //Technology progress of this tribe
    private TechnologyTree techTree;

    //Current number of stars (resources) of this tribe.
    private int stars; //TODO: compute this amount at the beginning of each turn.

    //Game result for this player.
    private Types.RESULT winner = Types.RESULT.INCOMPLETE;

    //Score for the tribe.
    private int score = 0;

    private boolean obsGrid[][];


    public Tribe(Types.TRIBE tribe)
    {
        this.tribe = tribe;
        init();
    }

    public Tribe(int tribeID, int cityID, Types.TRIBE tribe) {
        this.tribeID = tribeID;
        citiesID.add(cityID);
        this.tribe = tribe;
        init();
    }

    private void init()
    {
        techTree = new TechnologyTree();
        techTree.doResearch(tribe.getInitialTech());
        citiesID = new ArrayList<>();
        stars = TribesConfig.INITIAL_STARS;
    }

    public void initObsGrid(int size)
    {
        obsGrid = new boolean[size][size];
    }


    public Tribe copy()
    {
        Tribe tribeCopy = new Tribe(this.tribe);
        tribeCopy.tribeID = this.tribeID;
        tribeCopy.techTree = this.techTree.copy();
        tribeCopy.stars = this.stars;
        tribeCopy.winner = this.winner;
        tribeCopy.score = this.score;

        tribeCopy.citiesID = new ArrayList<>();
        for(int cityID : citiesID)
        {
            tribeCopy.citiesID.add(cityID);
        }

        return tribeCopy;
    }


    public void clearView(int x, int y)
    {
        int size = obsGrid.length;
        for(int i = x-1; i <= x+1; ++i)
            for(int j = y-1; j <= y+1; ++j)
            {
                //All these positions should be within my view.
                if(x >= 0 && y >= 0 && x < size && y < size)
                {
                    obsGrid[x][y] = true;
                }

            }
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

    public void setTechTree(TechnologyTree techTree) {this.techTree = techTree;}
    public TechnologyTree getTechTree() {return techTree;}

    public Types.TECHNOLOGY getInitialTechnology(){
        return tribe.getInitialTech();
    }

    public void addScore(int score){
        this.score += score;
    }



    public ArrayList<Integer> getCitiesID() {
        return citiesID;
    }

    public String getName(){return tribe.getName();}

    public boolean[][] getObsGrid() {return obsGrid;}

    public Types.TRIBE getType(){return tribe;}

    public Types.RESULT getWinner() {return winner;}
    public int getScore() {return score;}

    public void setTribeID(int tribeID) {
        this.tribeID = tribeID;
    }

    public int getTribeID() {
        return tribeID;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public void addStars(int stars) {this.stars += stars;}

    public void subtractStars(int stars) {this.stars -= stars;}

}
