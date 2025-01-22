package town.lost.g2k.view;

import java.awt.*;

/**
 * Represents a single tile’s animation state, transitioning from an old position
 * to a new position over a short duration. Typically used alongside a Timer or game loop
 * in a Swing-based 2048 implementation.
 * <p>
 * Note: This class assumes you have a visual representation of the tile’s (x, y) coordinates,
 * rather than just a row/column in the model. The actual 2D array (in GameBoard) only stores
 * integer values. So, this class is a “visual” helper, not part of the core logic.
 */
public class TileAnimation {

    // ================================
    // Animation State
    // ================================
    private final int startX;      // initial X coordinate (pixels)
    private final int startY;      // initial Y coordinate (pixels)
    private final int targetX;     // destination X coordinate (pixels)
    private final int targetY;     // destination Y coordinate (pixels)

    private final int tileValue;   // tile’s numeric value, e.g. 2, 4, 8, ...
    private final long startTime;  // time in ms when the animation started
    private final long duration;   // total animation time in ms (e.g., 300ms)

    // If you want merging effects, you might store a merge factor or highlight color
    private final boolean highlightMerge; // whether to “pop” the tile if it merged

    // ================================
    // Constructor
    // ================================
    /**
     * Creates a new TileAnimation for moving a tile from (startX, startY) to (targetX, targetY)
     * over a given duration. Optionally set highlightMerge if this tile was merged.
     *
     * @param tileValue      The numeric value of the tile (for drawing or color).
     * @param startX         Starting X coordinate in pixels.
     * @param startY         Starting Y coordinate in pixels.
     * @param targetX        Target X coordinate in pixels.
     * @param targetY        Target Y coordinate in pixels.
     * @param duration       Animation duration in milliseconds.
     * @param highlightMerge If true, we apply a “pop” effect when drawing.
     */
    public TileAnimation(int tileValue, int startX, int startY,
                         int targetX, int targetY, long duration,
                         boolean highlightMerge) {
        this.tileValue = tileValue;
        this.startX = startX;
        this.startY = startY;
        this.targetX = targetX;
        this.targetY = targetY;
        this.duration = duration;
        this.highlightMerge = highlightMerge;

        this.startTime = System.currentTimeMillis();
    }

    // ================================
    // Public Methods
    // ================================

    /**
     * Returns the current X coordinate (pixels) based on elapsed time.
     */
    public int getCurrentX() {
        double progress = getProgress();
        return (int) (startX + (targetX - startX) * progress);
    }

    /**
     * Returns the current Y coordinate (pixels) based on elapsed time.
     */
    public int getCurrentY() {
        double progress = getProgress();
        return (int) (startY + (targetY - startY) * progress);
    }

    /**
     * Returns true if the animation is completed or if it’s at 100% progress.
     */
    public boolean isComplete() {
        return getProgress() >= 1.0;
    }

    /**
     * Draws the tile at its current position. You would typically call this
     * from a custom Swing component’s paint method or a panel that handles tile drawing.
     *
     * @param g The Graphics context.
     */
    public void draw(Graphics2D g) {
        // For example, draw a rectangle and tile value text.
        int x = getCurrentX();
        int y = getCurrentY();
        int sizePx = 80; // e.g., tile size in pixels
        Color tileColor = Tiles.getTileColor(tileValue);

        g.setColor(tileColor);
        g.fillRoundRect(x, y, sizePx, sizePx, 10, 10);

        // If highlightMerge is true, we could draw a border or do a quick scale “pop”
        if (highlightMerge) {
            g.setColor(Color.WHITE);
            g.setStroke(new BasicStroke(2f));
            g.drawRoundRect(x, y, sizePx, sizePx, 10, 10);
        }

        // Draw the tile’s value in the center
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        FontMetrics fm = g.getFontMetrics();
        String text = String.valueOf(tileValue);
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent();

        int textX = x + (sizePx - textWidth) / 2;
        int textY = y + (sizePx + textHeight) / 2 - 4; // small offset
        g.drawString(text, textX, textY);
    }

    // ================================
    // Private Helpers
    // ================================

    /**
     * 0.0 = just started, 1.0 = fully reached the target.
     */
    private double getProgress() {
        long now = System.currentTimeMillis();
        long elapsed = now - startTime;
        if (elapsed >= duration) {
            return 1.0;
        }
        return (double) elapsed / duration;
    }
}
