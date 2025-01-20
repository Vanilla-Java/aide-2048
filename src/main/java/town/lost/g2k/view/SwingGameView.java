package town.lost.g2k.view;

import town.lost.g2k.controller.GameController;
import town.lost.g2k.model.Direction;
import town.lost.g2k.model.GameBoard;
import town.lost.g2k.model.GameConfig;
import town.lost.g2k.model.GameStatus;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

/**
 * A Swing-based 2048 View that demonstrates basic sliding animations
 * in front of tile JLabels by overriding paintChildren(...) instead of paintComponent(...).
 */
public class SwingGameView extends JFrame implements GameView {

    // Visual settings
    private static final int TILE_SIZE_PX = 80;
    private static final int TILE_FONT_SIZE = 24;

    private final GameController controller;
    private final GameBoard model;
    private final GameConfig config;

    // GUI components
    private JLabel scoreLabel;
    private JLabel statusLabel;
    private JLabel highScoreLabel;
    private JLabel[][] tileLabels;
    private BoardPanel boardPanel;
    private AnimationManager animationManager;

    public SwingGameView(GameController controller, GameBoard model) {
        super("2048 Game (Swing) – Animations in Front");
        this.controller = controller;
        this.model = model;
        this.config = controller.getConfig();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
    }

    @Override
    public void initializeView() {
        setLayout(new BorderLayout());

        createInfoPanel();
        createBoardPanel();
        setupKeyBindings();

        // If undo is enabled, add a bottom panel with the Undo button
        if (config.isUndoEnabled()) {
            createUndoButton();
        }

        // Start/Reset the game once we've built the UI
        controller.resetGame();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        // Ensures we get key events
        setFocusTraversalKeysEnabled(false);
        requestFocusInWindow();
    }

    /**
     * Displays the score, game status, and high score in a top info panel.
     */
    private void createInfoPanel() {
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        scoreLabel = new JLabel("Score: 0");
        statusLabel = new JLabel("Status: RUNNING");
        highScoreLabel = new JLabel("High Score: 0");

        infoPanel.add(scoreLabel);
        infoPanel.add(statusLabel);
        infoPanel.add(highScoreLabel);

        add(infoPanel, BorderLayout.NORTH);
    }

    /**
     * Builds a custom panel that contains:
     * 1) A grid of JLabels for showing tiles.
     * 2) Overridden paintChildren(...) to draw animations on top of child components.
     */
    private void createBoardPanel() {
        int rows = model.getYSize();
        int cols = model.getXSize();

        boardPanel = new BoardPanel();
        boardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        boardPanel.setLayout(new GridLayout(rows, cols, 5, 5));

        tileLabels = new JLabel[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                JLabel label = new JLabel("", SwingConstants.CENTER);
                label.setPreferredSize(new Dimension(TILE_SIZE_PX, TILE_SIZE_PX));
                label.setFont(new Font("Arial", Font.BOLD, TILE_FONT_SIZE));
                label.setOpaque(true);
                label.setBackground(Color.LIGHT_GRAY);

                tileLabels[r][c] = label;
                boardPanel.add(label);
            }
        }

        add(boardPanel, BorderLayout.CENTER);

        // Create an AnimationManager for tile sliding/merging
        animationManager = new AnimationManager(boardPanel);
    }

    /**
     * We override paintChildren(...) in BoardPanel so that all child components
     * (our tile JLabels) are painted first, then we draw animations **on top**.
     */
    private class BoardPanel extends JPanel {
        @Override
        protected void paintChildren(Graphics g) {
            super.paintChildren(g);
            // Now draw animations in front
            animationManager.drawAllAnimations((Graphics2D) g);
        }
    }

    /**
     * Sets up key bindings for arrows, WASD, and 'U' for undo.
     */
    private void setupKeyBindings() {
        final int condition = JComponent.WHEN_IN_FOCUSED_WINDOW;

        // Arrow keys
        bindKey(condition, KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0),    "moveUp",    Direction.UP);
        bindKey(condition, KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0),  "moveDown",  Direction.DOWN);
        bindKey(condition, KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0),  "moveLeft",  Direction.LEFT);
        bindKey(condition, KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "moveRight", Direction.RIGHT);

        // WASD
        bindKey(condition, KeyStroke.getKeyStroke('w'), "moveUpW",    Direction.UP);
        bindKey(condition, KeyStroke.getKeyStroke('s'), "moveDownS",  Direction.DOWN);
        bindKey(condition, KeyStroke.getKeyStroke('a'), "moveLeftA",  Direction.LEFT);
        bindKey(condition, KeyStroke.getKeyStroke('d'), "moveRightD", Direction.RIGHT);

        // Undo key (U)
        getRootPane().getInputMap(condition).put(KeyStroke.getKeyStroke('u'), "undo");
        getRootPane().getActionMap().put("undo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (config.isUndoEnabled()) {
                    controller.onUndo();
                }
            }
        });
    }

    private void bindKey(int condition, KeyStroke keystroke, String actionKey, Direction direction) {
        getRootPane().getInputMap(condition).put(keystroke, actionKey);
        getRootPane().getActionMap().put(actionKey, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.onUserMove(direction);
            }
        });
    }

    /**
     * Adds an Undo button at the bottom if undo is enabled.
     */
    private void createUndoButton() {
        JPanel bottomPanel = new JPanel(new FlowLayout());
        JButton undoButton = new JButton("Undo");
        undoButton.addActionListener(e -> controller.onUndo());
        bottomPanel.add(undoButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Displays the final layout of the board in each tileLabel.
     * Called after moves or after an animation completes.
     */
    @Override
    public void renderBoard(int[][] board) {
        int rows = board.length;
        int cols = board[0].length;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int val = board[r][c];
                JLabel label = tileLabels[r][c];
                if (val == 0) {
                    label.setText("");
                    label.setBackground(Color.LIGHT_GRAY);
                } else {
                    label.setText(String.valueOf(val));
                    label.setBackground(getTileColor(val));
                }
            }
        }
        repaint();
    }

    /**
     * Called by GameController to animate tile movements from old->new positions,
     * and then eventually call onAnimationsComplete.
     */
    @Override
    public void showAnimations(List<TileMovement> movements, Runnable onAnimationsComplete) {
        if (movements == null || movements.isEmpty()) {
            // If no tiles actually moved, just update immediately
            onAnimationsComplete.run();
            return;
        }

        // 1) Create TileAnimation objects for each movement
        for (TileMovement tm : movements) {
            // Convert row/col to pixel coordinates
            int startX = tm.oldCol * TILE_SIZE_PX;
            int startY = tm.oldRow * TILE_SIZE_PX;
            int endX = tm.newCol * TILE_SIZE_PX;
            int endY = tm.newRow * TILE_SIZE_PX;

            // Duration set to 300ms
            TileAnimation anim = new TileAnimation(
                    tm.value, startX, startY,
                    endX, endY,
                    300, // 300ms
                    tm.merged
            );
            animationManager.addAnimation(anim);
        }

        // 2) Use a Swing Timer to wait ~300ms, then finalize the move
        new Timer(300, e -> {
            ((Timer) e.getSource()).stop();

            // The animation is "complete" from the user's perspective
            // So we show the final board
            renderBoard(model.getBoard());

            // Then run whatever callback the controller gave us
            onAnimationsComplete.run();
        }).start();
    }

    @Override
    public void displayScore(int score) {
        scoreLabel.setText("Score: " + score);
    }

    @Override
    public void displayGameStatus(GameStatus status) {
        statusLabel.setText("Status: " + status);
    }

    /**
     * For a console-based view, we would poll input,
     * but Swing is event-driven. This method is effectively unused.
     */
    @Override
    public void captureUserMove() {
        // Not used in this Swing implementation
    }

    /**
     * Displays a dialog box announcing the final result.
     */
    @Override
    public void displayEndScreen(GameStatus finalStatus, int finalScore) {
        String message;
        if (finalStatus == GameStatus.WON) {
            message = "Congratulations! You reached " + config.getWinTileValue()
                    + ".\nFinal Score: " + finalScore;
        } else {
            message = "No more moves left. Game Over!\nFinal Score: " + finalScore;
        }
        JOptionPane.showMessageDialog(this, message, "Game Over", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Updates the high score label displayed at the top.
     */
    @Override
    public void updateHighScore(int highScore) {
        highScoreLabel.setText("High Score: " + highScore);
    }

    /**
     * Basic color lookup for tile backgrounds.
     */
    private Color getTileColor(int value) {
        switch (value) {
            case 2:    return new Color(0xeee4da);
            case 4:    return new Color(0xede0c8);
            case 8:    return new Color(0xf2b179);
            case 16:   return new Color(0xf59563);
            case 32:   return new Color(0xf67c5f);
            case 64:   return new Color(0xf65e3b);
            case 128:  return new Color(0xedcf72);
            case 256:  return new Color(0xedcc61);
            case 512:  return new Color(0xedc850);
            case 1024: return new Color(0xedc53f);
            case 2048: return new Color(0xedc22e);
            default:
                // For tiles > 2048 or unknown
                return new Color(0x3c3a32);
        }
    }
}
