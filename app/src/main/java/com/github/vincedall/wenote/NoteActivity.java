package com.github.vincedall.wenote;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;

public class NoteActivity extends AppCompatActivity {
    private Button backArrow;
    private Button saveButton;
    private EditText note;
    private Util util;
    private boolean saved = true;
    private String noteNumber;
    private TextView savingState;
    private TextView actionBar;
    private boolean exists = false;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        util = new Util();
        savingState = findViewById(R.id.saving_state);
        actionBar = findViewById(R.id.toolbar_title);
        noteNumber = getIntent().getStringExtra("noteName");
        backArrow = findViewById(R.id.back_arrow);
        note = findViewById(R.id.note);
        File file = new File(NoteActivity.this.getFilesDir().getParent() +"/shared_prefs/"+ noteNumber + ".xml");
        if (file.exists()) {
            SharedPreferences prefs = getSharedPreferences(noteNumber, MODE_PRIVATE);
            String noteText = prefs.getString("note", "");
            if (noteText != null)
                note.setText(noteText);
            else
                finish();
        }
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = getSharedPreferences(noteNumber, MODE_PRIVATE).edit();
                editor.putString("preview", note.getText().toString().substring(0, util.getLength(note.getText().toString()))).apply();
                if (!exists)
                    editor.putString("date", Util.getCurrentDate()).apply();
                editor.putString("note", note.getText().toString()).apply();
                editor.putLong("modified_time", System.currentTimeMillis()).apply();
                CustomAsyncTask asyncTask = new CustomAsyncTask();
                if(asyncTask != null) {
                    if (asyncTask.getStatus().equals(CustomAsyncTask.Status.RUNNING))
                        asyncTask.cancel(true);
                }
                actionBar.setText(R.string.action_bar);
                savingState.setText(R.string.saving);
                asyncTask = new CustomAsyncTask();
                asyncTask.execute(savingState);
                saved = true;
            }
        });
        note.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                saved = false;
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });
    }

    @Override
    public void onBackPressed(){
        if(saved)
            super.onBackPressed();
        if (!saved){
            AlertDialog.Builder builder = new AlertDialog.Builder(NoteActivity.this);
            builder.setMessage(R.string.loseyournote).setTitle(R.string.confirm);
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    saved = true;
                    onBackPressed();
                }
            });
            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
}
