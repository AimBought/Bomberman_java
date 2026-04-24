import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class GamePanel extends JPanel implements Runnable {

    // USTAWIENIA EKRANU
    final int originalTileSize = 16; // Klasyczny kafel 16x16 pikseli
    final int scale = 3; // Skalujemy x3, żeby gra nie była mikroskopijna
    public final int tileSize = originalTileSize * scale; // Pojedyncze pole będzie miało 48x48 px

    // Klasyczna plansza Bombermana ma zazwyczaj nieparzystą liczbę wierszy i kolumn
    public final int maxScreenCol = 15;
    public final int maxScreenRow = 13;
    final int screenWidth = tileSize * maxScreenCol; // 720 pikseli szerokości
    final int screenHeight = tileSize * maxScreenRow; // 624 piksele wysokości

    // PĘTLA GRY
    int FPS = 60;
    Thread gameThread;

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        // Podwójne buforowanie zapobiega migotaniu ekranu podczas rysowania
        this.setDoubleBuffered(true);
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        // Obliczamy czas trwania jednej klatki (1 sekunda = 1 mld nanosekund)
        double drawInterval = 1000000000.0 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        // Główna pętla gry
        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update(); // 1. Zaktualizuj informacje (np. pozycję gracza)
                repaint(); // 2. Narysuj zaktualizowany ekran (wywołuje paintComponent)
                delta--;
            }
        }
    }

    public void update() {
        // Tutaj będziemy aktualizować logikę, na razie zostawiamy puste
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // TEST RENDEROWANIA: Narysujmy biały kwadrat na środku ekranu,
        // żeby upewnić się, że wszystko działa.
        g2.setColor(Color.WHITE);
        g2.fillRect(screenWidth / 2 - tileSize / 2, screenHeight / 2 - tileSize / 2, tileSize, tileSize);

        g2.dispose(); // Dobra praktyka: zwalnianie pamięci obiektu Graphics
    }
}