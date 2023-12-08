
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
	    private Tetrominoes tetrominoType;

	    public Tetromino(Tetrominoes tetrominoType) {
	        this.tetrominoType = tetrominoType;
	        setCurrentShape();
	    }

	    private void setCurrentShape() {
	        currentShape = tetrominoShapes[tetrominoType.ordinal()];
	    }

	    public int[][] getCurrentShape() {
	        return currentShape;
	    }

	    public Tetrominoes getTetrominoType() {
	        return tetrominoType;
	    }

	    // 테트로미노의 현재 모양을 시계방향으로 회전
	    public void rotateClockwise() {
	        for (int i = 0; i < currentShape.length; i++) {
	            int x = currentShape[i][0];
	            currentShape[i][0] = currentShape[i][1];
	            currentShape[i][1] = -x;
	        }
	    }

	    // 테트로미노의 현재 모양을 반시계방향으로 회전
	    public void rotateCounterclockwise() {
	        for (int i = 0; i < currentShape.length; i++) {
	            int x = currentShape[i][0];
	            currentShape[i][0] = -currentShape[i][1];
	            currentShape[i][1] = x;
	        }
	    }
	
}
