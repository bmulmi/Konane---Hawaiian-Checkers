/***********************************************************
 * Name: Bibhash Mulmi                                      *
 * Project:  Project 1 Konane                               *
 * Class:  CMPS 331 Artificial Intelligence                 *
 * Date:  02/16/2018                                        *
 ************************************************************/

package edu.ramapo.bibhash.konane.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Bibhash on 2/11/2018.
 */

public class State {

    InputStream is;
    private int dimension;
    private int blackScore;
    private int whiteScore;
    private boolean blackTurn;
    private boolean whiteTurn;
    private boolean isBlackComputer;
    private boolean isWhiteComputer;
    private String [][] board;

    public State(){
        blackTurn = true;
        whiteTurn = false;
        blackScore = 0;
        whiteScore =0;
    }

    /*
    parameters passed InputStream
    sets the InputStream as passed
     */
    public void setInputStream(InputStream j){
        is = j;
        getGameState();
    }

    private void setDimension(int value){
        dimension = value;
    }
    /*
    parameters passed String
    sets blackScore as parsedInt of String
     */
    private void setBlackScore(String text){
        blackScore = Integer.parseInt(text);
    }

    /*
    parameters passed String
    sets whiteScore as parsedInt of String
    */
    private void setWhiteScore(String text){
        whiteScore = Integer.parseInt(text);
    }

    /*
    parameters passed boolean
    sets blackTurn as boolean passed
    */
    private void setBlackTurn(boolean trn){
        blackTurn = trn;
    }

    /*
    parameters passed boolean
    sets whiteTurn as boolean passed
    */
    private void setWhiteTurn(boolean trn){
        whiteTurn = trn;
    }

    /*
    parameters passed boolean
    sets isBlackComputer as boolean passed
     */
    private void setBlackComputer(boolean val){ isBlackComputer = val;}

    /*
    parameters passed boolean
    sets isWhiteComputer as boolean passed
     */
    private void setWhiteComputer(boolean val){ isWhiteComputer = val;}

    /*
    parameters passed String
    split string according to spaces \\s
    write the string into the board array accordingly
    */
    private void setBoard(String text){
        double tempDimension = 0;
        //get the dimension of the board
        //iterate through the characters
        for (int i = 0; i <text.length(); i ++){
            char c = text.charAt(i);
            if ( c != ' ') tempDimension++;
        }

        tempDimension = Math.sqrt(tempDimension);
        int temp = (int) tempDimension;
        System.out.println("dimension read: "+ tempDimension + " passed: " + temp);

        setDimension(temp);

        String str = text;
        String[] splitStr = str.split("\\s+");
        int h = 0;
        board = new String[dimension][dimension];
        //set up the board
        for (int i = 0; i < dimension; i++){
            for (int j = 0; j < dimension; j++){
                board[i][j] = splitStr[h++];
            }
        }
    }

    //returns the Dimension
    public int getDimension(){return dimension; }
    //returns black player score
    public int getBlackScore(){
        return blackScore;
    }
    //returns white player score
    public int getWhiteScore(){
        return whiteScore;
    }
    //returns black stone turn
    public boolean getBlackTurn(){
        return blackTurn;
    }
    //return white stone turn
    public boolean getWhiteTurn(){
        return whiteTurn;
    }
    //return black is computer
    public boolean getBlackComputer(){return isBlackComputer;}
    //return white is computer
    public boolean getWhiteComputer(){return isWhiteComputer;}
    //returns board array
    public String[][] getBoard(){
        return board;
    }

    //reads the file and sets the game state accordingly
    public void getGameState(){
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String [] textRead = new String[15];
        try{
            String line;
            int index = 0;
            while ((line = reader.readLine())!= null){
                textRead[index++] = line;
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }

        //setDimension(textRead[1].trim());
        setBlackScore(textRead[1].trim());
        setWhiteScore(textRead[3].trim());
        String turn = textRead[5].trim();
        if (turn.toLowerCase().equals("white")){
            setWhiteTurn(true);
            setBlackTurn(false);
        }
        else {
            setWhiteTurn(false);
            setBlackTurn(true);
        }

        String human = textRead[7].trim();
        if(human.toLowerCase().equals("white")){
            setBlackComputer(true);
            setWhiteComputer(false);
        }
        else{
            setBlackComputer(false);
            setWhiteComputer(true);
        }

        setBoard(textRead[9]);
    }
}
