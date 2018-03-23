/************************************************************
 *  Name: Bibhash Mulmi                                     *
 * Project:  Project 3 Two Player Konane                    *
 * Class:  CMPS 331 Artificial Intelligence                 *
 * Date:  03/23/2018                                        *
 ************************************************************/

package edu.ramapo.bibhash.konane.view;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import edu.ramapo.bibhash.konane.R;

public class EndActivity extends Activity {

    //receive the scores as extra from mainActivity
    //if (score1>score2) winner = black
    //else if (score2>score1) winner = white
    //else winner = draw
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);
        Intent intent = getIntent();
        int pl1score = intent.getIntExtra("player1Score", 0);
        int pl2score = intent.getIntExtra("player2Score",0);
        String winnerName;

        if (pl1score > pl2score){
            winnerName = "Player 1 \nBlack Stone \nWINS";
        }
        else if(pl2score > pl1score){
            winnerName = "Player 2 \nWhite Stone \nWINS";
        }
        else{
            winnerName = "No One Wins \nIts a \nDRAW";
        }
        TextView text = findViewById(R.id.winnerName);
        text.setText(winnerName);
    }

    //OnClick Function
    //changes intent to MainActivity with state as 1
    public void playAgain(View view){
        Intent intent = new Intent(EndActivity.this, StartActivity.class);
        intent.putExtra("state", 1);
        finishAffinity();
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    //OnClick Function
    //kills the process and exits the app
    public void exit(View view){
        finishAffinity();
        System.exit(0);
    }
}
