package net.laboulangerie.laboulangeriemmo.core.combo;

import java.util.LinkedList;
import java.util.Queue;

import org.bukkit.scheduler.BukkitRunnable;

public class KeyStreak {
    private Queue<ComboKey> keyQueue = new LinkedList<ComboKey>();
    private BukkitRunnable previousTimer = null;

    public KeyStreak(ComboKey key1, ComboKey key2, ComboKey key3) {
        keyQueue.add(key1);
        keyQueue.add(key2);
        keyQueue.add(key3);
    }

    public KeyStreak() {
    }

    /**
     * @param key
     * @return true if the streak is full
     */
    public boolean addKey(ComboKey key) {
        keyQueue.add(key);
        if (keyQueue.size() > 3) {
            keyQueue.remove();
        }
        return keyQueue.size() >= 3;
    }

    /**
     * Used only when the period between key press matters
     * 
     * @param key
     * @param timer the runnable which will invalid this streak if the period
     *              between keys is too long
     * @return true if the streak is full
     */
    public boolean addKey(ComboKey key, BukkitRunnable timer) {
        // the previous timer is reset and replaced with the new one
        // the parent context should handle creating a new runner that will empty the
        // streak when expired
        if (previousTimer != null)
            previousTimer.cancel();
        previousTimer = timer;
        if (addKey(key)) {
            timer.cancel();
            return true;
        }
        return false;
    }

    /**
     * Test if <b>streak</b> matches this combo streak
     * 
     * @param streak
     * @return true if so
     */
    public boolean match(KeyStreak streak) {
        return keyQueue.equals(streak.getKeyQueue());
    }

    /**
     * Empty the key streak, called when the period between key press is to long,
     * this should be managed by the parent context
     */
    public void fail() {
        keyQueue.clear();
    }

    public Queue<ComboKey> getKeyQueue() {
        return keyQueue;
    }
}
