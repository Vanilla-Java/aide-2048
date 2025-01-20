package town.lost.g2k.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * The main logic for a 2048 puzzle board, allowing NxN dimension,
 * single-step undo if enabled, and reading spawn/win settings from GameConfig.
 */
public class GameBoard {

    private final GameConfig config;

    private int xSize;
    private int ySize;
    private int[][] board;
    private int score;
    private GameStatus status;
    private final Random random;

    // Single-step undo snapshots
    private int[][] prevBoard;
    private int prevScore;
    private boolean canUndo;

    /**
     * Constructs using a default config (4x4, standard spawn).
     */
    public GameBoard() {
        this(new GameConfig());
    }

    /**
     * Constructs using the provided GameConfig.
     * NxN dimension is config.getBoardSize().
     */
    public GameBoard(GameConfig config) {
        this.config = config;
        this.xSize = config.getXSize();
        this.ySize = config.getYSize();
        this.board = new int[ySize][xSize];
        this.score = 0;
        this.status = GameStatus.RUNNING;
        this.random = new Random();

        prevBoard = null;
        prevScore = 0;
        canUndo = false;

        initializeBoard();
    }

    public final void initializeBoard() {
        this.xSize = config.getXSize(); // re-check if changed
        this.ySize = config.getYSize(); // re-check if changed
        this.board = new int[ySize][xSize];
        this.score = 0;
        this.status = GameStatus.RUNNING;

        spawnNewTile();
        spawnNewTile();

        prevBoard = null;
        prevScore = 0;
        canUndo = false;
    }

    /**
     * Shifts/merges in the given direction, spawns a new tile if changed, checks for win/lose.
     */
    public void move(Direction dir) {
        if (status != GameStatus.RUNNING) {
            return;
        }
        if (config.isUndoEnabled()) {
            saveUndoSnapshot();
        }

        boolean boardChanged = false;
        switch (dir) {
            case LEFT:
                boardChanged = moveLeft();
                break;
            case RIGHT:
                boardChanged = moveRight();
                break;
            case UP:
                boardChanged = moveUp();
                break;
            case DOWN:
                boardChanged = moveDown();
                break;
        }

        if (boardChanged) {
            spawnNewTile();
            checkForWin();

            if (isBoardFull() && !canMergeAny()) {
                status = GameStatus.LOST;
            }
        } else {
            // no move => discard undo
            if (config.isUndoEnabled()) {
                canUndo = false;
            }
            if (isBoardFull() && !canMergeAny()) {
                status = GameStatus.LOST;
            }
        }
    }

    /**
     * Single-step undo if enabled. Reverts board & score to previous snapshot.
     */
    public void undo() {
        if (!config.isUndoEnabled() || !canUndo) {
            return;
        }
        restoreUndoSnapshot();
    }

    public void reset() {
        initializeBoard();
    }

    public boolean isGameOver() {
        return (status == GameStatus.WON || status == GameStatus.LOST);
    }

    // -- Move Logic Helpers --

    private boolean moveLeft() {
        boolean changed = false;
        for (int r = 0; r < board.length; r++) {
            int[] row = board[r];
            int[] merged = compressAndMerge(row);
            if (!rowsEqual(row, merged)) {
                board[r] = merged;
                changed = true;
            }
        }
        return changed;
    }

    private boolean moveRight() {
        boolean changed = false;
        for (int r = 0; r < board.length; r++) {
            int[] reversed = reverseArray(board[r]);
            int[] merged = compressAndMerge(reversed);
            int[] newRow = reverseArray(merged);
            if (!rowsEqual(board[r], newRow)) {
                board[r] = newRow;
                changed = true;
            }
        }
        return changed;
    }

    private boolean moveUp() {
        boolean changed = false;
        for (int c = 0; c < board[0].length; c++) {
            int[] col = extractColumn(c);
            int[] merged = compressAndMerge(col);
            if (!rowsEqual(col, merged)) {
                putColumn(c, merged);
                changed = true;
            }
        }
        return changed;
    }

    private boolean moveDown() {
        boolean changed = false;
        for (int c = 0; c < board[0].length; c++) {
            int[] col = extractColumn(c);
            int[] reversed = reverseArray(col);
            int[] merged = compressAndMerge(reversed);
            int[] newCol = reverseArray(merged);
            if (!rowsEqual(col, newCol)) {
                putColumn(c, newCol);
                changed = true;
            }
        }
        return changed;
    }

    private int[] compressAndMerge(int[] row) {
        List<Integer> filtered = new ArrayList<>();
        for (int val : row) {
            if (val != 0) {
                filtered.add(val);
            }
        }

        for (int i = 0; i < filtered.size() - 1; i++) {
            if (filtered.get(i).equals(filtered.get(i + 1))) {
                int mergedVal = filtered.get(i) * 2;
                score += mergedVal;
                filtered.set(i, mergedVal);
                filtered.remove(i + 1);
            }
        }

        int[] newRow = new int[row.length];
        for (int i = 0; i < filtered.size(); i++) {
            newRow[i] = filtered.get(i);
        }
        return newRow;
    }

    // -- Spawning & Checks --

     void spawnNewTile() {
        List<int[]> emptyCells = getEmptyCells();
        if (emptyCells.isEmpty()) {
            return;
        }
        int[] cell = emptyCells.get(random.nextInt(emptyCells.size()));
        board[cell[0]][cell[1]] = chooseRandomTileValue();
    }

    private int chooseRandomTileValue() {
        Map<Integer, Double> probs = config.getTileSpawnProbabilities();
        double roll = random.nextDouble();
        double cumulative = 0.0;
        int chosenValue = 2; // fallback

        for (int tileValue : probs.keySet()) {
            double p = probs.get(tileValue);
            cumulative += p;
            if (roll <= cumulative) {
                chosenValue = tileValue;
                break;
            }
        }
        return chosenValue;
    }

    private void checkForWin() {
        int target = config.getWinTileValue();
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[r].length; c++) {
                if (board[r][c] >= target) {
                    status = GameStatus.WON;
                    return;
                }
            }
        }
    }

    private boolean isBoardFull() {
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[r].length; c++) {
                if (board[r][c] == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean canMergeAny() {
        // horizontal check
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[r].length - 1; c++) {
                if (board[r][c] == board[r][c + 1]) {
                    return true;
                }
            }
        }
        // vertical check
        for (int c = 0; c < xSize; c++) {
            for (int r = 0; r < ySize - 1; r++) {
                if (board[r][c] == board[r + 1][c]) {
                    return true;
                }
            }
        }
        return false;
    }

    private List<int[]> getEmptyCells() {
        List<int[]> list = new ArrayList<>();
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[r].length; c++) {
                if (board[r][c] == 0) {
                    list.add(new int[]{r, c});
                }
            }
        }
        return list;
    }

    // -- Undo Snapshots --

    private void saveUndoSnapshot() {
        prevBoard = new int[board.length][board[0].length];
        for (int r = 0; r < board.length; r++) {
            System.arraycopy(board[r], 0, prevBoard[r], 0, board[r].length);
        }
        prevScore = score;
        canUndo = true;
    }

    private void restoreUndoSnapshot() {
        for (int r = 0; r < board.length; r++) {
            System.arraycopy(prevBoard[r], 0, board[r], 0, board[r].length);
        }
        score = prevScore;
        status = GameStatus.RUNNING;
        canUndo = false;
    }

    // -- Helpers --

    private int[] reverseArray(int[] arr) {
        int[] rev = new int[arr.length];
        for (int i = 0; i < arr.length; i++) {
            rev[i] = arr[arr.length - 1 - i];
        }
        return rev;
    }

    private boolean rowsEqual(int[] a, int[] b) {
        if (a.length != b.length) return false;
        for (int i = 0; i < a.length; i++) {
            if (a[i] != b[i]) return false;
        }
        return true;
    }

    private int[] extractColumn(int c) {
        int[] col = new int[board.length];
        for (int r = 0; r < board.length; r++) {
            col[r] = board[r][c];
        }
        return col;
    }

    private void putColumn(int c, int[] colData) {
        for (int r = 0; r < colData.length; r++) {
            board[r][c] = colData[r];
        }
    }

    // -- Public Accessors --

    public int[][] getBoard() {
        int[][] copy = new int[board.length][board[0].length];
        for (int r = 0; r < board.length; r++) {
            System.arraycopy(board[r], 0, copy[r], 0, board[0].length);
        }
        return copy;
    }

    /**
     * For tests or specialized setups, must match the NxM dimension.
     */
    public void setBoardState(int[][] newState) {
        if (newState.length != ySize || newState[0].length != xSize) {
            throw new IllegalArgumentException("Dimension mismatch; must be " + xSize + "x" + ySize);
        }
        for (int r = 0; r < ySize; r++) {
            System.arraycopy(newState[r], 0, board[r], 0, xSize);
        }
    }

    public int getScore() {
        return score;
    }

    public GameStatus getStatus() {
        return status;
    }

    public int getXSize() {
        return xSize;
    }

    public int getYSize() {
        return ySize;
    }
}
