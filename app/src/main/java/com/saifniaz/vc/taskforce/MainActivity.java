package com.saifniaz.vc.taskforce;

import android.app.Dialog;
import android.app.Presentation;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaDataSource;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button add, restore;
    private EditText task;
    private LinearLayout list;
    private int index = 0, complete = 0;
    private TextView progress;

    private List<String> task_list = new ArrayList<String>();

    private String temp;

    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        add = (Button)findViewById(R.id.add);
        restore = (Button) findViewById(R.id.restore);
        list = (LinearLayout) findViewById(R.id.list_task);
        progress = (TextView) findViewById(R.id.progress);

        dialog = new Dialog(this);
        progress.setText(""+complete+"/"+index+"");

        OpenFile();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTask(view);
            }
        });

        restore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list.removeAllViews();
                index = 0;
                complete = 0;
                for(int r = task_list.size() -1; r>=0 ;r--){
                    task_list.remove(r);
                }
                progress.setText(""+complete+"/"+index+"");
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        SaveFile();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SaveFile();
    }

    public  void SaveFile(){

        try {
            FileOutputStream out = openFileOutput("data.txt", MODE_PRIVATE);
            OutputStreamWriter writer = new OutputStreamWriter(out);

            //writer.write(temp);

            if(!task_list.isEmpty()){
                for(int a = 0; a < task_list.size(); a++){
                    if(a == 0){
                        writer.write(task_list.get(a) + "\n");
                    }else {
                        writer.append(task_list.get(a) + "\n");
                    }
                }
            }

            writer.flush();
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void OpenFile(){
        try {

            FileInputStream inputStream = openFileInput("data.txt");
            InputStreamReader streamReader = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(streamReader);
            for(String line; (line = reader.readLine()) != null;){
                setTask(line);
            }

            /*InputStream inputStream = getResources().openRawResource(
                    getResources().getIdentifier("data", "raw",
                            getPackageName())
            );
            InputStreamReader streamReader = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(streamReader);
            for(String line; (line = reader.readLine()) != null;){
                setTask(line);
            }*/
            inputStream.close();
            streamReader.close();
            reader.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void CheckTask(View v){
        if(index < complete){
            return;
        }else{
            if(((CheckBox)v).isChecked()){
                complete++;
            }else if(!((CheckBox)v).isChecked()){
                complete--;
            }
            progress.setText(""+complete+"/"+index+"");
        }
    }

    public void setTask(String chores){
        //Add chores to task list
        task_list.add(chores);

        //Remove Example Task
        if(index == 0){
            list.removeAllViewsInLayout();
        }

        //Append Checkboxes
        int pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics());

        CheckBox box = new CheckBox(this);
        box.setText(chores);
        box.setPadding(pixels, pixels, pixels, pixels);
        if(index%3== 0){
            box.setBackgroundColor(getResources().getColor(R.color.orangeLight));
        }else if(index%3== 1){
            box.setBackgroundColor(getResources().getColor(R.color.greenLight));
        }else{
            box.setBackgroundColor(getResources().getColor(R.color.blueLight));
        }
        box.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                CheckTask(view);
            }
        });

        list.addView(box);

        index++;
        progress.setText(""+complete+"/"+index+"");


    }

    public void addTask(View v){
        dialog.setContentView(R.layout.activity_add);
        task = (EditText)dialog.findViewById(R.id.task);
        Button ok = (Button) dialog.findViewById(R.id.ok);
        Button cancel = (Button) dialog.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                temp = task.getText().toString();
                dialog.dismiss();
                setTask(temp);
            }
        });
        dialog.show();
    }

}
