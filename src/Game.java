import java.awt.Image;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.awt.FontMetrics;
import java.awt.Font;

public class Game extends GameEngine {
	//Objects
	private Board board; 
	private Snake snake;
	//Game states
	private boolean gameOver, inGame, paused, help, options, gameWon;
	//Font
	private FontMetrics metrics;
	private Font font;
	private String endText;
	private int largeFont;
	//Window
	private int screenWidth, screenHeight;
	private static int cellRows, cellCols;
	private String dimension;
	//Game
	private Snake.Direction direction;
	private ArrayList<Integer> directions;
	private Boolean frameUpdate = false;
	private int cellSize;
	private Cell initPos;
	private int randGem;
	private static int warpZones;
	private static boolean extraWalls;
	//Animation states
	private boolean makeExplosion, makeParticle, makeFire;
	private char makeScoreChange;
	//Animation position
	private ArrayList<Cell> particlePos;
	private Cell explosionPos, scoreChangePos, firePos;
	//Animation frames
	private Image[] warpFrames, explosionFrames, lavaFrames, particleFrames, fireFrames;
	private Integer[] scoreChangeFrames;
	private int warpFrame, lavaFrame, explosionFrame, fireFrame, particleFrame, scoreChangeFrame;
	//Images
	private Image wall,head,body,diamond,emerald,amathyst,tnt,title,brokenWall
		,empty,background,infoBar,warpZone,lava,explosion,particle,optionsTitle
		,helpTitle,controlsTitle,fire;

	public static void main (String args[])  {
		createGame (new Game(),30);
	}
	
	public void init() {		
		//set default options
		dimension = "square";
		warpZones = 2;
		extraWalls = true;	
		cellSize = 20;
		largeFont = cellSize*2;
		inGame = false; 
		newGame();
	}

	public void update(double dt) {
		frameUpdate = (frameUpdate == true) ? false : true;
		if (!gameOver && !paused) {
			//move cell on frame update, change direction when move is complete
			if (frameUpdate == true){
				snake.move(inGame);
				if (inGame) {
					checkCollision();
					checkSnake();
				}
			} else {
				directions();
			}
		}
		//draw animations even when game ends to allow ending animations
		animations();
	}

	private void setWindow() {
		if (!inGame || (dimension == "square")){
			screenWidth = 600; screenHeight = 600;
		} else if (inGame && dimension == "rectangle") {
			screenWidth = 800; screenHeight = 400;
		}
		setWindowSize (screenWidth, screenHeight);
	}

	public void loadImages(){
		//Images
		wall = loadImage("src/resources/wall.png");
		infoBar = loadImage("src/resources/infoBar.png");
		empty = loadImage("src/resources/empty.png");
		brokenWall = loadImage("src/resources/brokenWall.png");
		head = loadImage("src/resources/head.png");
		body = loadImage("src/resources/body.png");
		diamond = loadImage("src/resources/diamond.png");
		emerald = loadImage("src/resources/emerald.png");
		amathyst = loadImage("src/resources/amathyst.png");
		tnt = loadImage("src/resources/tnt.png");
		title = loadImage("src/resources/title.png");
		background = loadImage("src/resources/background.png");
		warpZone = loadImage("src/resources/warpZone.png");
		lava = loadImage("src/resources/lava.png");
		explosion = loadImage("src/resources/explosion.png");
		particle = loadImage("src/resources/particle.png");
		fire = loadImage("src/resources/fire.png");
		optionsTitle = loadImage("src/resources/optionsTitle.png");
		helpTitle = loadImage("src/resources/helpTitle.png");
		controlsTitle = loadImage("src/resources/controlsTitle.png");

		//Animations
		warpFrames = new Image[32];
		explosionFrames = new Image[16];
		particleFrames = new Image[32];
		fireFrames = new Image[32];
		scoreChangeFrames = new Integer[16];
		lavaFrames = new Image[32];
		particlePos = new ArrayList<Cell>();
		//Animation sub images are 16x16px
		for (int i = 0; i < warpFrames.length; i++) {
			warpFrames[i] = subImage(warpZone, 0, i*16, 16, 16);			
		}
		for (int i = 0; i < explosionFrames.length; i++) {
			explosionFrames[i] = subImage(explosion, 0, i*16, 16, 16);			
		}
		for (int i = 0; i < particleFrames.length; i++) {
			particleFrames[i] = subImage(particle, 0, i*16, 16, 16);			
		}
		for (int i = 0; i < fireFrames.length; i++) {
			fireFrames[i] = subImage(fire, 0, i*16, 16, 16);			
		}
		for (int i = 0; i < lavaFrames.length; i++) {
			lavaFrames[i] = subImage(lava, 0, i*16, 16, 16);			
		}
	}

	public void newGame(){
		loadImages();
		//Board options 
		setWindow();
		cellRows = screenWidth/cellSize;
		cellCols = screenHeight/cellSize;
		board = new Board(cellRows,cellCols);
		//Snake starts 3 cells long when playing, on the menu it is 18
		if (inGame){
			initPos = new Cell(cellRows/2,cellCols/2);
			snake = new Snake(3,initPos);
		} else {
			initPos = new Cell(20,25);
			snake = new Snake(18,initPos);
		}
		//reset animations
		gameOver = gameWon = help =	paused = makeFire = false;
		makeExplosion = makeParticle = makeFire = false;
		warpFrame = lavaFrame = explosionFrame = fireFrame = particleFrame = scoreChangeFrame = 0;
		makeScoreChange = ' ';
		//generate iems
		if (inGame){
			board.generateItem("gem", initPos, snake.getSnake(), snake.getSnakeSize());
			randGem = rand(3);
			board.generateItem("tnt", initPos, snake.getSnake(), snake.getSnakeSize());
		}
		//reset directions and end text
		directions = new ArrayList<Integer>();
		direction = snake.getDirection();
		endText = "";
	}
	
	public static int getRows(){ return cellRows; }
	public static int getCols(){ return cellCols; }
	public static int getWarpZones(){ return warpZones; }
	public static boolean getExtraWalls(){ return extraWalls; }

	// Help with centering text
	// https://www.tutorialspoint.com/what-are-the-differences-between-a-font-and-a-fontmetrics-in-java
	//Takes width of input string and font size then centers by subtracting string width from screenwidth and / 2
	private void centerText(int y, String str, int size){
		font = new Font("Verdana", Font.PLAIN, size);
		metrics = mGraphics.getFontMetrics(font);
		//centerTextY = ((screenHeight - metrics.getHeight()) / 2) + metrics.getAscent();
		int centerX = (screenWidth - metrics.stringWidth(str)) / 2;
		drawText(centerX,y,str,"Verdana",size);
	}
	private void centerBoldText(int y, String str, int size){
		font = new Font("Verdana",Font.BOLD, size);
		metrics = mGraphics.getFontMetrics(font);
		int centerX = (screenWidth - metrics.stringWidth(str)) / 2;
		drawBoldText(centerX,y,str,"Verdana",size);
	}

	public void animations(){
		//constant animations
		warpFrame = (warpFrame + 1) % 32;
		lavaFrame = (lavaFrame + 1) % 32;
		//conditional animations
		if (makeFire) {
			if (fireFrame < fireFrames.length-1) {
				fireFrame++;
			} else {
				fireFrame = 0;
			}
		}
		if (makeExplosion) {
			if (explosionFrame < explosionFrames.length-1) {
				explosionFrame++;
			} else {
				explosionFrame = 0;
				makeExplosion = false;
			}
		}
		if (makeScoreChange != ' ') {
			if (scoreChangeFrame < scoreChangeFrames.length-1) {
				scoreChangeFrame++;
			} else {
				scoreChangeFrame = 0;
				makeScoreChange = ' ';
			}
		}
		if (makeParticle) {
			if (particleFrame < particleFrames.length-1) {
				particleFrame++;
			} else {
				particleFrame = 0;
				particlePos.clear();
				makeParticle = false;
			}
		}
	}

	public void directions(){
		//only allow direction not opposite from current direction
		if (directions.size() > 0) {
			if ((directions.get(0) == KeyEvent.VK_LEFT) && (direction != Snake.Direction.RIGHT)) {
				snake.setDirection(Snake.Direction.LEFT);
			}
			else if ((directions.get(0) == KeyEvent.VK_RIGHT) && (direction != Snake.Direction.LEFT)) {
				snake.setDirection(Snake.Direction.RIGHT);
			}
			else if ((directions.get(0) == KeyEvent.VK_UP) && (direction != Snake.Direction.DOWN)) {
				snake.setDirection(Snake.Direction.UP);
			}
			else if ((directions.get(0) == KeyEvent.VK_DOWN) && (direction != Snake.Direction.UP)) {
				snake.setDirection(Snake.Direction.DOWN);
			}
			directions.remove(0);
		}
	}
	
	public void checkCollision() 
	{
		Cell head =  snake.getPart(0);
		String cellType = board.getCell(head).getCellType();
		int snakeSize = snake.getSnakeSize();
		//check collision with object
		switch (cellType){
			case "wall":
				endText = "Walls hurt!";
				explosionPos = head;
				makeExplosion = true;
				gameOver = true;
				break;
			case "lava":
				explosionPos = head;
				makeFire = true;
				firePos = head;
				makeExplosion = true;
				endText = "Lava burns!";
				gameOver = true;
				break;
			case "gem":
				snake.grow();
				board.generateItem("gem", head, snake.getSnake(), snake.getSnakeSize());
				randGem = rand(3);
				scoreChangePos = head;
				makeScoreChange = '+';
				break;
			case "tnt":
				snake.shrink();
				board.generateItem("tnt", head, snake.getSnake(),snake.getSnakeSize());
				explosionPos = head;
				makeExplosion = true;
				scoreChangePos = head;
				makeScoreChange = '-';
				break;
			case "warp":
				particlePos.add(head);
				makeParticle = true;
				break;
		}		
		//Self collision removes end of tail to section hit and turn into wall
		//except section hit to allow snake to pass 
		for (int i = snakeSize-2; i > 0; i--) { 
			if (head.getRow() == snake.getPartRow(i) && head.getCol() == snake.getPartCol(i)) { 
				for (int j = i + 1; j < snakeSize; j++){
					board.getCell(snake.getPart(j)).setCellType("wall");
					explosionPos = snake.getPart(i);
					makeExplosion = true;
				}
				//remove lives by and reduce snake size
				snake.setLives(snake.getLives()-1);
				snake.setSnakeSize((snakeSize-(snakeSize-i)));
			}		
		}
	}

	public void checkSnake(){
		if (snake.getSnakeSize() >= 20){
			endText = "Max Score!";
			gameOver = true;
			gameWon = true;
		}
		//Minimum size for snake is a head and tail piece
		if (snake.getSnakeSize() <= 1){
			endText = "Avoid TNT!";
			gameOver = true;
		}
		if (snake.getLives() < 1){
			endText = "No lives!";
			gameOver = true;
		}
	} 
	
	public void keyPressed(KeyEvent event) {
		int key = event.getKeyCode();
		//Direction added to array and processed per move update to stop multi key presses
		//that allowed snake to go back on itself
		direction = snake.getDirection();
		if (!gameOver && inGame && !paused) {
			directions.add(key);
		}
		//Start menu
		if (key == KeyEvent.VK_P && inGame && !gameOver){
			paused = (paused) ? false : true;
		} 
		if (key == KeyEvent.VK_ENTER && !inGame && !(help || options)){
			inGame = true;
			newGame();
		} 
		if (key == KeyEvent.VK_H && !inGame && !options){
			help = true;
		} 
		if (key == KeyEvent.VK_O && !inGame && !help){
			options = true;
		}
		if (key == KeyEvent.VK_ESCAPE){
			if (inGame) {
				paused = false;
				inGame = false;
				newGame();
			} else if (options || help) {
				options = false;
				help = false;
			} else {
				System.exit(13);
			}
		}
		//Options
		if ((key == KeyEvent.VK_S) && (options)) {
			dimension = "square";
		} else if ((key == KeyEvent.VK_R) && (options)) {
			dimension = "rectangle";
		} else if ((key == KeyEvent.VK_0) && (options)) {
			warpZones = 0;
		} else if ((key == KeyEvent.VK_2) && (options)) {
			warpZones = 2;
		} else if ((key == KeyEvent.VK_4) && (options)) {
			warpZones = 4;
		}

		//Game over and options screen use the same Y/N keys
		if (key == KeyEvent.VK_Y) {
			if (gameOver) {
				newGame();
			} else if (options){
				extraWalls = true;
			}
		} else if (key == KeyEvent.VK_N) {
			if (gameOver) {
				inGame = false;
				newGame();
			} else if (options){
				extraWalls = false;
			}
		}
	}

	public void drawInfo(){
		changeColor(white);
		drawText(20, 55, "Lives: " + snake.getLives());
		drawText(screenWidth - (snake.getSnakeSize() < 10 ? cellSize*10 : cellSize*11), 55, "Score: " + snake.getSnakeSize());
	}

	private void drawHelpScreen(){
		drawImage(helpTitle, 250, 35, 125,40);
		changeColor(white);

		drawText(50, 100, "The goal of the game is to collect 20 gems", "Verdana", cellSize);
		drawText(50, 100 + cellSize, "while avoiding TNT and lava, walls or yourself.", "Verdana", cellSize);

		drawText(50, 100 + cellSize * 3, "The snake will constantly move in one direction", "Verdana", cellSize);
		
		drawText(50, 100 + cellSize * 5, "Hitting TNT will reduce your score by 1","Verdana", cellSize);
		
		drawText(50, 100 + cellSize * 7, "Hitting a wall or lava will end the game.","Verdana", cellSize);
		drawText(50, 100 + cellSize * 8, "However edges can have warp zones which will","Verdana", cellSize);
		drawText(50, 100 + cellSize * 9, "move the snake to the opposite side of the board.","Verdana", cellSize);

		drawText(50, 100 + cellSize * 11, "Hitting your tail will reduce its size and remove","Verdana", cellSize);
		drawText(50, 100 + cellSize * 12, "a life also creating a wall on the board to avoid","Verdana", cellSize);	

		drawImage(controlsTitle, 125, cellSize*19.5, 350,125);

		centerBoldText(100 + (cellSize * 22), "Press Escape to go to main menu", cellSize);
	}

	public void drawOptionsScreen(){
		drawImage(optionsTitle, 200, 35, 200,50);
		//Window dimensions
		changeColor(orange);
		centerBoldText(100 + cellSize, "- Set window dimensions -", cellSize);
		changeColor((dimension == "square") ? green : white);
		drawText(screenWidth/3, 100 + cellSize * 3, "[S] for square", "verdana", cellSize);
		changeColor((dimension == "rectangle") ? green : white);
		drawText(screenWidth/3, 100 + cellSize * 5, "[R] for rectangle", "verdana", cellSize);
		//Warp zones
		changeColor(orange);
		centerBoldText(100 + cellSize * 7, "- Number of warp zones -", cellSize);
		changeColor((warpZones == 0) ? green : white);
		drawText(screenWidth/3, 100 + cellSize * 9, "[0] No zones", "verdana", cellSize);
		changeColor((warpZones == 2) ? green : white);
		drawText(screenWidth/3, 100 + cellSize * 11, "[2] Only sides", "verdana", cellSize);
		changeColor((warpZones == 4) ? green : white);
		drawText(screenWidth/3, 100 + cellSize * 13, "[4] All edges", "verdana", cellSize);
		//Extra obstacles
		changeColor(orange);
		centerBoldText(100 + cellSize * 15, "- Add Extra obstacles -", cellSize);
		changeColor(extraWalls ? green : white);
		drawText(screenWidth/3, 100 + cellSize * 17, "[Y] Obstacles on board", "verdana", cellSize);
		changeColor(!extraWalls ? green : white);
		drawText(screenWidth/3, 100 + cellSize * 19, "[N] No Obstacles", "verdana", cellSize);
		changeColor(white);
		centerBoldText(100 + (cellSize * 22), "Press Escape to go to main menu", cellSize);
	}

	public void drawStartMenu(){
		drawImage(title, 165, 35);
		changeColor(white);
		drawText(140, 230, "[Enter] To Play");
		drawText(140, 310, "[O] For Options");
		drawText(140, 390, "[H] For Help");
		drawText(140, 470, "[ESC] To Quit");
		//draws lava in the corners
		drawImage(lavaFrames[lavaFrame], 40, 40, cellSize, cellSize);
		drawImage(lavaFrames[lavaFrame], 60, 40, cellSize, cellSize);
		drawImage(lavaFrames[lavaFrame], 40, 60, cellSize, cellSize);

		drawImage(lavaFrames[lavaFrame], 540, 40, cellSize, cellSize);
		drawImage(lavaFrames[lavaFrame], 520, 40, cellSize, cellSize);
		drawImage(lavaFrames[lavaFrame], 540, 60, cellSize, cellSize);

		drawImage(lavaFrames[lavaFrame], 40, 520, cellSize, cellSize);
		drawImage(lavaFrames[lavaFrame], 60, 540, cellSize, cellSize);
		drawImage(lavaFrames[lavaFrame], 40, 540, cellSize, cellSize);

		drawImage(lavaFrames[lavaFrame], 520, 540, cellSize, cellSize);
		drawImage(lavaFrames[lavaFrame], 540, 540, cellSize, cellSize);
		drawImage(lavaFrames[lavaFrame], 540, 520, cellSize, cellSize);

		drawImage(warpFrames[warpFrame], 0, 280, cellSize, cellSize);
		drawImage(warpFrames[warpFrame], 0, 300, cellSize, cellSize);
		drawImage(warpFrames[warpFrame], 0, 320, cellSize, cellSize);

		drawImage(warpFrames[warpFrame], 580, 280, cellSize, cellSize);
		drawImage(warpFrames[warpFrame], 580, 300, cellSize, cellSize);
		drawImage(warpFrames[warpFrame], 580, 320, cellSize, cellSize);

	}
	//interpolation help from http://paulbourke.net/miscellaneous/interpolation/
	//snake cell moves on second frame, inbetween move % of distance between current and next cell
	public double smoothMove(double start, double end, double percent){
		if (frameUpdate == true || paused || gameOver){
			return start;
		} else {	
			return start + (end - start ) * percent;
		}
	}

	public void drawSnake(){
		//percent set to half of the distance between the current and next cell
		double percent = 0.5;
		Cell snakeHead = snake.getPart(0);
		direction = snake.getDirection();
		//draw snake head interpolated to the next cell
		if (direction == Snake.Direction.LEFT || direction == Snake.Direction.RIGHT){
			drawImage((endText == "Walls hurt!") ? brokenWall : head, smoothMove(snakeHead.getRow(),(snakeHead.getRow()+1*direction.value),percent)*cellSize, snakeHead.getCol()*cellSize, cellSize, cellSize);	
		} else {
			drawImage((endText == "Walls hurt!") ? brokenWall : head, snakeHead.getRow()*cellSize, smoothMove(snakeHead.getCol(),(snakeHead.getCol()+1*direction.value),percent)*cellSize, cellSize, cellSize);
		}
		//body inherits position of previous section and interpolates between
		for(int i = 1; i < snake.getSnakeSize(); i++) {
			if (!(snake.getPartRow(i) <= 0 || snake.getPartRow(i) >= cellRows-1 || snake.getPartCol(i) <= 0 || snake.getPartCol(i) >= cellCols-1)){
				drawImage(body, smoothMove(snake.getPartRow(i),snake.getPartRow(i-1),percent)*cellSize, smoothMove(snake.getPartCol(i),snake.getPartCol(i-1),percent)*cellSize, cellSize, cellSize);
			}
		}
	}

	public void drawBoard(){
		//draw each cell based on it's type
		for(int x = 0; x < cellRows; x++) {
			for (int y = 0; y < cellCols; y++){
				String cellType = board.getCell()[x][y].getCellType();
				switch (cellType) {
					case "wall":
						drawImage(wall, x*cellSize, y*cellSize, cellSize, cellSize);
						break;
					case "gem":
						drawRandomGem(x,y);
						break;
					case "tnt":
						drawImage(tnt, x*cellSize, y*cellSize, cellSize, cellSize);
						break;
					case "empty":
						drawImage(empty, x*cellSize, y*cellSize, cellSize, cellSize);
						break;
					case "infoBar":
						drawImage(infoBar, x*cellSize, y*cellSize, cellSize, cellSize);
						break;
					case "warp":
						drawImage(warpFrames[warpFrame], x*cellSize, y*cellSize, cellSize, cellSize);
						break;
					case "lava":
						drawImage(lavaFrames[lavaFrame], x*cellSize, y*cellSize, cellSize, cellSize);
						break;
				}
			}		
		}	
		drawInfo();
	}
	//each generation select random gem to display
	public void drawRandomGem(int x, int y){
		switch (randGem) {
			case 0:
				drawImage(diamond,x*cellSize,y*cellSize,cellSize,cellSize);
				break;
			case 1:
				drawImage(emerald,x*cellSize,y*cellSize,cellSize,cellSize);
				break;
			case 2:
				drawImage(amathyst,x*cellSize,y*cellSize,cellSize,cellSize);
				break;
		}
	}
	
	public void drawEndScreen(){
		//draw rectangle and end text with color and content based on condition of game ending
		changeColor(gameWon ? darkGreen : red);
		drawSolidRectangle(screenWidth/6, screenHeight/2-largeFont, screenWidth/1.5, cellSize*7);
		changeColor(gameWon ? white : yellow);
		drawRectangle(screenWidth/6, screenHeight/2-largeFont, screenWidth/1.5, cellSize*7);

		changeColor(white);
		centerBoldText(screenHeight/2, (gameWon ? "You Won!" : "Game Over!"), largeFont);
		centerText(screenHeight/2 + largeFont, "- " + endText + " -", largeFont);
		centerText(screenHeight/2 + largeFont*2, "Play Again? Y/N", largeFont);
	}

	public void drawAnimations(){
		//draws explosion on obstacle hit and if game has ended draws "Ow" text
		if(makeExplosion){
			drawImage(explosionFrames[explosionFrame], (explosionPos.getRow()*cellSize)-explosionFrame*2, (explosionPos.getCol()*cellSize)-explosionFrame*2, cellSize*2+explosionFrame*2, cellSize*2+explosionFrame*2);
			if (gameOver)	{
				changeColor(orange);
				if (explosionPos.getRow() < cellRows/2)	{
					drawText((explosionPos.getRow()*cellSize)+explosionFrame, (explosionPos.getCol()*cellSize)-explosionFrame, "Ow!", "verdana", cellSize);
				} else {
					drawText((explosionPos.getRow()*cellSize)-explosionFrame-cellSize*2, (explosionPos.getCol()*cellSize)-explosionFrame, "Ow!", "verdana", cellSize);
				}
			}
		}
		//fire for if lava has been touched
		if (makeFire) {
			drawImage(fireFrames[fireFrame], (firePos.getRow()*cellSize), (firePos.getCol()*cellSize), cellSize, cellSize);
		}
		//display +/-1 for loss or gain of score
		if(makeScoreChange != ' '){
			changeColor(makeScoreChange == '+' ? white : red);
			drawText((scoreChangePos.getRow()*cellSize), (scoreChangePos.getCol()*cellSize)-scoreChangeFrame*2, makeScoreChange == '+' ? "+1" : "-1", "verdana", cellSize);
		}
		//particles for movement through portal
		if(makeParticle){
			for (int i = 0; i < particlePos.size(); i++){
				drawImage(particleFrames[particleFrame], (particlePos.get(i).getRow()*cellSize)-cellSize, (particlePos.get(i).getCol()*cellSize)-cellSize, cellSize*3, cellSize*3);
			}
		}
	}

	public void paintComponent() {
		changeBackgroundColor(black);
		clearBackground(screenWidth,screenHeight);
		changeColor(white);
		//if not in game draw start, help or options screens based on user input
		if (!inGame){
			drawImage(background, 0,0,screenWidth,screenHeight);
			if (help){
				drawHelpScreen();
			} else if (options) {
				drawOptionsScreen();
			} else { 
				drawStartMenu();
				drawSnake();
			}
		//otherwise draw game and endscreen if needed 
		} else {
			drawBoard();
			drawSnake();
			drawAnimations();
			if(gameOver){
				drawEndScreen();
			}
		}
		//pause screen
		if (paused) {
			changeColor(red);
			drawSolidRectangle(largeFont, 100, screenWidth-75, 75);
			changeColor(yellow);
			drawRectangle(largeFont, 100, screenWidth-75, 75);
			changeColor(white);
			centerBoldText(150, "Paused - P to continue", cellSize * 2);
		}
	}
}

//Copyright 2021, Caleb Ward with GameEngine class provided by Massey Univeristy and images from Minecraft®