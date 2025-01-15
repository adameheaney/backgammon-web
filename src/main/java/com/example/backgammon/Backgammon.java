package com.example.backgammon;

import java.util.ArrayList;
import java.util.Arrays;
/**
 * @author Adam Heaney
 * @version 1.0
 * Finish date: 1.0: 1/14/25
 * 
 * This is a rework of my other backgammon project, but this time it's a class that can be used in a Spring Boot application. Some of the normal functionality
 * has been changed up, but the core of the game is still the same. The difference is this allows for the game to be showcased on the web!
 * 
 */
public class Backgammon {

    /**
     * The backgammon board being used
     */
    private BackgammonBoard b;

    /**
     * current game state in a String form
     */
    String gameState;

    /**
     * the dice being used for generating the rolls
     */
    private final Dice d;

    /**
     * bot
     */
    BackgammonBot bot;

    /**
     * player's turn?
     */
    boolean playerTurn = true;


    ArrayList<int[]> allMoves;

    /**
     * The generated rolls; global variable to make it simpler for the extra methods pertaining to "rolls"
     * How rolls works:
     * - The dice class has a method named rolls(num), that returns an array of max size 4 filled with "num" rolls of 1-6.
     * - The rest of the array is filled with -1.
     * - EX: rolls = d.rolls(2) --> rolls = {4, 2, -1, -1}
     * - Of course, if there are doubles, then the rolls will be filled with the extra rolls: {4, 4, 4, 4}, but generally the last two
     * numbers will be -1.
     * - There are a few methods pertaining to rolls. In retrospect, I should have used an ArrayList :p
     */
    private int[] rolls;

    /**
     * Constructs a new Backgammon game with a fresh board, dice, and scanner
     */
    public Backgammon() {
        b = new BackgammonBoard();
        d = new Dice();
        allMoves = new ArrayList<>();
        bot = new RandomBot();
        playAgainstBot();
    }

    //=========================================
    //          METHODS FOR ROLLS // Getters
    //=========================================

    public String getRolls() {
        return rollsToString();
    }

    public BackgammonBoard getBoard() {
        return b;
    }

    public String getGameState() {
        return gameState;
    }

    /**
     * 
     * @return true if there are no more rolls to use
     */
    private boolean rollsIsEmpty() {
        for(int i = 0; i < rolls.length; i++) {
            if(rolls[i] != -1) {
                return false;
            }
        }
        return true;
    }

    /**
     * 
     * @param num : number to get index of 
     * @return index of the number in the array
     */
    private int indexOfRoll(int num) {
        if(num == -1) return -1;
        for(int i = 0; i < rolls.length; i++) {
            if(rolls[i] == num)
                return i;
        }
        return -1;
    }

    /**
     * 
     * @return the current available rolls in string form
     */
    private String rollsToString() {
        if(rolls == null) {
            return "[ ]";
        }
        String rollString = "[";
        for(int i = 0; i < rolls.length; i++) {
            if(rolls[i] != -1) {
                rollString += rolls[i] + ", ";
            }
        }
        if(rollString.length() > 1)
            return rollString.substring(0, rollString.length() - 2) + "]";
        else return "[]";
    }


    //========================================================
    //            METHODS FOR GETTING THE MOVES
    //========================================================

    

    /**
     * Takes in a list of backgammon moves and sifts through them to return possible valid moves
     * @param allMoves
     */
    private void getValidMoves() {
        //initial check to make sure that you don't have eaten pieces
        if(b.getTeams()[b.getTurn()].getEatenPieces() == null){
            //if you have no moves, then make all the rolls -1
            if(allMoves.isEmpty()) {
                gameState += "\nYou have no valid moves :(";
                for(int i = 0; i < rolls.length; i++) {
                    rolls[i] = -1;
                }
                return;
            }
            //checks if any of the rolls have literally no move possible even in the future, and if not removes the roll
            else {
                for(int r = 0; r < rolls.length; r++) {
                    if(rolls[r] == -1) continue;
                    boolean hasMove = false;
                    for(int i = 0; i < allMoves.size(); i++) {
                        if(allMoves.get(i)[0] == rolls[r]) {
                            hasMove = true;
                        }
                    }
                    if(!hasMove) {
                        gameState+="\nNo moves for roll " + rolls[r] +". :( \nRemoved roll " + rolls[r];
                        rolls[r] = -1;
                    }
                }
            }
            //checks if both dice have the same single valid move, and discards the lower dice, as the higher dice must be played in this situation
            if(allMoves.size() <= 2 
            && b.getTeams()[b.getTurn()].numPiecesOnSpace(allMoves.get(0)[1], allMoves.get(0)[2]) < 2){
                boolean onlyOneSpace = true;
                int[] firstMove = new int[] {allMoves.get(0)[1],allMoves.get(0)[2]};
                for(int i = 1; i < allMoves.size(); i++) {
                    int[] moveArray = new int[] {allMoves.get(i)[1],allMoves.get(i)[2], allMoves.get(i)[3]};
                    if(!Arrays.equals(moveArray, firstMove)) {
                        onlyOneSpace = false;
                    }
                }
                if(onlyOneSpace) {
                    int maxdex = 0;
                    for(int i = 1; i < allMoves.size(); i++) {
                        if(allMoves.get(i)[0] > allMoves.get(maxdex)[0]) {
                            maxdex = i;
                        }
                    } 
                    int[] temp = allMoves.get(maxdex);
                    allMoves.clear();
                    allMoves.add(temp);
                    for(int r = 0; r < rolls.length; r++) {
                        if(rolls[r] != allMoves.get(0)[0] && rolls[r] != -1) {
                            gameState+= "\nRemoved roll " + rolls[r] + " since you can only play one square!";
                            rolls[r] = -1;
                        }
                    }
                }
            }

            //MANY LINES: These lines deal with the rule that if a dice only has a valid 
            //move following a move with the other dice, the other dice must be played 
            //in the way that enables that dice to be played.

            if(!allMoves.isEmpty()) {
                for(int r = 0; r < rolls.length; r++) {
                    boolean hasStarterMove = false;
                    boolean hasSecondaryMove = false;
                    if(rolls[r] == -1) continue;
                    for(int i = 0; i < allMoves.size(); i++) {
                        int[] moveArray = allMoves.get(i);
                        if(moveArray[0] == rolls[r] && moveArray[3] == 1) {
                            hasStarterMove = true;
                        }
                        if(moveArray[0] == rolls[r] && moveArray[3] == 2) {
                            hasSecondaryMove = true;
                        }
                    }
                    if(hasSecondaryMove && !hasStarterMove) {
                        ArrayList<int[]> temp = new ArrayList<>();
                        for(int i = 0; i < allMoves.size(); i++) {
                            int[] moveArray = allMoves.get(i);
                            int index = r == 1 ? 0 : 1;
                            if(moveArray[0] == rolls[r] && moveArray[3] == 2) {
                                temp.add(new int[] {rolls[index], moveArray[1], moveArray[2], 1});
                            }
                        }
                        allMoves.clear();
                        allMoves.addAll(temp);
                        break;
                    }
                }
            }
        }
        pruneAllMoves(allMoves);
    }
    /**
     * prunes all of the valid moves to only include the roll, posx, and posy. Used in getValidMoves() method
     * @param allMoves
     */
    private void pruneAllMoves(ArrayList<int[]> allMoves) {
        //generates the moves if you have an eaten piece (the valid moves aren't generated properly with the getValidMoves() method)
        if(b.getTeams()[b.getTurn()].getEatenPieces() != null) {
            ArrayList<int[]> temp = new ArrayList<>();
            for(int r = 0; r < rolls.length; r++) {
                if(rolls[r] != -1 && b.getTeams()[1 - b.getTurn()].numPiecesOnSpace(12 - rolls[r], b.getTeams()[1 - b.getTurn()].getHomeYPos()) < 2) {
                    temp.add(new int[] {rolls[r], 12 - rolls[r], b.getTeams()[1 - b.getTurn()].getHomeYPos()});
                }
            }
            allMoves.clear();
            allMoves.addAll(temp);
        }
        //if there are no eaten pieces, then it'll clean up the moves for valid input
        else {
            ArrayList<int[]> temp = new ArrayList<>();
            for(int i = 0; i < allMoves.size(); i++) {
                int[] moveArray = allMoves.get(i);
                int[] tempAdd = new int[] {moveArray[0], moveArray[1], moveArray[2]};
                if(moveArray[3] == 1 && !intArrContains(temp, tempAdd))
                    temp.add(tempAdd);
            }
            allMoves.clear();
            allMoves.addAll(temp);
        }
    }

    /**
     * checks if the List contains an int[] with exact items as arr, since it doesn't normally do that >:(
     * @param list
     * @param arr
     * @return
     */
    private boolean intArrContains(ArrayList<int[]> list, int[] arr) {
        for(int i = 0; i < list.size(); i++) {
            int[] item = list.get(i);
            for(int j = 0; j < item.length; j++) {
                if(item[j] != arr[j]) {
                    break;
                }
                else if(j == item.length-1) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * checks if allMoves has the move "move"
     * @param move
     * @param allMoves
     * @return
     */
    private boolean hasMove(int[] move, ArrayList<int[]> allMoves) {
        for(int i = 0; i < allMoves.size(); i++) {
            if(Arrays.equals(move, allMoves.get(i))) return true;
        }
        return false;
    }





    //--------------------------------------------------------------------------------------------------------------------
    //Methods to play with bots
    //--------------------------------------------------------------------------------------------------------------------


    /**
     * Method to call that starts a game with a bot
     * @param bot
     */
    private void playAgainstBot() {
        gameState+="Welcome to the game of Backgammon! I will not be restating the rules as I assume you know them if you're playing this. The code for this game was created from scratch by Adam Heaney using only Java and dedication. \n\nHow to play: When it is your turn to move, input the roll and coordinates as follows: \"roll x y\". Here is what the board looks like:\n\n" + b.boardString() + "\nThe two teams are B and W (black and white). Black's home is the top row and White's is the bottom. E is how many of your pieces are eaten and I is how many of your pieces are home. You are the white team.";
        int turn = 1;
        bot.retrieveBoard(b);
        gameState+="\n---------------------------------------\n              TURN " + turn + "\n---------------------------------------";
        turn++;
        botGameLoop();
    }

    /**
     * The game loop for a game of a player vs a bot
     * @param bot
     */
    private void botGameLoop() {
        if(!(b.getTeams()[0].getNumActivePieces() > 0 && b.getTeams()[1].getNumActivePieces() > 0)) {
            if(!(b.getTeams()[0].getNumActivePieces() > 0)) {
                gameState+= "\nYOU WIN!";
            }
            else if(!(b.getTeams()[1].getNumActivePieces() > 0)) {
                gameState += "\nYou lose :(";
            }
            b = new BackgammonBoard();
            return;
        } 
        rolls = d.rolls(2);
        allMoves = new ArrayList<>(b.getAllPossibleMoves(rolls));
        getValidMoves();

        if(b.getTurn() == 0) {
            gameState+= "\nIt is your turn.";
            gameState += "\nYou rolled " + rollsToString();
            if(rolls[0] == rolls[1]) {
                gameState += "\nYou rolled doubles!";
                rolls[2] = rolls[0];
                rolls[3] = rolls[0];
            }
            gameState += "\nInput your desired move. You have the rolls " + rollsToString();
        }
        else {
            gameState+="\nIt is " + bot.getName() + "'s turn.";
            gameState += "\n" + bot.getName() + " rolled " + rollsToString();
            if(rolls[0] == rolls[1]) {
                gameState+= "\n" + bot.getName() + " rolled doubles!";
                rolls[2] = rolls[0];
                rolls[3] = rolls[0];
            }
            gameState+= "\n"+ b.boardString();

            //the while loop for the bot's turn
            while(!rollsIsEmpty() && !allMoves.isEmpty()) {
                int[] moveArray;
                moveArray = botTurn(bot, allMoves);

                //does the move
                rolls[indexOfRoll(moveArray[0])] = -1;
                if(b.getTeams()[b.getTurn()].getEatenPieces() == null)
                    b.movePiece(moveArray[1], moveArray[2], moveArray[0]);
                else b.moveEatenPiece(moveArray[0]);
                if(!rollsIsEmpty()) {
                    allMoves = new ArrayList<>(b.getAllPossibleMoves(rolls));
                    getValidMoves();
                }
                gameState+= "\n"+ b.boardString();
            }
            b.switchTurn();
            botGameLoop();
        } 
    }

    /**
     * the logic for a bot's turn to be processed.
     * @param bot the bot used
     * @param allMoves all valid moves
     * @return the move the bot chooses
     */
    private int[] botTurn(BackgammonBot bot, ArrayList<int[]> allMoves) {
        bot.evaluateMoves(allMoves);
        int[] botMove = bot.getMove();
        gameState+= "\nThe bot played the piece at coordinates " + botMove[1] + " " + botMove[2] + " with roll " + botMove[0];
        return botMove;
    }

    /**
     * The logic for a player's turn
     * @param allMoves arraylist of int[] of all valid moves
     * @return the move chosen by the player
     */
    public synchronized String playerTurn(String playerMove) {
        if(!this.playerTurn) {
            return "Not the player's turn";
        }
        String moveString = playerMove.replace("\"", "");
        
        String[] moveArraystr = moveString.split(" ");
        int[] moveArray = new int[moveArraystr.length];
        for(int i = 0; i < moveArraystr.length; i++) {
            try{ 
                moveArray[i] = Integer.parseInt(moveArraystr[i]);
            } catch (NumberFormatException e) {
                gameState+="\n##########################\nINVALID INPUT\n##########################\nRemember, the format is \"roll X Y\" and don't forget to take a better look at the board. You have the rolls " + rollsToString();
                return "Move " + Arrays.toString(moveArraystr) + " invalid" + "  ";
            }
        }
        
        //checks if the input is valid
        if(!hasMove(moveArray, allMoves)) {
            gameState+="\n##########################\nINVALID INPUT\n##########################\nRemember, the format is \"roll X Y\" and don't forget to take a better look at the board. You have the rolls " + rollsToString();
            return "Move " + Arrays.toString(moveArray) + " invalid" + "  ";
        }

  
        //does the move
        rolls[indexOfRoll(moveArray[0])] = -1;
        if(b.getTeams()[b.getTurn()].getEatenPieces() == null)
            b.movePiece(moveArray[1], moveArray[2], moveArray[0]);
        else b.moveEatenPiece(moveArray[0]);
        if(!rollsIsEmpty()) {
            allMoves = new ArrayList<>(b.getAllPossibleMoves(rolls));
            getValidMoves();
        }
        else{
            b.switchTurn();
            botGameLoop();
        }
        gameState+= "\n"+ b.boardString();
        return "Move VALID";
    }
}