import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class GamePanel extends JPanel implements Runnable {

    // USTAWIENIA EKRANU
    final int originalTileSize = 16;
    final int scale = 3;
    public final int tileSize = originalTileSize * scale;

    public final int maxScreenCol = 15;
    public final int maxScreenRow = 13;
    final int screenWidth = tileSize * maxScreenCol;
    final int screenHeight = tileSize * maxScreenRow;

    // PĘTLA GRY
    int FPS = 60;
    Thread gameThread;

    // MAPA GRY
    public int[][] map;

    // INICJALIZACJA KLAS (Klawiatura i Gracz)
    public KeyHandler keyH = new KeyHandler();
    public Player player = new Player(this, keyH);

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);

        // Nasłuchiwanie klawiatury
        this.addKeyListener(keyH);
        this.setFocusable(true); // Wymagane, by panel przyjmował wciskane klawisze

        // Inicjalizacja i generowanie mapy
        map = new int[maxScreenCol][maxScreenRow];
        generateMap();
    }

    public void generateMap() {
        for (int x = 0; x < maxScreenCol; x++) {
            for (int y = 0; y < maxScreenRow; y++) {

                // 1. Niezniszczalne bloki (układ szachownicy) na nieparzystych indeksach
                if (x % 2 != 0 && y % 2 != 0) {
                    map[x][y] = 1;
                }
                else {
                    // 2. Strefa startowa (bezpieczna, bez skrzynek)
                    boolean isSafeZone = (x == 0 && y == 0) || (x == 1 && y == 0) || (x == 0 && y == 1);

                    if (!isSafeZone) {
                        // 3. Losowe generowanie skrzynek
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
        // Aktualizacja logiki gracza (ruch i kolizje)
        player.update();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // 1. Rysowanie planszy (najpierw, żeby tło było pod spodem)
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

        // 2. Rysowanie gracza (na wierzchu)
        player.draw(g2);

        g2.dispose();
    }
}