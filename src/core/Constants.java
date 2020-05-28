package core;

public class Constants {
    public static boolean VERBOSE = false;
    public static boolean VISUALS = true;
    public static boolean DISABLE_NON_HUMAN_GRID_HIGHLIGHT = true;  // If true, human observing/playing doesn't have access to actions of non-human players
    public static int FRAME_DELAY = 500; //1000;
    public static boolean TURN_LIMITED = false;
    public static long TURN_TIME_MILLIS = 10000000; //10000; //10 seconds.
    public static int GUI_INFO_DELAY = 50000;

    // Display settings
    public static int GUI_GAME_VIEW_SIZE;
    public static int CELL_SIZE;
    public static int GUI_MIN_PAN;
    public static int GUI_COMP_SPACING;
    public static int GUI_CITY_TAG_WIDTH;
    public static boolean GUI_DRAW_EFFECTS = false;

    public static int GUI_SIDE_PANEL_WIDTH;
    public static int GUI_INFO_PANEL_HEIGHT;
    public static int GUI_ACTION_PANEL_HEIGHT;
    public static int GUI_TECH_PANEL_HEIGHT;
    public static int GUI_ACTION_PANEL_FULL_SIZE = 350;
    public static int GUI_TECH_PANEL_FULL_SIZE = 300;

    public static int SHADOW_OFFSET = 1;
    public static int ROUND_RECT_ARC = 5;
    public static int GUI_ZOOM_FACTOR = 5;

    //Maximum number of turns to be played and playing settings
    static final int MAX_TURNS = 30;
    static final int MAX_TURNS_CAPITALS = 100; //Integer.MAX_VALUE; //Setting a max is useful for experiments
    public static final boolean PLAY_WITH_FULL_OBS = true; //This is for agents
    public static final boolean GUI_FORCE_FULL_OBS = false; //This is for display
}
