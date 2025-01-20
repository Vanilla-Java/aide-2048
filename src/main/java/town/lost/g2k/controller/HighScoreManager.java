package town.lost.g2k.controller;

import town.lost.g2k.model.GameConfig;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages separate high scores for each board size.
 * Stores them in a single file "size=score" lines.
 */
public class HighScoreManager {

    private final GameConfig config;
    private final Map<Integer, Integer> sizeToScoreMap;

    public HighScoreManager(GameConfig config) {
        this.config = config;
        this.sizeToScoreMap = new HashMap<>();
    }

    public void loadHighScores() {
        String filePath = config.getHighScoreFilePath();
        File file = new File(filePath);

        if (!file.exists()) {
            return; // no file => no records
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            sizeToScoreMap.clear();
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split("=");
                if (parts.length == 2) {
                    try {
                        int sizeKey = Integer.parseInt(parts[0]);
                        int scoreVal = Integer.parseInt(parts[1]);
                        sizeToScoreMap.put(sizeKey, scoreVal);
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid line: " + line);
                    }
                } else {
                    System.err.println("Malformed line: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading high scores from: " + filePath);
        }
    }

    public void saveHighScores() {
        String filePath = config.getHighScoreFilePath();
        File file = new File(filePath);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Map.Entry<Integer, Integer> entry : sizeToScoreMap.entrySet()) {
                writer.write(entry.getKey() + "=" + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing high scores to: " + filePath);
        }
    }

    public int getHighScoreFor(int boardSize) {
        return sizeToScoreMap.getOrDefault(boardSize, 0);
    }

    public void setHighScoreFor(int boardSize, int newScore) {
        int current = sizeToScoreMap.getOrDefault(boardSize, 0);
        if (newScore > current) {
            sizeToScoreMap.put(boardSize, newScore);
        }
    }
}
