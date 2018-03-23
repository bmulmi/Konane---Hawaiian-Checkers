/************************************************************
 *  Name: Bibhash Mulmi                                     *
 * Project:  Project 3 Two Player Konane                    *
 * Class:  CMPS 331 Artificial Intelligence                 *
 * Date:  03/23/2018                                        *
 ************************************************************/

package edu.ramapo.bibhash.konane.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ListView;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import edu.ramapo.bibhash.konane.R;

public class StartActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }

    //changes intent to Main Activity
    //puts integer 1 as extra
    public void newGame(View view){
        AlertDialog.Builder prompt = new AlertDialog.Builder(this);
        prompt.setTitle("Choose Board Size");
        String [] items = {"6X6", "8X8", "10X10"};
        prompt.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ListView select = ((AlertDialog)dialog).getListView();
                String boardSize = (String) select.getAdapter().getItem(which);
                Intent intent = new Intent(StartActivity.this, MainActivity.class);
                intent.putExtra("state", 1);
                intent.putExtra("boardSize", boardSize);
                //finishAffinity();
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        prompt.show();
    }

    //initialises Alert Dialogue Load Game
    /*
        initialise final File[] files by getting ExternalStorageDirectory().listFiles();
        initialise List<String> nameList
        assign all file names to nameList

        assign nameList to an array of Strings
        prompt the file names in the dialogue
        set onClick function
        put file name as extra
        put integer value 2 as game state
        change activity to main activity
     */
    public void resume(View view){
        AlertDialog.Builder prompt = new AlertDialog.Builder(this);
        prompt.setTitle("Load Game");

        String fileDir = Environment.getExternalStorageDirectory().getAbsolutePath()+"/savefiles";
        final File[] files = new File(fileDir).listFiles();
        List<String> nameList = new ArrayList<>();
        for (File file : files ){
            String name = file.getName();
            if (name.endsWith(".txt")){
                nameList.add(name);
            }
        }

        String [] items = new String[nameList.size()];
        items = nameList.toArray(items);

        prompt.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int these) {
                ListView select = ((AlertDialog)dialog).getListView();
                String fileName = (String) select.getAdapter().getItem(these);
                Intent intent = new Intent(StartActivity.this, MainActivity.class);
                intent.putExtra("state", 2);
                intent.putExtra("file", fileName);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        prompt.show();
    }
}
