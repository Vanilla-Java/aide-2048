package town.lost.g2k.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds configuration settings for a 2048 game:
 *  - boardSize (NxN),
 *  - winTileValue (e.g., 2048),
 *  - tileSpawnProbabilities (2->0.9, 4->0.1, etc.),
 *  - undoEnabled,
 *  - animationsEnabled (optional),
 *  - highScoreFilePath (e.g., "highscores.txt").
 */
public class GameConfig {

    private int xSize, ySize;
    private int winTileValue;
    private boolean undoEnabled;
    private boolean animationsEnabled;
    private String highScoreFilePath;
    private Map<Integer, Double> tileSpawnProbabilities;

    /**
     * Default constructor for classic 4x4, 2048, single-step undo disabled, etc.
     */
    public GameConfig() {
        this.xSize = 4;
        this.ySize = 4;
        this.winTileValue = 2048;
        this.undoEnabled = false;
        this.animationsEnabled = true;
        this.highScoreFilePath = "highscore.txt";

        // Default spawn probabilities: 2->0.9, 4->0.1
        tileSpawnProbabilities = new HashMap<>();
        tileSpawnProbabilities.put(2, 0.9);
        tileSpawnProbabilities.put(4, 0.1);
    }

    /**
     * If you want a fully custom config.
     */
    public GameConfig(int xSize, int ySize, int winTileValue, boolean undoEnabled,
                      boolean animationsEnabled, String highScoreFilePath,
                      Map<Integer, Double> tileSpawnProbabilities) {
        this.xSize = xSize;
        this.ySize = ySize;
        this.winTileValue = winTileValue;
        this.undoEnabled = undoEnabled;
        this.animationsEnabled = animationsEnabled;
        this.highScoreFilePath = highScoreFilePath;
        this.tileSpawnProbabilities = tileSpawnProbabilities;
    }

    // ================================
    // Getters / Setters
    // ================================

    public int getXSize() {
        return xSize;
    }
    public int getYSize() {
        return ySize;
    }

    public void setBoardSize(int xSize, int ySize) {
        if (xSize < 2 || ySize < 2) {
            this.xSize = 4; // fallback
            this.ySize = 4; // fallback
        } else {
            this.xSize = xSize;
            this.ySize = ySize;
        }
    }

    public int getWinTileValue() {
        return winTileValue;
    }

    public void setWinTileValue(int winTileValue) {
        this.winTileValue = winTileValue;
    }

    public boolean isUndoEnabled() {
        return undoEnabled;
    }

    public void setUndoEnabled(boolean undoEnabled) {
        this.undoEnabled = undoEnabled;
    }

    public boolean isAnimationsEnabled() {
        return animationsEnabled;
    }

    public void setAnimationsEnabled(boolean animationsEnabled) {
        this.animationsEnabled = animationsEnabled;
    }

    public String getHighScoreFilePath() {
        return highScoreFilePath;
    }

    public void setHighScoreFilePath(String highScoreFilePath) {
        this.highScoreFilePath = highScoreFilePath;
    }

    public Map<Integer, Double> getTileSpawnProbabilities() {
        return tileSpawnProbabilities;
    }

    public void setTileSpawnProbabilities(Map<Integer, Double> tileSpawnProbabilities) {
        this.tileSpawnProbabilities = tileSpawnProbabilities;
    }
}
