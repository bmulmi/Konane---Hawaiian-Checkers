/************************************************************
 *  Name: Bibhash Mulmi                                     *
 * Project:  Project 3 Two Player Konane                    *
 * Class:  CMPS 331 Artificial Intelligence                 *
 * Date:  03/23/2018                                        *
 ************************************************************/

package edu.ramapo.bibhash.konane.model;

import android.util.Pair;

import java.io.InputStream;
import java.lang.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;
import java.util.Vector;

public class Board{
    private int boardDimension;
    public String[][] board;

    private Player blackPlayer = new Player();
    private Player whitePlayer = new Player();
    private State gameState = new State();
    private Move move = new Move(-1,-1);

    public String white = "W";
    public String black = "B";
    public String empty = "E";

    private boolean blackIsComputer = false;
    private boolean whiteIsComputer = false;

    private long timer = 0;


    private Pair<Integer, Integer> removedBtn1;
    private Pair<Integer, Integer> removedBtn2;


    /**----------accessors for player's private data----------**/
    /*
    parameters: nothing
    returns: int black stone's score
     */
    public int getBlackScore(){return blackPlayer.getScore();}

    /*
    parameters: nothing
    returns: int white stone's score
     */
    public int getWhiteScore(){return whitePlayer.getScore();}

    //accessor for black stone's turn
    public boolean getBlackTurn(){
        return blackPlayer.isTurn();
    }

    //accessor for white stone's turn
    public boolean getWhiteTurn(){
        return whitePlayer.isTurn();
    }

    //accessor true or false for Black as a Computer
    public boolean getIsBlackComputer (){return blackPlayer.isComputer();}

    //accessor true or false for White as a Computer
    public boolean getIsWhiteComputer (){return whitePlayer.isComputer();}

    /**----------mutators for player's private data----------**/
    /*
    parameters: int score
    returns: nothing
     */
    //increases the score by value of passed parameter for Black stone
    public void updateBlackScore(int score){
        blackPlayer.updateScore(score);
    }

    /*
    parameter: int score
    returns: nothing
     */
    //increases the score by value of passed parameter for White stone
    public void updateWhiteScore(int score){
        whitePlayer.updateScore(score);
    }

    //mutator for black stone's turn
    public void setBlackTurn(boolean i){
        blackPlayer.setTurn(i);
    }

    //mutator for white stone's turn
    public void setWhiteTurn(boolean i){
        whitePlayer.setTurn(i);
    }

    //mutator for black stone's computerPlays boolean
    public void setBlackAsComputer(){ blackIsComputer = true; blackPlayer.setComputerPlays(true); whitePlayer.setComputerPlays(false);}

    //mutator for white stone's computerPlays boolean
    public void setWhiteAsComputer(){ whiteIsComputer = true; whitePlayer.setComputerPlays(true); blackPlayer.setComputerPlays(false);}


    /**----------public functions of Board Class----------**/
    /*
    parameters: int dimension
    returns: nothing
     */
    //sets the board dimension
    public void setBoardDimension(int dimension){
        boardDimension = dimension;
    }

    /*
    parameters: nothing
    returns: int board dimension
     */
    //returns the board dimension
    public int getBoardDimension(){
        return boardDimension;
    }

    /*
    parameters: nothing
    returns: nothing
     */
    //initialises the new Game
    public void newGame (){
        makeBoard();
        removeButton();
        blackPlayer.setTurn(true);
        whitePlayer.setTurn(false);
    }

    /*
    parameters: nothing
    returns: nothing
     */
    //generates the board as a 2X2 array of strings
    private void makeBoard(){
        int temp = boardDimension;
        board = new String[temp][temp];
        for (int i = 0; i < temp; i++){
            for (int j = 0; j < temp; j++){
                if ((i%2==0 && j%2==0) || (i%2==1 && j%2==1)) board[i][j] = black;
                else board[i][j] = white;
            }
        }
    }

    /*
    parameters: Input stream is
    returns: nothing
     */
    //loads the game passed from the input stream
    /*
    sets board dimension
    sets board array
    sets black stone's turn
    sets white stone's turn
    sets black stone's computerPlays boolean
    sets white stone's computerPlays boolean
    sets blackIsComputer boolean of this class
    sets whiteIsComputer boolean of this class
     */
    public void loadGame(InputStream is){
        gameState.setInputStream(is);

        boardDimension = gameState.getDimension();
        //set up the board
        board = gameState.getBoard();

        //set player class values
        blackPlayer.setScore(gameState.getBlackScore());
        whitePlayer.setScore(gameState.getWhiteScore());

        blackPlayer.setTurn(gameState.getBlackTurn());
        whitePlayer.setTurn(gameState.getWhiteTurn());

        blackPlayer.setComputerPlays(gameState.getBlackComputer());
        whitePlayer.setComputerPlays(gameState.getWhiteComputer());


        if (getIsBlackComputer()){
            setBlackAsComputer();
        }
        else setWhiteAsComputer();
    }

    /*
    parameters: nothing
    returns: nothing
    */
    //removes two random elements from the 2X2 board array
    private void removeButton() {
        Random rdm = new Random();
        int row, col;
        do {
            row = rdm.nextInt(boardDimension) % boardDimension;
            col = rdm.nextInt(boardDimension) % boardDimension;
        } while ((row % 2 == 1 || col % 2 == 1));

        //update the board array
        removedBtn1 = new Pair(row, col);
        board[row][col] = empty;
        //row and col odd = white
        do {
            row = rdm.nextInt(boardDimension) % boardDimension;
            col = rdm.nextInt(boardDimension) % boardDimension;
        } while ((col % 2 == 0 && row % 2 == 0) || (col % 2 == 1 && row % 2 == 1));
        removedBtn2 = new Pair<>(row, col);
        board[row][col] = empty;
    }

    /*
    parameters: nothing
    returns: Pair<>[]
     */
    //accessor for the two initially removed buttons of the game
    public Pair<Integer, Integer>[] getRemovedBtns(){
        Pair<Integer, Integer> [] temp = new Pair[2];
        temp[0] = removedBtn1;
        temp[1] = removedBtn2;
        return temp;
    }

    /*
    parameters: int source row, int source column, int destination row, int destination column
    returns: Pair<>(captured stone's row, captured stone's column)
     */
    //updates the 2X2 board array
    public Pair updateButton(int srcRow, int srcCol, int dstRow, int dstCol){
        //-------update the destination------
        if (isBlack(srcRow,srcCol)) board[dstRow][dstCol] = black;
        else board[dstRow][dstCol] = white;

        //-------update the source-----------
        board[srcRow][srcCol] = empty;

        //-------update the captured stone--------
        int temp = slotsToRemove(srcRow, srcCol, dstRow, dstCol);
        int btnRow = temp/10;
        int btnCol = temp%10;
        board[btnRow][btnCol] = empty;
        Pair btnId = new Pair<>(btnRow, btnCol);
        return btnId;
    }

    /*
    parameters: int source row, int source column, int destination row, int destination column
    returns: boolean value
     */
    //checks if the move is valid or not
    public boolean isValid(int srcRow, int srcCol, int dstRow, int dstCol){
        int slot;
        //----------for black stones----------
        if (isBlack(dstRow, dstCol) && (isBlack(srcRow, srcCol))){
            if ((isTwoSlotsAway(srcRow, srcCol, dstRow, dstCol))&&(isEmptyStone(dstRow, dstCol))&&!(isEmptyStone(srcRow, srcCol))) {
                //get the stone that is to be captured
                slot = slotsToRemove(srcRow, srcCol, dstRow, dstCol);
                return (board[slot / 10][slot % 10].equals(white));
            }
            else return false;
        }

        //----------for white stones----------
        else if (isWhite(dstRow,dstCol) && isWhite(srcRow, srcCol)){

            if(isTwoSlotsAway(srcRow,srcCol,dstRow,dstCol) && isEmptyStone(dstRow, dstCol)&&!(isEmptyStone(srcRow, srcCol))){

                slot = slotsToRemove(srcRow, srcCol, dstRow, dstCol);
                return (board[slot / 10][slot % 10].equals(black));
            }
            else return false;
        }

        //----------for empty slots----------
        else return false;
    }

    /*
    parameters: int row, int col
    returns: boolean
     */
    //checks if the (row, col) is black stone's slot
    public boolean isBlack (int R, int C){
        return ((R%2==0 && C%2==0) || (R%2==1 && C%2==1));
    }

    /*
    parameters: int row, int col
    returns: boolean
     */
    //checks if the (row, col) is white stone's slot
    public boolean isWhite (int R, int C){
        return ((R%2!=0 && C%2==0) || (R%2==0 && C%2!=0));
    }

    /*
    parameters: int source row, source col, destination row, destination column
    returns: boolean
     */
    //checks if the destination is exactly two slots away from the source
    private boolean isTwoSlotsAway (int sR, int sC, int dR, int dC){
        return (((dR == sR + 2) && (dC == sC)) || ((dC == sC + 2)&& (dR == sR)) || ((dR == sR - 2)&& (dC == sC)) || ((dC == sC - 2) && (dR == sR)));
    }

    /*
    parameters: int row, int column
    returns: boolean
     */
    //checks if the (row, col) is equal to an empty slot in the 2X2 board array
    public boolean isEmptyStone(int dR, int dC){

        if (!isInBoard(dR, dC)){
            return false;
        }

        else {
            return (board[dR][dC].equals(empty));
        }
    }

    /*
    parameters: int row, int col
    returns: boolean
     */
    //checks if the (row, col) has a valid move
    public boolean isValidNextMove(int dR, int dC){
        int sR = dR;
        int sC = dC;

        return ((isValid(sR, sC, sR+2, sC))||(isValid(sR,sC,sR-2,sC))||(isValid(sR,sC, sR, sC+2))||isValid(sR,sC,sR,sC-2));
    }

    /*
    parameters: nothing
    returns: nothing
     */
    //checks if there is any remaining moves left for black stone in the board array
    public boolean checkRemainingMovesForBlack() {
        int count = 0;
        for (int i = 0; i < boardDimension; i++) {
            for (int j = 0; j < boardDimension; j++) {
                //check if slot is black
                if (isBlack(i,j) && !isEmptyStone(i,j)) {
                    //check if any one of the neighbouring slots is valid for move
                    if (isValid(i, j, i - 2, j)||isValid(i, j, i + 2, j)||isValid(i, j, i, j + 2)||isValid(i, j, i, j - 2)){
                        count++;
                    }
                }
            }
        }
        return (count!=0);
    }

    //same function as above, but for white stones
    public boolean checkRemainingMovesForWhite(){
        int count = 0;
        for (int i = 0; i < boardDimension; i++) {
            for (int j = 0; j < boardDimension; j++) {
                if (isWhite(i,j) && !isEmptyStone(i,j)) {
                    if (isValid(i, j, i - 2, j) || isValid(i, j, i + 2, j) || isValid(i, j, i, j + 2) || isValid(i, j, i, j - 2)) {
                        count ++;
                    }
                }
            }
        }
        return (count != 0);
    }

    //Receives either Row (destination, source) or Column (destination, source)
    //returns the neighboring slot in the board array that has been captured.
    public int disappearSlot(int destination, int source) {
        switch (destination) {
            case 0:
                return 1;
            case 1:
                return 2;
            case 2:
                switch (source) {
                    case 0:
                        return 1;
                    case 4:
                        return 3;
                    default:
                        break;
                }
            case 3:
                switch (source) {
                    case 1:
                        return 2;
                    case 5:
                        return 4;
                    default:
                        break;
                }
            case 4:
                return 3;
            case 5:
                return 4;
            default:
                return 0;
        }
    }

    //Receives (Source Row, Source Column, Destination Row, Destination Column)

    /*if the destination row and source rows are the same
        call disappearSlot function to get the column that is captured
        return the Row, Col slot values of the array
     else if the destination column and source column are the same
        call disappearSlot to get the row that is captured
        return the Row, Col values of the array
     */

    //returns the row and column of the array as row*10+col
    public int slotsToRemove(int srcRow, int srcCol, int dstRow, int dstCol){
        //System.out.println("update button called");
        int btnRow;
        int btnCol=0;
        int btnId=0;
        //if same row
        if ((dstRow == srcRow) && (dstCol != srcCol)) {
            if(srcCol>dstCol) btnCol = dstCol+1;
            else btnCol = srcCol+1;
            btnRow = dstRow;
            btnId = btnRow*10+btnCol;
        }

        else if ((dstCol == srcCol) && (dstRow != srcRow)){
            if (srcRow>dstRow) btnRow = dstRow+1;
            else btnRow = srcRow+1;
            btnCol = dstCol;
            btnId = btnRow*10+btnCol;
        }

        else{
            System.out.println("");
        }
        return btnId;
    }

    //checks if the slot is in board 6X6
    private boolean isInBoard(int r, int c){
        return (r >= 0 && r <boardDimension && c >= 0 && c <boardDimension);
    }

    /*
    parameters: nothing
    returns: double time
     */
    //accessor for timer value
    public double getAlgorithmTime(){
        return (double)timer;
    }

    /*
    parameters: int cut off value, boolean prune, boolean computer
    returns: nothing
     */
    /*
    Sets alpha, beta values according to the boolean of prune passed
    Calls MiniMax function according to the boolean of computer passed
     */
    public void getMinimaxMoves(int cutOff, boolean prune, boolean computer){
        //---set up default alpha beta values for when pruning is not to be used---
        int alpha = Integer.MAX_VALUE;
        int beta = Integer.MIN_VALUE;
        if (prune){
            alpha = Integer.MIN_VALUE;
            beta = Integer.MAX_VALUE;
        }

        //---copy the current board---
        String[][]tempBoard = new String[boardDimension][boardDimension];
        copyBoard(tempBoard, board);

        int maxP = 0;
        int minP = 0;
        //---check which player is computer---
        if(getIsBlackComputer()){ //black is computer
            int computerScore = getBlackScore();
            int humanScore = getWhiteScore();
            //---start the timer---
            long startTime = System.currentTimeMillis();
            //---call MiniMax algorithm---
            MiniMax(true, prune, computer, tempBoard, cutOff, computerScore, humanScore, alpha, beta, maxP,minP, new Child (tempBoard, 0,0));
            //---end the timer---
            long endTime = System.currentTimeMillis();
            //---set the timer---
            timer = endTime-startTime;

        }
        else{ //white is computer
            int computerScore = getWhiteScore();
            int humanScore = getBlackScore();
            //---start the timer---
            long startTime = System.currentTimeMillis();
            //---call MiniMax algorithm---
            MiniMax(true, prune, computer, tempBoard, cutOff, computerScore, humanScore, alpha, beta, maxP, minP, new Child (tempBoard, 0,0));
            //---end the timer---
            long endTime = System.currentTimeMillis();
            //---set the timer---
            timer = endTime-startTime;
        }
    }

    public  Pair<Move, Move>  bestMove;
    public Child cld;

    //minimax Algorithm
    private int MiniMax(boolean maximizer, boolean prune, boolean computer, String[][]MMboard, int cutOff, int computerScore, int humanScore, int alpha, int beta, int maxPoints, int minPoints, Child passedChild){
        //Base Case
        if ((cutOff == 0)||((blackIsComputer && computer && !checkRemainingMovesForBlackMiniMax(MMboard)) || (whiteIsComputer && computer && !checkRemainingMovesForWhiteMiniMax(MMboard)) || (!blackIsComputer && !computer && !checkRemainingMovesForBlackMiniMax(MMboard) || (!whiteIsComputer && !computer && !checkRemainingMovesForWhiteMiniMax(MMboard))))) {
            int heuristic;
            if (getBlackTurn()) {
                if (getIsBlackComputer()) heuristic = computerScore - humanScore;
                else heuristic = humanScore - computerScore;
            } else {
                if (getIsWhiteComputer()) heuristic = computerScore - humanScore;
                else heuristic = humanScore - computerScore;
            }
            System.out.println("-----*-----");
            System.out.println("Heuristic returned: " + heuristic);
            return heuristic;
        }

        if (maximizer){
            //int maxiTotalScore = 0;
            //int miniTotalScore = 0;
            int bestHeuristic = Integer.MIN_VALUE;
            Pair<Move, Move> tempBest = new Pair<>(new Move(-1,-1), new Move(-1,-1));
            Child tempBestChild = new Child (MMboard, maxPoints, minPoints);

            //---generate the tree of the current game state---
            //get all the possible moves from the current game state
            Queue<Pair<Move, Move>> allMoves = getAllAvailableMoves(MMboard, computer);
            //go to each move and generate its own tree
            while(!allMoves.isEmpty()) {
                //get the source node, destination node
                Pair<Move, Move> child = allMoves.poll();

                //Store the current board
                String[][] previousBoard = new String[boardDimension][boardDimension];

                copyBoard(previousBoard, MMboard);

                //make the move in the passed board
                makeMoveForMiniMax(child, MMboard);

                //store the child destination node as Move
                Move childMove = child.second;

                //add the score earned by that move
                maxPoints += childMove.score;

                Child passingChild = new Child(MMboard, childMove.score, 0);

                //change turn and add scores respectively for the next ply
                if (computer){
                    computerScore += childMove.score;
                    computer = false;
                }
                else {
                    humanScore += childMove.score;
                    computer = true;
                }
                //go to the next ply with changed turn
                int tempHeuristic = MiniMax(false, prune, computer, MMboard, cutOff-1, computerScore, humanScore, alpha, beta, maxPoints, minPoints, passingChild);

                //change heuristic value only if the tempHeuristic is maximum
                if (tempHeuristic > bestHeuristic){
                    //set the returned child as the best child of current child
                    passedChild.bestChild = passingChild;
                    tempBestChild = passedChild;
                    //maxiTotalScore = maxPoints;
                    tempBest = child;
                    bestHeuristic = tempHeuristic;
                    //System.out.println("maximizer pts: " + maxiTotalScore);
                }

                MMboard = previousBoard;

                if(prune){
                    if (bestHeuristic>alpha) alpha = bestHeuristic;
                    if (alpha>=beta){
                        System.out.println("****pruned rest of the children****");
                        break;
                    }
                }

                //re-store the maxPoints
                maxPoints-=childMove.score;
                //re-store the turn that was changed previously
                computer = !computer;
                //restore the score as well
                if (computer) computerScore-=childMove.score;
                else humanScore-=childMove.score;
                //loop to get the next child
            }
            bestMove = tempBest;
            cld = tempBestChild;
            //MaximizerScore += maxiTotalScore;
            //MinimizerScore = miniTotalScore;
            return bestHeuristic;
        }
        else{//minimizer
            int bestHeuristic = Integer.MAX_VALUE;
            //int maxiTotalScore = 0;
            //int miniTotalScore = 0;
            Pair<Move, Move>  tempBest = new Pair<>(new Move(-1,-1), new Move(-1,-1));
            Child tempBestChild = new Child (MMboard, maxPoints, minPoints);
            Queue<Pair<Move, Move>> allMoves = getAllAvailableMoves(MMboard, computer);
            while(!allMoves.isEmpty()) {
                Pair<Move, Move> child = allMoves.poll();

                String[][] previousBoard = new String[boardDimension][boardDimension];

                copyBoard(previousBoard, MMboard);

                makeMoveForMiniMax(child, MMboard);

                Move childMove = child.second;

                minPoints += childMove.score;

                Child passingChild = new Child(MMboard, 0, childMove.score);

                if (computer){
                    computerScore += childMove.score;
                    computer = false;
                }
                else {
                    humanScore += childMove.score;
                    computer = true;
                }

                int tempHeuristic = MiniMax(true, prune, computer, MMboard, cutOff-1, computerScore, humanScore, alpha, beta, maxPoints, minPoints, passingChild);

                if (tempHeuristic < bestHeuristic){
                    //set the current child's best child as the passed child
                    passedChild.bestChild = passingChild;
                    tempBestChild = passedChild;
                    //miniTotalScore = minPoints;
                    //maxiTotalScore += maxPoints;
                    //System.out.println("minimizer's points: " + miniTotalScore);
                    tempBest = child;
                    bestHeuristic = tempHeuristic;
                }

                MMboard = previousBoard;

                if(prune){
                    if (bestHeuristic < beta) beta = bestHeuristic;
                    if (beta <= alpha) break;
                }
                //restore the values
                minPoints-=childMove.score;
                computer = !computer;
                if(computer) computerScore-=childMove.score;
                else humanScore-=childMove.score;
            }
            bestMove = tempBest;
            cld = tempBestChild;
            //MinimizerScore += miniTotalScore;
            //MaximizerScore = maxiTotalScore;
            return bestHeuristic;
        }
    }

    /*
    parameters: String[][] to be changed board, String[][] to be copied board
    returns: nothing
     */
    //copies the elements from one 2X2 array to another
    private void copyBoard(String[][]toThis, String[][]fromThis){
        for(int i = 0; i<fromThis.length; i++){
            for (int j = 0; j<fromThis[i].length; j++){
                toThis[i][j] = fromThis[i][j];
            }
        }
    }

    /*
    parameters: Pair<Source Move, Destination Move>, String[][] board
    returns: nothing
     */
    //makes changes to the board array by making moves from the source to destination
    private void makeMoveForMiniMax(Pair<Move,Move> moves, String[][]MMboard){
        Move source = move.clone(moves.first);
        Move destination = move.clone(moves.second);

        Stack<Pair<Integer, Integer>> jumps = getPath(source, destination);

        int sourceRow = source.row;
        int sourceCol = source.col;
        //System.out.println("-----next move(s)-----");
        //System.out.println("source: "+source.row+" X "+ source.col);

        //make all the moves individually
        while(!jumps.isEmpty()){
            Pair<Integer, Integer> nextJump = jumps.pop();
            int destinationRow = nextJump.first;
            int destinationCol = nextJump.second;

            //System.out.println("destination: "+ destinationRow + " X "+ destinationCol);

            MMboard = updateButtonForMiniMax(sourceRow, sourceCol, destinationRow, destinationCol, MMboard);

            //set the destination as the new source for the next move to be made
            sourceRow = destinationRow;
            sourceCol = destinationCol;

            //check the board after the move is made
            /*for(int i = 0; i < boardDimension; i++){
                for (int j = 0; j<boardDimension; j++){
                    System.out.print(MMboard[i][j] + " ");
                }
                System.out.println("\n");
            }*/
        }
        /*System.out.println("Check for source and destination: \n");
        System.out.println("source: " + source.row + " X " + source.col);
        System.out.println("destination: " + destination.row + " X " + destination.col);*/
    }

    //parameters: source node, destination node
    //returns: Stack<Pair<rows, columns>>; from first jump to destination(last) jump, does not include the source
    public Stack<Pair<Integer, Integer>> getPath(Move source, Move destination){
        //int count = destination.score;
        Move temp = move.clone(destination);

        Stack<Pair<Integer, Integer>> movesMade = new Stack<>();

        while(!move.areEqual(temp, source)){
            movesMade.push(new Pair<>(temp.row, temp.col));
            temp = temp.parent;
        }
        /*while(!movesMade.isEmpty()){
            Pair<Integer,Integer> t = movesMade.pop();
            System.out.println("node: " + t.first + " X " + t.second);
        }*/
        return movesMade;
    }

    /*
    parameters: String[][] board, computer player or not
    returns: Queue<Pair<Move, Move>> all the available moves for the board.
     */
    //does a depth first search on every available move and stores the source Move and destination Move as a Pair
    private Queue<Pair<Move,Move>> getAllAvailableMoves(String[][]MMboard, boolean computer){
        int row = -1;
        int col = -1;
        System.out.println("*****new Node*****");
        //initialise the Vector that stores all Moves
        Queue<Pair<Move, Move>> allMoves = new LinkedList<>();
        //visit every node of the board
        while ((row < boardDimension-2 && row >=-1) || (col < boardDimension-2 && col>=-1)) {
            if(row == -1) row++;
            if (col < boardDimension-1 && col >= -1) col++;
            else if (row < boardDimension-1){
                row++;
                col = 0;
            }
            else continue;
            //now see if this node is the one that we want to expand
            if((blackIsComputer && computer && isBlack(row,col)) || (whiteIsComputer && computer && isWhite(row, col)) || (!blackIsComputer && !computer && isBlack(row, col)) || (!whiteIsComputer && !computer && isWhite(row,col))) {
                //do a DFS on this node
                //check if it has next valid move and if its not an empty stone
                if(isValidNextMoveForMiniMax(row, col, MMboard) && !isEmptyStoneForMiniMax(row, col, MMboard)) {
                    //get all the destination nodes
                    Move sourceMove = new Move(row, col);

                    Vector<Move> destinationMoves = DFS(sourceMove, MMboard);
                    //System.out.println("source: "+sourceMove.row+" X "+sourceMove.col);
                    for (int i = 0; i < destinationMoves.size(); i++) {
                        //now store all the destination Moves into the allMoves array
                        //System.out.println("destination: "+ destinationMoves.get(i).row + " X " + destinationMoves.get(i).col + " score: "+ destinationMoves.get(i).score);
                        allMoves.add(new Pair<>(sourceMove, destinationMoves.get(i)));
                    }
                }
            }
        }
        return allMoves;
    }

    //checks if the passed node in the passed board has a valid next move with empty stone
    public boolean isValidNextMoveForMiniMax(int sR, int sC, String[][]MMboard){
        //just pass move(-1,-1) as a dummy move for now
        //we only need to see if the destination is empty slot or not
        return ((isValidForMiniMax(sR, sC, sR+2, sC, MMboard, new Move(-1,-1)))||(isValidForMiniMax(sR,sC,sR-2,sC, MMboard, new Move(-1,-1)))||(isValidForMiniMax(sR,sC, sR, sC+2, MMboard, new Move(-1,-1)))||isValidForMiniMax(sR,sC,sR,sC-2, MMboard, new Move(-1,-1)));
    }

    /*
    parameters: Move source, String[][] current Board
    returns: Vector<Move> all possible destinations from source
     */
    //does Depth First Search to all the possible move nodes
    private Vector<Move> DFS(Move move, String [][]MMboard){
        Vector<Move> moves = new Vector<>();
        Stack<Move> stack = new Stack<>();
        ArrayList<Pair<Integer, Integer>> visited = new ArrayList<>();
        stack.push(move);
        boolean initial = true;

        //DFS
        while (!stack.isEmpty()) {
            Move current = stack.pop();
            int r = current.row;
            int c = current.col;

            //exclude the root move for the first time since it can be visited by one of its child moves
            if (!visited.contains(new Pair<>(r,c)) && !initial) {
                visited.add(new Pair<>(r,c));
            }

            initial = false;

            //west
            if (isValidForMiniMax(r, c, r, c - 2, MMboard, move)) {
                Move temp = new Move( current.row, current.col-2);
                //there is a valid west move
                //change temp to that move with score updated
                temp.score = current.score + 1;

                //now check if that move was visited

                //for the first generated children
                if (!visited.contains(new Pair<>(temp.row, temp.col)) && current.parent == null) {
                    temp.parent = current;
                    //if not visited yet, then push it into the stack
                    stack.push(temp);
                    moves.add(temp);
                }
                //we do not want to go back and forth with the same child and parent.
                else if (!visited.contains(new Pair<>(temp.row, temp.col))&& (temp.row!=current.parent.row || temp.col!=current.parent.col)){
                    temp.parent = current;
                    //if not visited yet, then push it into the stack
                    stack.push(temp);
                    moves.add(temp);
                }
            }
            //south
            if (isValidForMiniMax(r, c, r + 2, c, MMboard, move)) {
                Move temp = new Move( current.row+2, current.col);
                temp.score = current.score+1;
                temp.parent = current;
                if(!visited.contains(new Pair<>(temp.row, temp.col))&& current.parent==null) {
                    stack.push(temp);
                    moves.add(temp);
                }
                else if (!visited.contains(new Pair<>(temp.row, temp.col))&&(temp.row!=current.parent.row || temp.col!=current.parent.col)){
                    stack.push(temp);
                    moves.add(temp);
                }
            }
            //east
            if (isValidForMiniMax(r, c, r, c+2, MMboard, move)) {
                Move temp = new Move( current.row, current.col+2);
                temp.score = current.score+1;
                temp.parent = current;
                if(!visited.contains(new Pair<>(temp.row, temp.col)) &&  current.parent==null) {
                    stack.push(temp);
                    moves.add(temp);
                }
                else if (!visited.contains(new Pair<>(temp.row, temp.col))&& (temp.row!=current.parent.row || temp.col!=current.parent.col)) {
                    stack.push(temp);
                    moves.add(temp);
                }
            }
            //north
            if (isValidForMiniMax(r, c, r - 2, c, MMboard, move)) {
                Move temp = new Move( current.row-2, current.col);
                temp.score = current.score+1;
                temp.parent = current;
                if(!visited.contains(new Pair<>(temp.row, temp.col))&&  current.parent==null) {
                    stack.push(temp);
                    moves.add(temp);
                }
                else if (!visited.contains(new Pair<>(temp.row, temp.col))){
                    if (temp.row!=current.parent.row || temp.col!=current.parent.col) {
                        stack.push(temp);
                        moves.add(temp);
                    }
                }
            }
        }
        return moves;
    }

    /*
    parameters: source row, source column, destination row, destination column, current board array
    returns: modified board array after making the moves from source to destination
     */
    //makes the jump for the board passed.
    public String[][] updateButtonForMiniMax(int srcRow, int srcCol, int dstRow, int dstCol, String[][]board){
        //-------update the destination------
        if (isBlack(srcRow,srcCol)) board[dstRow][dstCol] = black;
        else board[dstRow][dstCol] = white;

        //-------update the source-----------
        board[srcRow][srcCol] = empty;

        //-------update the captured stone--------
        int temp = slotsToRemove(srcRow, srcCol, dstRow, dstCol);
        int btnRow = temp/10;
        int btnCol = temp%10;
        board[btnRow][btnCol] = empty;

        return board;
    }

    /*
    parameters: source row, source column, destination row, destination column, current board array, source move from DFS
    returns: boolean value
     */
    //checks if the move is valid or not
    public boolean isValidForMiniMax(int srcRow, int srcCol, int dstRow, int dstCol, String[][]board, Move move){
        int slot;
        //for black stones
        if (isBlack(dstRow, dstCol) && isBlack(srcRow, srcCol) && isTwoSlotsAway(srcRow, srcCol, dstRow, dstCol) && isInBoard(dstRow, dstCol)){
            //---check if destination is an empty slot---
            if(isEmptyStoneForMiniMax(dstRow, dstCol, board)) {
                //get the stone that is to be captured
                slot = slotsToRemove(srcRow, srcCol, dstRow, dstCol);
                return (board[slot / 10][slot % 10].equals(white));
            }
            //---for the cases in DFS when we reach the starting move from a child---
            else if (dstRow == move.row && dstCol == move.col){
                slot = slotsToRemove(srcRow, srcCol, dstRow, dstCol);
                return (board[slot / 10][slot % 10].equals(white));
            }
            else return false;
        }
        //for white stones
        else if (isWhite(dstRow,dstCol) && isWhite(srcRow, srcCol) && isTwoSlotsAway(srcRow,srcCol,dstRow,dstCol) && isInBoard(dstRow, dstCol)){
            if (isEmptyStoneForMiniMax(dstRow,dstCol, board)) {
                slot = slotsToRemove(srcRow, srcCol, dstRow, dstCol);
                return (board[slot / 10][slot % 10].equals(black));
            }
            else if ( dstRow == move.row && dstCol == move.col){
                slot = slotsToRemove(srcRow, srcCol, dstRow, dstCol);
                return (board[slot / 10][slot % 10].equals(black));
            }
            else return false;
        }
        //for empty slots
        else return false;
    }

    /*
    parameters: destination row, destination column, current board array
    returns: boolean value
     */
    //checks if the slot in the passed coordinate is empty or not
    public boolean isEmptyStoneForMiniMax(int dR, int dC, String[][]board){

        if (!isInBoard(dR, dC)){
            return false;
        }

        else {
            return (board[dR][dC].equals(empty));
        }
    }

    /*
    parameters: current board array
    returns: boolean value
     */
    //checks if there is valid moves remaining for black stone in the current board
    public boolean checkRemainingMovesForBlackMiniMax(String [][] board) {
        int count = 0;
        //go to every node in the board
        for (int i = 0; i < boardDimension; i++) {
            for (int j = 0; j < boardDimension; j++) {
                //check if slot is black
                if (isBlack(i,j) && !isEmptyStoneForMiniMax(i,j, board)) {
                    //check if any one of the neighbouring slots is valid for move
                    //check in north, south, east, west direction
                    if (isValidForMiniMax(i, j, i - 2, j,board, new Move(-1,-1))||isValidForMiniMax(i, j, i + 2, j,board, new Move(-1,-1))||isValidForMiniMax(i, j, i, j + 2, board, new Move(-1,-1))||isValidForMiniMax(i, j, i, j - 2, board, new Move(-1,-1))){
                        count++;
                    }
                }
            }
        }
        return (count!=0);
    }

    //same function as above, but for white stones
    public boolean checkRemainingMovesForWhiteMiniMax(String[][]board){
        int count = 0;
        for (int i = 0; i < boardDimension; i++) {
            for (int j = 0; j < boardDimension; j++) {
                if (isWhite(i,j) && !isEmptyStoneForMiniMax(i,j, board)) {
                    if (isValidForMiniMax(i, j, i - 2, j, board, new Move(-1,-1)) || isValidForMiniMax(i, j, i + 2, j, board, new Move(-1,-1)) || isValidForMiniMax(i, j, i, j + 2, board, new Move(-1,-1)) || isValidForMiniMax(i, j, i, j - 2, board, new Move(-1,-1))) {
                        count ++;
                    }
                }
            }
        }
        return (count != 0);
    }
}