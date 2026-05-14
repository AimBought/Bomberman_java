package game; // Twoja nowa nazwa pakietu!

import java.awt.Color;
import java.awt.Graphics2D;

public class Bomb {
    public int col, row;
    public int timer = 180;
    public Player owner;

    // Konstruktor nie potrzebuje już GamePanel
    public Bomb(int col, int row, Player owner) {
        this.col = col;
        this.row = row;
        this.owner = owner;
    }

    public void update() {
        timer--;
    }

    public void draw(Graphics2D g2) {
        // Używamy statycznej stałej bezpośrednio z klasy GamePanel!
        int screenX = col * GamePanel.TILE_SIZE;
        int screenY = row * GamePanel.TILE_SIZE;

        g2.setColor(Color.RED);
        g2.fillOval(screenX + 8, screenY + 8, GamePanel.TILE_SIZE - 16, GamePanel.TILE_SIZE - 16);
    }
}