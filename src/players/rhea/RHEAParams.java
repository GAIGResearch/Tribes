package players.rhea;

import players.heuristics.AlgParams;

public class RHEAParams extends AlgParams {

    public int POP_SIZE = 100;
    public double MUTATION_RATE = 0.1;
    public int INDIVIDUAL_LENGTH = 20;
    public int TOURNAMENT_SIZE = 3;
    public int MUTATE_BEST = 9;
    public boolean ELITISM = true;

    public void print() {
        System.out.println("RHEA Params:");
        System.out.println("\tPop Size: " + POP_SIZE);
        System.out.println("\tMutation Rate: " + MUTATION_RATE);
        System.out.println("\tIndividual Length: " + INDIVIDUAL_LENGTH);
        System.out.println("\tTournament Size: " + TOURNAMENT_SIZE);
        System.out.println("\tMutate best: " + MUTATE_BEST);
        System.out.println("\tElitism: " + ELITISM);
    }

}