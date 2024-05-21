package com.fatalcubez.game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;


public class Game extends JPanel implements Runnable, KeyListener, MouseListener, MouseMotionListener{

    private static final long serialVersionUID = 1L;
    public static final int WIDTH = 500;
    public static final int HEIGHT = 600;
    public static final Font main = new Font("Bebas Neue Regular", Font.PLAIN, 28);
    private final GameBoard board;
    private Thread game;
    private boolean running;
    private BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);



    public Game() {

        setFocusable(true);
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);

        //half width of screen minus half width of gameboard
        //x will be center on x axis and y move up 10px from bottom of the screen
        board = new GameBoard(WIDTH/2 - GameBoard.BOARD_WIDTH / 2, HEIGHT - GameBoard.BOARD_HEIGHT - 10);

    }

    private void update() {
//        screen.update();
//        if(Keyboard.pressed[KeyEvent.VK_LEFT]){
//            System.out.println("hit right");
//        }
        board.update();
        Keyboard.update();

    }

    private void render() {
        Graphics2D g = (Graphics2D) image.getGraphics();
        g.setColor(Color.white);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        //render gameboard
        board.render(g); // need to pass g
        g.dispose(); // after drawing, it it good to dispose to be more efficient in memory

        Graphics2D g2d = (Graphics2D) getGraphics();
        g2d.drawImage(image, 0, 0, null);   //u must draw your above virtual to panel
        g2d.dispose();
    }

    @Override
    public void run() {
        int fps = 0;
        int updates = 0;
        long fpsTimer = System.currentTimeMillis();
        double nsPerUpdate = 1000000000.0 / 60;

        // last update time in nanoseconds
        double then = System.nanoTime();
        double unprocessed = 0;

        while (running) {

            boolean shouldRender = false;

            double now = System.nanoTime();
            unprocessed += (now - then) / nsPerUpdate;
            then = now;

            // Update queue
            while (unprocessed >= 1) {

                // update
                updates++;
                update();
                unprocessed--;
                shouldRender = true;
            }

            // Render
            if (shouldRender) {
                fps++;
                render();
                shouldRender = false;
            }
            else {
                try {
                    Thread.sleep(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // FPS timer
            if (System.currentTimeMillis() - fpsTimer > 1000) {
//                System.out.printf("%d fps %d updates", fps, updates);
//                System.out.println("");
                fps = 0;
                updates = 0;
                fpsTimer += 1000;
            }
        }
    }

    public synchronized void start() {
        if (running) return;
        running = true;
        game = new Thread(this, "game");
        game.start();
    }

    public synchronized void stop() {
        if (!running) return;
        running = false;
        System.exit(0);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        Keyboard.keyPressed(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        Keyboard.keyReleased(e);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
//        screen.mousePressed(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
//        screen.mouseReleased(e);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
//        screen.mouseDragged(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
//        screen.mouseMoved(e);
    }
}
