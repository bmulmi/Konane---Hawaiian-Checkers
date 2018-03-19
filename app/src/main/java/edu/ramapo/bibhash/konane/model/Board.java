/************************************************************
 *  Name: Bibhash Mulmi                                     *
 * Project:  Project 2 Konane                               *
 * Class:  CMPS 331 Artificial Intelligence                 *
 * Date:  03/07/2018                                        *
 ************************************************************/

package edu.ramapo.bibhash.konane.model;

import android.util.Pair;

import java.io.InputStream;
import java.lang.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
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
/*
    public Queue<Pair<Integer,Integer>> DFSvisitedNodes = new LinkedList<>();
    public Queue<Pair<Integer,Integer>> BFSvisitedNodes = new LinkedList<>();
    public Queue<Pair<Integer, Integer>> BestFirstSearchList = new LinkedList<>();*/

    //public Map<Pair<Integer, Integer>, Stack<Pair<Integer, Integer>>> nMoves= new HashMap();

    public Pair<Integer, Integer> fromThisButton;
    /*public Pair<Integer, Integer> toThisButton;*/
    public ArrayList<Pair<Integer, Integer>> toTheseButtons;

    public String white = "W";
    public String black = "B";
    public String empty = "E";

    private Game game = new Game();

    public void setBoardDimension(int dimension){
        boardDimension = dimension;
    }

    public int getBoardDimension(){
        return boardDimension;
    }

    public void newGame (){
        makeBoard();
        removeButton();
        blackPlayer.setTurn(true);
        whitePlayer.setTurn(false);
    }

    //generates the board as an array of strings
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
    sets the prune status as true or false
     */
    //public void setPrune(boolean v){
        //prune = v;
    //}
    /*
    sets the plyCutoff int value
     */
    //public void setPlyCutoff(int v){
      //  plyCutoff = v;
    //}
    /*
    returns the score of the Black Stone from Player Class
     */
    public int getBlackScore(){
        return blackPlayer.getScore();
    }

    /*
    returns the score of the White Stone from Player Class
     */
    public int getWhiteScore(){
        return whitePlayer.getScore();
    }

    /*
    sets the score of the Black Stone from Player Class
     */
    public void updateBlackScore(){
        blackPlayer.updateScore();
    }

    /*
    sets the score of the White Stone from Player Class
     */
    public void updateWhiteScore(){
        whitePlayer.updateScore();
    }

    /*
    returns the turn of the Black Stone from Player Class
     */
    public boolean getBlackTurn(){
        return blackPlayer.isTurn();
    }

    /*
    returns the turn of the White Stone from Player Class
     */
    public boolean getWhiteTurn(){
        return whitePlayer.isTurn();
    }

    /*
    sets the turn of the Black Stone from Player Class
    accepts boolean parameter
    setBlackTurn(boolean)
     */
    public void setBlackTurn(boolean i){
        blackPlayer.setTurn(i);
    }

    /*
   sets the turn of the White Stone from Player Class
   accepts boolean parameter
   setWhiteTurn(boolean)
    */
    public void setWhiteTurn(boolean i){
        whitePlayer.setTurn(i);
    }
    private boolean blackIsComputer = false;
    private boolean whiteIsComputer = false;
    public void setBlackAsComputer(){ blackIsComputer = true; blackPlayer.setComputerPlays(true); whitePlayer.setComputerPlays(false);}

    public void setWhiteAsComputer(){ whiteIsComputer = true; whitePlayer.setComputerPlays(true); blackPlayer.setComputerPlays(false);}

    public boolean getIsBlackComputer (){return blackPlayer.isComputer();}

    public boolean getIsWhiteComputer (){return whitePlayer.isComputer();}
    /*
    accepts InputStream parameter
    Loads the selected game file from State.java
    sets the gameState
    sets score of each player
    sets turn of each player
     */
    public void loadGame(InputStream is){
        gameState.setInputStream(is);

        boardDimension = gameState.getDimension();
        //System.out.println(boardDimension);
        board = gameState.getBoard();
        //System.out.println(board);
        blackPlayer.setScore(gameState.getBlackScore());
        whitePlayer.setScore(gameState.getWhiteScore());

        blackPlayer.setTurn(gameState.getBlackTurn());
        whitePlayer.setTurn(gameState.getWhiteTurn());

        blackPlayer.setComputerPlays(gameState.getBlackComputer());
        whitePlayer.setComputerPlays(gameState.getWhiteComputer());

        if (getIsBlackComputer()) blackIsComputer = true;
        else whiteIsComputer = true;
    }
    //randomly removes two buttons, set their imageResource to 0
    /*
    for black stones
    do {
        generate random row and col
    }while ((anyone is odd))
    updateBoard[row][col]

    for white stones
    do{
        generate random row and col
    } while((both are odd) or (both are even))

    updateBoard[row][col]
    */

    private Pair<Integer, Integer> removedBtn1;
    private Pair<Integer, Integer> removedBtn2;
    public void removeButton() {
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

    public Pair<Integer, Integer>[] getRemovedBtns(){
        Pair<Integer, Integer> [] temp = new Pair[2];
        temp[0] = removedBtn1;
        temp[1] = removedBtn2;
        return temp;
    }

    //updateButton updates the board array once the move is made
    //calls slotsToRemove from game class, receives the row and column values as row*10+col
    //breaks down the row = row/10, col = col%10.
    //update Button (Source Row, Source Column, Destination Row, Destination Column)
    //returns the pair<row, col>
    public Pair updateButton(int srcRow, int srcCol, int dstRow, int dstCol){
        //-------update the destination------
        if (isBlack(srcRow,srcCol)) board[dstRow][dstCol] = black;
        else board[dstRow][dstCol] = white;

        //-------update the source-----------
        board[srcRow][srcCol] = empty;

        //-------update the captured stone--------
        int temp = game.slotsToRemove(srcRow, srcCol, dstRow, dstCol);
        int btnRow = temp/10;
        int btnCol = temp%10;
        board[btnRow][btnCol] = empty;
        Pair btnId = new Pair<>(btnRow, btnCol);
        return btnId;
    }

    //check to see if the move is valid or not
    //first, checks if both destination and source are the same stone slots
    //second, checks if destination and source are two slots away, if the destination slot is empty, and if the source slot is empty
    //third, checks the slotsToRemove, returns true if the slotsToRemove is opponent's stone| else returns false
    //isValid(Source Row, Source Column, Destination Row, Destination Column)
    //returns boolean value
    public boolean isValid(int srcRow, int srcCol, int dstRow, int dstCol){
        int slot;
        //for black stones
        if (isBlack(dstRow, dstCol) && (isBlack(srcRow, srcCol))){
            if ((isTwoSlotsAway(srcRow, srcCol, dstRow, dstCol))&&(isEmptyStone(dstRow, dstCol))&&!(isEmptyStone(srcRow, srcCol))) {
                //get the stone that is to be captured
                slot = game.slotsToRemove(srcRow, srcCol, dstRow, dstCol);
                return (board[slot / 10][slot % 10].equals(white));
            }
            else return false;
        }

        //for white stones
        else if (isWhite(dstRow,dstCol) && isWhite(srcRow, srcCol)){

            if(isTwoSlotsAway(srcRow,srcCol,dstRow,dstCol) && isEmptyStone(dstRow, dstCol)&&!(isEmptyStone(srcRow, srcCol))){

                slot = game.slotsToRemove(srcRow, srcCol, dstRow, dstCol);
                return (board[slot / 10][slot % 10].equals(black));
            }
            else return false;
        }

        //for empty slots
        else return false;
    }

    //checks if the stone is black or not
    //isBlack(Row, Column)
    //returns true if the (Row, Column) == (Even, Even) or (Odd,Odd)
    public boolean isBlack (int R, int C){
        return ((R%2==0 && C%2==0) || (R%2==1 && C%2==1));
    }

    //checks if the stone is white or not
    //isWhite(Row, Column)
    //Returns true if the (Row, Column) == (Odd, Even) or (Even, Odd)
    public boolean isWhite (int R, int C){
        return ((R%2!=0 && C%2==0) || (R%2==0 && C%2!=0));
    }

    //checks if the destination slot is two slots away
    /*returns true if
        destinationColumn==SourceColumn and (destinationRow ==SourceRow +- 2)
        if destinationRow==SourceRow and (destinationCol==SourceCol+-2)
    */
    //isTwoSlotsAway(Source Row, Source Column, Destination Row, destination Column
    //returns boolean value
    private boolean isTwoSlotsAway (int sR, int sC, int dR, int dC){
        return (((dR == sR + 2) && (dC == sC)) || ((dC == sC + 2)&& (dR == sR)) || ((dR == sR - 2)&& (dC == sC)) || ((dC == sC - 2) && (dR == sR)));
    }

    //checks if the board[Row][Col] equals empty in the board array
    //isEmpty(Row, Column)
    //returns false if either row or col < 0 or > 5
    public boolean isEmptyStone(int dR, int dC){

        if (!isInBoard(dR, dC)){
            return false;
        }

        else {
            return (board[dR][dC].equals(empty));
        }
    }

    //check to see if immediate next move is possible
    //uses the isValid function mentioned above

    //isValidNextMove(Row, Column)
    /*returns true if
        isValid(same Col, Row+-2) and isValid(same Row, Col+-2)
    */
    public boolean isValidNextMove(int dR, int dC){
        int sR = dR;
        int sC = dC;

        return ((isValid(sR, sC, sR+2, sC))||(isValid(sR,sC,sR-2,sC))||(isValid(sR,sC, sR, sC+2))||isValid(sR,sC,sR,sC-2));
    }

    //check in every slot to see if there is any more move left for black stone
    //traverses the board array and uses the isValid function, isValid(sameRow, Col+-2) or isValid(sameCol, Row+-2)
    //keeps count if any one returns true
    //if count > 0, returns true
    public boolean checkRemainingMovesForBlack() {
        int count = 0;
        for (int i = 0; i < boardDimension; i++) {
            for (int j = 0; j < boardDimension; j++) {
                //check if slot is black
                if (isBlack(i,j) && !isEmptyStone(i,j)) {
                    //check if any one of the neighbouring slots is valid for move
                    //check up
                    if (isValid(i, j, i - 2, j)||isValid(i, j, i + 2, j)||isValid(i, j, i, j + 2)||isValid(i, j, i, j - 2)){
                        count++;
                    }
                }
            }
        }
        //System.out.print("Black valid moves: ");
        //System.out.println(count);
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
        //System.out.print("White valid moves: ");
        //System.out.println(count);
        return (count != 0);
    }

    //checks if the slot is in board 6X6
    public boolean isInBoard(int r, int c){
        return (r >= 0 && r <boardDimension && c >= 0 && c <boardDimension);
    }

    public void getHumanMoves(int cutOff, boolean prune){

        //getNextChild(board, false);
        //getAllAvailableMoves(board, false);
        getMinimaxMoves(cutOff, prune, false);
    }

    public void getComputerMoves(int cutOff, boolean prune){

        //getNextChild(board, true);
        //getAllAvailableMoves(board, true);
        getMinimaxMoves(cutOff, prune, true);
    }

    private void getMinimaxMoves(int cutOff, boolean prune, boolean computer){
        //set up default alpha beta values
        int alpha = Integer.MAX_VALUE;
        int beta = Integer.MIN_VALUE;

        final String[][]tempBoard = board;

        if(getIsBlackComputer()){
            MiniMax(true, prune, computer, tempBoard, cutOff, getBlackScore(), getWhiteScore());
            //minimax(getWhiteScore(), getBlackScore(), alpha, beta, cutOff, computer,true, prune, tempBoard );
            System.out.println(bestMove.row+" "+bestMove.col+" "+bestMove.score);
        }
        else{
            MiniMax(true, prune, computer, tempBoard, cutOff, getWhiteScore(), getBlackScore());
            //minimax(getBlackScore(), getWhiteScore(), alpha, beta, cutOff, computer, true, prune, tempBoard );
            System.out.println(bestMove.row+" "+bestMove.col+" "+bestMove.score);
        }
    }

    public Move bestMove;
    private int MiniMax(boolean maximizer, boolean prune, boolean computer, String[][]board, int cutOff, int computerScore, int humanScore){
        //Base Case
        if (cutOff == 0 || !checkRemainingMovesForWhiteMiniMax(board) || !checkRemainingMovesForBlackMiniMax(board)){
            int heuristic;
            if (getBlackTurn()){
                if (getIsBlackComputer()) heuristic = computerScore-humanScore;
                else heuristic = humanScore-computerScore;
            }
            else{
                if (getIsWhiteComputer()) heuristic = computerScore-humanScore;
                else heuristic = humanScore-computerScore;
            }
            System.out.println("-----*-----");
            System.out.println("Heuristic returned: "+ heuristic);
            return heuristic;
        }

        if (maximizer){
            int bestHeuristic = Integer.MIN_VALUE;
            Move tempBest = new Move(-1,-1);

            //---generate the tree of the current game state---
            //get all the possible moves from the current game state
            Queue<Pair<Move, Move>> allMoves = getAllAvailableMoves(board, computer);
            //go to each move and generate its own tree
            while(!allMoves.isEmpty()) {
                //get the source node, destination node
                Pair<Move, Move> child = allMoves.poll();
                //Store the current board
                String[][] previousBoard = new String[boardDimension][boardDimension];
                copyBoard(previousBoard, board);
                //make the move in the passed board
                makeMoveForMiniMax(child, board);
                //store the child destination node as Move
                Move childMove = child.second;
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
                int tempHeuristic = MiniMax(false, prune, computer, board, cutOff-1, computerScore, humanScore);

                board = previousBoard;
                //change heuristic value only if the tempHeuristic is maximum
                if (tempHeuristic > bestHeuristic){
                    tempBest = childMove;
                    bestHeuristic = tempHeuristic;
                }

                //re-store the turn that was changed previously
                computer = !computer;
                //restore the score as well
                if (computer) computerScore-=childMove.score;
                else humanScore-=childMove.score;
                //do the while loop to get the next child
            }
            bestMove = tempBest;
            return bestHeuristic;
        }
        else{//minimizer
            int bestHeuristic = Integer.MAX_VALUE;
            Move tempBest = new Move(-1,-1);
            Queue<Pair<Move, Move>> allMoves = getAllAvailableMoves(board, computer);
            while(!allMoves.isEmpty()) {
                Pair<Move, Move> child = allMoves.poll();
                String[][] previousBoard = new String[boardDimension][boardDimension];
                copyBoard(previousBoard, board);
                makeMoveForMiniMax(child, board);
                Move childMove = child.second;
                if (computer){
                    computerScore += childMove.score;
                    computer = false;
                }
                else {
                    humanScore += childMove.score;
                    computer = true;
                }
                int tempHeuristic = MiniMax(true, prune, computer, board, cutOff-1, computerScore, humanScore);
                board = previousBoard;
                if (tempHeuristic < bestHeuristic){
                    tempBest = childMove;
                    bestHeuristic = tempHeuristic;
                }
                computer = !computer;
                if(computer) computerScore-=childMove.score;
                else humanScore-=childMove.score;
            }
            bestMove = tempBest;
            return bestHeuristic;
        }
    }

    private void copyBoard(String[][]toThis, String[][]fromThis){
        for(int i = 0; i<fromThis.length; i++){
            for (int j = 0; j<fromThis[i].length; j++){
                toThis[i][j] = fromThis[i][j];
            }
        }
    }

    private void makeMoveForMiniMax(Pair<Move,Move> moves, String[][]board){
        Move source = moves.first;
        Move destination = moves.second;
        Stack<Pair<Integer, Integer>> jumps = getPath(source, destination);

        int sourceRow = source.row;
        int sourceCol = source.col;
        System.out.println("-----next-----");
        System.out.println("source: "+source.row+" X "+ source.col);

        while(!jumps.isEmpty()){
            Pair<Integer, Integer> nextJump = jumps.pop();
            int destinationRow = nextJump.first;
            int destinationCol = nextJump.second;
            System.out.println("destination: "+ destinationRow + " X "+ destinationCol);
            board = updateButtonForMiniMax(sourceRow, sourceCol, destinationRow, destinationCol, board);
            sourceRow = destinationRow;
            sourceCol = destinationCol;
            for(int i = 0; i < boardDimension; i++){
                for (int j = 0; j<boardDimension; j++){
                    System.out.print(board[i][j] + " ");
                }
                System.out.println("\n");
            }
        }
    }

    /*private void getNextChild(String[][]board, boolean computer){
        Queue<Pair<Move, Move>> allMoves = getAllAvailableMoves(board, computer);
        while(!allMoves.isEmpty()) {
            Pair<Move, Move> temp = allMoves.poll();
            Move source = temp.first;
            Move destination = temp.second;
            getPath(source, destination);
        }
    }*/

    private Stack<Pair<Integer, Integer>> getPath(Move source, Move destination){
        //int count = destination.score;
        Move temp = destination;
        Stack<Pair<Integer, Integer>> movesMade = new Stack<>();
        while(temp!=source){
            movesMade.push(new Pair<>(temp.row, temp.col));
            temp = temp.parent;
        }
        /*while(!movesMade.isEmpty()){
            Pair<Integer,Integer> t = movesMade.pop();
            System.out.println("node: " + t.first + " X " + t.second);
        }*/
        return movesMade;
    }


    //returns all the available moves for this board.
    //does a depth first search on every available move and stores the source Move and destination Move as a Pair
    //returns: Vector<Pair<Move,Move>>
    private Queue<Pair<Move,Move>> getAllAvailableMoves(String[][]board, boolean computer){
        int row = -1;
        int col = -1;
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
                if(isValidNextMoveForMiniMax(row, col, board) && !isEmptyStoneForMiniMax(row, col, board)) {
                    //get all the destination nodes
                    Move sourceMove = new Move(row, col);
                    Vector<Move> destinationMoves = DFS(sourceMove);
                    System.out.println("source: "+sourceMove.row+" X "+sourceMove.col);
                    for (int i = 0; i < destinationMoves.size(); i++) {
                        //now store all the destination Moves into the allMoves array
                        System.out.println("destination: "+ destinationMoves.get(i).row + " X " + destinationMoves.get(i).col + " score: "+ destinationMoves.get(i).score);
                        allMoves.add(new Pair<>(sourceMove, destinationMoves.get(i)));
                    }
                }
            }
        }
        return allMoves;
    }

    //checks if the passed node in the passed board has a valid next move with empty stone
    public boolean isValidNextMoveForMiniMax(int sR, int sC, String[][]board){
        //just pass move(-1,-1) as a dummy move for now
        //we only need to see if the destination is empty slot or not
        return ((isValidForMiniMax(sR, sC, sR+2, sC, board, new Move(-1,-1)))||(isValidForMiniMax(sR,sC,sR-2,sC, board, new Move(-1,-1)))||(isValidForMiniMax(sR,sC, sR, sC+2, board, new Move(-1,-1)))||isValidForMiniMax(sR,sC,sR,sC-2, board, new Move(-1,-1)));
    }

    //returns all available moves for the move parameter passed
    private Vector<Move> DFS(Move move){
        Vector<Move> moves = new Vector<>();
        Stack<Move> stack = new Stack<>();
        ArrayList<Pair<Integer, Integer>> visited = new ArrayList<>();
        stack.push(move);
        boolean initial = true;
        //DFS starts here
        while (!stack.isEmpty()) {
            Move current = stack.pop();
            int r = current.row;
            int c = current.col;

            if (!visited.contains(new Pair<>(r,c)) && !initial) {
                visited.add(new Pair<>(r,c));
            }

            initial = false;

            //west
            if (isValidForMiniMax(r, c, r, c - 2, board, move)) {
                Move temp = new Move( current.row, current.col-2);
                //temp.score = current.score;
                //there is a valid north move
                //change temp to that move with score updated
                temp.score = current.score + 1;
                //now check if that move was visited
                //for the first generated children
                if (!visited.contains(new Pair<>(temp.row, temp.col)) && current.parent == null) {
                    temp.parent = current;
                    //visited.add(new Pair<>(temp.row, temp.col));
                    //if not visited yet, then push it into the stack
                    stack.push(temp);
                    moves.add(temp);
                }
                else if (!visited.contains(new Pair<>(temp.row, temp.col))&& (temp.row!=current.parent.row || temp.col!=current.parent.col)){
                    temp.parent = current;
                    //visited.add(new Pair<>(temp.row, temp.col));
                    //if not visited yet, then push it into the stack
                    stack.push(temp);
                    moves.add(temp);
                }
            }
            //south
            if (isValidForMiniMax(r, c, r + 2, c, board, move)) {
                Move temp = new Move( current.row+2, current.col);
                temp.score = current.score+1;
                temp.parent = current;
                if(!visited.contains(new Pair<>(temp.row, temp.col))&& current.parent==null) {
                    //visited.add(new Pair<>(temp.row, temp.col));
                    stack.push(temp);
                    moves.add(temp);
                }
                else if (!visited.contains(new Pair<>(temp.row, temp.col))&&(temp.row!=current.parent.row || temp.col!=current.parent.col)){
                    //visited.add(new Pair<>(temp.row, temp.col));
                    stack.push(temp);
                    moves.add(temp);
                }
            }
            //east
            if (isValidForMiniMax(r, c, r, c+2, board, move)) {
                Move temp = new Move( current.row, current.col+2);
                temp.score = current.score+1;
                temp.parent = current;
                if(!visited.contains(new Pair<>(temp.row, temp.col)) &&  current.parent==null) {
                    //visited.add(new Pair<>(temp.row, temp.col));
                    stack.push(temp);
                    moves.add(temp);
                }
                else if (!visited.contains(new Pair<>(temp.row, temp.col))&& (temp.row!=current.parent.row || temp.col!=current.parent.col)) {
                    //visited.add(new Pair<>(temp.row, temp.col));
                    stack.push(temp);
                    moves.add(temp);
                }
            }
            //north
            if (isValidForMiniMax(r, c, r - 2, c, board, move)) {
                Move temp = new Move( current.row-2, current.col);
                temp.score = current.score+1;
                temp.parent = current;
                if(!visited.contains(new Pair<>(temp.row, temp.col))&&  current.parent==null) {
                    //visited.add(new Pair<>(temp.row, temp.col));
                    stack.push(temp);
                    moves.add(temp);
                }
                else if (!visited.contains(new Pair<>(temp.row, temp.col))){
                    if (temp.row!=current.parent.row || temp.col!=current.parent.col) {
                        //visited.add(new Pair<>(temp.row, temp.col));
                        stack.push(temp);
                        moves.add(temp);
                    }
                }
            }
        }
        //finished DFS
        return moves;
    }

    //keep track of visited children in a certain board.
    //Map<String[][], Vector<Move>> visitedChildren = new HashMap<>();
    //
    /*private Move getNextNode(Move move, String[][] tempBoard, boolean computer){
        int row = move.row;
        int col = move.col;
        Move temp = null;

        Vector<Move> moveVector = new Vector<>();
        if(visitedChildren.containsKey(tempBoard)) {
            moveVector = visitedChildren.get(tempBoard);
        }
        else{
            visitedChildren.put(tempBoard, moveVector);
        }

        //visit every node of the board and check it's valid moves
        while ((row < boardDimension-1 && row >=-1) || (col < boardDimension && col>=-1)){
            if(row==-1) row++;
            if (col < boardDimension-1 && col >= -1){
                col++;
            }
            else if (row < boardDimension-1){
                row++;
                col = 0;
            }
            else continue; //we have the next row and col from the board

            if((blackIsComputer && computer && isBlack(row,col)) || (whiteIsComputer && computer && isWhite(row, col)) || (!blackIsComputer && !computer && isBlack(row, col)) || (!whiteIsComputer && !computer && isWhite(row,col))){
                //now check it's next move
                //north
                if (isValidForMiniMax(row, col, row - 2, col, tempBoard) && !visitedChildren.get(tempBoard).contains(new Move(row - 2, col))) {
                    temp = new Move(row - 2, col);
                    temp.score++;
                    moveVector.add(temp);
                    visitedChildren.put(tempBoard, moveVector);
                    tempBoard = updateButtonForMiniMax(row, col, row - 2, col, tempBoard);
                }
                //east
                else if (isValidForMiniMax(row, col, row, col + 2, tempBoard) && !visitedChildren.get(tempBoard).contains(new Move(row, col + 2))) {
                    temp = new Move(row, col + 2);
                    temp.score++;
                    moveVector.add(temp);
                    visitedChildren.put(tempBoard, moveVector);
                    tempBoard = updateButtonForMiniMax(row, col, row, col + 2, tempBoard);
                }
                //south
                else if (isValidForMiniMax(row, col, row + 2, col, tempBoard) && !visitedChildren.get(tempBoard).contains(new Move(row, col + 2))) {
                    temp = new Move(row + 2, col);
                    temp.score++;
                    moveVector.add(temp);
                    visitedChildren.put(tempBoard, moveVector);
                    tempBoard = updateButtonForMiniMax(row, col, row + 2, col, tempBoard);
                }
                //west
                else if (isValidForMiniMax(row, col, row, col - 2, tempBoard) && !visitedChildren.get(tempBoard).contains(new Move(row, col + 2))) {
                    temp = new Move(row, col - 2);
                    temp.score++;
                    moveVector.add(temp);
                    visitedChildren.put(tempBoard, moveVector);
                    tempBoard = updateButtonForMiniMax(row, col, row, col - 2, tempBoard);
                } else continue;
            }
            if(temp!=null){ //if there were no moves in the board then temp would remain null
                return temp;
            }
        }
        return null;
    }*/

    //makes the jump for the board passed.
    public String[][] updateButtonForMiniMax(int srcRow, int srcCol, int dstRow, int dstCol, String[][]board){
        //-------update the destination------
        if (isBlack(srcRow,srcCol)) board[dstRow][dstCol] = black;
        else board[dstRow][dstCol] = white;

        //-------update the source-----------
        board[srcRow][srcCol] = empty;

        //-------update the captured stone--------
        int temp = game.slotsToRemove(srcRow, srcCol, dstRow, dstCol);
        int btnRow = temp/10;
        int btnCol = temp%10;
        board[btnRow][btnCol] = empty;

        return board;
    }

    public boolean isValidForMiniMax(int srcRow, int srcCol, int dstRow, int dstCol, String[][]board, Move move){
        int slot;
        //for black stones
        if (isBlack(dstRow, dstCol) && isBlack(srcRow, srcCol) && isTwoSlotsAway(srcRow, srcCol, dstRow, dstCol) && isInBoard(dstRow, dstCol)){
            if(isEmptyStoneForMiniMax(dstRow, dstCol, board)) {
                //get the stone that is to be captured
                slot = game.slotsToRemove(srcRow, srcCol, dstRow, dstCol);
                return (board[slot / 10][slot % 10].equals(white));
            }
            else if (dstRow == move.row && dstCol == move.col){
                slot = game.slotsToRemove(srcRow, srcCol, dstRow, dstCol);
                return (board[slot / 10][slot % 10].equals(white));
            }
            else return false;
        }
        //for white stones
        else if (isWhite(dstRow,dstCol) && isWhite(srcRow, srcCol) && isTwoSlotsAway(srcRow,srcCol,dstRow,dstCol) && isInBoard(dstRow, dstCol)){
            if (isEmptyStoneForMiniMax(dstRow,dstCol, board)) {
                slot = game.slotsToRemove(srcRow, srcCol, dstRow, dstCol);
                return (board[slot / 10][slot % 10].equals(black));
            }
            else if ( dstRow == move.row && dstCol == move.col){
                slot = game.slotsToRemove(srcRow, srcCol, dstRow, dstCol);
                return (board[slot / 10][slot % 10].equals(black));
            }
            else return false;
        }
        //for empty slots
        else return false;
    }

    public boolean isEmptyStoneForMiniMax(int dR, int dC, String[][]board){

        if (!isInBoard(dR, dC)){
            return false;
        }

        else {
            return (board[dR][dC].equals(empty));
        }
    }
    public boolean checkRemainingMovesForBlackMiniMax(String [][] board) {
        int count = 0;
        for (int i = 0; i < boardDimension; i++) {
            for (int j = 0; j < boardDimension; j++) {
                //check if slot is black
                if (isBlack(i,j) && !isEmptyStoneForMiniMax(i,j, board)) {
                    //check if any one of the neighbouring slots is valid for move
                    //check up
                    if (isValidForMiniMax(i, j, i - 2, j,board, new Move(-1,-1))||isValidForMiniMax(i, j, i + 2, j,board, new Move(-1,-1))||isValidForMiniMax(i, j, i, j + 2, board, new Move(-1,-1))||isValidForMiniMax(i, j, i, j - 2, board, new Move(-1,-1))){
                        count++;
                    }
                }
            }
        }
        //System.out.print("Black valid moves: ");
        //System.out.println(count);
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
        //System.out.print("White valid moves: ");
        //System.out.println(count);
        return (count != 0);
    }




    /*
    //empties the queues  BFS,DFS, BestFirstSearch and tempQueue
    public void clearQueues(){
        while(!tempQueue.isEmpty()){
            tempQueue.poll();
        }
        while(!BFSvisitedNodes.isEmpty()){
            BFSvisitedNodes.poll();
        }
        while(!DFSvisitedNodes.isEmpty()){
            DFSvisitedNodes.poll();
        }
        while(!BestFirstSearchList.isEmpty()) {
            BestFirstSearchList.poll();
        }
        toThisButton = null;
        fromThisButton = null;
    }

    //Breadth First Search
    /*
    clear tempQueue
    initialize Pair(row, col), queue, visited List
    add Pair(0,0) to queue

    while(queue is not empty){
        Pair current = queue.poll
        if (not visited)
            add current to BFSQueue
            add current to visited

        traverse to every valid node in the following order and if not visited then add it to queue
        west
        east
        north
        south
    }

    public void BFS(int row, int col){
        if (row == 0 && col == 0 && !getBlackTurn()) {
            row = 0;
            col = 1;
        }

        while(!tempQueue.isEmpty()) tempQueue.poll();
        Pair<Integer, Integer> pair = new Pair<>(row, col);
        Queue<Pair<Integer, Integer>> queue = new LinkedList<>();
        ArrayList<Pair<Integer, Integer>> visited = new ArrayList<>();
        queue.add(pair);

        while(!queue.isEmpty()){
            //because all nodes should be visited and a tree is constructed as BFSVisitedNodes queue
            Pair<Integer, Integer> current = queue.poll();

            if (!visited.contains(current)) {
                BFSvisitedNodes.add(current);
                visited.add(current);
                //System.out.println(current.first+"X"+current.second);
            }

            int r = current.first;
            int c = current.second;

            //west
            if (!visited.contains(new Pair<>(r, c - 1)) && (isInBoard(r, c - 1))) {
                queue.add(new Pair<>(r, c - 1));
                //System.out.println(r+"X"+ c);
            }
            //east
            if (!visited.contains(new Pair<>(r, c + 1)) && (isInBoard(r, c + 1))) {
                queue.add(new Pair<>(r, c + 1));
                //System.out.println(r+"X"+c);
            }
            //north
            if (!visited.contains(new Pair<>(r - 1, c)) && (isInBoard(r - 1, c))) {
                queue.add(new Pair<>(r - 1, c));
                //System.out.println(r+"X"+c);
            }
            //south
            if (!visited.contains(new Pair<>(r + 1, c)) && (isInBoard(r + 1, c))) {
                queue.add(new Pair<>(r + 1, c));
                //System.out.println(r+"X"+c);
            }
        }
    }

    //Depth First Search
    /*

    clear tempQueue
    initialize Pair(row, col), stack, visited List
    push Pair(0,0) to stack

    while(queue is not empty){
        Pair current = stack.pop
        if (not visited)
            add current to DFSQueue
            add current to visited

        traverse to every valid node in following order and if not visited then push it to stack
        south
        north
        west
        east

    public void DFS(int row, int col){
        if (row == 0 && col == 0 && !getBlackTurn()) {
            row = 0;
            col = 1;
        }

        while(!tempQueue.isEmpty()) tempQueue.poll();
        Pair<Integer, Integer> pair = new Pair<>(row, col);
        Stack<Pair<Integer, Integer>> stack = new Stack<>();
        ArrayList<Pair<Integer, Integer>> visited = new ArrayList<>();
        stack.push(pair);

        while (!stack.isEmpty()) {
            Pair<Integer, Integer> current = stack.pop();
            if (!visited.contains(current)) {
                DFSvisitedNodes.add(current);
                visited.add(current);
            }

            int r = current.first;
            int c = current.second;

            //to agree with row major order
            if (c == 5) c = 0;

            if (!visited.contains(new Pair<>(r + 1, c)) && (isInBoard(r + 1, c))) {
                stack.push(new Pair<>(r + 1, c));
            }
            if (!visited.contains(new Pair<>(r, c - 1)) && (isInBoard(r, c - 1))) {
                stack.push(new Pair<>(r, c - 1));
            }
            if (!visited.contains(new Pair<>(r, c + 1)) && (isInBoard(r, c + 1))) {
                stack.push(new Pair<>(r, c + 1));
            }
            if (!visited.contains(new Pair<>(r - 1, c)) && (isInBoard(r - 1, c))) {
                stack.push(new Pair<>(r - 1, c));
            }
        }
    }

    private boolean sameBtn = false;
    private Queue<Pair<Integer, Integer>> tempQueue = new LinkedList<>();

    //parameters passed integer id from spinner id
    //returns the next valid move from the BFS, DFS and BestFirstSearch queues

    Queue<Pair<Integer, Integer>> queue = new LinkedList<>();
    set queue with respect to the id passed. DFSVisitedNodes, BFSVisitedNodes, BestFirstSearchList
    if (id != 2) do DFS and BFS search lists
        if (not same button)
            while (queue is not empty)
            Pair<Integer, Integer> pair = queue.poll
            get r as row, c as column from pair

            if (the stone corresponds to the current player turn and the stone has next valid move)
                call heuristic(pair) and update tempQueue, assign every child of pair to it
                poll tempQueue as we do not need the first pair, its r,c itself
                set sameBtn false;
                if (tempQueue is not empty)
                    set toThisButton to tempQueue.poll
                    if(tempQueue is Empty()) set sameBtn false
                    else set sameBtn true
                    break out of while loop

        else
            if (tempQueue is not empty)
                toThisButton = tempQueue.poll();
                if(tempQueue.isEmpty()) set sameBtn false;
                else set sameBtn true;
            else set sameBtn false;

    else do BestFirstSearch
        while (queue is not empty)
            Pair<Integer, Integer> pair = queue.poll();
            int r = pair.first;
            int c = pair.second;
            if (the stone corresponds to the current player turn and the stone has next valid move) {
                call heuristic(pair)
                set fromThisButton as pair
                set toTheseButtons as list returned by getAllButtons()
                break the while loop

    update the global queue for future use

    public void getNextValidMove(int id) {
        Queue<Pair<Integer, Integer>> queue = new LinkedList<>();
        if (id == 0) {
            queue = DFSvisitedNodes;
        } else if (id == 1) {
            queue = BFSvisitedNodes;
        } else if (id == 2){
            queue = BestFirstSearchList;
        }

        if (id != 2) {
            //DFS and BFS searches
            if (!sameBtn) {
                while (!queue.isEmpty()) {
                    Pair<Integer, Integer> pair = queue.poll();
                    int r = pair.first;
                    int c = pair.second;

                    if (((getBlackTurn() && isBlack(r, c)) || (getWhiteTurn() && isWhite(r, c))) && isValidNextMove(r, c)) {
                        heuristic(pair); //updates tempQueue, assigns every child of pair to it
                        tempQueue.poll(); //the first pair is r,c itself
                        sameBtn = false;
                        if (!tempQueue.isEmpty()) {
                            toThisButton = tempQueue.poll(); //the child of r,c
                            if(tempQueue.isEmpty()) sameBtn = false;
                            else sameBtn = true;
                            break;
                        }
                    }
                }
            }

            else {
                if (!tempQueue.isEmpty()) {
                    toThisButton = tempQueue.poll();
                    if(tempQueue.isEmpty()) sameBtn = false;
                    else sameBtn = true;
                } else sameBtn = false;
            }
        }

        else {
            //Best First Search
            while (!queue.isEmpty()) {
                Pair<Integer, Integer> pair = queue.poll();
                int r = pair.first;
                int c = pair.second;
                if (((getBlackTurn() && isBlack(r, c)) || (getWhiteTurn() && isWhite(r, c))) && isValidNextMove(r, c)) {
                    heuristic(pair);
                    fromThisButton = pair;
                    toTheseButtons = getAllButtons(1);
                    break;
                }

                //for (int i = 0; i < toTheseButtons.size(); i++) System.out.println("dest Btns" + toTheseButtons);
                //   if (((getBlackTurn() && isBlack(r, c)) || (getWhiteTurn() && isWhite(r, c))) && isValidNextMove(r, c)) {
                //       heuristic(pair);
                //       break;
                //   }
            }
        }

        //update the global queue for future use
        if (id == 0) {
            DFSvisitedNodes = queue;
        } else if (id == 1) {
            BFSvisitedNodes = queue;
        } else if (id == 2){
            BestFirstSearchList = queue;
        }
    }

    //returns true even if source is an empty slot, but destination should also be empty
    //everything else is similar to isValid function
    public boolean isValidForHeuristic(int srcRow, int srcCol, int dstRow, int dstCol){
        int slot;
        //for black stones
        if (isBlack(dstRow, dstCol) && (isBlack(srcRow, srcCol))){
            if ((isTwoSlotsAway(srcRow, srcCol, dstRow, dstCol))&&(isEmptyStone(dstRow, dstCol))) {
                slot = game.slotsToRemove(srcRow, srcCol, dstRow, dstCol);
                return (board[slot / 10][slot % 10].equals(white));
            }
            else return false;
        }

        //for white stones
        else if (isWhite(dstRow,dstCol) && isWhite(srcRow, srcCol)){

            if(isTwoSlotsAway(srcRow,srcCol,dstRow,dstCol) && isEmptyStone(dstRow, dstCol)){

                slot = game.slotsToRemove(srcRow, srcCol, dstRow, dstCol);
                return (board[slot / 10][slot % 10].equals(black));
            }
            else return false;
        }

        //for empty slots
        else return false;
    }

    //generate a Queue with head as the node with maximum heuristic

        call BFS
        initialise ArrayList<Pair<Integer, Integer>> examined = new ArrayList<>();
        initialise maxScore = 0;
        pass it to an arrayList
        while(BFS list is not empty)
            initialise Pair<Integer, Integer> current = BFSvisitedNodes.poll();
            //add to arrayList only if it has a next valid move
            if (current has valid moves)
                add current to examined list

        sort the array according to the heuristic, the one with highest heuristic will be at the top
        now we have heuristic of every node which has a valid move
        we have the highest score
        set fromThisButton to first element of sorted examined list

    public void BestFirstSearch(){
        //call BFS
        //use that tree to start with
        BFS(0,0);
        ArrayList<Pair<Integer, Integer>> examined = new ArrayList<>();
        int maxScore = 0;
        //pass it to an arrayList
        while(!BFSvisitedNodes.isEmpty()){
            Pair<Integer, Integer> current = BFSvisitedNodes.poll();
            //add to arrayList only if it has a next valid move
            if (isValidNextMove(current.first, current.second)) {
                examined.add(current);
            }
        }
        //sort the array according to the heuristic, the one with highest heuristic will be at the top
        for (int i = 0; i<examined.size()-1;i++){
            for(int j = 0; j<examined.size()-1;j++){
                int value1 = heuristic(examined.get(j));
                int value2 = heuristic(examined.get(j+1));
                if (value2 > value1){
                    Collections.swap(examined, j, j+1);
                }
                if (value1 > maxScore) maxScore = value1;
                if (value2 > maxScore) maxScore = value2;
            }
        }
        //now we have heuristic of every node which has a valid move
        //we have the highest score
        for (int i = 0 ;i<examined.size(); i++) {
            BestFirstSearchList.add(examined.get(i));
        }
        fromThisButton = examined.get(0);
        //just to get the origin and destination buttons
        heuristic(examined.get(0));
    }

    private boolean goalNode = false;

    //no parameters passed
    //returns the list of Buttons from Source node to Goal node

    check every child, set its boolean value
    while (goal Node is not found)
        go to every node that is true
        call getList()
        check if goalNode was visited by getList(), if true then break, else continue

    private ArrayList<Pair<Integer, Integer>> getAllButtons(int type){
        //traverse through the tree, goal node = toThisButton, source node = fromThisButton
        //if the heuristic of source matches the count, then
        int destinationRow;
        int destinationCol;
        int count;
        //if it is Best First Search
        if (type == 1) {
            count = heuristic(fromThisButton);
            destinationRow = toThisButton.first;
            destinationCol = toThisButton.second;
        }
        //Branch and Bound
        else {
            count = BnBDepth;
            if (count > heuristic(fromThisButton)) count = heuristic(fromThisButton);
            destinationRow = BnBToThisButton.first;
            destinationCol = BnBToThisButton.second;
        }

        ArrayList<Pair<Integer, Integer>> finalList = new ArrayList<>();

        int r = fromThisButton.first;
        int c = fromThisButton.second;

        boolean northNode = false;
        boolean southNode = false;
        boolean eastNode = false;
        boolean westNode = false;

        if ((isInBoard(r + 2, c)) && (isValidForHeuristic(r, c, r + 2, c))){
            southNode = true;
        }
        if ((isInBoard(r - 2, c)) && (isValidForHeuristic(r, c, r - 2, c))){
            northNode = true;
        }
        if ((isInBoard(r, c - 2)) && (isValidForHeuristic(r, c, r, c - 2))){
            westNode = true;
        }
        if ((isInBoard(r, c + 2)) && (isValidForHeuristic(r, c, r, c + 2))) {
            eastNode = true;
        }

        while(!goalNode) {

            if (southNode) {
                finalList = getList(destinationRow, destinationCol, r+2, c, count);
                if (goalNode) break;
                else goalNode = false;
            }

            if (eastNode) {
                finalList = getList(destinationRow, destinationCol, r, c+2, count);
                if (goalNode) break;
                else goalNode = false;
            }

            if (westNode) {
                finalList = getList(destinationRow, destinationCol, r, c-2, count);
                if (goalNode) break;
                else goalNode = false;
            }

            if (northNode) {
                finalList = getList(destinationRow, destinationCol, r-2, c, count);
                if (goalNode) break;
                else goalNode = false;
            }

        }
        goalNode = false;
        return finalList;
    }

    //returns the list of buttons from Source to Destination bounded by count
    //DFS on the source passed
    //DFS algorithm until destination row and column are matched with the current node and the count is equal to the parameter passed into
    private ArrayList<Pair<Integer,Integer>> getList(int destinationRow, int destinationCol, int sourceRow, int sourceCol, int count){
        int tempCount = 0;
        //do DFS on EastNode until stack is empty
        Stack <Pair<Integer, Integer>> stack = new Stack<>();
        ArrayList <Pair <Integer, Integer>> tempList = new ArrayList<>();
        ArrayList<Pair <Integer, Integer>> finalList = new ArrayList<>();

        stack.push(new Pair<>(sourceRow, sourceCol));

        while (!stack.isEmpty()) {
            Pair<Integer, Integer> current = stack.pop();
            int row = current.first;
            int col = current.second;

            if (!tempList.contains(current)) {
                tempList.add(current);
                tempCount++;
                if (row == destinationRow && col == destinationCol && tempCount == count) {
                    finalList = tempList;
                    goalNode = true;
                    break;
                }
            }
            //if the goalButton is reached and temp count == score.
            if (!tempList.contains(new Pair<>(row + 2, col)) && (isInBoard(row + 2, col)) && (isValidForHeuristic(row, col, row + 2, col))) {
                stack.push(new Pair<>(row + 2, col));
            }
            if (!tempList.contains(new Pair<>(row - 2, col)) && (isInBoard(row - 2, col)) && (isValidForHeuristic(row, col, row - 2, col))) {
                stack.push(new Pair<>(row - 2, col));
            }
            if (!tempList.contains(new Pair<>(row, col - 2)) && (isInBoard(row, col - 2)) && (isValidForHeuristic(row, col, row, col - 2))) {
                stack.push(new Pair<>(row, col - 2));
            }
            if (!tempList.contains(new Pair<>(row, col + 2)) && (isInBoard(row, col + 2)) && (isValidForHeuristic(row, col, row, col + 2))) {
                stack.push(new Pair<>(row, col + 2));
            }
        }
        return finalList;
    }

    //private Map<Pair<Integer, Integer>, Integer> allScoreMap = new HashMap<>();
    //private ArrayList<Pair<Integer, Integer>> HeuristicVisited = new ArrayList<>();

    //Parameters passed Pair<Integer, Integer>
    //returns the integer heuristic value

    /*
    perform DFS on the pair passed
    initialise stack, arraylist of visited nodes, hashMap to keep the score of the current node
    push pair into stack
    put 0 into scoreMap of pair
    now generate a tree with heuristics assigned to all of its nodes.

        while (stack is not empty)
            initialise Pair<Integer, Integer> current = stack.pop()
            add current to tempQueue

            if (visited does not contain current pair)
                visited.add(current);

            if (current stone corresponds to the player turn)
                travers into every direction: east, west, north, south
                if (visited does not contain the pair and its in board and has valid next move)
                    if (scoreMap contains the pair and the score is greater) set the higher score
                    else put the current pair into score map by increasing its score by one from its parent

        now go through the map and get the highest score assigned pair
        that pair is the destination button to be prompted to the user

        set fromThisButton pair; //the pair whose heuristic was to be examined
        set toThisButton as first element of sorted visited list

        return scoreMap.get(visited.get(0));

    private int heuristic(Pair<Integer, Integer> pair){
        // do a dfs to this pair.
        Stack<Pair<Integer, Integer>> stack = new Stack<>();
        ArrayList<Pair<Integer, Integer>> visited = new ArrayList<>();

        //hashMap to keep the score of the current node
        Map<Pair<Integer, Integer>, Integer> scoreMap = new HashMap<>();
        stack.push(pair);
        //the parent node is initially of score zero
        scoreMap.put(pair,0);
        //generate a tree with heuristics assigned to all of its nodes.
        while (!stack.isEmpty()) {
            Pair<Integer, Integer> current = stack.pop();
            tempQueue.add(current);
            if (!visited.contains(current)) {
                visited.add(current);
                //HeuristicVisited.add(current);
            }
            int r = current.first;
            int c = current.second;

            if (((getBlackTurn() && isBlack(r, c)) || (getWhiteTurn() && isWhite(r, c)))) {
                if (!visited.contains(new Pair<>(r, c - 2)) && (isInBoard(r, c - 2)) && isValidForHeuristic(r, c, r, c - 2)) {
                    stack.push(new Pair<>(r, c - 2));
                    if (scoreMap.containsKey(new Pair<>(r, c - 2))) {
                        if ((scoreMap.get(current) + 1) > scoreMap.get(new Pair<>(r, c - 2))) {
                            scoreMap.put(new Pair<>(r, c - 2), scoreMap.get(current) + 1);
                            //allScoreMap.put(new Pair<>(r, c - 2), scoreMap.get(current) + 1);
                        }
                    } else {
                        scoreMap.put(new Pair<>(r, c - 2), scoreMap.get(current) + 1);
                        //allScoreMap.put(new Pair<>(r, c - 2), scoreMap.get(current) + 1);
                    }
                    /*int z = c - 2;
                    int y = scoreMap.get(current) + 1;
                    System.out.println(r + "X" + z + " has value " + y);
                }

                if (!visited.contains(new Pair<>(r + 2, c)) && (isInBoard(r + 2, c)) && (isValidForHeuristic(r, c, r + 2, c))) {
                    stack.push(new Pair<>(r + 2, c));
                    //if it is valid then put it in the scoreMap
                    //its score must be 1 more than its parents
                    //check if the scoreMap already contains the pair
                    if (scoreMap.containsKey(new Pair<>(r + 2, c))) {
                        //if yes, then get the higher value and assign that to the pair
                        if ((scoreMap.get(current) + 1) > scoreMap.get(new Pair<>(r + 2, c))) {
                            scoreMap.put(new Pair<>(r + 2, c), scoreMap.get(current) + 1);
                            //allScoreMap.put(new Pair<>(r + 2, c), scoreMap.get(current) + 1);
                        }
                    } else {
                        scoreMap.put(new Pair<>(r + 2, c), scoreMap.get(current) + 1);
                        //allScoreMap.put(new Pair<>(r + 2, c), scoreMap.get(current) + 1);
                    }
                    /*int z = r + 2;
                    int y = scoreMap.get(current) + 1;
                    System.out.println(z + "X" + c + " has value " + y);
                }
                if (!visited.contains(new Pair<>(r, c + 2)) && (isInBoard(r, c + 2)) && isValidForHeuristic(r, c, r, c + 2)) {
                    stack.push(new Pair<>(r, c + 2));
                    //nextMove.push(new Pair<>(r,c+2));
                    if (scoreMap.containsKey(new Pair<>(r, c + 2))) {
                        if ((scoreMap.get(current) + 1) > scoreMap.get(new Pair<>(r, c + 2))) {
                            scoreMap.put(new Pair<>(r, c + 2), scoreMap.get(current) + 1);
                            //allScoreMap.put(new Pair<>(r, c + 2), scoreMap.get(current) + 1);
                        }
                    } else {
                        scoreMap.put(new Pair<>(r, c + 2), scoreMap.get(current) + 1);
                        //allScoreMap.put(new Pair<>(r, c + 2), scoreMap.get(current) + 1);
                    }
                    /*int z = c + 2;
                    int y = scoreMap.get(current) + 1;
                    System.out.println(r + "X" + z + " has value " + y);
                }

                if (!visited.contains(new Pair<>(r - 2, c)) && (isInBoard(r - 2, c)) && isValidForHeuristic(r, c, r - 2, c)) {
                    stack.push(new Pair<>(r - 2, c));
                    if (scoreMap.containsKey(new Pair<>(r - 2, c))) {
                        if ((scoreMap.get(current) + 1) > scoreMap.get(new Pair<>(r - 2, c))) {
                            scoreMap.put(new Pair<>(r - 2, c), scoreMap.get(current) + 1);
                            //allScoreMap.put(new Pair<>(r - 2, c), scoreMap.get(current) + 1);
                        }
                    } else {
                        scoreMap.put(new Pair<>(r - 2, c), scoreMap.get(current) + 1);
                        //allScoreMap.put(new Pair<>(r - 2, c), scoreMap.get(current) + 1);
                    }
                    /*int z = r - 2;
                    int y = scoreMap.get(current) + 1;
                    System.out.println(z + "X" + c + " has value " + y)
                }
            }
        }


        for (int i = 0; i < visited.size(); i++) {
            System.out.println(scoreMap.get(visited.get(i)));
        }

        //reaches this point when the DFS is complete and scores are assigned to its nodes
        //now go through the map and get the highest score assigned pair
        //that pair is the destination button to be prompted to the user
        for (int j = 0; j < visited.size()-1; j++) {
            for (int i = 0; i < visited.size()-1; i++) {
                int value1 = scoreMap.get(visited.get(i));
                int value2 = scoreMap.get(visited.get(i + 1));
                if (value2 > value1) {
                    Collections.swap(visited, i, i + 1);
                }
            }
        }

        //HeuristicVisited = visited;
        //visited list is now sorted with maximum at the beginning

        /*for (int i = 0; i < visited.size(); i++) {
            System.out.println(scoreMap.get(visited.get(i)));
        }

        fromThisButton = pair; //the pair whose heuristic was to be examined
        toThisButton = visited.get(0); //the pair that had the highest heuristic

        //System.out.println("Max score: "+scoreMap.get(visited.get(0)));
        //return the maximum score received.
        return scoreMap.get(visited.get(0));
    //}

    public int BnBscore;
    public int BnBDepth;
    private Pair<Integer, Integer> BnBToThisButton;
    //parameters passed integer

    call BFS
    initialise queue as BFSVisitedNodes, pair, stack, arraylist of visited nodes, hashMap to keep the score of the current node, examined array list
    push pair into stack
    put 0 into scoreMap of pair
    now generate a tree with heuristics assigned to all of its nodes.

        while (stack is not empty)
            initialise Pair<Integer, Integer> current = stack.pop()
            add current to tempQueue

            if (visited does not contain current pair)
                visited.add(current);

            if (current stone corresponds to the player turn)
                travers into every direction: east, west, north, south
                if (visited does not contain the pair and its in board and has valid next move)
                    if (scoreMap contains the pair and the score is greater) set the higher score
                    else put the current pair into score map by increasing its score by one from its parent

        now go through the map and get the highest score assigned pair
        that pair is the destination button to be prompted to the user

        set fromThisButton pair; //the pair whose heuristic was to be examined
        set toThisButton as first element of sorted visited list


    public void BranchAndBound(int depth){
        BestFirstSearch();
        BnBDepth = depth;
        Queue<Pair<Integer,Integer>> queue = BestFirstSearchList;

        Pair<Integer, Integer> pair = queue.poll();

        Stack<Pair<Integer, Integer>> stack = new Stack<>();

        ArrayList<Pair<Integer, Integer>> visited = new ArrayList<>();

        Map<Pair<Integer, Integer>, Integer> scoreMap = new HashMap<>();

        ArrayList<Pair<Integer, Integer>> examined = new ArrayList<>();

        stack. push(pair);
        scoreMap.put(pair, 0);
        int temp = 0;

        while (temp < depth) {
            if (stack.isEmpty()) break;
            else {
                Pair<Integer, Integer> current = stack.pop();
                if (!visited.contains(current)) {
                    visited.add(current);
                }
                int r = current.first;
                int c = current.second;

                if (((getBlackTurn() && isBlack(r, c)) || (getWhiteTurn() && isWhite(r, c)))) {
                    if (!visited.contains(new Pair<>(r + 2, c)) && (isInBoard(r + 2, c)) && (isValidForHeuristic(r, c, r + 2, c))) {
                        stack.push(new Pair<>(r + 2, c));
                        examined.add(new Pair<>(r + 2, c));
                        if (scoreMap.containsKey(new Pair<>(r + 2, c)) && ((scoreMap.get(current) + 1) > scoreMap.get(new Pair<>(r + 2, c)))) {
                            scoreMap.put(new Pair<>(r + 2, c), scoreMap.get(current) + 1);
                        } else {
                            scoreMap.put(new Pair<>(r + 2, c), scoreMap.get(current) + 1);
                        }
                    }
                    if (!visited.contains(new Pair<>(r - 2, c)) && (isInBoard(r - 2, c)) && isValidForHeuristic(r, c, r - 2, c)) {
                        stack.push(new Pair<>(r - 2, c));
                        examined.add(new Pair<>(r - 2, c));
                        if (scoreMap.containsKey(new Pair<>(r - 2, c)) && ((scoreMap.get(current) + 1) > scoreMap.get(new Pair<>(r - 2, c)))) {
                            scoreMap.put(new Pair<>(r - 2, c), scoreMap.get(current) + 1);
                        } else {
                            scoreMap.put(new Pair<>(r - 2, c), scoreMap.get(current) + 1);
                        }
                    }
                    if (!visited.contains(new Pair<>(r, c - 2)) && (isInBoard(r, c - 2)) && isValidForHeuristic(r, c, r, c - 2)) {
                        stack.push(new Pair<>(r, c - 2));
                        examined.add(new Pair<>(r, c - 2));
                        if (scoreMap.containsKey(new Pair<>(r, c - 2)) && ((scoreMap.get(current) + 1) > scoreMap.get(new Pair<>(r, c - 2)))) {
                            scoreMap.put(new Pair<>(r, c - 2), scoreMap.get(current) + 1);
                        } else {
                            scoreMap.put(new Pair<>(r, c - 2), scoreMap.get(current) + 1);
                        }
                    }
                    if (!visited.contains(new Pair<>(r, c + 2)) && (isInBoard(r, c + 2)) && isValidForHeuristic(r, c, r, c + 2)) {
                        stack.push(new Pair<>(r, c + 2));
                        examined.add(new Pair<>(r, c + 2));
                        if (scoreMap.containsKey(new Pair<>(r, c + 2)) && ((scoreMap.get(current) + 1) > scoreMap.get(new Pair<>(r, c + 2)))) {
                            scoreMap.put(new Pair<>(r, c + 2), scoreMap.get(current) + 1);
                        } else {
                            scoreMap.put(new Pair<>(r, c + 2), scoreMap.get(current) + 1);
                        }
                    }
                }
                temp++;
            }
        }

        for (int j = 0; j < examined.size()-1; j++) {
            for (int i = 0; i < examined.size()-1; i++) {
                int value1 = scoreMap.get(examined.get(i));
                int value2 = scoreMap.get(examined.get(i + 1));
                if (value2 > value1) {
                    Collections.swap(examined, i, i + 1);
                }
            }
        }

        BnBscore = scoreMap.get(examined.get(0));
        System.out.println("Score: " + BnBscore);
        fromThisButton = pair;
        System.out.println("Origin: " + fromThisButton);
        BnBToThisButton = examined.get(0);
        toTheseButtons = getAllButtons(2);
        System.out.println("destination: " + toThisButton);
    }*/
}