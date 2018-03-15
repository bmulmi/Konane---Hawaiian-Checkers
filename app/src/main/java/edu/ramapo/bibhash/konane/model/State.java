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
    private int blackScore;
    private int whiteScore;
    private boolean blackTurn;
    private boolean whiteTurn;
    private String [][] board;

    public State(){
        board = new String[6][6];
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
    parameters passed String
    split string according to spaces \\s
    write the string into the board array accordingly
    */
    private void setBoard(String text){
        String str = text;
        String[] splitStr = str.split("\\s+");
        int h = 0;
        for (int i = 0; i < 6; i++){
            for (int j = 0; j < 6; j++){
                board[i][j] = splitStr[h++];
            }
        }
    }

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
    //returns board array
    public String[][] getBoard(){
        return board;
    }

    //reads the file and sets the game state accordingly
    public void getGameState(){
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String [] textRead = new String[13];
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
        setBlackScore(textRead[1]);
        setWhiteScore(textRead[4]);
        setBoard(textRead[7]);
        String turn = textRead[9];
        if (turn.toLowerCase().equals("white")){
            setWhiteTurn(true);
            setBlackTurn(false);
        }
        else {
            setWhiteTurn(false);
            setBlackTurn(true);
        }
    }
}
