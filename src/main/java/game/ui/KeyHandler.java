package game.ui;

import game.core.GamePanel;
import game.core.GameState;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    GamePanel gp;

    public class PlayerInput {
        public int upCode, downCode, leftCode, rightCode, bombCode;
        public boolean up, down, left, right, bomb;
    }

    public PlayerInput p1 = new PlayerInput();
    public PlayerInput p2 = new PlayerInput();
    public PlayerInput p3 = new PlayerInput();
    public PlayerInput p4 = new PlayerInput();

    public boolean waitingForKey = false;
    public int keyToRebind = -1;

    public KeyHandler(GamePanel gp) {
        this.gp = gp;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        if (gp.currentState == GameState.OPTIONS) {
            if (waitingForKey) {
                switch (keyToRebind) {
                    case 0: p1.upCode = code; break; case 1: p1.downCode = code; break; case 2: p1.leftCode = code; break; case 3: p1.rightCode = code; break; case 4: p1.bombCode = code; break;
                    case 5: p2.upCode = code; break; case 6: p2.downCode = code; break; case 7: p2.leftCode = code; break; case 8: p2.rightCode = code; break; case 9: p2.bombCode = code; break;
                    case 10: p3.upCode = code; break; case 11: p3.downCode = code; break; case 12: p3.leftCode = code; break; case 13: p3.rightCode = code; break; case 14: p3.bombCode = code; break;
                    case 15: p4.upCode = code; break; case 16: p4.downCode = code; break; case 17: p4.leftCode = code; break; case 18: p4.rightCode = code; break; case 19: p4.bombCode = code; break;
                }
                waitingForKey = false;
                gp.config.saveConfig(this);
                return;
            }

            if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) { gp.ui.commandNum--; if (gp.ui.commandNum < 0) gp.ui.commandNum = 20; }
            if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) { gp.ui.commandNum++; if (gp.ui.commandNum > 20) gp.ui.commandNum = 0; }
            if (code == KeyEvent.VK_SPACE || code == KeyEvent.VK_ENTER) {
                if (gp.ui.commandNum == 20) { gp.currentState = gp.previousState; gp.ui.commandNum = (gp.previousState == GameState.MENU) ? 7 : 2; }
                else { waitingForKey = true; keyToRebind = gp.ui.commandNum; }
            }
            if (code == KeyEvent.VK_ESCAPE) { gp.currentState = gp.previousState; gp.ui.commandNum = (gp.previousState == GameState.MENU) ? 7 : 2; }
        }
        else if (gp.currentState == GameState.EDITOR) {
            if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) { gp.editorCursorRow--; if(gp.editorCursorRow < 0) gp.editorCursorRow = GamePanel.MAX_SCREEN_ROW - 1; }
            if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) { gp.editorCursorRow++; if(gp.editorCursorRow >= GamePanel.MAX_SCREEN_ROW) gp.editorCursorRow = 0; }
            if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) { gp.editorCursorCol--; if(gp.editorCursorCol < 0) gp.editorCursorCol = GamePanel.MAX_SCREEN_COL - 1; }
            if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) { gp.editorCursorCol++; if(gp.editorCursorCol >= GamePanel.MAX_SCREEN_COL) gp.editorCursorCol = 0; }

            // NOWE: Blokada strefy spawnu
            if (code == KeyEvent.VK_1 || code == KeyEvent.VK_2) {
                int c = gp.editorCursorCol;
                int r = gp.editorCursorRow;
                int maxC = GamePanel.MAX_SCREEN_COL;
                int maxR = GamePanel.MAX_SCREEN_ROW;

                boolean safe = (c<=1 && r==0) || (c==0 && r==1) ||
                        (c>=maxC-2 && r==maxR-1) || (c==maxC-1 && r==maxR-2) ||
                        (c>=maxC-2 && r==0) || (c==maxC-1 && r==1) ||
                        (c<=1 && r==maxR-1) || (c==0 && r==maxR-2);

                // Maluj tylko, jeśli pole NIE JEST strefą bezpieczną
                if (!safe) {
                    if (code == KeyEvent.VK_1) gp.editorMap[c][r] = 1;
                    if (code == KeyEvent.VK_2) gp.editorMap[c][r] = 2;
                }
            }
            if (code == KeyEvent.VK_3 || code == KeyEvent.VK_0) gp.editorMap[gp.editorCursorCol][gp.editorCursorRow] = 0;

            if (code == KeyEvent.VK_ENTER) gp.currentState = GameState.EDITOR_NAMING;
            if (code == KeyEvent.VK_ESCAPE) gp.currentState = GameState.MENU;
        }
        else if (gp.currentState == GameState.EDITOR_NAMING) {
            if (code == KeyEvent.VK_ENTER) {
                gp.saveEditorMap();
                gp.currentState = GameState.MENU;
                gp.ui.commandNum = 4;
            } else if (code == KeyEvent.VK_ESCAPE) {
                gp.currentState = GameState.EDITOR;
            } else if (code == KeyEvent.VK_BACK_SPACE && gp.newMapName.length() > 0) {
                gp.newMapName = gp.newMapName.substring(0, gp.newMapName.length() - 1);
            } else if (code >= KeyEvent.VK_A && code <= KeyEvent.VK_Z) {
                if (gp.newMapName.length() < 15) gp.newMapName += (char)code;
            } else if (code >= KeyEvent.VK_0 && code <= KeyEvent.VK_9) {
                if (gp.newMapName.length() < 15) gp.newMapName += (char)code;
            } else if (code == KeyEvent.VK_SPACE) {
                if (gp.newMapName.length() < 15) gp.newMapName += " ";
            } else if (code == KeyEvent.VK_MINUS) {
                if (gp.newMapName.length() < 15) gp.newMapName += "_";
            }
        }
        else if (gp.currentState == GameState.MENU) {
            if (code == KeyEvent.VK_E) {
                if (!gp.mapNames.isEmpty() && gp.mapNames.get(gp.currentMapIndex).startsWith("custom:")) {
                    gp.initEditor(true);
                }
            }
            if (code == KeyEvent.VK_DELETE || code == KeyEvent.VK_BACK_SPACE) {
                gp.deleteCustomMap();
            }

            if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) { gp.ui.commandNum--; if (gp.ui.commandNum < 0) gp.ui.commandNum = 8; }
            if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) { gp.ui.commandNum++; if (gp.ui.commandNum > 8) gp.ui.commandNum = 0; }

            if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
                if (gp.ui.commandNum == 1) {
                    gp.numberOfPlayers--; if (gp.numberOfPlayers < 1) gp.numberOfPlayers = 4;
                    if (gp.numberOfPlayers + gp.numberOfBots > 4) gp.numberOfBots = 4 - gp.numberOfPlayers;
                    if (gp.numberOfPlayers == 1 && gp.numberOfBots == 0) gp.numberOfBots = 1;
                }
                if (gp.ui.commandNum == 2) {
                    gp.numberOfBots--;
                    int minBots = (gp.numberOfPlayers == 1) ? 1 : 0;
                    if (gp.numberOfBots < minBots) gp.numberOfBots = 4 - gp.numberOfPlayers;
                }
                if (gp.ui.commandNum == 3) { gp.botDifficulty = (gp.botDifficulty == 0) ? 1 : 0; }
                if (gp.ui.commandNum == 4) { gp.currentMapIndex--; if (gp.currentMapIndex < 0) gp.currentMapIndex = gp.mapNames.size() - 1; }
                if (gp.ui.commandNum == 5) { gp.gameMode--; if (gp.gameMode < 0) gp.gameMode = 3; }
            }
            if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
                if (gp.ui.commandNum == 1) {
                    gp.numberOfPlayers++; if (gp.numberOfPlayers > 4) gp.numberOfPlayers = 1;
                    if (gp.numberOfPlayers + gp.numberOfBots > 4) gp.numberOfBots = 4 - gp.numberOfPlayers;
                    if (gp.numberOfPlayers == 1 && gp.numberOfBots == 0) gp.numberOfBots = 1;
                }
                if (gp.ui.commandNum == 2) {
                    gp.numberOfBots++;
                    if (gp.numberOfBots > 4 - gp.numberOfPlayers) gp.numberOfBots = (gp.numberOfPlayers == 1) ? 1 : 0;
                }
                if (gp.ui.commandNum == 3) { gp.botDifficulty = (gp.botDifficulty == 0) ? 1 : 0; }
                if (gp.ui.commandNum == 4) { gp.currentMapIndex++; if (gp.currentMapIndex > gp.mapNames.size() - 1) gp.currentMapIndex = 0; }
                if (gp.ui.commandNum == 5) { gp.gameMode++; if (gp.gameMode > 3) gp.gameMode = 0; }
            }
            if (code == KeyEvent.VK_SPACE || code == KeyEvent.VK_ENTER) {
                if (gp.ui.commandNum == 0) { gp.resetGame(); gp.currentState = GameState.PLAY; }
                if (gp.ui.commandNum == 6) { gp.initEditor(false); }
                if (gp.ui.commandNum == 7) { gp.previousState = GameState.MENU; gp.ui.commandNum = 0; gp.currentState = GameState.OPTIONS; }
                if (gp.ui.commandNum == 8) System.exit(0);
            }
        }
        else if (gp.currentState == GameState.PAUSE) {
            if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) { gp.ui.commandNum--; if (gp.ui.commandNum < 0) gp.ui.commandNum = 3; }
            if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) { gp.ui.commandNum++; if (gp.ui.commandNum > 3) gp.ui.commandNum = 0; }
            if (code == KeyEvent.VK_SPACE || code == KeyEvent.VK_ENTER) {
                if (gp.ui.commandNum == 0) gp.currentState = GameState.PLAY;
                if (gp.ui.commandNum == 1) { gp.resetGame(); gp.currentState = GameState.PLAY; }
                if (gp.ui.commandNum == 2) { gp.previousState = GameState.PAUSE; gp.ui.commandNum = 0; gp.currentState = GameState.OPTIONS; }
                if (gp.ui.commandNum == 3) { gp.ui.commandNum = 0; gp.currentState = GameState.MENU; }
            }
            if (code == KeyEvent.VK_ESCAPE) { gp.currentState = GameState.PLAY; }
        }
        else if (gp.currentState == GameState.LOGO) {
            if (code == KeyEvent.VK_SPACE || code == KeyEvent.VK_ENTER) gp.currentState = GameState.MENU;
        }
        else if (gp.currentState == GameState.PLAY) {
            if (code == KeyEvent.VK_ESCAPE) { gp.currentState = GameState.PAUSE; gp.ui.commandNum = 0; }
            if (code == p1.upCode) p1.up = true; if (code == p1.downCode) p1.down = true; if (code == p1.leftCode) p1.left = true; if (code == p1.rightCode) p1.right = true; if (code == p1.bombCode) p1.bomb = true;
            if (code == p2.upCode) p2.up = true; if (code == p2.downCode) p2.down = true; if (code == p2.leftCode) p2.left = true; if (code == p2.rightCode) p2.right = true; if (code == p2.bombCode) p2.bomb = true;
            if (code == p3.upCode) p3.up = true; if (code == p3.downCode) p3.down = true; if (code == p3.leftCode) p3.left = true; if (code == p3.rightCode) p3.right = true; if (code == p3.bombCode) p3.bomb = true;
            if (code == p4.upCode) p4.up = true; if (code == p4.downCode) p4.down = true; if (code == p4.leftCode) p4.left = true; if (code == p4.rightCode) p4.right = true; if (code == p4.bombCode) p4.bomb = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (gp.currentState == GameState.PLAY) {
            if (code == p1.upCode) p1.up = false; if (code == p1.downCode) p1.down = false; if (code == p1.leftCode) p1.left = false; if (code == p1.rightCode) p1.right = false; if (code == p1.bombCode) p1.bomb = false;
            if (code == p2.upCode) p2.up = false; if (code == p2.downCode) p2.down = false; if (code == p2.leftCode) p2.left = false; if (code == p2.rightCode) p2.right = false; if (code == p2.bombCode) p2.bomb = false;
            if (code == p3.upCode) p3.up = false; if (code == p3.downCode) p3.down = false; if (code == p3.leftCode) p3.left = false; if (code == p3.rightCode) p3.right = false; if (code == p3.bombCode) p3.bomb = false;
            if (code == p4.upCode) p4.up = false; if (code == p4.downCode) p4.down = false; if (code == p4.leftCode) p4.left = false; if (code == p4.rightCode) p4.right = false; if (code == p4.bombCode) p4.bomb = false;
        }
    }
}