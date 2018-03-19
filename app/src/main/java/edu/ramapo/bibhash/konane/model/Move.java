package edu.ramapo.bibhash.konane.model;

/**
 * Created by Bibhash on 3/17/2018.
 */

public class Move {
    public int row;
    public int col;
    public int score;
    //public boolean visited;
    public Move parent;

    public Move(int row, int col){
        this.row = row;
        this.col = col;
        score = 0;
        parent = null;
    }
}
