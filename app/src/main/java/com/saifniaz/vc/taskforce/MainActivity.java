package com.saifniaz.vc.taskforce;

import android.app.Dialog;
import android.app.Presentation;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
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

    /*public void checkChores(View v){
        TextView temp = ((TextView)v);
        temp.setPaintFlags(temp.getPaintFlags() |
                Paint.STRIKE_THRU_TEXT_FLAG);
        temp.setPaintFlags(temp.getPaintFlags() &
                (~ Paint.STRIKE_THRU_TEXT_FLAG));

    }*/



    public void OpenFile(){
        try {

            FileInputStream inputStream = openFileInput("data.txt");
            InputStreamReader streamReader = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(streamReader);
            for(String line; (line = reader.readLine()) != null;){
                setTask(line);
            }

            inputStream.close();
            streamReader.close();
            reader.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void CheckTask(View v){
        LinearLayout layout = ((LinearLayout)v);
        int i = 0, c = layout.getChildCount();

        while(i < c){
            View check = layout.getChildAt(i);
            CheckBox box = (CheckBox)check;
            i++;
            View text = layout.getChildAt(i);
            TextView tv = (TextView)text;
            if(index < complete){
                    return;
            }else{
                if(!box.isChecked()){
                    box.setChecked(true);
                    tv.setPaintFlags(tv.getPaintFlags() |
                            Paint.STRIKE_THRU_TEXT_FLAG);
                    complete++;
                }else if(box.isChecked()){
                    box.setChecked(false);
                    tv.setPaintFlags(tv.getPaintFlags() &
                            (~ Paint.STRIKE_THRU_TEXT_FLAG));
                    complete--;
                }
            }
            i++;
        }
        progress.setText(""+complete+"/"+index+"");
    }

    public void setTask(String chores){
        //Add chores to task list
        task_list.add(chores);

        //Remove Example Task
        if(index == 0){
            list.removeAllViewsInLayout();
        }


        //Add Linear Layout

        LinearLayout tas = new LinearLayout(this);
        tas.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT);



        //Append Checkboxes
        int pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());

        CheckBox box = new CheckBox(this);
        box.setLayoutParams(params1);
        box.setEnabled(false);
        TextView text = new TextView(this);
        text.setText(chores);
        text.setTypeface(Typeface.create("casual", Typeface.BOLD));
        text.setTextColor(getResources().getColor(R.color.black));
        text.setLayoutParams(params);
        box.setPadding(pixels, pixels, pixels, pixels);

        tas.addView(box);
        tas.addView(text);

        if(index%3== 0){
            tas.setBackgroundColor(getResources().getColor(R.color.orangeLight));
        }else if(index%3== 1){
            tas.setBackgroundColor(getResources().getColor(R.color.greenLight));
        }else{
            tas.setBackgroundColor(getResources().getColor(R.color.blueLight));
        }
        tas.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                CheckTask(view);
            }
        });

        list.addView(tas, params);

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
