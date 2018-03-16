/************************************************************
 *  Name: Bibhash Mulmi                                     *
 * Project:  Project 2 Konane                               *
 * Class:  CMPS 331 Artificial Intelligence                 *
 * Date:  03/07/2018                                        *
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

    //mutator to set Player's score
    //updates the score by 1 point every time
    public void updateScore() {
        score++;
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