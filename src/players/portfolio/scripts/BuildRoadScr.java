package players.portfolio.scripts;

import core.actions.Action;
import core.actions.tribeactions.BuildRoad;
import core.actors.Actor;
import core.actors.City;
import core.actors.Tribe;
import core.game.Board;
import core.game.GameState;
import utils.Vector2d;
import java.util.*;


public class BuildRoadScr extends BaseScript {

    //Selects the action that builds the better road.

    enum RoadSpot
    {
        TRADE,
        HOT,
    }

    private Random rnd;

    public BuildRoadScr(Random rnd)
    {
        this.rnd = rnd;
    }

    @Override
    public Action process(GameState gs, Actor ac)
    {
        ArrayList<Action> candidate_actions = new ArrayList<>();
        Tribe thisTribe = (Tribe)ac;
        int numConnectedCities = thisTribe.getConnectedCities().size();
        City capital = (City) gs.getActor(thisTribe.getCapitalID());

        //1. We only add roads if we have a city that is not connected to our capital.
        if(thisTribe.getCitiesID().size() == numConnectedCities)
            return null;
        else
        {
            //Build hot spots for Roads
            Board b = gs.getBoard();
            RoadSpot[][] hotspots = calcHotspots(b, capital.getPosition(), thisTribe.getTribeId());
            //printH(hotspots);

            for(Action act: actions)
            {
                BuildRoad br = (BuildRoad) act;
                Vector2d roadPos = br.getPosition();
                if(hotspots[roadPos.x][roadPos.y] == RoadSpot.HOT)
                {
                    candidate_actions.add(act);


                    //The code below checks for increments in the connected cities. Too slow.
//                    Action bestAction = null;
//                    int highestInc = 0;
//                    GameState gsCopy = gs.copy();
//
//                    //Add a road in a copy of this state.
//                    Board bCopy = gsCopy.getBoard();
//                    bCopy.addRoad(roadPos.x, roadPos.y);
//
//                    //Check if this new road adds cities to our trade network.
//                    ArrayList<Integer> newConnCities = bCopy.getTribe(ac.getActorId()).getConnectedCities();
//                    int newConnectedCities = newConnCities.size() - numConnectedCities;
//                    if(newConnectedCities > highestInc)
//                    {
//                        //We connect more cities!
//                        highestInc = newConnectedCities;
//                        bestAction = act;
//                    }else if(bestAction == null && newConnectedCities == 0)
//                    {
//                        //We can't connect more cities. Just add to candidates as this expands our network
//                        candidate_actions.add(act);
////                        System.out.println("BUILD ROAD: " + act.toString() + "; capital at " + capital.getPosition());
//                    }
                }
            }
        }

        //Return an action at random that builds a road increasing our capital reach.
        int nActions = candidate_actions.size();
        if( nActions > 0)
            return candidate_actions.get(rnd.nextInt(nActions));

        //Can't add any road that expands our network. No road building.
        return null;

    }

    private void printH(RoadSpot[][] hotspots) {
        System.out.println("--------------------------");
        for (RoadSpot[] hotspot : hotspots)
        {
            for (RoadSpot roadSpot : hotspot) {
                char v = '.';
                if (roadSpot == RoadSpot.HOT)
                    v = 'O';
                else if (roadSpot == RoadSpot.TRADE)
                    v = 'x';
                System.out.print(v);
            }
            System.out.println();
        }

    }

    private RoadSpot[][] calcHotspots(Board board, Vector2d startingPos, int tribeId)
    {
        RoadSpot[][] hotspots = new RoadSpot[board.getSize()][board.getSize()];
        hotspots[startingPos.x][startingPos.y] = RoadSpot.TRADE;
        _calcHotspotsRecv(startingPos, hotspots, board, tribeId);
        return hotspots;
    }

    private void _calcHotspotsRecv(Vector2d position, RoadSpot[][] hotspots, Board board, int tribeId)
    {
        LinkedList<Vector2d> neighs = position.neighborhood(1, 0, board.getSize());

        for (Vector2d neighPos : neighs) {
            if(hotspots[neighPos.x][neighPos.y] == null) {
                if (board.checkTradeNetwork(neighPos.x, neighPos.y)) {
                    hotspots[neighPos.x][neighPos.y] = RoadSpot.TRADE;
                    _calcHotspotsRecv(neighPos, hotspots, board, tribeId);
                } else {
                    if(board.canBuildRoadAt(tribeId, neighPos.x, neighPos.y)) {
                        hotspots[neighPos.x][neighPos.y] = RoadSpot.HOT;
                    }
                }
            }
        }
    }


}
