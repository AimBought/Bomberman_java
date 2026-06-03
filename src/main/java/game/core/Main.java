package game.core;

import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) {
        JFrame window = new JFrame("Bomberman");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // ZMIANA: Usunięto linijkę window.setUndecorated(true);
        // Dzięki temu systemowy pasek wraca na swoje miejsce!

        GamePanel gamePanel = new GamePanel();
        window.add(gamePanel);

        window.pack();

        // Gra wystartuje na całym ekranie (zmaksymalizowana),
        // ale w każdej chwili możesz ją pomniejszyć przyciskiem na górnym pasku.
        window.setExtendedState(JFrame.MAXIMIZED_BOTH);

        // window.setResizable(true); // W Swingu domyślnie jest to 'true', więc okno można płynnie rozciągać

        window.setLocationRelativeTo(null);
        window.setVisible(true);

        gamePanel.startGameThread();
    }
}