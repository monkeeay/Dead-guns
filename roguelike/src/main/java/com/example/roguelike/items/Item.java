package com.example.roguelike.items;

import java.util.Random;

public class Item {
    private ItemType type;
    private String name;
    private char symbol; // For map display
    private int attackBonus;
    private int defenseBonus;
    private int healingAmount; // Added for health potions
    
    private int x; // Tile coordinate for item's location on map
    private int y;

    public Item(ItemType type, int x, int y) {
        this.type = type;
        this.x = x;
        this.y = y;
        generateProperties();
    }
    
    private void generateProperties() {
        Random random = new Random();
        switch (type) {
            case WEAPON_SWORD:
                this.name = "Sword";
                this.symbol = '/';
                this.attackBonus = random.nextInt(5) + 1; // +1 to +5 attack
                this.defenseBonus = 0;
                break;
            case ARMOR_SHIELD:
                this.name = "Shield";
                this.symbol = ']';
                this.defenseBonus = random.nextInt(3) + 1; // +1 to +3 defense
                this.attackBonus = 0;
                this.healingAmount = 0;
                break;
            case CONSUMABLE_HEALTH_POTION:
                this.name = "Health Potion";
                this.symbol = '!'; 
                this.attackBonus = 0;
                this.defenseBonus = 0;
                this.healingAmount = 25 + random.nextInt(26); // Heals for 25-50 HP
                break;
            default:
                this.name = "Unknown Item";
                this.symbol = '?';
                this.attackBonus = 0;
                this.defenseBonus = 0;
                break;
        }
    }

    public ItemType getType() { return type; }
    public String getName() { return name; }
    public char getSymbol() { return symbol; }
    public int getAttackBonus() { return attackBonus; }
    public int getDefenseBonus() { return defenseBonus; }
    public int getHealingAmount() { return healingAmount; } // Added getter
    public int getX() { return x; }
    public int getY() { return y; }
}
