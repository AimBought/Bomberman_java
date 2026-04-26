import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Player {

    GamePanel gp;
    KeyHandler.PlayerInput input; // Unikalne wejście z klawiatury dla tego gracza

    public int id;
    public Color color;
    public int x, y;

    int drawSize = 32;
    int drawOffset = 8;

    public Rectangle solidArea;

    int bombCooldown = 0;
    public boolean isDead = false;

    public int speed;
    public int maxBombs;
    public int bombPower;
    public int bombsPlaced = 0; // Śledzi ilość obecnie postawionych bomb

    public Player(GamePanel gp, KeyHandler.PlayerInput input, int id, Color color, int startCol, int startRow) {
        this.gp = gp;
        this.input = input;
        this.id = id;
        this.color = color;

        // Obliczamy piksele na podstawie kolumn i rzędów
        this.x = startCol * gp.tileSize;
        this.y = startRow * gp.tileSize;

        setDefaultValues();
        solidArea = new Rectangle(10, 10, 28, 28);
    }

    public void setDefaultValues() {
        speed = 4;
        maxBombs = 1;
        bombPower = 1;
        bombCooldown = 0;
        bombsPlaced = 0;
        isDead = false;
    }

    public void update() {
        if (isDead) return;

        if (bombCooldown > 0) bombCooldown--;

        if (input.bomb && bombCooldown == 0 && bombsPlaced < maxBombs) {
            int centerCol = (x + gp.tileSize / 2) / gp.tileSize;
            int centerRow = (y + gp.tileSize / 2) / gp.tileSize;

            // Przekazujemy referencję do gracza, żeby bomba wiedziała do kogo należy
            gp.bombs.add(new Bomb(gp, centerCol, centerRow, this));
            bombsPlaced++;
            bombCooldown = 30;
        }

        int nextX = x;
        int nextY = y;

        if (input.up) nextY -= speed;
        if (input.down) nextY += speed;
        if (input.left) nextX -= speed;
        if (input.right) nextX += speed;

        if (!checkCollision(nextX, y)) x = nextX;
        if (!checkCollision(x, nextY)) y = nextY;
    }

    private boolean checkCollision(int targetX, int targetY) {
        int leftX = targetX + solidArea.x;
        int rightX = targetX + solidArea.x + solidArea.width;
        int topY = targetY + solidArea.y;
        int bottomY = targetY + solidArea.y + solidArea.height;

        if (leftX < 0 || rightX > gp.screenWidth || topY < 0 || bottomY > gp.screenHeight) return true;

        int leftCol = leftX / gp.tileSize;
        int rightCol = (rightX - 1) / gp.tileSize;
        int topRow = topY / gp.tileSize;
        int bottomRow = (bottomY - 1) / gp.tileSize;

        return (gp.map[leftCol][topRow] != 0 || gp.map[rightCol][topRow] != 0 ||
                gp.map[leftCol][bottomRow] != 0 || gp.map[rightCol][bottomRow] != 0);
    }

    public void draw(Graphics2D g2) {
        if (isDead) {
            g2.setColor(Color.DARK_GRAY);
        } else {
            g2.setColor(color); // Rysowanie wybranym kolorem (Biały lub Niebieski)
        }
        g2.fillRect(x + drawOffset, y + drawOffset, drawSize, drawSize);
    }
}