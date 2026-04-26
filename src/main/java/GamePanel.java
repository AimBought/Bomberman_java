import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;

public class GamePanel extends JPanel implements Runnable {

    final int originalTileSize = 16;
    final int scale = 3;
    public final int tileSize = originalTileSize * scale;

    public final int maxScreenCol = 15;
    public final int maxScreenRow = 13;

    public final int screenWidth = tileSize * maxScreenCol;
    public final int screenHeight = tileSize * maxScreenRow;

    public final int hudHeight = tileSize * 2;
    public final int totalScreenHeight = screenHeight + hudHeight;

    int FPS = 60;
    Thread gameThread;
    public int[][] map;

    public GameState currentState = GameState.LOGO;
    public UI ui = new UI(this);

    public KeyHandler keyH = new KeyHandler(this);
    public Player player = new Player(this, keyH);

    public ArrayList<Bomb> bombs = new ArrayList<>();
    public ArrayList<Explosion> explosions = new ArrayList<>();
    public ArrayList<PowerUp> powerUps = new ArrayList<>();

    public int deathTimer = 300;

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, totalScreenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);

        map = new int[maxScreenCol][maxScreenRow];
        generateMap();
    }

    public void resetGame() {
        player.setDefaultValues();
        bombs.clear();
        explosions.clear();
        powerUps.clear();
        deathTimer = 300;
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
        if (currentState == GameState.PLAY) {
            if (!player.isDead) {
                player.update();
                checkPowerUpCollection();
            }

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

            if (!player.isDead) {
                checkPlayerDeath();
            } else {
                deathTimer--;
                if (deathTimer <= 0) {
                    currentState = GameState.SUMMARY;
                }
            }
        }
    }

    public void checkPowerUpCollection() {
        Rectangle playerRect = new Rectangle(player.x + player.solidArea.x, player.y + player.solidArea.y, player.solidArea.width, player.solidArea.height);
        for (int i = 0; i < powerUps.size(); i++) {
            PowerUp p = powerUps.get(i);
            Rectangle pRect = new Rectangle(p.col * tileSize, p.row * tileSize, tileSize, tileSize);
            if (playerRect.intersects(pRect)) {
                if (p.type == 0) player.bombPower++;
                else if (p.type == 1) player.maxBombs++;
                else if (p.type == 2) {
                    player.speed++;
                    if (player.speed > 8) player.speed = 8;
                }
                powerUps.remove(i);
                i--;
            }
        }
    }

    public void checkPlayerDeath() {
        Rectangle playerRect = new Rectangle(player.x + player.solidArea.x, player.y + player.solidArea.y, player.solidArea.width, player.solidArea.height);
        for (Explosion e : explosions) {
            Rectangle expRect = new Rectangle(e.col * tileSize, e.row * tileSize, tileSize, tileSize);
            if (playerRect.intersects(expRect)) {
                player.isDead = true;
                break;
            }
        }
    }

    public void explodeBomb(Bomb b) {
        int c = b.col;
        int r = b.row;
        int power = player.bombPower;

        explosions.add(new Explosion(this, c, r));
        int[][] directions = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};

        for (int[] dir : directions) {
            for (int i = 1; i <= power; i++) {
                int nextCol = c + dir[0] * i;
                int nextRow = r + dir[1] * i;

                if (nextCol < 0 || nextCol >= maxScreenCol || nextRow < 0 || nextRow >= maxScreenRow) break;

                for (int j = 0; j < powerUps.size(); j++) {
                    if (powerUps.get(j).col == nextCol && powerUps.get(j).row == nextRow) {
                        powerUps.remove(j);
                        j--;
                    }
                }

                int tileType = map[nextCol][nextRow];

                if (tileType == 1) break;
                else if (tileType == 2) {
                    map[nextCol][nextRow] = 0;
                    explosions.add(new Explosion(this, nextCol, nextRow));

                    if (Math.random() < 0.30) {
                        int type = (int)(Math.random() * 3);
                        powerUps.add(new PowerUp(this, nextCol, nextRow, type));
                    }
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

        double scaleRatio = Math.min((double) getWidth() / screenWidth, (double) getHeight() / totalScreenHeight);

        int drawX = (int) ((getWidth() - (screenWidth * scaleRatio)) / 2);
        int drawY = (int) ((getHeight() - (totalScreenHeight * scaleRatio)) / 2);

        g2.translate(drawX, drawY);
        g2.scale(scaleRatio, scaleRatio);

        // Ustawiamy klip żeby nic się nie rysowało poza naszym kwadratem
        g2.setClip(0, 0, screenWidth, totalScreenHeight);

        // POPRAWKA: Rysujemy czarne tło wewnątrz obszaru gry na wypadek błędów skalowania
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, screenWidth, totalScreenHeight);

        if (currentState == GameState.PLAY) {

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

            for (PowerUp p : powerUps) p.draw(g2);
            for (Bomb b : bombs) b.draw(g2);
            for (Explosion e : explosions) e.draw(g2);
            player.draw(g2);
        }

        ui.draw(g2);
        g2.dispose();
    }
}