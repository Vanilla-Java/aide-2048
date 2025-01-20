package town.lost.g2k.model;

/**
 * Represents the state of the game at any point in time.
 */
public enum GameStatus {
    RUNNING, // The game is active and moves can be made
    WON,     // A tile reached (or exceeded) the target value (e.g., 2048)
    LOST     // No further moves are possible
}
