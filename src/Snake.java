public class Snake {
	private int snakeSize;
	private int lives;
	private Cell partsArray[] = new Cell[20];
    public static enum Direction {
        LEFT(-1),
        RIGHT(1),
        UP(-1),
        DOWN(1);

        int value;  
        private Direction(int value){  
            this.value=value; 
        }
    }
	private Direction direction; 

	public Snake(int snakeSize, Cell initPos) {
		this.snakeSize = snakeSize;
		this.partsArray[0] = initPos;
		this.partsArray[0].setCellType("head");
		this.lives = 3;
		direction = Direction.RIGHT;
		for (int i = 1; i < snakeSize; i++) {
			Cell shiftBody = new Cell(initPos.getRow()-i,initPos.getCol());
			partsArray[i] = shiftBody;
			partsArray[i].setCellType("body");
		}
	}

	public void move(Boolean inGame) { 
		//shift snake inheriting current body section from previous section
		for (int i = snakeSize; i > 0; i--) {
			partsArray[i] = partsArray[(i-1)];
			partsArray[i].setCellType("body");
		}
		//move cell horizontal or vertical by enums value *1 eg up decreases cell col by 1 
		//or left decreases row cell by 1 where as right would increase cell by 1
		if (direction == Direction.LEFT || direction == Direction.RIGHT) {
			partsArray[0] = new Cell(getPartRow(0)+(1*direction.value),getPartCol(0));
		} else {
			partsArray[0] = new Cell(getPartRow(0),getPartCol(0)+(1*direction.value));
		}
		//Warp to beginning/end of board if snake goes out of bounds
		if(getPartRow(0) >= Game.getRows()){
			partsArray[0] = new Cell(0,getPartCol(0));
		} else if(partsArray[0].getRow() < 0){
			partsArray[0] = new Cell(Game.getRows()-1,getPartCol(0));
		}
		if(getPartCol(0) >= Game.getCols()){
			partsArray[0] = new Cell(getPartRow(0),0);
		} else if(partsArray[0].getCol() < 0){
			partsArray[0] = new Cell(getPartRow(0),Game.getCols()-1);
		}
		//menu move -- set directions based on col/row values
		if(!inGame){
			if (getPartRow(0) >= 24 && direction == Direction.RIGHT){
				direction = Direction.UP;
			}
			if (getPartCol(0) <= 8 && direction == Direction.UP){
				direction = Direction.LEFT;
			}
			if (getPartRow(0) <= 5 && direction == Direction.LEFT){
				direction = Direction.DOWN;
			}
			if (getPartCol(0) >= 25 && direction == Direction.DOWN){
				direction = Direction.RIGHT;
			}
		}
		partsArray[0].setCellType("head");
	} 
	
	public int getLives() { return lives; } 
	public void setLives(int newLives) { this.lives = newLives; } 

	public int getSnakeSize() { return snakeSize; } 
	public void setSnakeSize(int newSize) { this.snakeSize = newSize; } 

	public int getPartRow(int i) { return partsArray[i].getRow(); } 
	public int getPartCol(int i) { return partsArray[i].getCol(); } 

	public Cell getPart(int i) { return partsArray[i]; } 
	public Cell setPart(int i, Cell newPart) { return partsArray[i] = newPart; }
	
	public int getHeadRow() { return partsArray[0].getRow(); } 
	public int getHeadCol() { return partsArray[0].getCol(); } 
	
	public Cell[] getSnake() { return partsArray; }
	
	public void grow() { snakeSize++; } 
	public void shrink() { snakeSize--; } 

	public Direction getDirection() { return direction; } 
	public void setDirection(Direction dir) { direction = dir; }
}
