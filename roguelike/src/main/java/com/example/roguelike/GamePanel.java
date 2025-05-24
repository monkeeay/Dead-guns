package com.example.roguelike;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Color;

public class GamePanel extends JPanel {
    private Player player;
    private Room room;

    public GamePanel(Player player, Room room) {
        this.player = player;
        this.room = room;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Call superclass method first

        // Draw the room
        if (room != null) {
            g.setColor(Color.GRAY);
            g.drawRect(room.getX(), room.getY(), room.getWidth(), room.getHeight());
        }

        // Draw the player
        if (player != null) {
            g.setColor(Color.BLUE);
            // Assuming player X, Y are top-left coordinates
            g.fillRect(player.getX(), player.getY(), Player.PLAYER_WIDTH, Player.PLAYER_HEIGHT);
        }
    }
}
