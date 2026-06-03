package game;

import java.awt.Color;
import java.awt.Graphics2D;

public class Bomb {

    public int col, row;
    public Player owner;
    public int timer = 180;

    public Bomb(int col, int row, Player owner) {
        this.col = col;
        this.row = row;
        this.owner = owner;
    }

    public void update() {
        timer--;
    }

    public void draw(Graphics2D g2) {
        int screenX = col * GamePanel.TILE_SIZE;
        int screenY = row * GamePanel.TILE_SIZE;

        // Animacja dynamicznego pulsowania przy użyciu sinusoidy
        double scale = 1.0 + 0.15 * Math.sin(timer * 0.2);

        // Jeśli bomba zaraz wybuchnie, pulsuje dużo szybciej
        if (timer < 60) scale = 1.0 + 0.25 * Math.sin(timer * 0.6);

        int size = (int)(32 * scale);
        int offset = (GamePanel.TILE_SIZE - size) / 2;

        // Alarmowe mruganie na czerwono tuż przed detonacją
        if (timer < 60 && timer % 10 < 5) g2.setColor(Color.RED);
        else g2.setColor(Color.BLACK);

        g2.fillOval(screenX + offset, screenY + offset, size, size);

        // Rdzeń bomby dla efektu wypukłości
        g2.setColor(Color.DARK_GRAY);
        g2.fillOval(screenX + offset + size/4, screenY + offset + size/4, size/2, size/2);

        // Lont na szczycie bomby
        g2.setColor(new Color(139, 69, 19));
        g2.fillRect(screenX + GamePanel.TILE_SIZE/2 - 2, screenY + offset - 6, 4, 8);

        // Płonąca iskra na loncie (wygasa, gdy timer dobiega zera)
        if (timer > 10 && timer % 4 < 2) {
            g2.setColor(Color.YELLOW);
            g2.fillOval(screenX + GamePanel.TILE_SIZE/2 - 4, screenY + offset - 12, 8, 8);
        }
    }
}