import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Player {

    GamePanel gp;
    KeyHandler keyH;

    public int x, y;
    public int speed;
    public Rectangle solidArea;

    // NOWE: Rozmiar gracza i wyśrodkowanie
    int playerSize = 32; // Zamiast 48 (tileSize)
    int offset = 8;      // (48 - 32) / 2 = 8 pikseli marginesu z każdej strony

    public Player(GamePanel gp, KeyHandler keyH) {
        this.gp = gp;
        this.keyH = keyH;

        setDefaultValues();

        // Hitbox idealnie dopasowany do nowego, mniejszego wyglądu
        solidArea = new Rectangle(offset, offset, playerSize, playerSize);
    }

    public void setDefaultValues() {
        x = 0;
        y = 0;
        speed = 4;
    }

    public void update() {
        int nextX = x;
        int nextY = y;

        if (keyH.upPressed) nextY -= speed;
        if (keyH.downPressed) nextY += speed;
        if (keyH.leftPressed) nextX -= speed;
        if (keyH.rightPressed) nextX += speed;

        if (!checkCollision(nextX, y)) {
            x = nextX;
        }
        if (!checkCollision(x, nextY)) {
            y = nextY;
        }
    }

    private boolean checkCollision(int targetX, int targetY) {
        int leftX = targetX + solidArea.x;
        int rightX = targetX + solidArea.x + solidArea.width;
        int topY = targetY + solidArea.y;
        int bottomY = targetY + solidArea.y + solidArea.height;

        // 1. POPRAWKA: Twarde blokowanie na krawędziach okna (w pikselach)
        if (leftX < 0 || rightX > gp.screenWidth || topY < 0 || bottomY > gp.screenHeight) {
            return true; // Zwracamy kolizję, jeśli dotkniemy granicy ekranu
        }

        // 2. Przeliczanie na siatkę mapy
        int leftCol = leftX / gp.tileSize;
        // Odejmujemy 1 piksel, aby postać nie blokowała się o sąsiedni kafel po prawej/na dole
        int rightCol = (rightX - 1) / gp.tileSize;
        int topRow = topY / gp.tileSize;
        int bottomRow = (bottomY - 1) / gp.tileSize;

        // Pobieramy dane o kafelkach z mapy
        int tileNum1 = gp.map[leftCol][topRow];
        int tileNum2 = gp.map[rightCol][topRow];
        int tileNum3 = gp.map[leftCol][bottomRow];
        int tileNum4 = gp.map[rightCol][bottomRow];

        // Jeśli którykolwiek kafel to ściana (1) lub skrzynia (2), blokujemy ruch
        return (tileNum1 != 0 || tileNum2 != 0 || tileNum3 != 0 || tileNum4 != 0);
    }

    public void draw(Graphics2D g2) {
        g2.setColor(Color.WHITE);
        // POPRAWKA: Rysujemy mniejszy kwadrat z uwzględnieniem przesunięcia (offset)
        g2.fillRect(x + offset, y + offset, playerSize, playerSize);
    }
}