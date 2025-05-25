package com.example.roguelike.entities;

import com.example.roguelike.world.GameMap;
import com.example.roguelike.rendering.ProceduralSpriteData; // Added
import com.example.roguelike.rendering.SpriteGenerator; // Added
import java.awt.Color;

public class Enemy implements GameEntity {
    private int x; // tile coordinate
    private int y; // tile coordinate
    private long spriteSeed;
    private ProceduralSpriteData spriteData; // Added
    private Player player; // Reference to the player for AI
    private Color baseColorHint; 
    private int health; 
    private static final int GRUNT_ATTACK_POWER = 10; 
    // Scout does not attack in this version.
    private int experienceValue; // Added
    
    private EnemyType type; // Added field for enemy type

    public Enemy(int x, int y, long seed, Player player, EnemyType type) { // Updated constructor
        this.x = x;
        this.y = y;
        this.spriteSeed = seed;
        this.player = player;
        this.type = type;

        if (type == EnemyType.SCOUT) {
            this.health = 15;
            this.baseColorHint = Color.CYAN;
            this.experienceValue = 15; // Scout XP
        } else { // GRUNT or default
            this.health = 20;
            this.baseColorHint = Color.RED;
            this.experienceValue = 10; // Grunt XP
        }
        this.spriteData = SpriteGenerator.generateEnemySprite(this.spriteSeed, this.type, this.baseColorHint); // Initialize spriteData
    }
    
    public ProceduralSpriteData getSpriteData() { // Added getter
        return spriteData;
    }
    
    public int getExperienceValue() { // Added getter
        return experienceValue;
    }

    public EnemyType getType() { // Added getter
        return type;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    public long getSpriteSeed() {
        return spriteSeed;
    }

    public Color getBaseColorHint() {
        return baseColorHint;
    }

    @Override
    public int getHealth() {
        return health;
    }

    @Override
    public void takeDamage(int amount) {
        this.health -= amount;
        if (this.health < 0) {
            this.health = 0;
        }
        System.out.println("Enemy takes " + amount + " damage. Enemy health: " + this.health);
    }

    @Override
    public boolean isAlive() {
        return this.health > 0;
    }

    @Override
    public void update(GameMap map) {
        if (this.player == null || map == null || !this.isAlive() || !this.player.isAlive()) {
            return; 
        }

        int dx = this.player.getX() - this.x;
        int dy = this.player.getY() - this.y;
        double distanceToPlayer = Math.sqrt(dx * dx + dy * dy);

        if (this.type == EnemyType.GRUNT) {
            int detectionRadius = 8;
            if (distanceToPlayer < detectionRadius) {
                int moveX = 0;
                int moveY = 0;

                if (Math.abs(dx) > Math.abs(dy)) {
                    moveX = Integer.signum(dx);
                } else {
                    moveY = Integer.signum(dy);
                }

                int targetX = this.x + moveX;
                int targetY = this.y + moveY;

                if (targetX == this.player.getX() && targetY == this.player.getY()) {
                    System.out.println("Grunt attacks player!");
                    this.player.takeDamage(GRUNT_ATTACK_POWER);
                    return; 
                }

                if (map.isWalkable(targetX, targetY)) {
                    this.x = targetX;
                    this.y = targetY;
                } else if (moveX != 0 && dy != 0) { 
                    moveX = 0; 
                    moveY = Integer.signum(dy);
                    targetY = this.y + moveY; // Update targetY for the new move
                    if (map.isWalkable(this.x, targetY)) {
                        if (this.x == this.player.getX() && targetY == this.player.getY()) {
                             System.out.println("Grunt attacks player!");
                             this.player.takeDamage(GRUNT_ATTACK_POWER);
                        } else {
                            this.y = targetY;
                        }
                    }
                } else if (moveY != 0 && dx != 0) { 
                    moveY = 0; 
                    moveX = Integer.signum(dx);
                    targetX = this.x + moveX; // Update targetX for the new move
                    if (map.isWalkable(targetX, this.y)) {
                        if (targetX == this.player.getX() && this.y == this.player.getY()) {
                            System.out.println("Grunt attacks player!");
                            this.player.takeDamage(GRUNT_ATTACK_POWER);
                        } else {
                             this.x = targetX;
                        }
                    }
                }
            }
        } else if (this.type == EnemyType.SCOUT) {
            int detectionRadius = 10;
            int preferredDistance = 5;
            int moveX = 0;
            int moveY = 0;

            if (distanceToPlayer < detectionRadius) {
                if (distanceToPlayer > preferredDistance) { // Too far, move closer
                    if (Math.abs(dx) > Math.abs(dy)) { moveX = Integer.signum(dx); } 
                    else { moveY = Integer.signum(dy); }
                } else if (distanceToPlayer < preferredDistance - 1) { // Too close, move away
                    moveX = -Integer.signum(dx);
                    moveY = -Integer.signum(dy); 
                } else { // In preferred range, do nothing or move randomly (optional)
                    return;
                }

                int nextX = this.x + moveX;
                int nextY = this.y + moveY;

                if (map.isWalkable(nextX, nextY) && !(nextX == player.getX() && nextY == player.getY())) {
                    this.x = nextX;
                    this.y = nextY;
                } else if (distanceToPlayer < preferredDistance -1) { // Retreat is blocked, try perpendicular
                    // Try perpendicular 1
                    int perpMoveX1 = -Integer.signum(dy); 
                    int perpMoveY1 = Integer.signum(dx);
                    if (perpMoveX1 == 0 && perpMoveY1 == 0 && dx!=0) { // if dy is 0, signum(dy) is 0, use dx for perp
                        perpMoveX1 = 0; perpMoveY1 = (dx > 0 ? 1: -1) ;
                    } else if (perpMoveX1 == 0 && perpMoveY1 == 0 && dy!=0) { // if dx is 0, signum(dx) is 0, use dy for perp
                        perpMoveX1 = (dy > 0 ? 1: -1); perpMoveY1 = 0;
                    }


                    if (map.isWalkable(this.x + perpMoveX1, this.y + perpMoveY1) && !((this.x + perpMoveX1) == player.getX() && (this.y + perpMoveY1) == player.getY())) {
                        this.x += perpMoveX1;
                        this.y += perpMoveY1;
                    } else { // Try perpendicular 2
                        int perpMoveX2 = Integer.signum(dy);
                        int perpMoveY2 = -Integer.signum(dx);
                         if (perpMoveX2 == 0 && perpMoveY2 == 0 && dx!=0) {
                             perpMoveX2 = 0; perpMoveY2 = (dx > 0 ? -1: 1) ;
                         } else if (perpMoveX2 == 0 && perpMoveY2 == 0 && dy!=0) {
                             perpMoveX2 = (dy > 0 ? -1: 1); perpMoveY2 = 0;
                         }

                        if (map.isWalkable(this.x + perpMoveX2, this.y + perpMoveY2) && !((this.x + perpMoveX2) == player.getX() && (this.y + perpMoveY2) == player.getY())) {
                            this.x += perpMoveX2;
                            this.y += perpMoveY2;
                        }
                    }
                }
            }
        }
    }
}
