import java.awt.Color;
import java.awt.Graphics2D;

public class Bomb {
    GamePanel gp;
    public int col, row;
    public int timer = 180;

    // NOWE: Zmienna przechowująca referencję do gracza, który podłożył bombę
    public Player owner;

    // ZMIANA: Konstruktor przyjmuje teraz dodatkowy parametr "Player owner"
    public Bomb(GamePanel gp, int col, int row, Player owner) {
        this.gp = gp;
        this.col = col;
        this.row = row;
        this.owner = owner; // Przypisujemy właściciela bomby przy jej tworzeniu
    }

    public void update() {
        timer--;
    }

    public void draw(Graphics2D g2) {
        int screenX = col * gp.tileSize;
        int screenY = row * gp.tileSize;

        g2.setColor(Color.RED);
        g2.fillOval(screenX + 8, screenY + 8, gp.tileSize - 16, gp.tileSize - 16);
    }
}