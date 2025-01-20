package town.lost.g2k.view;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Tests the TileAnimation class for animation progress,
 * start/end positions, and basic "merge highlight" logic.
 */
class TileAnimationTest {

    @Test
    void testProgressAndCompletion() throws InterruptedException {
        // Create an animation of 200ms from (0,0) to (80,80)
        TileAnimation anim = new TileAnimation(
                2, 0, 0,    // tileValue=2, startX=0, startY=0
                80, 80,     // targetX=80, targetY=80
                200,        // duration=200ms
                false       // highlightMerge=false
        );

        // Immediately after creation
        assertEquals(0, anim.getCurrentX());
        assertEquals(0, anim.getCurrentY());
        assertFalse(anim.isComplete(), "Should not be complete yet.");

        // Wait ~half the duration
        Thread.sleep(100);
        int midX = anim.getCurrentX();
        int midY = anim.getCurrentY();
        assertTrue(midX > 0 && midX < 80, "Should be mid-way in X after 100ms.");
        assertTrue(midY > 0 && midY < 80, "Should be mid-way in Y after 100ms.");
        assertFalse(anim.isComplete(), "Still not complete at 100ms.");

        // Wait the remainder
        Thread.sleep(120);
        assertEquals(80, anim.getCurrentX(), "Reached final X");
        assertEquals(80, anim.getCurrentY(), "Reached final Y");
        assertTrue(anim.isComplete(), "Animation complete after 200ms total.");
    }

    @Test
    void testHighlightMergeFlag() {
        // Basic check for highlight = true
        TileAnimation anim = new TileAnimation(4, 0, 0, 0, 0, 100, true);
        // We can't directly test visuals, but can confirm the constructor sets the flag:
        // In a real scenario, you'd verify drawing code, but here we just confirm no crash.
        assertFalse(anim.isComplete());
    }
}
