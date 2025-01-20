package town.lost.g2k.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Tests for the GameConfig class, focusing on xSize/ySize,
 * default values, spawn probabilities, and fallback for invalid sizes.
 */
class GameConfigTest {

    @Test
    @DisplayName("Default constructor sets 4x4, 2048, undo=false, animations=true.")
    void testDefaultConstructor() {
        GameConfig config = new GameConfig();

        assertEquals(4, config.getXSize(), "Default xSize = 4");
        assertEquals(4, config.getYSize(), "Default ySize = 4");
        assertEquals(2048, config.getWinTileValue(), "Default win tile=2048");
        assertFalse(config.isUndoEnabled(), "Undo default = false");
        assertTrue(config.isAnimationsEnabled(), "Animations default = true");
        assertEquals("highscore.txt", config.getHighScoreFilePath(), "Default highscore path");

        // Probabilities: 2->0.9, 4->0.1
        Map<Integer, Double> probs = config.getTileSpawnProbabilities();
        assertEquals(2, probs.size());
        assertEquals(0.9, probs.get(2), 0.0001);
        assertEquals(0.1, probs.get(4), 0.0001);
    }

    @Test
    @DisplayName("Setting board size below 2 reverts to 4x4, valid sizes remain as set.")
    void testSetBoardSize() {
        GameConfig config = new GameConfig();
        config.setBoardSize(1, 1); // invalid => fallback
        assertEquals(4, config.getXSize(), "Fallback to 4 if x<2");
        assertEquals(4, config.getYSize(), "Fallback to 4 if y<2");

        config.setBoardSize(6, 9);
        assertEquals(6, config.getXSize(), "Now 6 columns");
        assertEquals(9, config.getYSize(), "Now 9 rows");
    }

    @Test
    @DisplayName("Custom constructor assigns all fields properly.")
    void testCustomConstructor() {
        Map<Integer, Double> customMap = new HashMap<>();
        customMap.put(2, 0.75);
        customMap.put(4, 0.25);

        GameConfig config = new GameConfig(
                3, 7,        // x=3, y=7
                9999,        // win tile
                true,        // undo
                false,       // animations
                "myScores.txt",
                customMap
        );

        assertEquals(3, config.getXSize());
        assertEquals(7, config.getYSize());
        assertEquals(9999, config.getWinTileValue());
        assertTrue(config.isUndoEnabled());
        assertFalse(config.isAnimationsEnabled());
        assertEquals("myScores.txt", config.getHighScoreFilePath());

        Map<Integer, Double> spawn = config.getTileSpawnProbabilities();
        assertEquals(0.75, spawn.get(2), 0.0001);
        assertEquals(0.25, spawn.get(4), 0.0001);
    }

    @Test
    @DisplayName("Toggling undo and animations via setters.")
    void testUndoAndAnimationsToggles() {
        GameConfig config = new GameConfig();
        config.setUndoEnabled(true);
        config.setAnimationsEnabled(false);

        assertTrue(config.isUndoEnabled());
        assertFalse(config.isAnimationsEnabled());
    }
}
