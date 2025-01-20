package town.lost.g2k.view;

import town.lost.g2k.model.GameStatus;

import java.util.List;

/**
 * A common interface for any 2048 View (console, GUI, etc.).
 * Renders the board, displays status & score, captures moves,
 * and shows end screens. Also includes a method to show the updated
 * high score for the current board size.
 */
public interface GameView {

    /**
     * Called once when the view is first created,
     * e.g., to initialize UI components or print a welcome banner.
     */
    void initializeView();

    /**
     * Renders the current state of the board (NxN) to the user.
     *
     * @param board a 2D int array representing tile values
     */
    void renderBoard(int[][] board);

    /**
     * Displays the player's current score.
     *
     * @param score the total of all merged tile values
     */
    void displayScore(int score);

    /**
     * Shows the current game status (RUNNING, WON, LOST).
     *
     * @param status the current status of the game
     */
    void displayGameStatus(GameStatus status);

    /**
     * Potentially captures user input for a move (console),
     * or is unused in an event-driven GUI.
     */
    void captureUserMove();

    /**
     * Called when the game is over (WON or LOST), showing a final message
     * and the player's final score.
     *
     * @param finalStatus the end state (WON or LOST)
     * @param finalScore the player's last score
     */
    void displayEndScreen(GameStatus finalStatus, int finalScore);

    /**
     * Informs the view of the updated high score for the current board dimension.
     * Implementations (Console or GUI) can show or log this record
     * so the user sees the best score for that NxN size.
     *
     * @param highScore the dimension-specific best record
     */
    void updateHighScore(int highScore);

    void showAnimations(List<TileMovement> movements, Runnable onAnimationsComplete);
}
