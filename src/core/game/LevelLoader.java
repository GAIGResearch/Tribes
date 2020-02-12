package core.game;

import core.Types;
import core.actors.City;
import utils.IO;

import java.awt.*;
import java.util.ArrayList;

public class LevelLoader
{

    private Dimension size;

    public LevelLoader()
    {
        size = new Dimension();
    }

    /**
     * Builds a level, receiving a file name.
     *
     * @param gamelvl
     *            file name containing the level.
     */
    public Board buildLevel(Board board, String gamelvl, int numPlayers) {
        String[] lines = new IO().readFile(gamelvl);

        // Dimensions of the level read from the file.
        size.width = lines.length;
        size.height = lines.length;
        int tribeID = 0;

        board.init(size.width, numPlayers);


        //Go through evert token in the level file
        for (int i = 0; i < size.height; ++i) {
            String line = lines[i];

            String[] tile = line.split(",");
            for(int j = 0; j < tile.length; ++j)
            {
                //Format <terrain_char>:[<resource_char>]
                // (<resource_char> is optional)
                // Retrieve the chars and assign the corresponding enum values in the board.
                String[] tileSplit = tile[j].split(":");
                char terrainChar = tileSplit[0].charAt(0);

                if(terrainChar == Types.TERRAIN.CITY.getMapChar())
                {
                    if(tribeID==numPlayers)
                    {
                        //If we've already allocated all the cities to the number of tribes, turn this
                        //extra city into a village.
                        terrainChar = Types.TERRAIN.VILLAGE.getMapChar();
                    }else
                    {
                        //A city to create. Add it and assign it to the next tribe.
                        City c = new City(i, j, tribeID);
                        board.addCityToTribe(c, tribeID);
                        tribeID++;
                    }
                }

                board.setTerrainAt(i,j, Types.TERRAIN.getType(terrainChar));

                if(tileSplit.length == 2)
                {
                    char resourceChar = tileSplit[1].charAt(0);
                    board.setResourceAt(i,j,Types.RESOURCE.getType(resourceChar));
                }
            }
        }


        board.init();

        return board;

    }


}
