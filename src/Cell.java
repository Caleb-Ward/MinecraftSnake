public class Cell {
	private int row,col;
	private String cellType;
	//each (row,col) is a cell with a type eg wall for game logic
	public Cell(int row, int col) 
	{ 
		this.row = row; 
		this.col = col; 
	} 
	
	public String getCellType() { return cellType; } 
	public void setCellType(String cellType) { this.cellType = cellType; } 

	public int getRow() { return row; } 
	public int getCol() { return col; } 
}