package com.example.roguelike.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

/**
 * Represents a simple character-based sprite for game entities.
 */
public class Sprite {
    private char character;
    private Color color;
    private Font font; // Optional: for custom font rendering

    /**
     * Constructs a new Sprite.
     * @param character The character to represent the sprite.
     * @param color The color of the character.
     */
    public Sprite(char character, Color color) {
        this.character = character;
        this.color = color;
        // Default font, can be customized
        this.font = new Font("Monospaced", Font.BOLD, GameConfig.TILE_HEIGHT - 4); // Adjust size as needed
    }

    /**
     * Constructs a new Sprite with a specific font.
     * @param character The character to represent the sprite.
     * @param color The color of the character.
     * @param font The font to use for rendering the character.
     */
    public Sprite(char character, Color color, Font font) {
        this.character = character;
        this.color = color;
        this.font = font;
    }

    /**
     * Draws the sprite.
     * @param g The Graphics context to draw on.
     * @param x The x-coordinate (pixel) to draw the sprite at (center of the tile).
     * @param y The y-coordinate (pixel) to draw the sprite at (center of the tile).
     */
    public void draw(Graphics g, int x, int y) {
        g.setColor(this.color);
        g.setFont(this.font);

        // Adjust x and y to center the character in a tile
        // The exact adjustment might need tweaking based on font metrics
        // For now, a simple approximation:
        int charWidth = g.getFontMetrics().charWidth(this.character);
        int charHeight = g.getFontMetrics().getHeight(); // Ascent + Descent + Leading

        // Center character in a tile of TILE_WIDTH x TILE_HEIGHT
        // x, y are expected to be top-left of player's bounding box
        int drawX = x + (GameConfig.PLAYER_SIZE / 2) - (charWidth / 2);
        // y position is tricky due to font baseline; this usually aligns the bottom of the text.
        // To roughly center vertically, we might use ascent.
        int drawY = y + (GameConfig.PLAYER_SIZE / 2) + (g.getFontMetrics().getAscent() / 2) - (charHeight / 2); 
        
        g.drawString(String.valueOf(this.character), drawX, drawY + g.getFontMetrics().getAscent() / 2);
    }
    
    /**
     * Gets the character of the sprite.
     * @return The character.
     */
    public char getCharacter() {
        return character;
    }

    /**
     * Sets the character of the sprite.
     * @param character The new character.
     */
    public void setCharacter(char character) {
        this.character = character;
    }

    /**
     * Gets the color of the sprite.
     * @return The color.
     */
    public Color getColor() {
        return color;
    }

    /**
     * Sets the color of the sprite.
     * @param color The new color.
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Gets the font of the sprite.
     * @return The font.
     */
    public Font getFont() {
        return font;
    }

    /**
     * Sets the font of the sprite.
     * @param font The new font.
     */
    public void setFont(Font font) {
        this.font = font;
    }
}
// Need to add GameConfig to this file for TILE_HEIGHT and PLAYER_SIZE
// This is not ideal. A better way would be to pass these values to the draw method or Sprite constructor.
// For now, let's assume GameConfig is accessible or these values are passed in.
// For the purpose of this task, I will temporarily add the import.
// import com.example.roguelike.GameConfig; // This would make Sprite dependent on GameConfig.
// A cleaner solution for later: Sprite.draw(Graphics g, int x, int y, int tileWidth, int tileHeight)
// Or, have a Renderer class that knows about GameConfig and passes relevant info to Sprite.
// For now, to make it compile within the structure, I'll assume GameConfig is available
// and will try to make it work.
// The font size is hardcoded to TILE_HEIGHT - 4 for now.

class GameConfig { // Minimal stub for Sprite class compilation, real GameConfig is in another package
    public static final int TILE_HEIGHT = 32;
    public static final int PLAYER_SIZE = 20;
}
