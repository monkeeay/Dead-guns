package com.example.roguelike.entities;

import com.example.roguelike.world.GameMap;
import com.example.roguelike.rendering.GameRenderer;
import com.example.roguelike.rendering.ProceduralSpriteData; // Added
import com.example.roguelike.rendering.SpriteGenerator; // Added
import com.example.roguelike.items.Item;
import com.example.roguelike.items.ItemType;
// import com.example.roguelike.items.ItemEffect; // Old system, to be removed
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

public class Player implements GameEntity {
    // Player size constants (assuming grid units for now, or TILE_SIZE if pixel units)
    // The prompt says GameRenderer uses them like Player.PLAYER_WIDTH.
    // If these are pixel dimensions, they should probably be TILE_SIZE.
    // If they are grid dimensions (e.g. player occupies 1x1 cell), it'd be 1.
    // Given GameRenderer draws player in a TILE_SIZE box, let's make these TILE_SIZE.
    public static final int PLAYER_WIDTH = GameRenderer.TILE_WIDTH; 
    public static final int PLAYER_HEIGHT = GameRenderer.TILE_HEIGHT;

    // x and y are now grid coordinates
    private int x;
    private int y;
    // private PlayerAppearance appearance; // Replaced by simpler spriteSeed system
    private long spriteSeed; 
    private ProceduralSpriteData spriteData; // Added
    private int health;
    private int maxHealth;
    // private int attackPower; // Replaced by baseAttackPower + weapon
    // private int defense; // Replaced by baseDefense + armor

    private final int baseAttackPower = 5; 
    private final int baseDefense = 0;    
    private Item equippedWeapon;
    private Item equippedArmor;
    private List<Item> consumablesInventory = new ArrayList<>(); // Added
    
    private int experiencePoints = 0; 
    private int currentLevel = 1; 
    private int experienceToNextLevel = 100; 
    private boolean movementDelayed = false; 
    private boolean justDamaged = false; // Added for damage flash

    public Player(int gridX, int gridY) {
        this.x = gridX;
        this.y = gridY;
        this.spriteSeed = 12345L; 
        this.maxHealth = 100; 
        this.health = this.maxHealth; 
        this.spriteData = SpriteGenerator.generatePlayerSprite(this.spriteSeed); // Initialize spriteData
        // attackPower and defense are now dynamic
    }

    public ProceduralSpriteData getSpriteData() { // Added getter
        return spriteData;
    }

    // Getter for baseDefense (as specified)
    public int getBaseDefense() { 
        return baseDefense;
    }

    // Getters for equipped items (already existed, confirmed)
    public Item getEquippedWeapon() { 
        return equippedWeapon;
    }

    public Item getEquippedArmor() { 
        return equippedArmor;
    }
    
    public void pickUpItem(Item item, List<String> gameMessages) { 
        if (item.getType() == ItemType.WEAPON_SWORD) {
            if (equippedWeapon != null) gameMessages.add("You dropped " + equippedWeapon.getName() + ".");
            equippedWeapon = item;
            gameMessages.add("You equipped " + item.getName() + " (Attack +" + item.getAttackBonus() + ").");
            System.out.println("SFX: Item_Pickup"); // Sound Cue
        } else if (item.getType() == ItemType.ARMOR_SHIELD) {
            if (equippedArmor != null) gameMessages.add("You dropped " + equippedArmor.getName() + ".");
            equippedArmor = item;
            gameMessages.add("You equipped " + item.getName() + " (Defense +" + item.getDefenseBonus() + ").");
            System.out.println("SFX: Item_Pickup"); // Sound Cue
        } else if (item.getType() == ItemType.CONSUMABLE_HEALTH_POTION) {
            consumablesInventory.add(item); 
            gameMessages.add("You picked up a " + item.getName() + ".");
            System.out.println("SFX: Item_Pickup"); // Sound Cue
        }
    }

    public void useHealthPotion(List<String> gameMessages) {
        Item potionToUse = null;
        for (Item item : consumablesInventory) {
            if (item.getType() == ItemType.CONSUMABLE_HEALTH_POTION) {
                potionToUse = item;
                break;
            }
        }
        if (potionToUse != null) {
            if (this.health < this.maxHealth) {
                this.health += potionToUse.getHealingAmount();
                if (this.health > this.maxHealth) {
                    this.health = this.maxHealth;
                }
                consumablesInventory.remove(potionToUse);
                gameMessages.add("You drank a " + potionToUse.getName() + ", healing " + potionToUse.getHealingAmount() + " HP.");
                System.out.println("SFX: Player_Heal"); // Sound Cue
            } else {
                gameMessages.add("You are already at full health.");
            }
        } else {
            gameMessages.add("You have no health potions.");
        }
    }

    public void addExperience(int amount, List<String> gameMessages) {
        if (!isAlive()) return; 
        this.experiencePoints += amount;
        gameMessages.add("You gained " + amount + " XP.");
        while (this.experiencePoints >= this.experienceToNextLevel) {
            this.experiencePoints -= this.experienceToNextLevel;
            levelUp(gameMessages);
        }
    }

    private void levelUp(List<String> gameMessages) {
        this.currentLevel++;
        this.maxHealth += 20; 
        this.health = this.maxHealth; 
        // this.baseAttackPower += 2; // Optionally increase base attack
        this.experienceToNextLevel = (int)(this.experienceToNextLevel * 1.5); 
        gameMessages.add("You reached Level " + this.currentLevel + "! Max HP increased. You are fully healed.");
    }

    // Getters for progression
    public int getCurrentLevel() { return currentLevel; }
    public int getExperiencePoints() { return experiencePoints; }
    public int getExperienceToNextLevel() { return experienceToNextLevel; }
    public List<Item> getConsumablesInventory() { return consumablesInventory; } // For count or direct access

    // Removed old useItem method as it's incompatible with the new item system.

    @Override
    public int getHealth() {
        return health;
    }

    // getMaxHealth might be useful for UI, keeping it.
    public int getMaxHealth() {
        return maxHealth;
    }

    // getAttackPower now calculates based on base and weapon
    public int getAttackPower() {
        int totalAttack = baseAttackPower;
        if (equippedWeapon != null) {
            totalAttack += equippedWeapon.getAttackBonus();
        }
        return totalAttack;
    }

    @Override
    public void takeDamage(int amount) {
        int actualDamage = amount;
        int currentDefense = baseDefense;
        if (equippedArmor != null) {
            currentDefense += equippedArmor.getDefenseBonus();
        }
        actualDamage -= currentDefense;
        if (actualDamage < 0) actualDamage = 0;
        
        if (actualDamage > 0) { // Only set damaged if actual damage was taken
            this.health -= actualDamage;
            if (this.health < 0) this.health = 0;
            setJustDamaged(true);
            System.out.println("SFX: Player_Damaged"); // Sound Cue
        }
        // Console message for damage taken can be added in GameManager or here if needed
    }

    @Override
    public void setJustDamaged(boolean damaged) {
        this.justDamaged = damaged;
    }

    @Override
    public boolean wasJustDamaged() {
        return justDamaged;
    }

    // getSpriteSeed, getX, getY, move, isAlive, update methods remain from previous steps.
    // public PlayerAppearance getAppearance() { // Old system
    //     return appearance;
    // }

    public boolean isMovementDelayed() { // Added
        return movementDelayed;
    }

    public void setMovementDelayed(boolean delayed) { // Added
        this.movementDelayed = delayed;
    }
    
    public long getSpriteSeed() { 
        return spriteSeed;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    // dx and dy are grid steps (e.g., 1, 0, -1)
    public void move(int dx, int dy, GameMap map, List<Enemy> enemies, List<String> gameMessages) {
        if (this.movementDelayed) {
            this.movementDelayed = false; // Use up the delay
            if (gameMessages != null) gameMessages.add("You are slowed by water and skip your move.");
            return; // Skip this move attempt
        }

        int newX = this.x + dx;
        int newY = this.y + dy;

        com.example.roguelike.world.Tile targetTile = map.getTile(newX, newY); 

        // Check for door at the target location first
        if (targetTile != null && targetTile.getType() == com.example.roguelike.world.TileType.DOOR_CLOSED) {
            targetTile.setType(com.example.roguelike.world.TileType.DOOR_OPEN);
            if (gameMessages != null) gameMessages.add("You opened a door.");
            System.out.println("SFX: Door_Open"); // Sound Cue
            return; 
        }

        // Check for enemy at the target location
        for (Enemy enemy : enemies) {
            if (enemy.getX() == newX && enemy.getY() == newY && enemy.isAlive()) {
                if (gameMessages != null) gameMessages.add("Player attacks " + enemy.getType().toString().toLowerCase() + "!");
                System.out.println("SFX: Player_Attack_Hit"); // Sound Cue
                enemy.takeDamage(getAttackPower()); 
                return; 
            }
        }

        // If no door and no enemy, proceed with normal movement
        if (map.isWalkable(newX, newY)) {
            this.x = newX;
            this.y = newY;

            // Apply effects of the new tile AFTER moving
            com.example.roguelike.world.Tile newTileSteppedOn = map.getTile(this.x, this.y); // Re-fetch for safety, though targetTile should be the same
            if (newTileSteppedOn != null) {
                if (newTileSteppedOn.getType() == com.example.roguelike.world.TileType.TRAP_HIDDEN) {
                    int damage = newTileSteppedOn.getDamageOnStep(); // Get damage before reveal (which sets it to 0)
                    takeDamage(damage);
                    if (gameMessages != null) gameMessages.add("You stepped on a trap! Took " + damage + " damage.");
                    newTileSteppedOn.revealTrap(); 
                }
                if (newTileSteppedOn.slowsMovement()) {
                    this.movementDelayed = true;
                    if (gameMessages != null) gameMessages.add("You are slowed by water.");
                }
            }
        } else {
            if (gameMessages != null) gameMessages.add("You can't move there.");
        }
    }

    @Override
    public boolean isAlive() {
        return this.health > 0;
    }

    // getHealth() and takeDamage(int amount) are already implemented from previous context

    @Override
    public void update(GameMap map) { // Updated signature
        // Placeholder for future player-specific logic per game tick
        // Player-specific updates could go here, e.g., passive regeneration, checking for status effects, etc.
        // For now, it's empty as per the task description.
    }

    // render(Graphics g) method removed as per GameEntity interface and task description
}
