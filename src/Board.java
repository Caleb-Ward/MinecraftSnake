
public class Board {
	private int rowNum, colNum;
	private Cell[][] cell;
	private int infoHeight, warpZones;
	private boolean extraWalls;
 
	
	public Board(int rowNum, int colNum) 
	{
		this.rowNum = rowNum; 
		this.colNum = colNum; 
		this.infoHeight = 3;
		this.warpZones = Game.getWarpZones();
		this.extraWalls = Game.getExtraWalls();
		//the board is made up of cells with types dictated by conditions
		cell = new Cell[rowNum][colNum]; 
		for (int row = 0; row < rowNum; row++) { 
			for (int col = 0; col < colNum; col++) { 
				cell[row][col] = new Cell(row, col);
				//Info Bar where score and lives are displayed
				if ((row > 0 && row < rowNum - 1) && (col > 0 && col < infoHeight)){
					cell[row][col].setCellType("infoBar"); 
				} else if (col == 0 || col == infoHeight || row == 0 || col == colNum-1 || row == rowNum-1){
					//Side warpzones
					if ((warpZones == 2 || warpZones == 4) && (col > colNum/2-2 && col < colNum/2+2)) {
						cell[row][col].setCellType("warp"); 
						//Top+bottom warpzones
					} else if (warpZones == 4 && (row > rowNum/2 - 2 && row < rowNum/2 + 2)) {
						cell[row][col].setCellType("warp"); 
					} else {
						//wall creation for edges
						cell[row][col].setCellType("wall"); 
					}
				//set rest of cells to empty
				} else {
					cell[row][col].setCellType("empty"); 
				}
			} 
		}
		//Gate for top warpzone to stop player entering infobar
		if(warpZones == 4) {
			cell[(rowNum/2-2)][1].setCellType("wall"); 
			cell[(rowNum/2-2)][2].setCellType("wall"); 
			cell[(rowNum/2+2)][1].setCellType("wall"); 
			cell[(rowNum/2+2)][2].setCellType("wall"); 
			for (int i = rowNum/2-1; i < rowNum/2+2; i++) {
				for (int j = 1; j < 3; j++) {
					cell[i][j].setCellType("warp");
				}
			}
		}
		//lava creation if option is set
		if(extraWalls == true) {
			cell[(rowNum/2-5)][(colNum/2-4)].setCellType("lava"); 
			cell[(rowNum/2-4)][(colNum/2-4)].setCellType("lava"); 
			cell[(rowNum/2-5)][(colNum/2-3)].setCellType("lava"); 

			cell[(rowNum/2-5)][(colNum/2+6)].setCellType("lava"); 
			cell[(rowNum/2-4)][(colNum/2+6)].setCellType("lava"); 
			cell[(rowNum/2-5)][(colNum/2+5)].setCellType("lava"); 

			cell[(rowNum/2+5)][(colNum/2-4)].setCellType("lava"); 
			cell[(rowNum/2+4)][(colNum/2-4)].setCellType("lava"); 
			cell[(rowNum/2+5)][(colNum/2-3)].setCellType("lava"); 

			cell[(rowNum/2+5)][(colNum/2+6)].setCellType("lava"); 
			cell[(rowNum/2+4)][(colNum/2+6)].setCellType("lava"); 
			cell[(rowNum/2+5)][(colNum/2+5)].setCellType("lava"); 

		}
	} 
	//Overloaded function to get whole cell or just position
	public Cell[][] getCell() { return cell; }  
	public Cell getCell(Cell get) { return cell[get.getRow()][get.getCol()]; } 
	
	public void setCell(Cell[][] cell) { this.cell = cell; } 

	//random math help from https://www.geeksforgeeks.org/java-math-random-method-examples/
	public void generateItem(String foodType, Cell oldPos, Cell[] snakeParts, int snakeSize){
		int x = (int) (Math.random() * (rowNum - 1) + 1);
		int y = (int) (Math.random() * (colNum - 1) + 1);
		boolean isTaken = false;
		//check snake position to not spawn on parts
		for (int i = 0; i < snakeSize; i++){
			if (x == snakeParts[i].getRow() && y == snakeParts[i].getCol()){
				isTaken = true;
				break;				
			}
		}
		//check if wall or other obstacle
		if (cell[x][y].getCellType() != "empty") {
			isTaken = true;
		} 
		//if cell is taken call function again to generate new numbers
		if (isTaken){
			generateItem(foodType, oldPos,snakeParts, snakeSize);
		//otherwise place new item and set old position to empty
		} else {
			cell[x][y].setCellType(foodType);
			getCell(oldPos).setCellType("empty");
		}
	}
}
