import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) {
        JFrame window = new JFrame();
        // Zamyka proces po kliknięciu krzyżyka
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Blokujemy zmianę rozmiaru okna, żeby siatka gry się nie rozjechała
        window.setResizable(false);
        window.setTitle("Bomberman");

        // Tworzymy naszą planszę gry
        GamePanel gamePanel = new GamePanel();
        window.add(gamePanel);

        // Dopasowuje rozmiar okna do preferencji GamePanel
        window.pack();

        // Wyświetla okno na środku ekranu
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        // Uruchamiamy pętlę gry
        gamePanel.startGameThread();
    }
}