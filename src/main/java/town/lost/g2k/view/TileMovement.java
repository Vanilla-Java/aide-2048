// =========================================
// =========== TileMovement.java ===========
// =========================================
package town.lost.g2k.view;

/**
 * Represents a single tile's old -> new position for animation.
 */
public class TileMovement {
    public final int value;
    public final int oldRow;
    public final int oldCol;
    public final int newRow;
    public final int newCol;
    public final boolean merged;

    public TileMovement(int value, int oldRow, int oldCol,
                        int newRow, int newCol, boolean merged) {
        this.value = value;
        this.oldRow = oldRow;
        this.oldCol = oldCol;
        this.newRow = newRow;
        this.newCol = newCol;
        this.merged = merged;
    }
}
