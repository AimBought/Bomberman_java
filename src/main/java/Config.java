import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.awt.event.KeyEvent;

public class Config {
    Properties prop;
    String configFilePath = "config.properties";

    public Config() {
        prop = new Properties();
    }

    public void loadConfig(KeyHandler keyH) {
        try (FileInputStream fis = new FileInputStream(configFilePath)) {
            prop.load(fis);

            // Gracz 1
            keyH.p1.upCode = Integer.parseInt(prop.getProperty("P1_UP", String.valueOf(KeyEvent.VK_W)));
            keyH.p1.downCode = Integer.parseInt(prop.getProperty("P1_DOWN", String.valueOf(KeyEvent.VK_S)));
            keyH.p1.leftCode = Integer.parseInt(prop.getProperty("P1_LEFT", String.valueOf(KeyEvent.VK_A)));
            keyH.p1.rightCode = Integer.parseInt(prop.getProperty("P1_RIGHT", String.valueOf(KeyEvent.VK_D)));
            keyH.p1.bombCode = Integer.parseInt(prop.getProperty("P1_BOMB", String.valueOf(KeyEvent.VK_SPACE)));

            // Gracz 2
            keyH.p2.upCode = Integer.parseInt(prop.getProperty("P2_UP", String.valueOf(KeyEvent.VK_UP)));
            keyH.p2.downCode = Integer.parseInt(prop.getProperty("P2_DOWN", String.valueOf(KeyEvent.VK_DOWN)));
            keyH.p2.leftCode = Integer.parseInt(prop.getProperty("P2_LEFT", String.valueOf(KeyEvent.VK_LEFT)));
            keyH.p2.rightCode = Integer.parseInt(prop.getProperty("P2_RIGHT", String.valueOf(KeyEvent.VK_RIGHT)));
            keyH.p2.bombCode = Integer.parseInt(prop.getProperty("P2_BOMB", String.valueOf(KeyEvent.VK_ENTER)));

            // Gracz 3
            keyH.p3.upCode = Integer.parseInt(prop.getProperty("P3_UP", String.valueOf(KeyEvent.VK_I)));
            keyH.p3.downCode = Integer.parseInt(prop.getProperty("P3_DOWN", String.valueOf(KeyEvent.VK_K)));
            keyH.p3.leftCode = Integer.parseInt(prop.getProperty("P3_LEFT", String.valueOf(KeyEvent.VK_J)));
            keyH.p3.rightCode = Integer.parseInt(prop.getProperty("P3_RIGHT", String.valueOf(KeyEvent.VK_L)));
            keyH.p3.bombCode = Integer.parseInt(prop.getProperty("P3_BOMB", String.valueOf(KeyEvent.VK_O)));

            // Gracz 4
            keyH.p4.upCode = Integer.parseInt(prop.getProperty("P4_UP", String.valueOf(KeyEvent.VK_NUMPAD8)));
            keyH.p4.downCode = Integer.parseInt(prop.getProperty("P4_DOWN", String.valueOf(KeyEvent.VK_NUMPAD5)));
            keyH.p4.leftCode = Integer.parseInt(prop.getProperty("P4_LEFT", String.valueOf(KeyEvent.VK_NUMPAD4)));
            keyH.p4.rightCode = Integer.parseInt(prop.getProperty("P4_RIGHT", String.valueOf(KeyEvent.VK_NUMPAD6)));
            keyH.p4.bombCode = Integer.parseInt(prop.getProperty("P4_BOMB", String.valueOf(KeyEvent.VK_NUMPAD0)));

        } catch (IOException e) {
            System.out.println("Brak pliku konfiguracyjnego. Użyto ustawień domyślnych.");
            saveConfig(keyH);
        }
    }

    public void saveConfig(KeyHandler keyH) {
        prop.setProperty("P1_UP", String.valueOf(keyH.p1.upCode));
        prop.setProperty("P1_DOWN", String.valueOf(keyH.p1.downCode));
        prop.setProperty("P1_LEFT", String.valueOf(keyH.p1.leftCode));
        prop.setProperty("P1_RIGHT", String.valueOf(keyH.p1.rightCode));
        prop.setProperty("P1_BOMB", String.valueOf(keyH.p1.bombCode));

        prop.setProperty("P2_UP", String.valueOf(keyH.p2.upCode));
        prop.setProperty("P2_DOWN", String.valueOf(keyH.p2.downCode));
        prop.setProperty("P2_LEFT", String.valueOf(keyH.p2.leftCode));
        prop.setProperty("P2_RIGHT", String.valueOf(keyH.p2.rightCode));
        prop.setProperty("P2_BOMB", String.valueOf(keyH.p2.bombCode));

        prop.setProperty("P3_UP", String.valueOf(keyH.p3.upCode));
        prop.setProperty("P3_DOWN", String.valueOf(keyH.p3.downCode));
        prop.setProperty("P3_LEFT", String.valueOf(keyH.p3.leftCode));
        prop.setProperty("P3_RIGHT", String.valueOf(keyH.p3.rightCode));
        prop.setProperty("P3_BOMB", String.valueOf(keyH.p3.bombCode));

        prop.setProperty("P4_UP", String.valueOf(keyH.p4.upCode));
        prop.setProperty("P4_DOWN", String.valueOf(keyH.p4.downCode));
        prop.setProperty("P4_LEFT", String.valueOf(keyH.p4.leftCode));
        prop.setProperty("P4_RIGHT", String.valueOf(keyH.p4.rightCode));
        prop.setProperty("P4_BOMB", String.valueOf(keyH.p4.bombCode));

        try (FileOutputStream fos = new FileOutputStream(configFilePath)) {
            prop.store(fos, "Bomberman Key Configuration");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}