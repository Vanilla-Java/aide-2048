package town.lost.g2k.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Additional tests focusing on custom dimensions (NxM),
 * merges, and other edge cases.
 */
class GameBoardNxMTest {

    @Test
    @DisplayName("5×5 board initializes properly and handles merges.")
    void testInitializeBoard5x5() {
        GameConfig config = new GameConfig();
        config.setBoardSize(5, 5); // 5x5
        GameBoard board = new GameBoard(config);

        int[][] grid = board.getBoard();
        assertEquals(5, grid.length,    "Should be 5 rows.");
        assertEquals(5, grid[0].length, "Should be 5 columns.");

        // Exactly two tiles spawned
        int nonEmpty = countNonEmptyCells(grid);
        assertEquals(2, nonEmpty, "Should spawn exactly 2 tiles on init.");

        // Merge test
        int[][] custom = {
            {2, 2, 4, 4, 8},
            {0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0},
        };
        board.setBoardState(custom);
        board.move(Direction.LEFT);
        int[][] result = board.getBoard();

        // Expect (2+2)->4 at col0, (4+4)->8 at col1, original 8 at col2
        assertEquals(4, result[0][0]);
        assertEquals(8, result[0][1]);
        assertEquals(8, result[0][2]);
    }

    @Test
    @DisplayName("3×4 board: merges along columns (UP), allowing new tile spawns in the third column")
    void test3x4BoardMergingUp() {
        GameConfig config = new GameConfig();
        config.setBoardSize(3, 4); // x=3 columns, y=4 rows
        GameBoard board = new GameBoard(config);

        // Overwrite with a custom layout (4 rows, 3 columns).
        // We'll focus merges on columns 0 and 1, leaving column 2 empty initially.
        int[][] layout = {
                {2, 2, 0},
                {2, 2, 0},
                {4, 4, 0},
                {8, 8, 0}
        };
        board.setBoardState(layout);

        // Move UP => merges in each column
        board.move(Direction.UP);
        int[][] result = board.getBoard();

        // Column 0 merges
        // e.g., merges might form 4 in row0, 4 or 8 in row1, etc.
        assertTrue(result[0][0] >= 4, "Should have merged some tiles in col 0 (UP).");
        assertTrue(result[1][0] >= 4, "Should have merged more tiles in col 0 (UP).");

        // Column 1 merges similarly
        assertTrue(result[0][1] >= 4, "Merges in col 1 too.");
        assertTrue(result[1][1] >= 4, "Second row col1 also merges.");

        // The last column (index 2) was empty before, but a new tile may have spawned there.
        // So it can be 0, 2, 4, 8, or 16.
        for (int r = 0; r < 4; r++) {
            int cellVal = result[r][2];
            boolean allowed = (cellVal == 0 || cellVal == 2 || cellVal == 4 || cellVal == 8 || cellVal == 16);
            assertTrue(allowed, "Third column might have a newly spawned tile (2,4,8,16) or remain 0.");
        }
    }

    // Helper
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
}
