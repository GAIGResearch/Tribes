package core;

public class TechnologyTree {

    private boolean[] researched;
    private boolean everythingResearched = false;

    public TechnologyTree(){
        researched = new boolean[Types.TECHNOLOGY.values().length];
    }

    public TechnologyTree(boolean[] researched){
        this.researched = researched;
        checkEverythingResearched();
    }

    public boolean isResearched(Types.TECHNOLOGY target) {
        return researched[target.ordinal()];
    }

    public TechnologyTree copy(){
        return new TechnologyTree(researched);
    }

    public boolean isResearchable(Types.TECHNOLOGY target) {

        boolean decision = false;

        switch (target) {
            case CLIMBING:
            case FISHING:
            case HUNTING:
            case ORGANIZATION:
            case RIDING:
                decision = true;
                break;
            case ARCHERY:
            case FORESTRY:
                if (isResearched(Types.TECHNOLOGY.HUNTING)) {
                    decision = true;
                }
                break;
            case FARMING:
            case SHIELDS:
                if (isResearched(Types.TECHNOLOGY.ORGANIZATION)) {
                    decision = true;
                }
                break;
            case FREE_SPIRIT:
            case ROADS:
                if (isResearched(Types.TECHNOLOGY.RIDING)) {
                    decision = true;
                }
                break;
            case MEDITATION:
            case MINING:
                if (isResearched(Types.TECHNOLOGY.CLIMBING)) {
                    decision = true;
                }
                break;
            case SAILING:
            case WHALING:
                if (isResearched(Types.TECHNOLOGY.FISHING)) {
                    decision = true;
                }
                break;
            case AQUATISM:
                if (isResearched(Types.TECHNOLOGY.WHALING)) {
                    decision = true;
                }
                break;
            case CHIVALRY:
                if (isResearched(Types.TECHNOLOGY.FREE_SPIRIT)) {
                    decision = true;
                }
                break;
            case CONSTRUCTION:
                if (isResearched(Types.TECHNOLOGY.FARMING)) {
                    decision = true;
                }
                break;
            case MATHEMATICS:
                if (isResearched(Types.TECHNOLOGY.FORESTRY)) {
                    decision = true;
                }
                break;
            case NAVIGATION:
                if (isResearched(Types.TECHNOLOGY.SAILING)) {
                    decision = true;
                }
                break;
            case SMITHERY:
                if (isResearched(Types.TECHNOLOGY.MINING)) {
                    decision = true;
                }
                break;
            case SPIRITUALISM:
                if (isResearched(Types.TECHNOLOGY.ARCHERY)) {
                    decision = true;
                }
                break;
            case TRADE:
                if (isResearched(Types.TECHNOLOGY.ROADS)) {
                    decision = true;
                }
                break;
            case PHILOSOPHY:
                if (isResearched(Types.TECHNOLOGY.MEDITATION)) {
                    decision = true;
                }
                break;
        }
        return decision;
    }

    private void checkEverythingResearched(){
        for (boolean b : researched){
            if(!b){return;}
        }
        everythingResearched = true;
    }

    public boolean getEverythingResearched(){
        return everythingResearched;
    }

    public void doResearch(Types.TECHNOLOGY target) {
        switch (target) {
            case CLIMBING:
            case FISHING:
            case HUNTING:
            case ORGANIZATION:
            case RIDING:
                researched[target.ordinal()] = true;
                break;
            case ARCHERY:
            case FORESTRY:
                if (isResearched(Types.TECHNOLOGY.HUNTING)) {
                    researched[target.ordinal()] = true;
                }
                break;
            case FARMING:
            case SHIELDS:
                if (isResearched(Types.TECHNOLOGY.ORGANIZATION)) {
                    researched[target.ordinal()] = true;
                    checkEverythingResearched();
                }
                break;
            case FREE_SPIRIT:
            case ROADS:
                if (isResearched(Types.TECHNOLOGY.RIDING)) {
                    researched[target.ordinal()] = true;
                }
                break;
            case MEDITATION:
            case MINING:
                if (isResearched(Types.TECHNOLOGY.CLIMBING)) {
                    researched[target.ordinal()] = true;
                }
                break;
            case SAILING:
            case WHALING:
                if (isResearched(Types.TECHNOLOGY.FISHING)) {
                    researched[target.ordinal()] = true;
                }
                break;
            case AQUATISM:
                if (isResearched(Types.TECHNOLOGY.WHALING)) {
                    researched[target.ordinal()] = true;
                    checkEverythingResearched();
                }
                break;
            case CHIVALRY:
                if (isResearched(Types.TECHNOLOGY.FREE_SPIRIT)) {
                    researched[target.ordinal()] = true;
                    checkEverythingResearched();
                }
                break;
            case CONSTRUCTION:
                if (isResearched(Types.TECHNOLOGY.FARMING)) {
                    researched[target.ordinal()] = true;
                    checkEverythingResearched();
                }
                break;
            case MATHEMATICS:
                if (isResearched(Types.TECHNOLOGY.FORESTRY)) {
                    researched[target.ordinal()] = true;
                    checkEverythingResearched();
                }
                break;
            case NAVIGATION:
                if (isResearched(Types.TECHNOLOGY.SAILING)) {
                    researched[target.ordinal()] = true;
                    checkEverythingResearched();
                }
                break;
            case SMITHERY:
                if (isResearched(Types.TECHNOLOGY.MINING)) {
                    researched[target.ordinal()] = true;
                    checkEverythingResearched();
                }
                break;
            case SPIRITUALISM:
                if (isResearched(Types.TECHNOLOGY.ARCHERY)) {
                    researched[target.ordinal()] = true;
                    checkEverythingResearched();
                }
                break;
            case TRADE:
                if (isResearched(Types.TECHNOLOGY.ROADS)) {
                    researched[target.ordinal()] = true;
                    checkEverythingResearched();
                }
                break;
            case PHILOSOPHY:
                if (isResearched(Types.TECHNOLOGY.MEDITATION)) {
                    researched[target.ordinal()] = true;
                    checkEverythingResearched();
                }
                break;
       }
   }

}