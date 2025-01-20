package town.lost.g2k;

import town.lost.g2k.controller.GameController;
import town.lost.g2k.controller.HighScoreManager;
import town.lost.g2k.model.GameBoard;
import town.lost.g2k.model.GameConfig;
import town.lost.g2k.view.GameView;
import town.lost.g2k.view.SwingGameView;

import javax.swing.*;
import java.util.Map;

/**
 * The main entry point for the 2048 application with a Swing View.
 */
public class Main {
    static final String[] selectionValues = {"3x3", "3x4", "4x3", "4x4", "4x5", "5x4", "5x5", "5x7", "7x5", "6x6", "6x9", "9x6"};

    public static void main(String[] args) {
        // 1) Create or customize a GameConfig
        GameConfig config = new GameConfig();
        int boardSize = pickBoardSizeAtStartup();
        config.setBoardSize(boardSize / 10, boardSize % 10); // default 4x4 initially
        config.setUndoEnabled(true); // allow undo
        int sizeToWinValue = 8;

        switch (boardSize) {
            case 33: sizeToWinValue = 32; break;
            case 34: sizeToWinValue = 128; break;
            case 43: sizeToWinValue = 128; break;
            case 44: sizeToWinValue = 2048; break;
            case 45: sizeToWinValue = 4096; break;
            case 54: sizeToWinValue = 4096; break;
            case 55: sizeToWinValue = 8192; break;
            case 57: sizeToWinValue = 16384; break;
            case 75: sizeToWinValue = 16384; break;
            case 66: sizeToWinValue = 32768; break;
            case 69: sizeToWinValue = 65536; break;
            case 96: sizeToWinValue = 65536; break;
        }
        config.setWinTileValue(sizeToWinValue); // 2048 for 4x4
        config.setHighScoreFilePath("highscore.txt");
        config.setTileSpawnProbabilities(
                Map.of(2, 0.8,
                        4, 0.15,
                        8, 0.04,
                        16, 0.01));

        // 2) Create the GameBoard (Model), reading from config
        GameBoard model = new GameBoard(config);

        // 3) Create the GameController with (model, config) but no GameView
        GameController controller = new GameController(model, config);

        // 4) Optionally create a HighScoreManager to track per-size records
        HighScoreManager scoreManager = new HighScoreManager(config);
        controller.setHighScoreManager(scoreManager);

        // 5) For a Swing approach:
        GameView view = new SwingGameView(controller, model);

        // 6) Finalize the MVC wiring: setView on the controller
        controller.setView(view);

        // 7) Start the game
        controller.startGame();
    }

    /**
     * Asks the user for board size in a loop, forcing valid numeric input
     * Defaults to 4x4 if the dialog is canceled.
     */
    static int pickBoardSizeAtStartup() {
        String input = (String) (String) JOptionPane.showInputDialog(null, "Select Board Size:",
                UIManager.getString("OptionPane.inputDialogTitle",
                        null), JOptionPane.QUESTION_MESSAGE, null, selectionValues,
                "4x4");

        // If dialog was closed or canceled, fallback to "4"
        if (input == null) {
            input = "4x4";
        }
        int xSize = input.charAt(0) - '0';
        int ySize = input.charAt(2) - '0';
        return xSize * 10 + ySize; // convert first and third char to int
    }
}
