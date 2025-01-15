package com.example.backgammon;

import java.util.ArrayList;

/**
 * @author Adam Heaney
 * @version 1.0
 * This class is an abstract class that every Backgammon bot must extend to be able to be played against. It simplifies the process of creating
 * new bots and going through multiple versions of old ones.
 */
public abstract class BackgammonBot {
    
    /**
     * the current board that the bot is playing on
     */
    BackgammonBoard board;

    /**
     * the name of the bot
     */
    final String botName;

    /**
     * the selected move of the bot
     */
    protected int[] move;


    /**
     * constructs a new bot with the name botName
     * @param botName
     */
    public BackgammonBot(String botName) {
        this.botName = botName;
        move = new int[3];
    }

    /**
     * instantiates a bot of type <b>botType</b> by looking in the HashMap
     * @param botType
     * @return
     */

    /**
     * 
     * @return botName
     */
    public String getName() {
        return botName;
    }

    /**
     * gives the board to the bot
     * @param b
     */
    public void retrieveBoard(BackgammonBoard b) {
        board = b;
    }

    /**
     * 
     * @return chosen move
     */
    public int[] getMove() {
        return move;
    }

    /**
     * This method must be completed by every bot:
     * The method must set move to a valid move within possibleMoves
     * @param possibleMoves
     */
    public abstract void evaluateMoves(ArrayList<int[]> possibleMoves);
    
}
