package town.lost.g2k.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList; 
import java.util.Iterator;
import java.util.List;

/**
 * Manages a list of TileAnimation objects that animate tile movement or merging
 * in a Swing-based 2048 game. Uses a Swing Timer to update animations ~60 fps
 * and requests a repaint of the parent component each frame.
 */
public class AnimationManager {

    // The list of active animations (tiles in motion or merge highlighting)
    private final List<TileAnimation> animations;

    // A Swing timer that ticks every ~16 ms (~60 fps) to update animation frames
    private final Timer animationTimer;

    // A reference to the Swing component (e.g., panel) that draws these animations
    private final JComponent parentComponent;

    // Example default ~16ms for ~60fps
    private static final int FRAME_INTERVAL_MS = 16; 

    /**
     * Constructs a new AnimationManager for a given parent component.
     *
     * @param parentComponent the JComponent (panel/frame) that handles painting.
     */
    public AnimationManager(JComponent parentComponent) {
        this.parentComponent = parentComponent;
        this.animations = new ArrayList<>();

        // Create a Swing Timer that calls onTimerTick at ~60 fps
        this.animationTimer = new Timer(FRAME_INTERVAL_MS, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onTimerTick();
            }
        });
    }

    /**
     * Adds a new TileAnimation to the manager's list and ensures the timer is running.
     *
     * @param animation the TileAnimation to be added
     */
    public void addAnimation(TileAnimation animation) {
        animations.add(animation);
        // If the timer is not already running, start it.
        if (!animationTimer.isRunning()) {
            animationTimer.start();
        }
    }

    /**
     * Draws all active animations onto the Graphics2D context.
     * Call this from your panel's paintComponent() method.
     *
     * @param g2 the Graphics2D context to draw on.
     */
    public void drawAllAnimations(Graphics2D g2) {
        for (TileAnimation animation : animations) {
            animation.draw(g2);
        }
    }

    /**
     * The timer callback. Updates or removes animations that have finished,
     * then repaints the parent component.
     */
    private void onTimerTick() {
        boolean anyActive = false;

        // Use an iterator to remove completed animations in one pass
        Iterator<TileAnimation> iterator = animations.iterator();
        while (iterator.hasNext()) {
            TileAnimation anim = iterator.next();
            if (anim.isComplete()) {
                // Animation is done, remove it
                iterator.remove();
            } else {
                anyActive = true;
            }
        }

        // Request repaint so we draw the updated positions
        parentComponent.repaint();

        // If no animations remain active, stop the timer
        if (!anyActive) {
            animationTimer.stop();
        }
    }

    /**
     * Optional: Clear all animations immediately and stop the timer.
     */
    public void clearAnimations() {
        animations.clear();
        animationTimer.stop();
        parentComponent.repaint();
    }
}
