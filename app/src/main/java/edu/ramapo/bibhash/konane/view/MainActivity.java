/************************************************************
 *  Name: Bibhash Mulmi                                     *
 * Project:  Project 3 Two Player Konane                    *
 * Class:  CMPS 331 Artificial Intelligence                 *
 * Date:  03/23/2018                                        *
 ************************************************************/

package edu.ramapo.bibhash.konane.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import edu.ramapo.bibhash.konane.R;
import edu.ramapo.bibhash.konane.model.Board;
import edu.ramapo.bibhash.konane.model.Child;
import edu.ramapo.bibhash.konane.model.Move;


public class MainActivity extends Activity {
    private int click = 0;
    private boolean successiveMove = false;

    private ImageView sourceClick;
    private ImageView destinationClick;
    private Animation animation = new AlphaAnimation(1, 0);

    private HashMap<Integer, Pair<Integer, Integer>> key = new HashMap<>();
    private Board gameBoard = new Board();

    private int srcRow, srcCol, dstRow, dstCol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Integer gameState = getIntent().getIntExtra("state", 1);
        final String boardSize = getIntent().getStringExtra("boardSize");

        //----------if its a new game----------
        if(gameState == 1) {
            final int boardDimension;
            if (boardSize.equals("6X6")) boardDimension = 6;
            else if (boardSize.equals("8X8")) boardDimension = 8;
            else boardDimension = 10;
            //System.out.println("board Dim: " + boardDimension);

            gameBoard.setBoardDimension(boardDimension);
            gameBoard.newGame();
            initializeBoard();
            guessTheSlot();
            updateScoreView();
        }

        //----------if its a loaded game----------
        else if (gameState == 2){
            String fileName = (String) getIntent().getSerializableExtra("file");
            try{
                if(isExternalStorageReadable()){
                    String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/savefiles/"+fileName;
                    InputStream is = new FileInputStream(path);
                    gameBoard.loadGame(is);
                    initializeBoard();
                    updateScoreView();
                    setScoreText(gameBoard.getIsBlackComputer(), gameBoard.getIsWhiteComputer());
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

        //----------set the score board by turn----------
        if (gameBoard.getBlackTurn()){
            TextView pl = findViewById(R.id.blackplayer);
            pl.setBackgroundColor(Color.WHITE);
        }

        else {
            TextView pl = findViewById(R.id.whiteplayer);
            pl.setBackgroundColor(Color.BLACK);
        }
    }

    //function to prompt user to guess the coordinates of the slots that were removed
    public void guessTheSlot(){
        Pair<Integer, Integer>[] temp = gameBoard.getRemovedBtns();
        int rowb,colb,roww,colw;
        rowb = temp[0].first+1;
        colb = temp[0].second+1;
        roww = temp[1].first+1;
        colw = temp[1].second+1;
        AlertDialog.Builder guess = new AlertDialog.Builder(this);
        guess.setTitle("Guess the Removed Black Stone");
        final String [] items = { rowb+"X"+colb, roww+"X"+colw};

        guess.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ListView select = ((AlertDialog)dialog).getListView();
                String selected = (String) select.getAdapter().getItem(which);
                if (selected.equals(items[0])) {
                    makeToast("You guessed correctly.");
                    gameBoard.setWhiteAsComputer();
                    setScoreText(gameBoard.getIsBlackComputer(), gameBoard.getIsWhiteComputer());
                }
                else {
                    makeToast("You guessed wrong.");
                    gameBoard.setBlackAsComputer();
                    setScoreText(gameBoard.getIsBlackComputer(), gameBoard.getIsWhiteComputer());
                }
            }
        });
        guess.show();
    }

    private void setScoreText(boolean black, boolean white){
        TextView blackP = findViewById(R.id.blackplayer);
        TextView whiteP = findViewById(R.id.whiteplayer);
        //----------black is computer----------
        if(black && !white){
            blackP.setText("Black\n(Computer)");
            whiteP.setText("White\n(Human)");
        }
        //----------white is computer----------
        else{
            blackP.setText("Black\n(Human)");
            whiteP.setText("White\n(Computer)");
        }

        TextView blackS = findViewById(R.id.player1score);
        TextView whiteS = findViewById(R.id.player2score);

        int width = getDrawableSizes()[0]/4;
        blackP.setWidth(width);
        whiteP.setWidth(width);
        blackS.setWidth(width);
        whiteS.setWidth(width);
    }

    private boolean prune;
    //On Click Listener for Pruning CheckBox
    public void pruneCheckboxClicked( View view){
        boolean checked = ((CheckBox) view).isChecked();
        if (checked){
            prune = true;
        }

        else {
            prune = false;
        }
    }

    private boolean calledMinimax = false;
    private int plyCutOff;
    //On Click Listener for ply Enter button
    //takes the input from the player for the ply cut-off and passes it to the Board Class
    //also prompts the user the moves of computer by animation.
    public void plyEnter(View view){
        clearBackground();
        //----------get the value entered as string----------
        EditText cutOff = findViewById(R.id.plyCutoff);
        String value = cutOff.getText().toString();
        //----------no value entered----------
        if (value.equals("")){
            //---set plyCutOff as the highest value---
            plyCutOff = Integer.MAX_VALUE;
            makeToast("Ply Cut-off is max.");
        }
        //----------value entered----------
        else {
            plyCutOff = Integer.parseInt(value);
        }

        //----------if its Computer's turn then animate the moves----------
        if ((gameBoard.getBlackTurn() && gameBoard.getIsBlackComputer()) || (gameBoard.getWhiteTurn() && gameBoard.getIsWhiteComputer())) {
            makeToast("Ply Cut-off entered: " + plyCutOff);
            calledMinimax = true;
            //---call the algorithm---
            gameBoard.getMinimaxMoves(plyCutOff, prune, true);
            //---bestMove is updated---
            //---get the source---
            Pair<Move, Move> temp = gameBoard.bestMove;

            //---handle the null exception---
            while (temp==null){
                gameBoard.getMinimaxMoves(plyCutOff, prune, true);
                temp = gameBoard.bestMove;
            }

            int sourceRow = temp.first.row;
            int sourceCol = temp.first.col;

            int tempId = (sourceRow+1) * 10 + (sourceCol+1);

            ImageView btn = findViewById(tempId);

            //---null object reference handler---
            if (btn==null){
                System.out.println("exception handled!!! on button with ID: " + tempId);
                return;
            }

            animateButtons(btn);
            int score = 0;

            //---get the destinations---
            Stack<Pair<Integer, Integer>> jumps = gameBoard.getPath(temp.first, temp.second);

            System.out.println("Source: "+sourceRow+"X"+sourceCol);
            System.out.println("Destination: \n");
            //---animate every one of them---
            while (!jumps.isEmpty()) {
                score++;
                Pair<Integer, Integer> j = jumps.pop();
                System.out.println(j.first+"X"+j.second);
                tempId = (j.first+1) * 10 + (j.second+1);
                btn = findViewById(tempId);
                animateButtons(btn);
            }

            //---get maximizer and minimizer's scores---
            int maxSc = 0;
            int minSc = 0;
            Child tempChild = gameBoard.cld;
            System.out.println("Moves made: ");
            while ( tempChild != null){
                maxSc += tempChild.maximizerScore;
                minSc += tempChild.minimizerScore;
                System.out.println("Points Earned (max): "+maxSc);
                System.out.println("Points Earned (min): "+minSc);
                tempChild = tempChild.bestChild;
                //System.out.println("Source: "+tempChild.bestMove.first.row+"X"+tempChild.bestMove.first.col+"\n");
                //System.out.println("Destination: "+tempChild.bestMove.second.row+"X"+tempChild.bestMove.second.col);
            }

            String str = "Points Gained Computer: " + maxSc + "\nPoints Gained Human: " + minSc +"\nImmediate jumps: " + score;
            showAlgorithmTime(str);
        }
        //----------human's turn----------
        else makeToast("ply Cut-off: " + plyCutOff + " entered for human.");
    }

    //go ahead button for computer to make the move(s)
    public void goButton(View view){

        if (!calledMinimax) {
            gameBoard.getMinimaxMoves(plyCutOff, prune, true);
        }

        //----------computer's turn and same player is computer----------
        if ((gameBoard.getBlackTurn() && gameBoard.getIsBlackComputer()) || (gameBoard.getWhiteTurn() && gameBoard.getIsWhiteComputer())) {

            calledMinimax = false;

            Pair<Move, Move> temp = gameBoard.bestMove;

            int sourceRow = temp.first.row;
            int sourceCol = temp.first.col;

            int tempId = (sourceRow+1) * 10 + (sourceCol+1);

            ImageView sourceBtn = findViewById(tempId);

            //---get the destinations---
            Stack<Pair<Integer, Integer>> jumps = gameBoard.getPath(temp.first, temp.second);
            int score = 0;

            System.out.println("Source: "+sourceRow+"X"+sourceCol);
            System.out.println("Destination: \n");
            //---make every move---
            while (!jumps.isEmpty()) {
                score++;
                Pair<Integer, Integer> j = jumps.pop();
                System.out.println(j.first+"X"+j.second);

                tempId = (j.first+1) * 10 + (j.second+1);
                ImageView destinationBtn = findViewById(tempId);

                makeMove(sourceBtn, destinationBtn);
                sourceBtn = destinationBtn;
            }

            //---update the score---
            if (gameBoard.getBlackTurn()) gameBoard.updateBlackScore(score);
            else gameBoard.updateWhiteScore(score);
            updateScoreView();

            //---after it makes all the moves---change players---
            changePlayer();

            //---check if the next player has remaining moves---
            //---if not change player again---
            if((gameBoard.getWhiteTurn()) && !gameBoard.checkRemainingMovesForWhite()){
                changePlayer();
                makeToast("No remaining moves White");
            }
            if((gameBoard.getBlackTurn() && !gameBoard.checkRemainingMovesForBlack())){
                changePlayer();
                makeToast("No remaining moves Black");
            }

            //---no remaining moves for both stones---
            if (!gameBoard.checkRemainingMovesForBlack() && !gameBoard.checkRemainingMovesForWhite()){
                declareWinner();
            }
        }
        //----------Human's turn----------
        else makeToast("Its your turn.");
    }

    //animate the best move for human
    public void hintMove(View view) {
        clearBackground();

        //----------validate human's turn----------
        if ((gameBoard.getBlackTurn() && gameBoard.getIsWhiteComputer()) || (gameBoard.getWhiteTurn() && gameBoard.getIsBlackComputer())) {
            gameBoard.getMinimaxMoves(plyCutOff, prune, false);
            //---bestMove is updated---
            //---get the source---

            Pair<Move, Move> temp = gameBoard.bestMove;

            //handle the null exception
            while (temp==null){
                gameBoard.getMinimaxMoves(plyCutOff, prune, false);
                temp = gameBoard.bestMove;
            }

            int sourceRow = temp.first.row;
            int sourceCol = temp.first.col;

            int tempId = (sourceRow+1)*10+(sourceCol+1);

            ImageView btn = findViewById(tempId);

            //---null object reference handler---
            if (btn==null){
                System.out.println("exception handled!!! on button with ID: " + tempId);
                return;
            }

            animateButtons(btn);
            System.out.println("Source: "+sourceRow+"X"+sourceCol);
            System.out.println("Destination: \n");
            int score = 0;
            //---get the destinations---
            Stack<Pair<Integer, Integer>> jumps = gameBoard.getPath(temp.first, temp.second);

            //---animate every one of them---
            while (!jumps.isEmpty()) {
                score++;
                Pair<Integer, Integer> j = jumps.pop();
                System.out.println(j.first+"X"+j.second);
                tempId = (j.first+1) * 10 + (j.second+1);
                btn = findViewById(tempId);
                animateButtons(btn);
            }

            //---get maximizer's and minimizer's scores---
            int maxSc = 0;
            int minSc = 0;
            Child tempChild = gameBoard.cld;
            System.out.println("Moves made: ");
            while ( tempChild != null){
                maxSc += tempChild.maximizerScore;
                System.out.println("Points earned (max): "+maxSc);
                minSc += tempChild.minimizerScore;
                System.out.println("Points earned (min): "+minSc);
                tempChild = tempChild.bestChild;
                //System.out.println("Source: "+tempChild.bestMove.first.row+"X"+tempChild.bestMove.first.col+"\n");
                //System.out.println("Destination: "+tempChild.bestMove.second.row+"X"+tempChild.bestMove.second.col+"\n");
            }

            //makeToast("Points Gained Human: "+ gameBoard.MaximizerScore);
            //makeToast("Points Gained Computer: " +gameBoard.MinimizerScore);
            //makeToast("Immediate jumps: " +score);
            //makeToast("Maximizer: "+gameBoard.bestState.maximizerScore + "\nMinimizer: " + gameBoard.bestState.minimizerScore);
            String str = "Points Gained Human: "+ maxSc + "\nPoints Gained Computer: " + minSc + "\nImmediate jumps: " +score;
            showAlgorithmTime(str);
        }
        //----------Computer's turn----------
        else makeToast("Its Computer's turn!");
    }

    //shows the algorithm time, and scores generated
    private void showAlgorithmTime(String str){
        double time = gameBoard.getAlgorithmTime();
        TextView timer = findViewById(R.id.log);
        timer.setMovementMethod(new ScrollingMovementMethod());
        String text = "Time taken: ";
        text += time;
        text += " milliseconds.\n" + str;
        timer.setText(text);
    }

    //Initialises the board, assigns values to the hashmap to communicate with the logic and view
    //Grid Layout assigns its child rows or columns with integer value that increases as we move to next row or col
    //if (Row,Col)==(0,0), its index = 0, (Row, Col)==(0,1) index = 1, (0,2) = 2... (1,3) = 9 and so on...
    //creates a grid object, initialises index = 0;
    //nested for loop
    //traverses the board array from Board Class
    //finds the View button from grid.getChildAt(index)
    //if (board[i][j] == board.white) setImage as white, else if (==board.black) setImage as black, else setImage as empty
    //assign the buttonId to HashMap (id, Pair<>(i,j));
    private void initializeBoard() {
        //---set row count labels---
        /*System.out.println(getDrawableSizes()[0]);
        System.out.println(getDrawableSizes()[1]);*/
        int width = (getDrawableSizes()[0])/(gameBoard.getBoardDimension()+1);
        int height = (getDrawableSizes()[0])/(gameBoard.getBoardDimension()+1);
        /*System.out.println(width);
        System.out.println(height);*/

        LinearLayout rowLayout = findViewById(R.id.rowsLabel);
        LinearLayout a = new LinearLayout(this);
        a.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        a.setLayoutParams(params);

        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(width, height);

        for (int i = 0; i <= gameBoard.getBoardDimension(); i++){
            TextView label = new TextView(this);
            label.setBackgroundColor(Color.rgb(149,155,165));
            label.setLayoutParams(p);
            label.setGravity(Gravity.CENTER);
            label.setTextColor(Color.BLACK);
            label.setTextSize(20);
            String txt = Integer.toString(i);
            label.setText(txt);
            a.addView(label);
        }
        rowLayout.addView(a);

        //---set column count labels---
        LinearLayout columnLayout = findViewById(R.id.columnsLabel);
        LinearLayout b = new LinearLayout(this);
        b.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        b.setLayoutParams(params2);

        for (int i = 1; i <= gameBoard.getBoardDimension(); i++){
            TextView label = new TextView(this);
            label.setBackgroundColor(Color.rgb(149,155,165));
            label.setLayoutParams(p);
            label.setGravity(Gravity.CENTER);
            label.setTextColor(Color.BLACK);
            label.setTextSize(20);
            String txt = Integer.toString(i);
            label.setText(txt);
            b.addView(label);
        }
        columnLayout.addView(b);


        //---set Grid Layout---
        GridLayout grid = findViewById(R.id.konaneLayout);
        grid.setRowCount(gameBoard.getBoardDimension());
        grid.setColumnCount(gameBoard.getBoardDimension());
        //grid.setBackgroundColor(Color.);

        for (int i = 0; i < gameBoard.board.length; i++) {
            for (int j = 0; j < gameBoard.board[i].length; j++) {

                ImageView button = new ImageView(this);
                button.setAdjustViewBounds(true); //to let the imageView adjust the drawable aspect ratios
                button.setLayoutParams(p);
                //-------------made changes here-------------------
                int i_ = i+1;
                int j_ = j+1;
                int id = i_*10+j_;
                button.setId(id);

                button.setMaxWidth(width);
                button.setMaxHeight(height);

                key.put(id, new Pair<>(i, j));
                //index++;

                if (gameBoard.board[i][j].equals(gameBoard.white)) {
                    button.setImageResource(R.drawable.white);
                    button.setBackgroundColor(Color.GRAY);
                } else if (gameBoard.board[i][j].equals(gameBoard.black)) {
                    button.setImageResource(R.drawable.black);
                    button.setBackgroundColor(Color.DKGRAY);
                } else if (gameBoard.board[i][j].equals(gameBoard.empty)) {
                    button.setImageResource(R.drawable.empty);
                    if (gameBoard.isBlack(i,j)) button.setBackgroundColor(Color.GRAY);
                    else button.setBackgroundColor(Color.LTGRAY);

                }
                //---add the button to the grid View---
                GridLayout.Spec gridRow = GridLayout.spec(i, 1);
                GridLayout.Spec gridCol = GridLayout.spec(j,1);
                GridLayout.LayoutParams gridLayoutParam = new GridLayout.LayoutParams(gridRow, gridCol);
                grid.addView(button, gridLayoutParam);

                button.setOnClickListener(new View.OnClickListener(){
                   @Override
                    public void onClick(View view){
                       click++;
                       //System.out.println("click: "+click);
                       if (click == 1) {
                           sourceClick = (ImageView) view;
                           sourceClick.setBackgroundColor(Color.YELLOW);

                           int srcId = sourceClick.getId();
                           //makeToast("id: " + srcId);

                           Pair<Integer, Integer> sourceRowCol = key.get(srcId);
                           srcRow = sourceRowCol.first;
                           srcCol = sourceRowCol.second;
                           //System.out.println(srcRow+""+srcCol);

                           //black is computer, black's turn, but user tries to press the stone
                           if (gameBoard.getIsBlackComputer() && gameBoard.getBlackTurn()){
                               makeToast("Its Computer's turn");
                               click = 0;
                               //sourceClick.setBackgroundColor(Color.GRAY);
                               clearBackground();
                           }

                           //white is computer, white's turn, but user tries to press the stone
                           if (gameBoard.getIsWhiteComputer() && gameBoard.getWhiteTurn()){
                               makeToast("Its Computer's turn");
                               click = 0;
                               //sourceClick.setBackgroundColor(Color.LTGRAY);
                               clearBackground();
                           }

                           //black is human, black's turn, but user presses white-----------------------------------------------||white is human, white's turn, but user presses black
                           if((gameBoard.getIsWhiteComputer() && gameBoard.getBlackTurn() && gameBoard.isWhite(srcRow, srcCol)) || (gameBoard.getIsBlackComputer() && gameBoard.getWhiteTurn() && gameBoard.isBlack(srcRow,srcCol))){
                               makeToast("Wrong Stone");
                               click = 0;
                               //sourceClick.setBackgroundColor(Color.LTGRAY);
                               clearBackground();
                           }
                       }

                       else if (click == 2) {
                           destinationClick = (ImageView) view;
                           destinationClick.setBackgroundColor(Color.YELLOW);

                           int dstId = destinationClick.getId();
                           Pair<Integer, Integer> destinationRowCol = key.get(dstId);
                           dstRow = destinationRowCol.first;
                           dstCol = destinationRowCol.second;
                           //---make move only if its a valid move---
                           if (gameBoard.isEmptyStone(dstRow, dstCol) && gameBoard.isValid(srcRow, srcCol, dstRow, dstCol)) {
                               makeMove(sourceClick, destinationClick);
                               //sourceClick.setBackgroundColor(0);
                               //destinationClick.setBackgroundColor(0);
                               //---update scores by one---
                               if (gameBoard.getBlackTurn()) gameBoard.updateBlackScore(1);
                               else gameBoard.updateWhiteScore(1);
                               updateScoreView();
                               //---check for valid next moves---
                               if (gameBoard.isValidNextMove(dstRow, dstCol)) {
                                   //samePlayer moves with the destination now being source
                                   click = 1;
                                   sourceClick = destinationClick;
                                   srcRow = dstRow;
                                   srcCol = dstCol;
                                   successiveMove = true;
                               }
                               //---no valid next move---
                               else {
                                   //change player
                                   successiveMove = false;
                                   click = 0;
                                   changePlayer();
                                   if (gameBoard.getBlackTurn() && !gameBoard.checkRemainingMovesForBlack()) {
                                       makeToast("No moves for BLACK");
                                       changePlayer();
                                   }
                                   if (gameBoard.getWhiteTurn() && !gameBoard.checkRemainingMovesForWhite()) {
                                       makeToast("No moves for WHITE");
                                       changePlayer();
                                   }
                                   if (!gameBoard.checkRemainingMovesForWhite() && !gameBoard.checkRemainingMovesForBlack()){
                                       declareWinner();
                                   }
                               }
                           }
                           else {
                               makeToast("Invalid Move");
                               //check if it was successive move
                               if (successiveMove) {
                                   click = 1;
                               }
                               else{
                                   click = 0;
                               }
                               //destinationClick.setBackgroundColor(0);
                               clearBackground();
                           }

                       } else {
                           makeToast("Invalid");
                           //sourceClick.setBackgroundColor(0);
                           clearBackground();
                           click = 0;
                       }
                   }
                });
            }
        }
    }

    //sets the size of the images according to the board dimension chosen
    private int[] getDrawableSizes(){
        int temp[] = new int [2];
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        temp[0] = dm.widthPixels;
        temp[1] = dm.heightPixels;
        return temp;
    }

    //parameters used: Pair<integer, integer>
    //returns the integer Id of the Pair button associated with the grid layout
    private int getIdOfBtn(Pair<Integer, Integer> pair){
        for (Map.Entry<Integer, Pair<Integer, Integer>> entry : key.entrySet()) {
            if (entry.getValue().equals(pair)) {
                return entry.getKey();
            }
        }
        return 0;
    }

    //animates the buttons
    //parameters passed ImageView object
    /*
    set background of the ImageView to Yellow
    animation set duration(1000);
    animation set interpolator(new LinearInterpolator());
    animation set repeat count(0);
    start animation;
     */
    private void animateButtons (ImageView source) {
        source.setBackgroundColor(Color.YELLOW);
        animation.setDuration(1000);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(0);
        //animation.setRepeatMode(Animation.REVERSE);
        source.startAnimation(animation);
    }

    //stops the button animations
    //parameters passed ImageView object
    //set the background color of object to null
    private void stopAnimation(ImageView source) {
        source.clearAnimation();
        source.setBackgroundColor(0);
    }

    //changes the player
    //resets the click
    //checks if the new turned player has valid moves, if no remaining moves then re-changes the player turn
    public void skipMove(View view) {
        click = 0;
        changePlayer();
        if (gameBoard.getBlackTurn() && !gameBoard.checkRemainingMovesForBlack()) {
            makeToast("No moves for BLACK");
            changePlayer();
        }
        if (gameBoard.getWhiteTurn() && !gameBoard.checkRemainingMovesForWhite()) {
            makeToast("No moves for WHITE");
            changePlayer();
        }
    }

    //checks the score of both players
    //declares the one with greater score as a winner
    //else prompts draw
    public void declareWinner() {
        Intent intent = new Intent(MainActivity.this,EndActivity.class);
        intent.putExtra("player1Score", gameBoard.getBlackScore());
        intent.putExtra("player2Score", gameBoard.getWhiteScore());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finishAffinity();
        startActivity(intent);
    }

    //changes the turn of Player object
    public void changePlayer() {
        TextView pl1 = findViewById(R.id.blackplayer);
        TextView pl2 = findViewById(R.id.whiteplayer);
        if (gameBoard.getBlackTurn()) {
            gameBoard.setBlackTurn(false);
            pl1.setBackgroundColor(0);

            gameBoard.setWhiteTurn(true);
            pl2.setBackgroundColor(Color.BLACK);
        }

        else {
            gameBoard.setBlackTurn(true);
            pl1.setBackgroundColor(Color.WHITE);

            gameBoard.setWhiteTurn(false);
            pl2.setBackgroundColor(0);
        }
    }

    //update the ScoreView by accessing each player's scores
    public void updateScoreView() {
        String p1score = Integer.toString(gameBoard.getBlackScore());
        String p2score = Integer.toString(gameBoard.getWhiteScore());
        TextView p1 = findViewById(R.id.player1score);
        TextView p2 = findViewById(R.id.player2score);
        p1.setText(p1score);
        p2.setText(p2score);
    }

    //makes the move from source to destination
    //change the captured stone image to 0
    //set the destination stone to source's image
    //set the source stone image to 0
    public void makeMove(ImageView src, ImageView dst) {
        int dstId = dst.getId();
        Pair<Integer, Integer> destinationRowCol = key.get(dstId);
        int dstRow = destinationRowCol.first;
        int dstCol = destinationRowCol.second;

        int srcId = src.getId();
        Pair<Integer, Integer> sourceRowCol = key.get(srcId);
        int srcRow = sourceRowCol.first;
        int srcCol = sourceRowCol.second;

        //----------destination button change--------------
        if (gameBoard.isBlack(srcRow, srcCol)) dst.setImageResource(R.drawable.black);
        else if (gameBoard.isWhite(srcRow, srcCol)) dst.setImageResource(R.drawable.white);

        //----------source button clearing-------------------
        src.setImageResource(R.drawable.empty);

        //---------deleting the captured button-------------------
        //returns the pair (row, col) to be deleted and also updates the gameBoard object
        Pair btnRowCol = gameBoard.updateButton(srcRow, srcCol, dstRow, dstCol);

        ImageView myBtn = findViewById(getIdOfBtn(btnRowCol));
        myBtn.setImageResource(R.drawable.empty);

        //clear the queues after the move is made
        //gameBoard.clearQueues();
        //clear background of buttons selected
        clearBackground();
        /*for (int i = 0; i<gameBoard.board.length; i++){
            System.out.printf("%s %s %s %s %s %s\n", gameBoard.board[i][0], gameBoard.board[i][1], gameBoard.board[i][2], gameBoard.board[i][3], gameBoard.board[i][4], gameBoard.board[i][5]);
        }*/
    }

    //clears background of every button of the board
    private void clearBackground(){
        GridLayout grid = findViewById(R.id.konaneLayout);
        int t = gameBoard.getBoardDimension();
        int h = 0;
        for (int i = 0; i<t; i++) {
            for (int j = 0; j < t; j++) {
                ImageView button = (ImageView) grid.getChildAt(h);
                h++;
                if (gameBoard.isBlack(i,j)) button.setBackgroundColor(Color.DKGRAY);
                else button.setBackgroundColor(Color.GRAY);
            }
        }
    }

    //makeText toast
    public void makeToast(String tst) {
        Toast.makeText(getApplicationContext(), tst, Toast.LENGTH_SHORT).show();
    }

    //saves the current game state into a file
    /*
    concatenate the current score, board, and player turn into a single string
    try
        if(External storage is writable)
           File file = getFile(); get file name and directory
           OutputStream os = new FileOutputStream(file);
           os.write(writeString.getBytes());
           os.close();
    catch
     */
    public void saveGame(View view){
        //String dimensionString = "Dimension:\n" + gameBoard.getBoardDimension() + "\n";

        String scoreString = "Black:\n" + gameBoard.getBlackScore()+"\n"+"White:\n"+gameBoard.getWhiteScore()+"\n";

        String turnString = "Next Player:\n";
        boolean turn = gameBoard.getBlackTurn();
        if(turn){
            turnString+="Black\n";
        }
        else turnString+="White\n";

        String humanString = "Human:\n";
        if (gameBoard.getIsBlackComputer()){
            humanString+="White\n";
        }
        else{
            humanString+="Black\n";
        }

        String boardString = "Board:\n";
        //write the layout
        for(int i = 0; i < gameBoard.getBoardDimension(); i++){
            for (int j = 0; j < gameBoard.getBoardDimension(); j++){
                if (gameBoard.board[i][j].equals("E")) {
                    boardString+="E";
                    boardString+=" ";
                }
                else if (gameBoard.board[i][j].equals("B")){
                    boardString+="B";
                    boardString+=" ";
                }
                else{
                    boardString+="W";
                    boardString+=" ";
                }
            }
        }
        boardString+="\n";

        //String writeString = dimensionString + scoreString + turnString + humanString + boardString;
        String writeString = scoreString + turnString + humanString + boardString;
        System.out.println(writeString);
        try{
            if(isExternalStorageWritable()){
                File file = getFile();
                OutputStream os = new FileOutputStream(file);
                os.write(writeString.getBytes());
                os.close();
                makeToast("Game saved as: " + file.getName()) ;
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }

        //finish();
        System.exit(0);
    }

    //checks if the ExternalStorage is Writable or not
    //returns boolean values
    public boolean isExternalStorageWritable(){
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    //checks if the ExternalStorage is Readable or not
    //returns boolean values
    public boolean isExternalStorageReadable(){
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state));
    }

    //returns the File object
    //no parameters passed
    //create a file name ending with a unique number
    //get file directory
    //write file into that directory
    //return the created file
    //private File fileN;
    private File getFile(){
        int i = 1;
        while (true){
            String fileName = "save"+i+".txt";
            String fileDir = Environment.getExternalStorageDirectory().getAbsolutePath()+"/savefiles/"+fileName;
            File file = new File(fileDir);
            if(!file.exists()){
                return file;
            }
            i++;
        }
    }
}
