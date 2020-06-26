package players;

import core.Types;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Queue;

public class KeyController extends KeyAdapter {

    private Queue<Types.DIRECTIONS> actionsQueue;
    private HashMap<Integer, Types.DIRECTIONS> keyMap;
    private int focusedPlayer = -1;
    private boolean primaryKeys;

    public KeyController(boolean primaryKeys)
    {
        this.primaryKeys = primaryKeys;
        actionsQueue = new ArrayDeque<>();
        keyMap = new HashMap<>();

        if (primaryKeys) {
            keyMap.put(KeyEvent.VK_LEFT, Types.DIRECTIONS.LEFT);
            keyMap.put(KeyEvent.VK_UP, Types.DIRECTIONS.UP);
            keyMap.put(KeyEvent.VK_DOWN, Types.DIRECTIONS.DOWN);
            keyMap.put(KeyEvent.VK_RIGHT, Types.DIRECTIONS.RIGHT);
            keyMap.put(KeyEvent.VK_SPACE, Types.DIRECTIONS.NONE);
        } else {
            keyMap.put(KeyEvent.VK_A, Types.DIRECTIONS.LEFT);
            keyMap.put(KeyEvent.VK_W, Types.DIRECTIONS.UP);
            keyMap.put(KeyEvent.VK_S, Types.DIRECTIONS.DOWN);
            keyMap.put(KeyEvent.VK_D, Types.DIRECTIONS.RIGHT);
            keyMap.put(KeyEvent.VK_SHIFT, Types.DIRECTIONS.NONE);
        }

    }

    /**
     * Invoked when a key has been typed.
     * This event occurs when a key press is followed by a key release.
     */
    public void keyTyped(KeyEvent e) {}

    /**
     * Invoked when a key has been pressed.
     */
    public void keyPressed(KeyEvent e)
    {
        int key = e.getKeyCode();
        Types.DIRECTIONS candidate = keyMap.get(key);
        if (candidate != null)
            actionsQueue.add(candidate);
        else {
            // If human is not a player and presses 1, 2, 3, 4, the main view shows what that player sees.
            // If human is not a player and presses 0, the main view shows the fully observable true game state.
            if (key >= KeyEvent.VK_0 && key <= KeyEvent.VK_4) {
                focusedPlayer = key - KeyEvent.VK_0 - 1;
            }
        }
    }

    /**
     * Invoked when a key has been released.
     */
    public void keyReleased(KeyEvent e) { }

    public KeyController copy() {
        KeyController copy = new KeyController(primaryKeys);
        copy.actionsQueue.addAll(actionsQueue);
        return copy;
    }
}