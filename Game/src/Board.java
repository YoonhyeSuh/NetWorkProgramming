import java.awt.event.ActionListener;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.JPanel;
public class Board extends JPanel {
    private Tetromino currentTetromino;
    private final int BOARD_WIDTH = 10;
    private final int BOARD_HEIGHT = 20;
    private final int DELAY = 300; // Timer delay for tetromino movement

public class Board extends JPanel implements ActionListener {
    private boolean[][] boardMatrix;

    public Board() {
        initBoard();
        currentTetromino = new Tetromino(Tetromino.Tetrominoes.STRAIGHT);

        setFocusable(true);
        addKeyListener(new TetrisKeyListener());
        startGame();
    }

    private void initBoard() {
        boardMatrix = new boolean[BOARD_HEIGHT][BOARD_WIDTH];
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                boardMatrix[i][j] = false;
            }
        }
    }

    private void startGame() {
        Timer timer = new Timer(DELAY, e -> {
            currentTetromino.moveDown(); // Move the tetromino down
            repaint();
        });
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw the game board
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                if (boardMatrix[i][j]) {
                    g.setColor(Color.BLUE); // Example color for filled cells
                    g.fillRect(j * 30, i * 30, 30, 30);
                }
            }
        }

        // Draw the current tetromino
        g.setColor(Color.RED); // Example color for the current tetromino
        int[][] shape = currentTetromino.getCurrentShape();
        for (int i = 0; i < shape.length; i++) {
            int x = shape[i][0] + currentTetromino.getX();
            int y = shape[i][1] + currentTetromino.getY();
            g.fillRect(x * 30, y * 30, 30, 30);
        }
    }

    private class TetrisKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int keyCode = e.getKeyCode();
            if (keyCode == KeyEvent.VK_LEFT) {
                currentTetromino.moveLeft(); // Move the tetromino left
                repaint();
            } else if (keyCode == KeyEvent.VK_RIGHT) {
                currentTetromino.moveRight(); // Move the tetromino right
                repaint();
            } else if (keyCode == KeyEvent.VK_UP) {
                currentTetromino.rotateClockwise(); // Rotate the tetromino clockwise
                repaint();
            } else if (keyCode == KeyEvent.VK_DOWN) {
                currentTetromino.rotateCounterclockwise(); // Rotate the tetromino counterclockwise
                repaint();
            }
            // Implement other controls as needed
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Tetris");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 600);
        frame.setLocationRelativeTo(null);
        Board board = new Board();
        frame.add(board);
        frame.setVisible(true);
    }
}