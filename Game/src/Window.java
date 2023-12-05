import java.awt.GridLayout;

import javax.swing.JFrame;

public class Window extends JFrame{
	public Window() {
		setTitle("Tetris");
		setSize(400, 800);
		setResizable(false);
		
		setLayout(new GridLayout(1, 2));
		Board gameBoard = new Board(this, 400);
		add(gameBoard);
		gameBoard.start();

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
	}
}
