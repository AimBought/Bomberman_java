import java.awt.Color;
import java.awt.Graphics2D;

public class PowerUp {
    GamePanel gp;
    public int col, row;
    public int type; // 0 = Zasięg ognia, 1 = Więcej bomb, 2 = Szybkość

    public PowerUp(GamePanel gp, int col, int row, int type) {
        this.gp = gp;
        this.col = col;
        this.row = row;
        this.type = type;
    }

    public void draw(Graphics2D g2) {
        int screenX = col * gp.tileSize;
        int screenY = row * gp.tileSize;

        // Zależnie od typu, ustawiamy inny kolor
        if (type == 0) {
            g2.setColor(Color.MAGENTA); // Zasięg
        } else if (type == 1) {
            g2.setColor(Color.CYAN);    // Ilość bomb
        } else if (type == 2) {
            g2.setColor(Color.GREEN);   // Prędkość
        }

        // Rysujemy power-up jako mniejszy kwadrat na środku kafelka
        int offset = 12;
        int size = gp.tileSize - (offset * 2);
        g2.fillRect(screenX + offset, screenY + offset, size, size);
    }
}