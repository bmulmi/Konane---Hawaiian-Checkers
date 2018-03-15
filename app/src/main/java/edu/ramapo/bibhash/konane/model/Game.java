/************************************************************
 *  Name: Bibhash Mulmi                                     *
 * Project:  Project 2 Konane                               *
 * Class:  CMPS 331 Artificial Intelligence                 *
 * Date:  03/07/2018                                        *
 ************************************************************/

package edu.ramapo.bibhash.konane.model;

public class Game {
    //constructor
    public Game() {
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
}
