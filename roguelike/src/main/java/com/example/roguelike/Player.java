package com.example.roguelike;

public class Player {
    public static final int PLAYER_WIDTH = 20;
    public static final int PLAYER_HEIGHT = 20;

    private int x;
    private int y;

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void move(int dx, int dy, Room room) {
        int newX = this.x + dx;
        // Check horizontal boundaries
        if (newX >= room.getX() && (newX + PLAYER_WIDTH) <= (room.getX() + room.getWidth())) {
            this.x = newX;
        }

        int newY = this.y + dy;
        // Check vertical boundaries
        if (newY >= room.getY() && (newY + PLAYER_HEIGHT) <= (room.getY() + room.getHeight())) {
            this.y = newY;
        }
    }
}
