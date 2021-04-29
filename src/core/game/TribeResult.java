package core.game;

import core.Types;

import java.util.Random;

public class TribeResult implements Comparable
{
    private Types.RESULT result;
    private int id;
    private int score;
    private int numTechsResearched;
    private int numCities;
    private int production;
    private int numWars;
    private int numStars;

    public TribeResult(int id, Types.RESULT res, int score, int numTechsResearched, int numCities, int production, int numWars, int numStars)
    {
        this.id = id;
        this.result = res;
        this.score = score;
        this.numTechsResearched = numTechsResearched;
        this.numCities = numCities;
        this.production = production;
        this.numWars = numWars;
        this.numStars = numStars;
    }

    @Override
    public int compareTo(Object o)
    {
        if(!(o instanceof TribeResult))
        {
            System.out.println("Comparison error: 'o' is not of type TribeResult: " + o);
            return 0;
        }

        TribeResult other = (TribeResult)o;

        //Winning status determines
        if(this.result == Types.RESULT.WIN && other.result != Types.RESULT.WIN)
            return -1;
        else if (this.result != Types.RESULT.WIN && other.result == Types.RESULT.WIN)
            return 1;

        //Tie breaker 0: score
        if(this.score > other.score)
            return -1;
        else if (this.score < other.score)
            return 1;


        //Tie breaker 1: num tech researched
        if(this.numTechsResearched > other.numTechsResearched)
            return -1;
        else if (this.numTechsResearched < other.numTechsResearched)
            return 1;

        //Tie breaker 2: num cities owned
        if(this.numCities > other.numCities)
            return -1;
        else if(this.numCities < other.numCities)
            return 1;

        //Tie breaker 3: production
        if(this.production > other.production)
            return -1;
        else if(this.production < other.production)
            return 1;

        //Tie breaker 4: wars
        if(this.numWars > other.numWars)
            return -1;
        else if(this.numWars < other.numWars)
            return 1;

        //Tie breaker 5: stars
        if(this.numStars > other.numStars)
            return -1;
        else if(this.numStars < other.numStars)
            return 1;

        //If here, all is the same. Choose at random.
        return new Random().nextBoolean() ? -1 : 1;
    }

    public int getId() {
        return id;
    }

    public int getNumTechsResearched() {
        return numTechsResearched;
    }

    public int getNumCities() {
        return numCities;
    }

    public int getProduction() {
        return production;
    }

    public double getScore() {
        return score;
    }

    public int getNumWars() { return numWars; }

    public int getNumStars() { return numStars; }

    public Types.RESULT getResult() {
        return result;
    }

    public TribeResult copy() {
        return new TribeResult(id, result, score, numTechsResearched, numCities, production, numWars, numStars);
    }

    public void setResult(Types.RESULT result) {
        this.result = result;
    }
}
