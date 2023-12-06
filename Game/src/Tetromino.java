
public class Tetromino {
	//Tetromino 모양 정의
	public enum Tetrominoes {
		STRAIGHT, SQUARE, T_SHAPE, L_SHAPE, SKEW
	}
	
	private int [][][] tetrominoShapes = {
		//STRAIGHT
		{
	        { 0, -1 }, { 0, 0 }, { 0, 1 }, { 0, 2 }
	    },
	    // SQUARE
	    {
	        { 0, 0 }, { 0, 1 }, { 1, 0 }, { 1, 1 }
	    },
	    // T_SHAPE
	    {
	        { 0, 0 }, { 1, 0 }, { -1, 0 }, { 0, 1 }
	    },
	    // L_SHAPE
	    {
	        { 0, 0 }, { 0, -1 }, { 0, 1 }, { 1, 1 }
	    },
	    // SKEW
	    {
	        { 0, 0 }, { 0, -1 }, { 1, 0 }, { 1, 1 }
	    }
	};
	
	private int[][] currentShape;
	
}