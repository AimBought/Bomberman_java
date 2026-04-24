import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle; // NOWY IMPORT do prostokątów kolizji
import java.util.ArrayList;

public class GamePanel extends JPanel implements Runnable {

    final int originalTileSize = 16;
    final int scale = 3;
    public final int tileSize = originalTileSize * scale;

    public final int maxScreenCol = 15;
    public final int maxScreenRow = 13;
    final int screenWidth = tileSize * maxScreenCol;
    final int screenHeight = tileSize * maxScreenRow;

    int FPS = 60;
    Thread gameThread;

    public int[][] map;

    public KeyHandler keyH = new KeyHandler();
    public Player player = new Player(this, keyH);

    public ArrayList<Bomb> bombs = new ArrayList<>();
    public ArrayList<Explosion> explosions = new ArrayList<>();

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);

        this.addKeyListener(keyH);
        this.setFocusable(true);

        map = new int[maxScreenCol][maxScreenRow];
        generateMap();
    }

    public void generateMap() {
        for (int x = 0; x < maxScreenCol; x++) {
            for (int y = 0; y < maxScreenRow; y++) {
                if (x % 2 != 0 && y % 2 != 0) {
                    map[x][y] = 1;
                }
                else {
                    boolean isSafeZone = (x == 0 && y == 0) || (x == 1 && y == 0) || (x == 0 && y == 1);
                    if (!isSafeZone) {
                        if (Math.random() < 0.4) {
                            map[x][y] = 2;
                        } else {
                            map[x][y] = 0;
                        }
                    } else {
                        map[x][y] = 0;
                    }
                }
            }
        }
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1000000000.0 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    public void update() {
        player.update();

        for (int i = 0; i < bombs.size(); i++) {
            Bomb b = bombs.get(i);
            b.update();
            if (b.timer <= 0) {
                explodeBomb(b);
                bombs.remove(i);
                i--;
            }
        }

        for (int i = 0; i < explosions.size(); i++) {
            Explosion e = explosions.get(i);
            e.update();
            if (e.timer <= 0) {
                explosions.remove(i);
                i--;
            }
        }

        // NOWE: Sprawdzanie czy gracz wszedł w ogień
        if (!player.isDead) {
            checkPlayerDeath();
        }
    }

    // NOWE: Metoda do detekcji kolizji z wybuchem
    public void checkPlayerDeath() {
        // Hitbox gracza w pikselach na ekranie
        Rectangle playerRect = new Rectangle(
                player.x + player.solidArea.x,
                player.y + player.solidArea.y,
                player.solidArea.width,
                player.solidArea.height
        );

        // Przechodzimy przez wszystkie aktywne płomienie
        for (Explosion e : explosions) {
            // Hitbox płomienia (cały kafelek, na którym się znajduje)
            Rectangle expRect = new Rectangle(
                    e.col * tileSize,
                    e.row * tileSize,
                    tileSize,
                    tileSize
            );

            // Jeśli prostokąt gracza przecina się z prostokątem ognia
            if (playerRect.intersects(expRect)) {
                player.isDead = true;
                System.out.println("BUM! Gracz wyeliminowany."); // Zobaczysz to w konsoli IntelliJ
                break; // Skoro już nie żyjemy, nie musimy sprawdzać reszty płomieni
            }
        }
    }

    public void explodeBomb(Bomb b) {
        int c = b.col;
        int r = b.row;
        int power = 1;

        explosions.add(new Explosion(this, c, r));

        int[][] directions = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};

        for (int[] dir : directions) {
            for (int i = 1; i <= power; i++) {
                int nextCol = c + dir[0] * i;
                int nextRow = r + dir[1] * i;

                if (nextCol < 0 || nextCol >= maxScreenCol || nextRow < 0 || nextRow >= maxScreenRow) {
                    break;
                }

                int tileType = map[nextCol][nextRow];

                if (tileType == 1) {
                    break;
                }
                else if (tileType == 2) {
                    map[nextCol][nextRow] = 0;
                    explosions.add(new Explosion(this, nextCol, nextRow));
                    break;
                }
                else {
                    explosions.add(new Explosion(this, nextCol, nextRow));
                }
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        for (int x = 0; x < maxScreenCol; x++) {
            for (int y = 0; y < maxScreenRow; y++) {
                int screenX = x * tileSize;
                int screenY = y * tileSize;

                if (map[x][y] == 1) {
                    g2.setColor(Color.GRAY);
                    g2.fillRect(screenX, screenY, tileSize, tileSize);
                } else if (map[x][y] == 2) {
                    g2.setColor(new Color(139, 69, 19));
                    g2.fillRect(screenX, screenY, tileSize, tileSize);
                }
            }
        }

        for (Bomb b : bombs) {
            b.draw(g2);
        }

        for (Explosion e : explosions) {
            e.draw(g2);
        }

        player.draw(g2);

        g2.dispose();
    }
}