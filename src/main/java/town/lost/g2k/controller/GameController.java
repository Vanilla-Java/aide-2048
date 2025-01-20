package town.lost.g2k.controller;

import town.lost.g2k.model.Direction;
import town.lost.g2k.model.GameBoard;
import town.lost.g2k.model.GameConfig;
import town.lost.g2k.model.GameStatus;
import town.lost.g2k.view.GameView;
import town.lost.g2k.view.TileMovement;

import java.util.ArrayList;
import java.util.List;

/**
 * Controls gameplay by receiving user inputs (move, undo, reset)
 * and calling the model (GameBoard).
 * Avoids circular references by excluding GameView from the constructor,
 * using setView(...) afterwards.
 * Can optionally track high scores per dimension (NxN) using HighScoreManager.
 */
public class GameController {

    private final GameBoard model;
    private final GameConfig config;
    private GameView view;
    private HighScoreManager scoreManager;

    public GameController(GameBoard model, GameConfig config) {
        this.model = model;
        this.config = config;
    }

    public void setHighScoreManager(HighScoreManager manager) {
        this.scoreManager = manager;
    }

    public void setView(GameView view) {
        this.view = view;
    }

    public GameConfig getConfig() {
        return config;
    }

    public void startGame() {
        if (view == null) {
            throw new IllegalStateException("View not set. Call setView(...) first.");
        }
        view.initializeView();

        // Load any existing records if a score manager is present
        if (scoreManager != null) {
            scoreManager.loadHighScores();
        }

        refreshView();
    }

    public void onUserMove(Direction dir) {
        if (!model.isGameOver()) {
            // 1) Capture the board before the move
            int[][] preMove = copyBoard(model.getBoard());

            // 2) Perform the move in the model
            model.move(dir);

            // 3) Construct sliding animations (pre vs. post)
            int[][] postMove = copyBoard(model.getBoard());
            List<TileMovement> tileMovements = findTileMovements(preMove, postMove);

            // 4) Let the view animate these movements
            //    (then show final board when done)
            if (view != null) {
                view.showAnimations(tileMovements, () -> {
                    // Callback after animation completes
                    postMoveUpdate();
                });
            } else {
                // If no view or no animation, just do normal postMoveUpdate
                postMoveUpdate();
            }
        }
    }

    public void onUndo() {
        if (config.isUndoEnabled()) {
            model.undo();
            refreshView();
        }
    }

    public void resetGame() {
        model.reset();
        refreshView();
    }

    private void postMoveUpdate() {
        if (model.getStatus() == GameStatus.WON || model.getStatus() == GameStatus.LOST) {
            updateHighScoreIfNeeded();
            refreshView();
            if (view != null) {
                view.displayEndScreen(model.getStatus(), model.getScore());
            }
        } else {
            refreshView();
            updateHighScoreIfNeeded();
        }
    }

    // A naive approach: For each non-empty cell in preMove,
    // find the "closest" matching cell in postMove to treat as "same tile".
    private List<TileMovement> findTileMovements(int[][] preMove, int[][] postMove) {
        List<TileMovement> movements = new ArrayList<>();

        // For each cell in preMove
        for (int r = 0; r < preMove.length; r++) {
            for (int c = 0; c < preMove[r].length; c++) {
                int oldVal = preMove[r][c];
                if (oldVal != 0) {
                    // Attempt to find that same tile in postMove
                    // (Naive: look for first matching val that hasn't been "used" yet)
                    int[] match = findMatchingCell(postMove, oldVal);
                    if (match != null) {
                        // We treat this as the same tile sliding from (r,c) to match
                        TileMovement tm = new TileMovement(oldVal,
                                r, c,
                                match[0], match[1],
                                false // we don't know if it merged or not
                        );
                        movements.add(tm);
                        // Mark that match as used by setting it to 0
                        postMove[match[0]][match[1]] = 0;
                    }
                }
            }
        }
        return movements;
    }

    // Finds the first cell with value == oldVal and returns row,col.
    // Returns null if not found.
    private int[] findMatchingCell(int[][] board, int oldVal) {
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[r].length; c++) {
                if (board[r][c] == oldVal) {
                    return new int[]{r, c};
                }
            }
        }
        return null;
    }

    // Called from onUserMove(...) to store the board for sliding reference
    private int[][] copyBoard(int[][] source) {
        int rows = source.length;
        int cols = source[0].length;
        int[][] copy = new int[rows][cols];
        for (int r = 0; r < rows; r++) {
            System.arraycopy(source[r], 0, copy[r], 0, cols);
        }
        return copy;
    }

    private void updateHighScoreIfNeeded() {
        if (scoreManager != null) {
            int boardSize = model.getXSize() * 10 + model.getYSize();
            int currentScore = model.getScore();
            int record = scoreManager.getHighScoreFor(boardSize);
            if (currentScore > record) {
                scoreManager.setHighScoreFor(boardSize, currentScore);
                scoreManager.saveHighScores();
            }
        }
    }

    private void refreshView() {
        if (view != null) {
            view.renderBoard(model.getBoard());
            view.displayScore(model.getScore());
            view.displayGameStatus(model.getStatus());

            if (scoreManager != null) {
                int dimKey = model.getXSize() * 10 + model.getYSize();
                int record = scoreManager.getHighScoreFor(dimKey);
                view.updateHighScore(record);
            }
        }
    }
}
