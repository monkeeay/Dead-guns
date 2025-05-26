package com.example.roguelike.world;

import com.example.roguelike.entities.GameEntity;
import com.example.roguelike.entities.Player; // For instanceof check
import com.example.roguelike.entities.Enemy;  // For instanceof check
import java.awt.Color;

public class MapFeature {
    private int x;
    private int y;
    private MapFeatureType type;
    private char displayCharClosed;
    private char displayCharOpen;
    private Color color;
    private boolean isSolid; 
    // blocksMovement can be the same as isSolid for these features

    public MapFeature(int x, int y, MapFeatureType initialType, char charClosed, char charOpen, Color color) {
        this.x = x;
        this.y = y;
        this.type = initialType;
        this.displayCharClosed = charClosed;
        this.displayCharOpen = charOpen;
        this.color = color;
        updateSolidState();
    }

    private void updateSolidState() {
        switch (this.type) {
            case DOOR_CLOSED:
                this.isSolid = true;
                break;
            case DOOR_OPEN:
            case TRAP_ARMED:
            case TRAP_TRIGGERED:
                this.isSolid = false;
                break;
            default:
                this.isSolid = false;
        }
    }

    // Getters
    public int getX() { return x; }
    public int getY() { return y; }
    public MapFeatureType getType() { return type; }
    public Color getColor() { return color; }

    public char getCurrentDisplayChar() {
        switch (type) {
            case DOOR_CLOSED:
            case TRAP_ARMED: // Show armed state
                return displayCharClosed;
            case DOOR_OPEN:
            case TRAP_TRIGGERED: // Show triggered/aftermath state
                return displayCharOpen;
            default:
                return '?'; 
        }
    }

    public boolean isCurrentlySolid() {
        return isSolid;
    }

    public void interact(GameEntity entity) {
        switch (type) {
            case DOOR_CLOSED:
                this.type = MapFeatureType.DOOR_OPEN;
                updateSolidState();
                System.out.println("Door opens at (" + x + "," + y + ").");
                break;
            case TRAP_ARMED:
                this.type = MapFeatureType.TRAP_TRIGGERED;
                updateSolidState(); // isSolid remains false
                System.out.println("Trap triggered at (" + x + "," + y + ")!");
                if (entity instanceof Player) {
                    ((Player) entity).takeDamage(1); // Example damage
                     System.out.println("Player takes 1 damage from trap. Player health: " + ((Player)entity).getHealth());
                } else if (entity instanceof Enemy) {
                    // ((Enemy) entity).takeDamage(1); // Example damage - Commented out as Enemy.takeDamage() is not defined in current subtask
                    // System.out.println("Enemy takes 1 damage from trap. Enemy health: " + ((Enemy)entity).getHealth()); // Commented out as Enemy.getHealth() is not defined
                }
                break;
            case DOOR_OPEN:
            case TRAP_TRIGGERED:
                // No interaction
                break;
        }
    }
}
