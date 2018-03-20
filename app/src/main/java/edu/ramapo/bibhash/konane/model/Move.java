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

    public Move clone(Move s){
        Move temp = new Move(-1,-1);
        temp.row = s.row;
        temp.col = s.col;
        temp.score = s.score;
        temp.parent = s.parent;
        return temp;
    }

    public boolean areEqual(Move s, Move t){
        if (s.row==t.row && s.col==t.col && s.score==t.score && s.parent==t.parent){
            return true;
        }
        else return false;
    }
}
