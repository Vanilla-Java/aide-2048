package town.lost.g2k.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests the single-step undo logic on rectangular boards.
 */
class GameBoardUndoTest {

    private GameConfig config;
    private GameBoard board;

    @BeforeEach
    void setUp() {
        // Enable undo
        config = new GameConfig();
        config.setUndoEnabled(true);
        config.setBoardSize(4, 4); // default 4×4
        board = new GameBoard(config);
    }

    @Test
    void testUndoSingleStep4x4() {
        int[][] custom = {
                {2, 2, 4, 4},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        };
        board.setBoardState(custom);

        // Move left => merges
        board.move(Direction.LEFT);
        assertEquals(12, board.getScore(), "After merging (2+2)+(4+4), score=12.");

        // Undo => revert
        board.undo();
        int[][] reverted = board.getBoard();
        assertEquals(0, board.getScore(), "Score should revert to 0 after undo.");
        assertArrayEquals(custom[0], reverted[0], "Row0 should match original state.");
    }

    @Test
    void testUndoWithRectBoard() {
        config.setBoardSize(3, 5); // 3 columns, 5 rows
        GameBoard rectBoard = new GameBoard(config);

        // Overwrite with a simple pattern
        int[][] layout = {
                {2, 2, 0},
                {4, 4, 0},
                {8, 8, 0},
                {0, 0, 0},
                {0, 0, 0}
        };
        rectBoard.setBoardState(layout);

        rectBoard.move(Direction.LEFT); // merges horizontally
        assertTrue(rectBoard.getScore() > 0, "Should have gained some score.");

        rectBoard.undo(); // revert
        int[][] reverted = rectBoard.getBoard();
        assertArrayEquals(layout[0], reverted[0], "Top row should revert to original after undo.");
        assertEquals(0, rectBoard.getScore(), "Score should revert to 0 (the original).");
    }
}
