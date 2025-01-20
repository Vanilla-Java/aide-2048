package town.lost.g2k.view;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import town.lost.g2k.controller.GameController;
import town.lost.g2k.model.Direction;
import town.lost.g2k.model.GameBoard;
import town.lost.g2k.model.GameStatus;

/**
 * A console-based view that prompts for board size,
 * captures moves (W/A/S/D, R=reset, U=undo, Q=quit),
 * and displays the board ASCII style.
 */
public class ConsoleGameView implements GameView {

    private final Scanner scanner;
    private final GameController controller;
    private final GameBoard model;

    public ConsoleGameView(GameController controller, GameBoard model) {
        this.scanner = new Scanner(System.in);
        this.controller = controller;
        this.model = model;
    }

    @Override
    public void initializeView() {
        System.out.println("==========================================");
        System.out.println("         Welcome to 2048 (Console)        ");
        System.out.println("   Use W/A/S/D or arrow keys (if mapped)  ");
        if (controller.getConfig().isUndoEnabled()) {
            System.out.println("   Type 'U' for undo.                     ");
        }
        System.out.println("   Type 'R' to reset, 'Q' to quit.        ");
        System.out.println("==========================================");

        pickBoardSizeAtStartup();
    }

    private void pickBoardSizeAtStartup() {
        int sizeChoice = 4;
        System.out.print("Please enter board size (4, 5, 6): ");
        try {
            sizeChoice = scanner.nextInt();
            if (sizeChoice < 2 || sizeChoice > 100) {
                System.out.println("Invalid size; defaulting to 4.");
                sizeChoice = 4;
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input; defaulting to 4.");
            scanner.nextLine();
        }
        // Set config, then reset game
        controller.getConfig().setBoardSize(sizeChoice, sizeChoice);
        controller.resetGame();
    }

    @Override
    public void renderBoard(int[][] board) {
        int gridSize = board.length;
        int cellWidth = 5;

        for (int r = 0; r < gridSize; r++) {
            // separator line
            StringBuilder sep = new StringBuilder();
            for (int c = 0; c < gridSize; c++) {
                sep.append("+");
                for (int w = 0; w < cellWidth; w++) {
                    sep.append("-");
                }
            }
            sep.append("+");
            System.out.println(sep);

            // row content
            StringBuilder rowStr = new StringBuilder();
            for (int c = 0; c < gridSize; c++) {
                int val = board[r][c];
                String cellStr = (val == 0) ? "" : String.valueOf(val);
                rowStr.append("|");
                rowStr.append(String.format("%" + cellWidth + "s", cellStr));
            }
            rowStr.append("|");
            System.out.println(rowStr);
        }
        // final separator
        StringBuilder finalSep = new StringBuilder();
        for (int c = 0; c < gridSize; c++) {
            finalSep.append("+");
            for (int w = 0; w < cellWidth; w++) {
                finalSep.append("-");
            }
        }
        finalSep.append("+");
        System.out.println(finalSep);
    }

    @Override
    public void displayScore(int score) {
        System.out.println("Score: " + score);
    }

    @Override
    public void displayGameStatus(GameStatus status) {
        System.out.println("Game Status: " + status);
    }

    @Override
    public void captureUserMove() {
        System.out.print("Your move (W/A/S/D, R=reset, U=undo, Q=quit): ");
        String input = scanner.nextLine().trim().toUpperCase();

        switch (input) {
            case "W":
                controller.onUserMove(Direction.UP);
                break;
            case "S":
                controller.onUserMove(Direction.DOWN);
                break;
            case "A":
                controller.onUserMove(Direction.LEFT);
                break;
            case "D":
                controller.onUserMove(Direction.RIGHT);
                break;
            case "U":
                if (controller.getConfig().isUndoEnabled()) {
                    controller.onUndo();
                } else {
                    System.out.println("Undo disabled.");
                }
                break;
            case "R":
                controller.resetGame();
                break;
            case "Q":
                System.out.println("Quitting the game...");
                System.exit(0);
                break;
            default:
                System.out.println("Invalid input! Please try again.");
        }
    }

    @Override
    public void displayEndScreen(GameStatus finalStatus, int finalScore) {
        System.out.println();
        if (finalStatus == GameStatus.WON) {
            System.out.println("*****************************");
            System.out.println("         YOU WIN!            ");
            System.out.println("*****************************");
        } else if (finalStatus == GameStatus.LOST) {
            System.out.println("*****************************");
            System.out.println("         GAME OVER!          ");
            System.out.println("*****************************");
        }
        System.out.println("Final Score: " + finalScore);
        System.out.println("Thank you for playing!");
    }

    @Override
    public void updateHighScore(int highScore) {
        System.out.println("Current High Score for " + model.getXSize() + "×" + model.getYSize()
                + ": " + highScore);
    }

    @Override
    public void showAnimations(List<TileMovement> movements, Runnable onAnimationsComplete) {

    }
}
