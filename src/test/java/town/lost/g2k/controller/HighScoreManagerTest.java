package town.lost.g2k.controller;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import town.lost.g2k.model.GameConfig;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Tests for HighScoreManager, verifying dimension-based
 * records (key = xSize*10 + ySize) load/save correctly.
 */
class HighScoreManagerTest {

    private GameConfig config;
    private HighScoreManager manager;
    private File tempFile;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = File.createTempFile("testHighScore", ".txt");
        tempFile.deleteOnExit();

        config = new GameConfig();
        config.setHighScoreFilePath(tempFile.getAbsolutePath());

        manager = new HighScoreManager(config);
    }

    @Test
    @DisplayName("Load returns 0 if file not present or empty.")
    void testLoadNoFile() {
        // Delete the temp file
        tempFile.delete();
        manager.loadHighScores();
        // Suppose dimensionKey=44 => default
        assertEquals(0, manager.getHighScoreFor(44));
    }

    @Test
    @DisplayName("Set and save high scores, then load them back.")
    void testSaveAndLoad() {
        manager.setHighScoreFor(44, 200);
        manager.setHighScoreFor(33, 300);
        manager.saveHighScores();

        HighScoreManager another = new HighScoreManager(config);
        another.loadHighScores();

        assertEquals(200, another.getHighScoreFor(44));
        assertEquals(300, another.getHighScoreFor(33));
    }

    @Test
    @DisplayName("Corrupted lines are skipped.")
    void testCorruptedLine() throws IOException {
        // Write an invalid line + a valid line
        Files.write(tempFile.toPath(), "InvalidLine\n44=999\n".getBytes());

        manager.loadHighScores();
        assertEquals(999, manager.getHighScoreFor(44));
        assertEquals(0, manager.getHighScoreFor(33));
    }
}
