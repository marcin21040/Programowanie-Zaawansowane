package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GameMarcinStart extends JFrame {
    private int[][] dungeonMap;  // Plansza gry (0 - puste pole, 1 - ściana)
    private int playerX, playerY;  // Pozycja gracza na planszy

    public GameMarcinStart() {
        setTitle("Gra Marcina");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Inicjalizacja planszy (prosta plansza z 1 jako ściany)
        dungeonMap = new int[][] {
                { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
                { 1, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
                { 1, 0, 1, 1, 1, 1, 1, 0, 0, 1 },
                { 1, 0, 0, 0, 0, 0, 0, 1, 0, 1 },
                { 1, 0, 1, 1, 0, 0, 0, 1, 0, 1 },
                { 1, 0, 0, 0, 0, 0, 0, 1, 0, 1 },
                { 1, 0, 1, 0, 1, 1, 0, 0, 0, 1 },
                { 1, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
                { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 }
        };

        // Pozycja startowa gracza
        playerX = 1;
        playerY = 1;

        // Panel gry
        GamePanel gamePanel = new GamePanel();
        add(gamePanel);

        // Obsługa klawiatury (ruch gracza)
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                movePlayer(e.getKeyCode());
                gamePanel.repaint();  // Odświeżanie okna po ruchu gracza
            }
        });

        setFocusable(true);
    }

    // Ruch gracza w zależności od naciśniętego klawisza
    private void movePlayer(int keyCode) {
        int newX = playerX;
        int newY = playerY;

        switch (keyCode) {
            case KeyEvent.VK_UP:
                newY--;  // Ruch w górę
                break;
            case KeyEvent.VK_DOWN:
                newY++;  // Ruch w dół
                break;
            case KeyEvent.VK_LEFT:
                newX--;  // Ruch w lewo
                break;
            case KeyEvent.VK_RIGHT:
                newX++;  // Ruch w prawo
                break;
        }

        // Sprawdzanie, czy nowa pozycja jest pusta (0 - puste pole)
        if (dungeonMap[newY][newX] == 0) {
            playerX = newX;
            playerY = newY;
        }
    }

    // Panel do rysowania mapy i gracza
    class GamePanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // Rozmiar pojedynczego kafelka
            int tileSize = 50;

            // Rysowanie planszy
            for (int y = 0; y < dungeonMap.length; y++) {
                for (int x = 0; x < dungeonMap[y].length; x++) {
                    if (dungeonMap[y][x] == 1) {
                        g.setColor(Color.BLACK);  // Ściana
                    } else {
                        g.setColor(Color.WHITE);  // Puste pole
                    }
                    g.fillRect(x * tileSize, y * tileSize, tileSize, tileSize);
                    g.setColor(Color.GRAY);
                    g.drawRect(x * tileSize, y * tileSize, tileSize, tileSize);  // Siatka
                }
            }

            // Rysowanie gracza
            g.setColor(Color.RED);
            g.fillOval(playerX * tileSize, playerY * tileSize, tileSize, tileSize);
        }
    }

    public void startGame() {
        setVisible(true);
    }
}
