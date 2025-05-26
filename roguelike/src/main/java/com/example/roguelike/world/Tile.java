package com.example.roguelike.world;

import java.util.Random; // Added for trap damage

public class Tile {
    private TileType type;
    private boolean slowsMovement = false; // Added
    private int damageOnStep = 0; // Added

    public Tile(TileType type) {
        this.type = type;
        if (type == TileType.WATER) {
            this.slowsMovement = true;
        }
        if (type == TileType.TRAP_HIDDEN) {
            // Trap deals 10-20 damage. Random instance should be created here or passed.
            this.damageOnStep = 10 + new Random().nextInt(11); 
        }
    }

    public TileType getType() {
        return type;
    }

    public boolean isWalkable() {
        return type == TileType.FLOOR || type == TileType.DOOR_OPEN ||
               type == TileType.TREASURE_FLOOR || type == TileType.WATER ||
               type == TileType.TRAP_HIDDEN || type == TileType.TRAP_REVEALED;
    }

    public boolean isDoor() {
        return type == TileType.DOOR_CLOSED || type == TileType.DOOR_OPEN;
    }

    public void setType(TileType newType) {
        this.type = newType;
        // Reset effects if type changes away from an effect-inducing type
        if (this.type != TileType.WATER) {
            this.slowsMovement = false;
        }
        // If it's no longer a hidden trap, damage should be 0 (or handled by revealTrap)
        if (this.type != TileType.TRAP_HIDDEN && this.type != TileType.TRAP_REVEALED) {
             // Damage is set to 0 by revealTrap. If setType is used to change from TRAP_HIDDEN
             // to something else directly, damage might remain. For now, rely on revealTrap.
        }
    }

    // Getters for effects
    public boolean slowsMovement() { 
        return slowsMovement; 
    }

    public int getDamageOnStep() { 
        return damageOnStep; 
    }

    public void revealTrap() {
        if (type == TileType.TRAP_HIDDEN) {
            setType(TileType.TRAP_REVEALED);
            this.damageOnStep = 0; // Trap triggers once
        }
    }
}
