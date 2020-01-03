package com.github.vincedall.wenote;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;

public class AutosaveNoteActivity extends AppCompatActivity {
    private Button backArrow;
    private Button saveButton;
    private EditText note;
    private Util util;
    private String noteNumber;
    private TextView savingState;
    private TextView actionBar;
    CustomAsyncTask asyncTask = new CustomAsyncTask();
    private boolean exists = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        util = new Util();
        savingState = findViewById(R.id.saving_state);
        actionBar = findViewById(R.id.toolbar_title);
        noteNumber = getIntent().getStringExtra("noteName");
        backArrow = findViewById(R.id.back_arrow);
        note = findViewById(R.id.note);
        File file = new File(AutosaveNoteActivity.this.getFilesDir().getParent() +"/shared_prefs/"+ noteNumber + ".xml");
        if (file.exists()) {
            exists = true; //To determine if the note already exists when saving the creation date (to not change it)
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
                int length = util.getLength(note.getText().toString());
                if (note.getText().length() > length)
                    editor.putString("preview", note.getText().toString().substring(0, length) + " ...").apply();
                else
                    editor.putString("preview", note.getText().toString().substring(0, length)).apply();
                if (!exists)
                    editor.putString("date", Util.getCurrentDate()).apply();
                editor.putString("note", note.getText().toString()).apply();
                editor.putLong("modified_time", System.currentTimeMillis()).apply();
                if(asyncTask != null) {
                    if (asyncTask.getStatus().equals(CustomAsyncTask.Status.RUNNING))
                        asyncTask.cancel(true);
                }
                actionBar.setText(R.string.action_bar);
                savingState.setText(R.string.saving);
                asyncTask = new CustomAsyncTask();
                asyncTask.execute(savingState);

            }
        });
        note.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                saveButton.performClick();
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });
    }
}
