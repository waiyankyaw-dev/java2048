package com.fatalcubez.game;

import java.awt.event.KeyEvent;

public class Keyboard {
    public static boolean[] pressed = new boolean[256];
    public static boolean[] prev = new boolean[256];

    //set everything in previous array equal to pressed array
    //run when we press, and run when we release // so run twice when u press and release
    public static void update(){
        for(int i = 0; i < 4; i++){
            if(i == 0) prev[KeyEvent.VK_LEFT] = pressed[KeyEvent.VK_LEFT]; //run first
            if(i == 1) prev[KeyEvent.VK_RIGHT] = pressed[KeyEvent.VK_RIGHT];//run second
            if(i == 2) prev[KeyEvent.VK_UP] = pressed[KeyEvent.VK_UP];//run third
            if(i == 3) prev[KeyEvent.VK_DOWN] = pressed[KeyEvent.VK_DOWN];//run fourth
        }
    }

    //when we press key, it updates the gameboard and gameboard checks ok which key is true and what should i move, after, it gonna set all the previous keys to normal(false)
    public static void keyPressed(KeyEvent e){
        pressed[e.getKeyCode()] = true;
    }

    public static void keyReleased(KeyEvent e){
        pressed[e.getKeyCode()] = false;
    }

    public static boolean typed(int keyEvent){
        return !pressed[keyEvent] && prev[keyEvent];  //pressed is not true but pre is true // this will only be called once
    }
}
