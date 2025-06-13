package de.thiomains.jumpandrun;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class JaRData {

    private Player player;
    private int streak;
    private Location lastBlock;
    private Location currentBlock;
    private JaRBlockColor color;

    public JaRData(Player player) {
        this.player = player;
        this.streak = 0;
        int random = (int) Math.floor(Math.random()*JaRBlockColor.values().length);
        this.color = JaRBlockColor.values()[random];
    }

    public Player getPlayer() {
        return player;
    }

    public int getStreak() {
        return streak;
    }

    public void setStreak(int streak) {
        this.streak = streak;
    }

    public Location getLastBlock() {
        return lastBlock;
    }

    public void setLastBlock(Location lastBlock) {
        this.lastBlock = lastBlock;
    }

    public Location getCurrentBlock() {
        return currentBlock;
    }

    public void setCurrentBlock(Location currentBlock) {
        this.currentBlock = currentBlock;
    }

    public JaRBlockColor getColor() {
        return color;
    }

    public void setColor(JaRBlockColor color) {
        this.color = color;
    }
}

