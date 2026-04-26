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
    // NOWE: Pamięta, skąd weszliśmy w opcje (Menu czy Pauza)
    public GameState previousState = GameState.MENU;

    public Config config = new Config();
    public KeyHandler keyH = new KeyHandler(this);
    public UI ui = new UI(this);

    public ArrayList<Player> players = new ArrayList<>();
    public ArrayList<Bomb> bombs = new ArrayList<>();
    public ArrayList<Explosion> explosions = new ArrayList<>();
    public ArrayList<PowerUp> powerUps = new ArrayList<>();

    public int numberOfPlayers = 2;
    public int deathTimer = 300;
    public boolean roundEnded = false;

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, totalScreenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
        config.loadConfig(keyH);
    }

    public void resetGame() {
        players.clear();
        bombs.clear();
        explosions.clear();
        powerUps.clear();
        deathTimer = 300;
        roundEnded = false;

        players.add(new Player(this, keyH.p1, 1, Color.WHITE, 0, 0));
        players.add(new Player(this, keyH.p2, 2, new Color(50, 150, 255), maxScreenCol - 1, maxScreenRow - 1));

        if (numberOfPlayers >= 3) {
            players.add(new Player(this, keyH.p3, 3, Color.RED, maxScreenCol - 1, 0));
        }
        if (numberOfPlayers >= 4) {
            players.add(new Player(this, keyH.p4, 4, Color.YELLOW, 0, maxScreenRow - 1));
        }

        generateMap();
    }

    public void generateMap() {
        map = new int[maxScreenCol][maxScreenRow];
        for (int x = 0; x < maxScreenCol; x++) {
            for (int y = 0; y < maxScreenRow; y++) {
                if (x % 2 != 0 && y % 2 != 0) {
                    map[x][y] = 1;
                }
                else {
                    boolean p1Safe = (x == 0 && y == 0) || (x == 1 && y == 0) || (x == 0 && y == 1);
                    boolean p2Safe = (x == maxScreenCol-1 && y == maxScreenRow-1) || (x == maxScreenCol-2 && y == maxScreenRow-1) || (x == maxScreenCol-1 && y == maxScreenRow-2);
                    boolean p3Safe = (x == maxScreenCol-1 && y == 0) || (x == maxScreenCol-2 && y == 0) || (x == maxScreenCol-1 && y == 1);
                    boolean p4Safe = (x == 0 && y == maxScreenRow-1) || (x == 1 && y == maxScreenRow-1) || (x == 0 && y == maxScreenRow-2);

                    if (!p1Safe && !p2Safe && !p3Safe && !p4Safe) {
                        if (Math.random() < 0.4) map[x][y] = 2;
                        else map[x][y] = 0;
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

        while (gameThread != null) {
            long currentTime = System.nanoTime();
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
        // Gra aktualizuje się TYLKO w stanie PLAY. Jeśli jest PAUSE, ignoruje ruch i odliczanie bomb!
        if (currentState == GameState.PLAY) {

            int aliveCount = 0;

            for (Player p : players) {
                if (!p.isDead) {
                    p.update();
                    checkPowerUpCollection(p);
                    checkPlayerDeath(p);
                    if (!p.isDead) aliveCount++;
                }
            }

            for (int i = 0; i < bombs.size(); i++) {
                Bomb b = bombs.get(i);
                b.update();
                if (b.timer <= 0) {
                    explodeBomb(b);
                    b.owner.bombsPlaced--;
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

            if (aliveCount <= 1) roundEnded = true;

            if (roundEnded) {
                deathTimer--;
                if (deathTimer <= 0) currentState = GameState.SUMMARY;
            }
        }
    }

    public void checkPowerUpCollection(Player p) {
        Rectangle playerRect = new Rectangle(p.x + p.solidArea.x, p.y + p.solidArea.y, p.solidArea.width, p.solidArea.height);
        for (int i = 0; i < powerUps.size(); i++) {
            PowerUp pu = powerUps.get(i);
            Rectangle puRect = new Rectangle(pu.col * tileSize, pu.row * tileSize, tileSize, tileSize);
            if (playerRect.intersects(puRect)) {
                if (pu.type == 0) p.bombPower++;
                else if (pu.type == 1) p.maxBombs++;
                else if (pu.type == 2) {
                    p.speed++;
                    if (p.speed > 8) p.speed = 8;
                }
                powerUps.remove(i);
                i--;
            }
        }
    }

    public void checkPlayerDeath(Player p) {
        Rectangle playerRect = new Rectangle(p.x + p.solidArea.x, p.y + p.solidArea.y, p.solidArea.width, p.solidArea.height);
        for (Explosion e : explosions) {
            Rectangle expRect = new Rectangle(e.col * tileSize, e.row * tileSize, tileSize, tileSize);
            if (playerRect.intersects(expRect)) {
                p.isDead = true;
                break;
            }
        }
    }

    public void explodeBomb(Bomb b) {
        int c = b.col;
        int r = b.row;
        int power = b.owner.bombPower;

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
        g2.setClip(0, 0, screenWidth, totalScreenHeight);

        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, screenWidth, totalScreenHeight);

        // ZMIANA: Rysujemy mapę zarówno gdy gramy, jak i gdy jesteśmy w menu PAUZY
        if (currentState == GameState.PLAY || currentState == GameState.PAUSE) {
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
            for (Player p : players) p.draw(g2);
        }

        ui.draw(g2); // UI narysuje na wierzchu półprzezroczysty ekran pauzy
        g2.dispose();
    }
}