package com.fatalcubez.game;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Random;

public class GameBoard {
    public static final int ROWS = 4;  //we can change row and col here(5*5 or 6*8 etc....)
    public static final int COLS = 4;

    private final int startingTiles = 2;
    private final BufferedImage finalBoard;
    private Tile[][] board;
    private boolean dead;
    private boolean won;
    private BufferedImage gameBoard;
    private int x;
    private int y;
    private static int SPACING = 10;
    public static int BOARD_WIDTH = (COLS + 1) * SPACING + COLS * Tile.WIDTH;
    public static int BOARD_HEIGHT = (ROWS + 1) * SPACING + ROWS * Tile.HEIGHT;
    private boolean hasStarted; // this is for timer

    public GameBoard(int x, int y) {
        this.x = x;
        this.y = y;
        board = new Tile[ROWS][COLS];
        gameBoard = new BufferedImage(BOARD_WIDTH, BOARD_HEIGHT, BufferedImage.TYPE_INT_RGB);
        finalBoard = new BufferedImage(BOARD_WIDTH, BOARD_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        createBoardImage();
        start();
    }

    //draw gameboard
    private void createBoardImage() {
        //draw bound and background
        Graphics2D g = (Graphics2D) gameBoard.getGraphics();
        g.setColor(Color.darkGray); // background color
        g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT); //then draw background whole rectangle

        g.setColor(Color.lightGray);// set color for each tile

        //create 16-tile position in gameboard
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                int x = SPACING + SPACING * col + Tile.WIDTH * col;  // x position(1st col is 0, and then to cols)
                int y = SPACING + SPACING * row + Tile.HEIGHT * row; // y position(1st row is 0, and then to rows)
                g.fillRoundRect(x, y, Tile.WIDTH, Tile.HEIGHT, Tile.ARC_WIDTH, Tile.ARC_HEIGHT);// draw 16 rectangles

            }
        }
    }

    private void start() {
        for (int i = 0; i < startingTiles; i++) {  //since startingtile is 2, put 2 random spawns on checkboard
            spawnRandom();
        }
//        spawn(0,0,2);
//        spawn(0,1,2);
//        spawn(0,2,2);
//        spawn(0,3,2);

    }

//    private void spawn(int row, int col; int value) {
//        board[row][col] = new Tile(value, getTileX(col), getTileY(row));
//    }

    private void spawnRandom() {
        Random random = new Random();
        boolean notValid = true; // assume the first spot of pics is not valid

        while (notValid) {
            int location = random.nextInt(ROWS * COLS); // 4*4(max=16) or 6*6
            int row = location / ROWS;   // eg- 5/4 = 1 (1,1) and 9/4 (2,1) location
            int col = location % COLS;
            Tile current = board[row][col];

            if (current == null) { //if current is not null, skip that and loop and create random location again
                int value = random.nextInt(10) < 9 ? 2 : 4; //0 to 9 (90% chance of getting 2 and 10%chance of getting 4)
                Tile tile = new Tile(value, getTileX(col), getTileY(row));
                board[row][col] = tile;
                notValid = false;
            }
        }
    }

    public int getTileX(int col) {
        return SPACING + col * Tile.WIDTH + col * SPACING;
    }

    public int getTileY(int row) {
        return SPACING + row * Tile.WIDTH + row * SPACING;
    }


    public void render(Graphics2D g) { //g is virtual white background

        //when rendering,we need to implement our virtual image
        Graphics2D g2d = (Graphics2D) finalBoard.getGraphics();
        g2d.drawImage(gameBoard, 0, 0, null); //put virtualgameboard to final

        //draw tiles
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                Tile current = board[row][col];
                if (current == null) continue; //if current tile is null, we don't need to render it
                current.render(g2d);
            }
        }

        //x,y is global variable now, we're drawing to screen

        g.drawImage(finalBoard, x, y, null); //g is virtual white background and we put finalrealgameboard to virtual white background
        g2d.dispose();
    }

    public void update() {
        checkKeys();

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                Tile current = board[row][col];
                if (current == null) continue;
                current.update();
                resetPosition(current, row, col);
                if (current.getValue() == 2048) {
                    won = true;
                }
            }
        }
    }

    private void resetPosition(Tile tile, int row, int col) {
        Tile current = board[row][col];
        if (current == null) return;

        int x = getTileX(col);
        int y = getTileY(row);

        int distX = current.getX() - x;
        int distY = current.getY() - y;

        if (Math.abs(distX) < Tile.SLIDE_SPEED) { //absolute value of distance X
            current.setX(current.getX() - distX);
        }

        if (Math.abs(distY) < Tile.SLIDE_SPEED) {
            current.setY(current.getY() - distY);
        }

        if (distX < 0) {
            current.setX(current.getX() + Tile.SLIDE_SPEED);
        }
        if (distY < 0) {
            current.setY(current.getY() + Tile.SLIDE_SPEED);
        }
        if (distX > 0) {
            current.setX(current.getX() - Tile.SLIDE_SPEED);
        }
        if (distY > 0) {
            current.setY(current.getY() - Tile.SLIDE_SPEED);
        }
    }
    private boolean checkOutOfBounds(Direction dir, int row, int col) {
        if (dir == Direction.LEFT) {
            return col < 0;
        }
        else if (dir == Direction.RIGHT ) {
            return col > COLS - 1;
        }
        else if (dir == Direction.UP) {
            return row < 0;
        }
        else if (dir == Direction.DOWN) {
            return row > ROWS - 1;
        }
        return false;
    }

    private boolean move(int row, int col, int horizontalDirection, int verticalDirection, Direction dir) {
        boolean canMove = false;

        //if current is null, return false
        Tile current = board[row][col];
        if (current == null) return false;

        boolean move = true;

        int newCol = col;
        int newRow = row;
        while (move) {  // either combine or slide to empty tile, and hasn't reached one of the walls, checking
            newCol += horizontalDirection;
            newRow += verticalDirection;
            if (checkOutOfBounds(dir, newRow, newCol)) break;

            if (board[newRow][newCol] == null) {
                board[newRow][newCol] = current; //move to new place
//                canMove = true;
                board[newRow - verticalDirection][newCol - horizontalDirection] = null; //old place set to null
                board[newRow][newCol].setSlideTo(new Point(newRow, newCol));
                canMove = true;
            }
            else if (board[newRow][newCol].getValue() == current.getValue() && board[newRow][newCol].canCombine()) { // can combine if value equal
                board[newRow][newCol].setCanCombine(false);
                board[newRow][newCol].setValue(board[newRow][newCol].getValue() * 2);
                canMove = true;
                board[newRow - verticalDirection][newCol - horizontalDirection] = null;
                board[newRow][newCol].setSlideTo(new Point(newRow, newCol));
//                board[newRow][newCol].setCombineAnimation(true);
//                scores.setCurrentScore(scores.getCurrentScore() + board[newRow][newCol].getValue());
            }
            else {
                move = false;
            }
        }
        return canMove;
    }

    private void moveTiles(Direction dir) {
        boolean canMove = false;
        int horizontalDirection = 0;
        int verticalDirection = 0;

        if(dir == Direction.LEFT) {
            horizontalDirection =- 1;
            for (int row = 0; row < ROWS; row++) {
                for(int col = 0; col < COLS; col++) {
                    if(!canMove) { //if canMove=false , run this
                        canMove = move(row,col, horizontalDirection, verticalDirection, dir); //this canMove variable will be true or false based on the last tile updating
                    }else move(row,col, horizontalDirection, verticalDirection, dir); // canMove = true, run this
                }
            }
        }

        else if(dir == Direction.RIGHT) {  // 2248  = 0 4 4 8  // 2248 = 0 0 0 16
            horizontalDirection = 1;
            for (int row = 0; row < ROWS; row++) {
                for(int col = COLS-1; col >= 0; col--) {
                    if(!canMove) {
                        canMove = move(row,col, horizontalDirection, verticalDirection, dir);
                    }else move(row,col, horizontalDirection, verticalDirection, dir);
                }
            }
        }

        else if(dir == Direction.UP) {
            verticalDirection =- 1;
            for (int row = 0; row < ROWS; row++) {
                for(int col = 0; col < COLS; col++) {
                    if(!canMove) {
                        canMove = move(row,col, horizontalDirection, verticalDirection, dir);
                    }else move(row,col, horizontalDirection, verticalDirection, dir);
                }
            }
        }

        else if(dir == Direction.DOWN) {
            verticalDirection = 1;
            for (int row = ROWS-1; row >= 0; row--) {
                for(int col = 0; col < COLS; col++) {
                    if(!canMove) {
                        canMove = move(row,col, horizontalDirection, verticalDirection, dir);
                    }else move(row,col, horizontalDirection, verticalDirection, dir);
                }
            }
        } else {
            System.out.println(dir + "is not a valid direction");
        }

        for(int row=0; row < ROWS; row++) {
            for(int col=0; col< COLS; col++) {
                Tile current = board[row][col];
                if(current == null) continue;
                current.setCanCombine(true);
            }
        }

        if(canMove){
            spawnRandom();
            checkDead();
        }
    }

    private void checkDead() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (board[row][col] == null) return;
                if (checkSurroundingTiles(row, col, board[row][col])) {
                    return;
                }
            }
        }
        dead = true;
        //setHighScore(score);
    }

    private boolean checkSurroundingTiles(int row, int col, Tile current) {
        if (row > 0) {
            Tile check = board[row - 1][col];
            if (check == null) return true;
            if (current.getValue() == check.getValue()) return true;
        }
        if (row < ROWS - 1) {
            Tile check = board[row + 1][col];
            if (check == null) return true;
            if (current.getValue() == check.getValue()) return true;
        }
        if (col > 0) {
            Tile check = board[row][col - 1];
            if (check == null) return true;
            if (current.getValue() == check.getValue()) return true;
        }
        if (col < COLS - 1) {
            Tile check = board[row][col + 1];
            if (check == null) return true;
            if (current.getValue() == check.getValue()) return true;
        }
        return false;
    }


    private void checkKeys() {
        if (Keyboard.typed(KeyEvent.VK_LEFT)) {
            //move titles left
            moveTiles(Direction.LEFT);
            if (!hasStarted) hasStarted = true;
        }

        if (Keyboard.typed(KeyEvent.VK_RIGHT)) {
            //move titles right
            moveTiles(Direction.RIGHT);

            if (!hasStarted) hasStarted = true;
        }

        if (Keyboard.typed(KeyEvent.VK_UP)) {
            moveTiles(Direction.UP);
            //move titles up

            if (!hasStarted) hasStarted = true;
        }

        if (Keyboard.typed(KeyEvent.VK_DOWN)) {
            //move titles down
            moveTiles(Direction.DOWN);

            if (!hasStarted) hasStarted = true;
        }
    }

}

