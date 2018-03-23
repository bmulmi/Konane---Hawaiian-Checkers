/************************************************************
 *  Name: Bibhash Mulmi                                     *
 * Project:  Project 3 Two Player Konane                    *
 * Class:  CMPS 331 Artificial Intelligence                 *
 * Date:  03/23/2018                                        *
 ************************************************************/
package edu.ramapo.bibhash.konane.model;

public class Player {
    private boolean turn;
    private int score;
    private boolean computerPlays;

    public Player() {
        score = 0;
        turn = false;
    }

    //mutator to set Player's turn
    public void setTurn(boolean value) {
        turn = value;
    }

    //accessor of Player's turn
    public boolean isTurn() {
        return turn;
    }

    //updates the score by value passed
    public void updateScore(int scr) {
        score += scr ;
    }

    //accessor of Player's score
    public int getScore() {
        return score;
    }

    //set the score of Player
    public void setScore(int s) {
        score = s;
    }

    public void setComputerPlays(boolean val) {
        computerPlays = val;
    }

    public boolean isComputer() {
        return computerPlays;
    }
}