package town.lost.g2k.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the GameBoard class in a 2048 puzzle.
 * <p>
 * This version tests the rectangular board logic (xSize by ySize),
 * initial tile spawning, merges, and basic scoring.
 */
class GameBoardTest {

    private GameBoard board;

    @BeforeEach
    void setUp() {
        // Creates a new GameBoard with default 4×4 in GameConfig.
        board = new GameBoard();
    }

    @Test
    @DisplayName("Initial board contains exactly two tiles and score=0.")
    void testInitializeBoard() {
        int[][] initialGrid = board.getBoard();
        int nonEmptyCount = countNonEmptyCells(initialGrid);

        assertEquals(2, nonEmptyCount,
                "Initial board should have exactly two tiles spawned.");
        assertEquals(0, board.getScore(),
                "Score should be 0 at the start.");
        assertEquals(GameStatus.RUNNING, board.getStatus(),
                "Game status should start as RUNNING.");

        // Check empty cells: 4×4 => 16 total; 2 spawned => 14 empty
        int emptyCount = countEmptyCells(initialGrid);
        assertEquals(14, emptyCount,
                "Should have 14 empty cells on a default 4×4 board.");
    }

    @Test
    @DisplayName("Move left merges adjacent equal values once and updates score.")
    void testMoveLeftMerge() {
        // Prepare the top row to merge: [2, 2, 4, 4], and the rest empty
        int[][] customBoard = {
                {2, 2, 4, 4},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        };
        board.setBoardState(customBoard);

        // Move left
        board.move(Direction.LEFT);
        int[][] result = board.getBoard();

        // Expect merges: (2+2)->4 at [0][0], (4+4)->8 at [0][1].
        assertEquals(4, result[0][0], "Should have merged 2 & 2 into 4.");
        assertEquals(8, result[0][1], "Should have merged 4 & 4 into 8.");

        // The spawn logic might place a new tile in one of the empty positions.
        // So [0][2] could be 0, 2, 4, 8, or 16 based on config probabilities.
        // Check at least that it is not '2' or '4' leftover from merging incorrectly.
        // We'll allow any tile since spawn is random.

        // Score check: 4 + 8 = 12
        assertEquals(12, board.getScore(),
                "Merging (2+2) + (4+4) should yield 12 points in total.");
    }

    @Test
    @DisplayName("A newly spawned tile appears in an empty cell.")
    void testSpawnNewTile() {
        // Fill the board except one cell
        int[][] nearlyFull = {
                {2, 4, 8, 16},
                {16, 8, 4, 2},
                {2, 4, 8, 16},
                {16, 8, 4, 0}
        };
        board.setBoardState(nearlyFull);

        // Force a spawn (call spawnNewTile directly or cause a valid move).
        board.spawnNewTile();

        int[][] afterSpawn = board.getBoard();
        int nonEmptyCount = countNonEmptyCells(afterSpawn);

        assertEquals(16, nonEmptyCount,
                "All cells should be filled after the new tile spawns.");
    }

    @Test
    @DisplayName("Rectangular dimension test (e.g., 3×5).")
    void testRectangularDimension() {
        // Create a config for 3×5
        GameConfig config = new GameConfig();
        config.setBoardSize(3, 5); // x=3, y=5
        GameBoard rectBoard = new GameBoard(config);

        int[][] grid = rectBoard.getBoard();
        assertEquals(5, grid.length,   "Should have 5 rows (ySize=5).");
        assertEquals(3, grid[0].length,"Should have 3 columns (xSize=3).");

        // Should have exactly two tiles spawned
        int nonEmpty = countNonEmptyCells(grid);
        assertEquals(2, nonEmpty, "Rect board should spawn two tiles on init.");
    }

    // Helper methods

    private int countNonEmptyCells(int[][] grid) {
        int count = 0;
        for (int[] row : grid) {
            for (int val : row) {
                if (val != 0) {
                    count++;
                }
            }
        }
        return count;
    }

    private int countEmptyCells(int[][] grid) {
        int total = 0;
        for (int[] row : grid) {
            for (int val : row) {
                if (val == 0) {
                    total++;
                }
            }
        }
        return total;
    }
}
