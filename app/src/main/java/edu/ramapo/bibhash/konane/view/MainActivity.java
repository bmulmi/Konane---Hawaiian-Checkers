/************************************************************
 *  Name: Bibhash Mulmi                                     *
 * Project:  Project 2 Konane                               *
 * Class:  CMPS 331 Artificial Intelligence                 *
 * Date:  03/07/2018                                        *
 ************************************************************/

package edu.ramapo.bibhash.konane.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

import edu.ramapo.bibhash.konane.R;
import edu.ramapo.bibhash.konane.model.Board;


public class MainActivity extends Activity {
    private int click = 0;
    private boolean successiveMove = false;
    private ImageView sourceClick;
    private ImageView destinationClick;
    private Animation animation = new AlphaAnimation(1, 0);
    private ArrayAdapter<CharSequence> adapter;
    private HashMap<Integer, Pair<Integer, Integer>> key = new HashMap<>();

    private Board gameBoard = new Board();

    private int srcRow, srcCol, dstRow, dstCol;

    @Override
    //check gameState and load game accordingly
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Integer gameState = getIntent().getIntExtra("state", 1);
        final String boardSize = getIntent().getStringExtra("boardSize");

        //if its a new game
        if(gameState == 1) {
            final int boardDimension;

            if (boardSize.equals("6X6")) boardDimension = 6;
            else if (boardSize.equals("8X8")) boardDimension = 8;
            else boardDimension = 10;
            System.out.println("board Dim: " + boardDimension);

            gameBoard.setBoardDimension(boardDimension);
            gameBoard.newGame();
            initializeBoard();
            guessTheSlot();
            updateScoreView();
        }

        //if its a loaded game
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

        //set the score board by turn
        if (gameBoard.getBlackTurn()){
            TextView pl = findViewById(R.id.blackplayer);
            pl.setBackgroundColor(Color.WHITE);
        }

        else {
            TextView pl = findViewById(R.id.whiteplayer);
            pl.setBackgroundColor(Color.BLACK);
        }
    }

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
        //if black is computer
        if(black && !white){
            blackP.setText("Black\n(Computer)");
            whiteP.setText("White\n(Human)");
        }
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

    boolean prune;
    //On Click Listener for Pruning CheckBox
    public void pruneCheckboxClicked( View view){
        boolean checked = ((CheckBox) view).isChecked();
        if (checked){
            makeToast("You checked");
            prune = true;
        }

        else {
            makeToast("You unchecked");
            prune = false;
        }
        gameBoard.setPrune(prune);
    }

    //On Click Listener for ply Enter button
    //takes the input from the player for the ply cut-off and passes it to the Board Class
    //also prompts the user the moves of computer by animation.
    public void plyEnter(View view){
        EditText plyCutOff = findViewById(R.id.plyCutoff);
        String value = plyCutOff.getText().toString();
        int v = Integer.parseInt(value);
        makeToast("Ply-Cutoff: " + v);
        gameBoard.setPlyCutoff(v);
    }

    //let the computer make the move
    public void goButton(View view){
        makeToast("you pressed go.");
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
        System.out.println(getDrawableSizes()[0]);
        System.out.println(getDrawableSizes()[1]);
        int width = (getDrawableSizes()[0])/(gameBoard.getBoardDimension()+1);
        int height = (getDrawableSizes()[0])/(gameBoard.getBoardDimension()+1);
        System.out.println(width);
        System.out.println(height);

        LinearLayout rowLayout = findViewById(R.id.rowsLabel);
        LinearLayout a = new LinearLayout(this);
        a.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        a.setLayoutParams(params);

        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(width, height);

        for (int i = 0; i <= gameBoard.getBoardDimension(); i++){
            TextView label = new TextView(this);
            label.setBackgroundColor(Color.LTGRAY);
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
            label.setBackgroundColor(Color.LTGRAY);
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
        grid.setBackgroundColor(Color.GRAY);

        for (int i = 0; i < gameBoard.board.length; i++) {
            for (int j = 0; j < gameBoard.board[i].length; j++) {

                ImageView button = new ImageView(this);
                button.setAdjustViewBounds(true); //to let the imageView adjust the drawable aspect ratios
                button.setLayoutParams(p);

                int id = i*10+j;
                button.setId(id);

                button.setMaxWidth(width);
                button.setMaxHeight(height);

                key.put(id, new Pair<>(i, j));
                //index++;

                if (gameBoard.board[i][j].equals(gameBoard.white)) {
                    button.setImageResource(R.drawable.white);
                } else if (gameBoard.board[i][j].equals(gameBoard.black)) {
                    button.setImageResource(R.drawable.black);
                } else if (gameBoard.board[i][j].equals(gameBoard.empty)) {
                    button.setImageResource(R.drawable.empty);
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

                           //black stone's turn but black is computer
                           if (gameBoard.getBlackTurn()  && gameBoard.getIsBlackComputer() && gameBoard.isBlack(srcRow,srcCol)){
                               makeToast("WRONG STONE");
                               click = 0;
                               sourceClick.setBackgroundColor(0);
                               clearBackground();
                           }

                           //white stone's turn and white is computer
                           if (gameBoard.getWhiteTurn() && gameBoard.getIsWhiteComputer() && gameBoard.isWhite(srcRow, srcCol) ){
                               makeToast("WRONG STONE");
                               click = 0;
                               sourceClick.setBackgroundColor(0);
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

                           if (gameBoard.isEmptyStone(dstRow, dstCol) && gameBoard.isValid(srcRow, srcCol, dstRow, dstCol)) {
                               makeMove(sourceClick, destinationClick);
                               sourceClick.setBackgroundColor(0);
                               destinationClick.setBackgroundColor(0);

                               if (gameBoard.getBlackTurn()) gameBoard.updateBlackScore();
                               else gameBoard.updateWhiteScore();
                               updateScoreView();

                               if (gameBoard.isValidNextMove(dstRow, dstCol)) {
                                   //samePlayer moves with the destination now being source
                                   click = 1;
                                   sourceClick = destinationClick;
                                   srcRow = dstRow;
                                   srcCol = dstCol;
                                   successiveMove = true;

                               } else {
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
                               destinationClick.setBackgroundColor(0);
                           }

                       } else {
                           makeToast("Invalid");
                           sourceClick.setBackgroundColor(0);
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
        /*
        int newWidth = width / gameBoard.getBoardDimension();
        int newHeight = height / gameBoard.getBoardDimension();

        Drawable blackDrawable = getResources().getDrawable(R.drawable.black);
        Bitmap bitmap = ((BitmapDrawable) blackDrawable).getBitmap();
        Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true));
        */
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

    private int nextMovePress = 0;
    private Pair<Integer,Integer> nextBtn;
    private Pair<Integer,Integer> originBtn;


    //on click listener for Next move button
    /*
    if (nextMovePress is greater than 1 and user chose DFS or BFS)
        stop previous animations of origin and destination buttons
    else if (nextMovePress is greater than 1 and user chose Best First Search)
        stop previous animation of origin button
        for ( i<size of the list of destination buttons)
            stop animation of the button at i

    if (DFS chosen)
        if (DFS List isEmpty()) initialise DFS tree;
        gameBoard.getNextValidMove for DFS
        nextBtn = gameBoard.toThisButton;
        originBtn = gameBoard.fromThisButton;
            if (nextBtn or originBtn id is not null)
                animate buttons;
    else if (BFS chosen)
        if (BFS List isEmpty()) initialise BFS tree;
        gameBoard.getNextValidMove for BFS
        nextBtn = gameBoard.toThisButton;
        originBtn = gameBoard.fromThisButton;
            if (nextBtn or originBtn id is not null)
                animate buttons;

    else if (BestFirstSearch chosen)
        if (BestFirstSearchList isEmpty()) initialise BestFirstSearch() and get the list;

        gameBoard.getNextValidMove for Best First Search;
        originBtn = gameBoard.fromThisButton;

        if (originBtn id is not null) animate origin button;
        for (int i = 0; i < size of the list of destination buttons; i++)
            nextBtn = gameBoard.toTheseButtons.get(i);
            if (nextBtn id is not null) animate next button;

    else if (Branch and Bound chosen) Note: BranchAndBound function already called when selected in spinner
         nextBtn = gameBoard.toThisButton;
         originBtn = gameBoard.fromThisButton;
         if (nextBtn id is not null) animate buttons origin and next;
         int score = gameBoard.BnBscore;
         makeToast("Score = +" + score);
    else
        prompt invalid press
     */

    public void hintMove(View view) {
        nextMovePress++;
        //stop last animation;
        //if its second or higher press then stop animation
        if (nextMovePress > 1 && getIdOfBtn(nextBtn)!=0 && getIdOfBtn(originBtn)!=0){
            originBtn = gameBoard.fromThisButton;
            if (getIdOfBtn(originBtn)!=0) {
                ImageView org = findViewById(getIdOfBtn(originBtn));
                stopAnimation(org);
            }

            for (int i = 0; i < gameBoard.toTheseButtons.size(); i++) {
                nextBtn = gameBoard.toTheseButtons.get(i);
                if (getIdOfBtn(nextBtn) != 0) {
                    ImageView nxt = findViewById(getIdOfBtn(nextBtn));
                    stopAnimation(nxt);
                }
            }
        }

        //if Best First Search tree is empty, create tree
        if (gameBoard.BestFirstSearchList.isEmpty()){
            gameBoard.BestFirstSearch();
        }
        //get the valid source and destination from the tree
        gameBoard.getNextValidMove(2);
        originBtn = gameBoard.fromThisButton;
        if (getIdOfBtn(originBtn)!=0) {
            ImageView org = findViewById(getIdOfBtn(originBtn));
            animateButtons(org);
        }
        else makeToast("Best First Search processing...");

        for (int i = 0; i < gameBoard.toTheseButtons.size(); i++){
            nextBtn = gameBoard.toTheseButtons.get(i);
            if (getIdOfBtn(nextBtn)!=0) {
                ImageView nxt = findViewById(getIdOfBtn(nextBtn));
                animateButtons(nxt);
            }
        }
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

    /*private void animateButtons (ImageView source, ImageView destination){
        destination.setBackgroundColor(Color.YELLOW);
        animation.setDuration(1000);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(0);
        //animation.setRepeatMode(Animation.REVERSE);
        source.startAnimation(animation);
        destination.startAnimation(animation);
    }*/

    //stops the button animations
    //parameters passed ImageView object
    //set the background color of object to null
    private void stopAnimation(ImageView source) {
        source.clearAnimation();
        source.setBackgroundColor(0);
    }
    /*private void stopAnimation(ImageView source, ImageView destination){
        destination.setBackgroundColor(0);
        source.setBackgroundColor(0);
    }*/

    /*
    int click increases every time respond is called.
    if (click == 1)
        if(player1's turn)
        set sourceClick;
            if (sourceClicked is white or is empty) prompt "wrong stone", reset click = 0;
        if(player2's turn)
        set sourceClick;
            if(sourceClicked is black or is empty) prompt "wrong stone", reset click = 0;

     if (click == 2)
        set destinationClick;

        dstRow = destination row coordinate;
        dstCol = destination column coordinate;

        if (destination is empty slot and is a valid move)
            makeMove;
            update corresponding player score;
                if (has next valid move)
                    click = 1;
                    sourceClick = destinationClick;
                    srcRow = dstRow;
                    srcCol = dstCol;
                    successiveMove = true;
                else
                    successiveMove = false;
                    click = 0;
                    changePlayer();
                    if (no remaining moves for the current player)
                        prompt no remaining moves
                    if (no remaining moves for both black and white)
                        declare winner;
            else
                if (successiveMove)
                    click = 1;
                else
                    click = 0 ;
            }
    */


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
        } else {
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
        gameBoard.clearQueues();
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
        for (int i = 0; i<t*t; i++) {
            ImageView button = (ImageView) grid.getChildAt(i);
            button.setBackgroundColor(0);
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
        String dimensionString = "Dimension:\n" + gameBoard.getBoardDimension() + "\n";

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

        String writeString = dimensionString + scoreString + turnString + humanString + boardString;
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

        finish();
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
        //---set up the alert dialog---
        /*AlertDialog.Builder prompt = new AlertDialog.Builder(this);
        prompt.setTitle("Enter Name");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        prompt.setView(input);
        prompt.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            private String fn;
            @Override
            public void onClick(DialogInterface dialog, int which) {
                fn = input.getText().toString();

            }
        });
        prompt.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        prompt.show();
        return fileN;*/
    }
}
