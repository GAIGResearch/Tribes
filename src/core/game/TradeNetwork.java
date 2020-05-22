package core.game;

import core.TribesConfig;
import core.Types;
import core.actors.City;
import core.actors.Tribe;
import utils.Vector2d;
import utils.graph.NeighbourHelper;
import utils.graph.PathNode;
import utils.graph.Pathfinder;

import java.util.ArrayList;
import java.util.HashMap;

import static core.Types.TERRAIN.*;

class TradeNetwork
{

    // Array that indicates presence of roads, cities, ports or naval links
    private boolean[][] networkTiles;

    //Size of this trade network.
    private int size;

    /**
     * Creates a trade network for a size by size board
     * @param size side of the board
     */
    TradeNetwork(int size)
    {
        this.size = size;
        this.networkTiles = new boolean[size][size];
    }

    /**
     * Creates a size network for trade given a connectivity grid
     * @param networkTiles connectivity grid that determines the trade network
     */
    TradeNetwork(boolean[][] networkTiles){
        this.size = networkTiles.length;
        this.networkTiles = networkTiles;
    }

    /**
     * Sets an element of the trade network to true or false, recomputes the network
     * @param board Board of the game
     * @param x x coordinate of the position to set.
     * @param y y coordinate of the position to set.
     * @param trade true if the position must belong to the trade network
     */
    void setTradeNetwork(Board board, int x, int y, boolean trade)
    {
        networkTiles[x][y] = trade;
        computeTradeNetwork(board);
    }


    /**
     * Computes the trade network for all tribes. Calls computeTradeNetworkTribe(...) on all tribes of the game.
     * @param board board of the game.
     */
    private void computeTradeNetwork(Board board)
    {
        Tribe[] tribes = board.getTribes();
        for(Tribe tribe : tribes)
            this.computeTradeNetworkTribe(board, tribe);
    }

    /**
     * Computes the trade network for one tribe. It builds to complementary graphs: connected tiles (via roads)
     * and connected cities (via ports and water tiles). It updates the populations of the cities according to
     * changes in the network.
     * @param board board of the game.
     * @param tribe tribe which network has to be computed.
     */
    void computeTradeNetworkTribe(Board board, Tribe tribe)
    {
        //We only update the trade network of all tribes for the current tribe if this is not native
        if(tribe.getTribeId() != board.getActiveTribeID() && !board.isNative())
            return;

        if (tribe.controlsCapital()) {
            boolean[][] connectedTiles = new boolean[networkTiles.length][networkTiles[0].length];
            boolean[][] navigable = new boolean[networkTiles.length][networkTiles[0].length];

            ArrayList<Vector2d> ports = new ArrayList<>();

            //First, set up the graph. Including all tiles that correspond to active trade points (roads, cities, ports)
            for (int i = 0; i < networkTiles.length; ++i) {
                for (int j = 0; j < networkTiles[0].length; ++j) {
                    //Only for this tribe
                    int cityId = board.getCityIdAt(i,j);
                    boolean myCity = tribe.controlsCity(cityId);
                    boolean notEnemy = myCity || cityId == -1;

                    //cities and ports, must be within my city boundaries.
                    Types.TERRAIN ter = board.getTerrainAt(i,j);
                    Types.BUILDING build = board.getBuildingAt(i,j);
                    if(myCity && (ter == CITY || build == Types.BUILDING.PORT))
                    {
                        connectedTiles[i][j] = networkTiles[i][j];
                        if(build == Types.BUILDING.PORT)
                            ports.add(new Vector2d(i, j));

                        //Roads, must be within my city boundaries OR in a neutral tile.
                    }else if (notEnemy && board.isRoad(i,j))
                    {
                        connectedTiles[i][j] = networkTiles[i][j];
                    }

                    //And navigable tiles: WATER, VISIBLE AND NOT ENEMY
                    if ((ter == SHALLOW_WATER || ter == DEEP_WATER)
                            && tribe.isVisible(i, j) && notEnemy) {
                        navigable[i][j] = true;
                    }
                }
            }

            TradeNetworkStep tns = new TradeNetworkStep(connectedTiles);

            //Now, we need to add jump links. In this case, two ports are connected if
            // separated by [0,TribesConfig.PORT_TRADE_DISTANCE] WATER, VISIBLE, NON-ENEMY tiles
            int nPorts = ports.size();
            for (int i = 0; i < nPorts - 1; ++i) {
                for (int j = i; j < nPorts; ++j) {
                    if (i != j) {
                        Vector2d portFrom = ports.get(i);
                        Vector2d portTo = ports.get(j);

                        Vector2d originPortPos = new Vector2d(portFrom.x, portFrom.y);
                        Pathfinder tp = new Pathfinder(originPortPos, new TradeWaterStep(navigable));
                        ArrayList<PathNode> path = tp.findPathTo(new Vector2d(portTo.x, portTo.y));

                        if (path != null) //+1 because path includes destination
                        {
                            //We add this as a link between ports.
                            tns.addJumpLink(portFrom, portTo, true);
                        }

                    }
                }
            }

            City capital = (City) board.getActor(tribe.getCapitalID());
            tribe.updateNetwork(new Pathfinder(capital.getPosition(), tns), board, tribe.getTribeId() == board.getActiveTribeID());
        }else {
            tribe.updateNetwork(null, board, tribe.getTribeId() == board.getActiveTribeID());
        }
    }


    void setTradeNetworkValue(int x, int y, boolean trade) {  this.networkTiles[x][y] = trade;  }
    boolean getTradeNetworkValue(int x, int y) { return networkTiles[x][y]; }


    /**
     * Private class that is used by Pathfinding to determine water node connectivity in a graph, considering
     * also distance between friendly ports.
     */
    private class TradeWaterStep implements NeighbourHelper
    {
        //Indicates which positions in the grid can be navigated (depends on terrain type,
        // technology and visibility).
        private boolean [][]navigable;

        TradeWaterStep(boolean [][]navigable)
        {
            this.navigable = navigable;
        }

        /**
         * Returns the neighbours of a given node in this data structure.
         * @param from position from which we need neighbours
         * @param costFrom is the total move cost computed up to "from"
         * @return  all the adjacent neighbours to tile in position "from"
         */
        @Override
        public ArrayList<PathNode> getNeighbours(Vector2d from, double costFrom) {

            ArrayList<PathNode> neighbours = new ArrayList<>();
            double stepCost = 1.0;
            TribesConfig tc = new TribesConfig();

            for(Vector2d tile : from.neighborhood(1, 0, size)) {
                int x = tile.x;
                int y = tile.y;
                if(navigable[x][y] && costFrom+stepCost <= tc.PORT_TRADE_DISTANCE)
                {
                    neighbours.add(new PathNode(new Vector2d(x, y), stepCost));
                }
            }

            return neighbours;
        }

        @Override
        public void addJumpLink(Vector2d from, Vector2d to, boolean reverse) {
            //No jump links
        }
    }

    /**
     * Private class that is used by Pathfinding to determine city connectivity in a graph.
     * Considers connection between ports for the connectivity between cities
     */
    private class TradeNetworkStep implements NeighbourHelper
    {
        //Grid that determines which position belong to the trade network. Adjacent points are connected
        private boolean [][]connected;

        //List of network links between non-adjacent tiles.
        private HashMap<Vector2d, ArrayList<Vector2d>> jumpLinks;

        /**
         * Creates a trade network step helper
         * @param connected list of connected tiles.
         */
        TradeNetworkStep (boolean [][]connected)
        {
            this.connected = connected;
            this.jumpLinks = new HashMap<>();
        }

        /**
         * Returns the neighbours of a given node in this data structure. Considers the jump links between
         * non-adjacent node.
         * @param from position from which we need neighbours
         * @param costFrom is the total move cost computed up to "from"
         * @return  all the adjacent neighbours to tile in position "from"
         */
        @Override
        public ArrayList<PathNode> getNeighbours(Vector2d from, double costFrom) {

            ArrayList<PathNode> neighbours = new ArrayList<>();
            double stepCost = 1.0;

            for(Vector2d tile : from.neighborhood(1, 0, size)) {
                int x = tile.x;
                int y = tile.y;
                if(connected[x][y])
                {
                    neighbours.add(new PathNode(new Vector2d(x, y), stepCost));
                }
            }

            //Now, add the jump link neighbours
            if(jumpLinks.containsKey(from))
            {
                ArrayList<Vector2d> connected = jumpLinks.get(from);
                for(Vector2d to: connected)
                {
                    neighbours.add(new PathNode(to, stepCost));
                }
            }


            return neighbours;
        }


        /**
         * Adds a jump link between two positions
         * @param from one end of the link
         * @param to the other end of the link
         * @param reverse true if another link from 'to' to 'from' must also be created
         */
        @Override
        public void addJumpLink(Vector2d from, Vector2d to, boolean reverse) {
            addAtoB(from, to);
            if(reverse) addAtoB(to, from);
        }

        /**
         * Adds a jump link from 'from' to 'to' to the list of jump links.
         * @param from one end of the link
         * @param to the other end of the link
         */
        private void addAtoB(Vector2d from, Vector2d to)
        {
            if(!jumpLinks.containsKey(from))
                jumpLinks.put(from, new ArrayList<>());

            ArrayList<Vector2d> connected = jumpLinks.get(from);
            connected.add(to);
        }
    }


}
