package town.lost.g2k.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import town.lost.g2k.model.Direction;
import town.lost.g2k.model.GameBoard;
import town.lost.g2k.model.GameConfig;
import town.lost.g2k.model.GameStatus;
import town.lost.g2k.view.GameView;
import org.junit.jupiter.api.*;

/**
 * Tests for GameController, ensuring correct interactions
 * with the model (GameBoard), the view, and optional high score updates.
 */
class GameControllerTest {

    private GameConfig config;
    private GameBoard model;
    private GameController controller;
    private GameView mockView;
    private HighScoreManager mockScoreManager;

    @BeforeEach
    void setUp() {
        config = new GameConfig();
        config.setBoardSize(4, 4);
        model = new GameBoard(config);

        controller = new GameController(model, config);
        mockView = mock(GameView.class);
        mockScoreManager = mock(HighScoreManager.class);
    }

    @Test
    @DisplayName("setView(...) and startGame() calls view.initializeView().")
    void testSetView() {
        controller.setView(mockView);
        controller.startGame();

        verify(mockView).initializeView();
        // Also check it calls refreshView at least once
        verify(mockView, atLeastOnce()).renderBoard(any(int[][].class));
    }

    @Test
    @DisplayName("onUserMove triggers model.move() and refreshes the view.")
    void testOnUserMove() {
        controller.setView(mockView);
        controller.startGame();

        controller.onUserMove(Direction.LEFT);
        verify(mockView, atLeastOnce()).renderBoard(any(int[][].class));
        verify(mockView, atLeastOnce()).displayScore(anyInt());
        verify(mockView, atLeastOnce()).displayGameStatus(any(GameStatus.class));
    }

    @Test
    @DisplayName("Undo calls model.undo() if enabled and refreshes the view.")
    void testUndo() {
        config.setUndoEnabled(true);
        controller.setView(mockView);
        controller.startGame();

        controller.onUndo();
        verify(mockView, atLeastOnce()).renderBoard(any(int[][].class));
    }

    @Test
    @DisplayName("High score updates when current score surpasses record.")
    void testHighScoreUpdate() {
        // Suppose we do dimension-based key = (xSize*10 + ySize)
        controller.setHighScoreManager(mockScoreManager);
        controller.setView(mockView);
        controller.startGame();

        // Force model's score to 100
        forceScore(model, 100);

        // Mock existing record as 50
        int dimensionKey = model.getXSize() * 10 + model.getYSize(); // e.g., 4*10+4=44
        when(mockScoreManager.getHighScoreFor(dimensionKey)).thenReturn(50);

        // Trigger a move => postMoveUpdate => updateHighScoreIfNeeded
        controller.onUserMove(Direction.LEFT);

        verify(mockScoreManager).setHighScoreFor(dimensionKey, 100);
        verify(mockScoreManager).saveHighScores();
    }

    // Helper to forcibly set the model's score
    private void forceScore(GameBoard board, int newScore) {
        try {
            var scoreField = GameBoard.class.getDeclaredField("score");
            scoreField.setAccessible(true);
            scoreField.setInt(board, newScore);
        } catch (Exception e) {
            fail("Could not set score for testing: " + e.getMessage());
        }
    }
}
