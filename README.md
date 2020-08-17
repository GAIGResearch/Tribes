# Tribes

![GitHub release (latest by date)](https://img.shields.io/github/v/release/GAIGResearch/Tribes?style=plastic) ![Tribes Java 93.5%](https://img.shields.io/github/languages/top/GAIGResearch/Tribes?style=plastic) ![GitHub All Releases](https://img.shields.io/github/downloads/GAIGResearch/Tribes/total?style=plastic) ![GitHub stars](https://img.shields.io/github/stars/GAIGResearch/Tribes?style=social)

# About

A turn-based strategy game framework. Tribes is a multi-player, multi-agent, stochastic and partially observable game that involves strategic and tactical combat decisions. A good playing strategy requires the management of a technology tree, build orders and economy. The framework provides a Forward Model, which can be used by Statistical Forward Planning methods.

# Requirements

**Java 8** or later is required to run the project. Download Java [here](https://java.com/en/download/).

# Getting started

In order to run the code, you must either download the repository, or clone it. If you are looking for a particular release, you can find all listed [here](https://github.com/GAIGResearch/Tribes/releases).

![Tribes Download](/img/download.png)

The simplest way to run the code is to create a new project in [IntelliJ IDEA](https://www.jetbrains.com/idea/) or a similar IDE. In IntelliJ, create a **new project from existing sources**, pointing to the code downloaded or cloned. This process should automatically set up the environment and add any project libraries as well (JSON library only required for version 1.0). 

Alternatively, open the code directly in your IDE of choice and add libraries included in the download from the `lib/` package. Make sure `src/` is marked as sources root.

# Running the framework

The main class for running the framework is `Play.java` found in the sources root directory. The `main` method in this class contains 3 ways of running the code. The parameters for all these execution modes can be found in the configuration file `play.json`.

1. **Play one game with visuals, using the level generator.** Call the `play` method with the following possible settings:
    * **tribes**: array of type `Types.TRIBE` containing the tribes that will be assigned to the players.
    * **level seed**: random seed for the level generator
    * **players**: array of type `PlayerType` containing the players taking part in the game; the tribe they receive depends on the order, mapping directly to the tribes array (thus the two arrays must also be of the same length).
    * **game mode**: variable of type `Types.GAME_MODE`, which can take either `Capitals` or `Score` values in version 1.0, to change the mode of the game played.

2. **Play one game with visuals, with level loaded from a file.** Call the alternative `play` method with the following possible settings:
    * **file name**: path to file containing the level to be loaded.
    * **players**: array of type `PlayerType` containing the players taking part in the game; the tribe they receive depends on the order, mapping directly to the tribes defined in the level; read order of tribes in level files is top-left to bottom-right (the player array must contain exactly enough players for the loaded level).
    * **game mode**: variable of type `Types.GAME_MODE`, which can take either `Capitals` or `Score` values in version 1.0, to change the mode of the game played.
    
3. **Play one game with visuals, with whole game loaded from a file.** Call the `load` method with the following possible settings:
    * **players**: array of type `PlayerType` containing the players taking part in the game; the tribe they receive depends on the order, mapping directly to the tribes array saved in the file (thus the two arrays must also be of the same length).
    * **file name**: path to file containing the JSON saved game to be loaded.

Additionally, other variables can be modified for an effect with all methods of running the framework in this file:
* **AGENT_SEED**: this variable defined at the top of the file can be given a new value, to set the random seed used by the AI agents.
* **GAME_SEED**: this variable defined at the top of the file can be given a new value, to set the random seed used by the game (e.g. for deciding random bonuses when exploring ruins).
* **RUN_VERBOSE**: this variable defined at the top of the file can be set to true (producing detailed output while running the game) or false.

The `_getAgent` method and the `PlayerType` enum can be used to set which AI players are available to run in the framework, and new ones can be defined by including both a type, and a corresponding constructor. Currently defined players have parameter options set as well, which can be modified for different behaviours.


## No visuals

To run **without visuals**, set the following variable in the `core.Constants.java` class to false:

```
    public static boolean VISUALS = true;
```

Other intersting variables in the `core.Constants.java` class for running games include:

```
    static final int MAX_TURNS = 30;  // Maximum number of turns when playing in Score mode
    static final int MAX_TURNS_CAPITALS = 50; // Maximum number of turns when playing in Capitals mode
    public static final boolean PLAY_WITH_FULL_OBS = true; // If false, agents receive game states with information hidden for areas covered by fog of war
    public static boolean GUI_FORCE_FULL_OBS = false; // If true, display still shows full observable game states even if players play with partial observability
    public static boolean WRITE_SAVEGAMES = false;  // If true, all games played are saved to files
    public static boolean DISABLE_NON_HUMAN_ACTION_HIGHLIGHT = true;  // If true, human observing/playing doesn't have access to actions of non-human players
    public static int FRAME_DELAY = 500; // The display delay between frames
    public static boolean TURN_LIMITED = false;  // Limits games to maximum number of turns
    public static long TURN_TIME_MILLIS = 10000000; // Limits a player's thinking time to this many milliseconds
    public static boolean GUI_PAN_TO_TRIBE = false;  // Pans to a tribe's capital when turn changes
    public static boolean GUI_DRAW_EFFECTS = false;  // Draws animations for unit actions
```

## Tournaments

To run round-robin tournaments between multiple AI players, run class `Tournament.java`. Running a tourament requires some parameters, which are indicated in the file `tournament.json`. Among these parameters, you may determine:

* **game mode**: Int, 0 for Capitals, 1 for Score
* **# repetitions**: Int, number of repetitions per match-up
* **max length**: Int, maximum number of turns per game
* **force turn end**: Int, 1 if true, 0 if false, forces players to end their turn after 5 moves if true.
* **MCTS rollouts**: Int, 1 if true, 0 if false, decides if MCTS is using rollouts or not
* **population size**: Int, for population-based algorithms, e.g. RHEA
* **players**: String x N, N player types taking part in the game
* **tribes**: String x N, N tribes for the game, same number as players
* **Level Seeds**: A series of random seeds for the game levels.

Tournament results indicate number of games played (N), number of wins (W) and win rate, score (S), number of technologies researched (T), number of cities (C) and star production (P) for each player. 

## Game configuration

The game itself can be modified by changing its configuration (e.g. attack power of certain units) in the `core.TribesConfig.java` file.

# Associated research

* Diego Perez Liebana, Yu-Jhen Hsu, Stavros Emmanouilidis, Bobby Khaleque, Raluca Gaina, "**Tribes: A New Turn-Based Strategy Game for AI**", in _Sixteenth AAAI Conference on Artificial Intelligence and Interactive Digital Entertainment (AIIDE)_, 2020.

# Acknowledgements

This work is supported by UK EPSRC research grants EP/T008962/1 and [IGGI CDT](http://iggi.org.uk) EP/L015846/1
