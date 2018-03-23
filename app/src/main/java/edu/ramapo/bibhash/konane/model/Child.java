/************************************************************
 *  Name: Bibhash Mulmi                                     *
 * Project:  Project 3 Two Player Konane                    *
 * Class:  CMPS 331 Artificial Intelligence                 *
 * Date:  03/23/2018                                        *
 ************************************************************/

package edu.ramapo.bibhash.konane.model;

/**
 * Created by Bibhash on 3/23/2018.
 */

public class Child {
    public int maximizerScore;
    public int minimizerScore;
    public String[][]board;
    public Child bestChild = null;

    public Child (String[][]brd, int maxScr, int minScr){
        board = brd;
        maximizerScore = maxScr;
        minimizerScore = minScr;
        bestChild = null;
    }
}
