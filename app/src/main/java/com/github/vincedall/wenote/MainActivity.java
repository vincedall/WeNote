package com.github.vincedall.wenote;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;

/**
 *Main GUI of the application. Uses the Controller methods to modify the model Data.
 *@author Vincent Dallaire
 *@version 1.0
 *@since 2019-03-31
 */
public class MainActivity extends AppCompatActivity {
    private FloatingActionButton fab;
    private Switch autosaveSwitch;
    private TextView autosave;
    private ListView listView;
    private TextView delete;
    private TextView export;
    private TextView cancel;
    private TextView dotMenu;
    private Controller controller = new Controller();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //Set content of the view
        fab = findViewById(R.id.fab);  //The Floating Action Button to add a new note
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (controller.getSelectionMode())
                    exitSelectionMode(); //Exits selection mode to avoid problems when coming back from the NoteActivity
                String number = controller.getUnusedNumber(getFilesDir().getParent() + "/shared_prefs"); //Gets a new number not already in use for the new note
                /*
                If autosaveSwitch is checked starts NoteActivity with autosave
                Adds an extra which is the name of the note
                 */
                if (autosaveSwitch.isChecked()) {
                    Intent autosaveNoteActivity = new Intent(MainActivity.this, AutosaveNoteActivity.class);
                    autosaveNoteActivity.putExtra("noteName", number);
                    startActivityForResult(autosaveNoteActivity, 2);
                }else{
                    Intent noteActivity = new Intent(MainActivity.this, NoteActivity.class);
                    noteActivity.putExtra("noteName", number);
                    startActivityForResult(noteActivity, 2);
                }
            }
        });
        autosaveSwitch = findViewById(R.id.switch1);

        /*
        This section saves the autosave preferences of the user in shared preferences.
        It listens for changes on the switch and saves them directly.
        It will then tcheck the state of preferences at app launch to set the switch as needed.
         */
        SharedPreferences prefs = getSharedPreferences("autosave", MODE_PRIVATE);
        SharedPreferences.Editor editor = getSharedPreferences("autosave", MODE_PRIVATE).edit();
        editor.putBoolean("autosave", prefs.getBoolean("autosave", false)).apply();
        autosave = findViewById(R.id.autosave);
        if (prefs.getBoolean("autosave", false)) {
            autosaveSwitch.setChecked(true);
            autosave.setText(R.string.autosave_on);
        } else {
            autosaveSwitch.setChecked(false);
            autosave.setText(R.string.autosave_off);
        }
        autosaveSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = getSharedPreferences("autosave", MODE_PRIVATE).edit();
                if (autosaveSwitch.isChecked()) {
                    editor.putBoolean("autosave", true);
                    editor.apply();
                    autosave.setText(R.string.autosave_on);
                } else {
                    editor.putBoolean("autosave", false);
                    editor.commit();
                    autosave.setText(R.string.autosave_off);
                }
            }
        });

        delete = findViewById(R.id.delete);
        export = findViewById(R.id.export);
        cancel = findViewById(R.id.cancel);
        dotMenu = findViewById(R.id.menu);

        /*
        Listens for the main ListView clioks. If selectionMode is active, selects the item associated.
        Otherwise, it starts a NoteActivity according to autosaveSwitch.
        If importMode is set, adds the selected note to the list of notes
         */
        listView = findViewById(R.id.list_view);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> AV, View v, int position, long num) {
                if (controller.getSelectionMode()){
                    if (controller.isNoteSelected(position)) {
                        controller.deselectNote(position);
                        if (controller.getAmountOfNotesSelected() == 0){
                            delete.setTextColor(Color.argb(120, 255,255, 255));
                            export.setTextColor(Color.argb(120, 255,255, 255));
                        }
                    }
                    else {
                        controller.selectNote(position);
                        delete.setTextColor(Color.WHITE);
                        export.setTextColor(Color.WHITE);
                    }
                    controller.notifyDataSetChanged("selection");
                    /*
                    If import was clicked then importMode is set.
                    It means that the ListView is presenting Notes directory content instead of usual notes.
                    When a user selects a note from Notes directory, this note will be added to the notes of the user.
                     */
                }else if(controller.getImportMode()) {
                    if(controller.importNote(position, MainActivity.this, getFilesDir().getParent() + "/shared_prefs")) {
                        controller.exitImportMode();
                        addNotesInList();
                        setListViewAdapters();
                    }
                    else
                        finish();
                    /*
                    If both importMode and selectionMode are not active then one of the NoteActivity will start.
                     */
                }else {
                    String number = controller.getNoteNumber(position);
                    if (autosaveSwitch.isChecked()) {
                        Intent autosaveNoteActivity = new Intent(MainActivity.this, AutosaveNoteActivity.class);
                        autosaveNoteActivity.putExtra("noteName", number);
                        startActivityForResult(autosaveNoteActivity, 2);
                    }else{
                        Intent noteActivity = new Intent(MainActivity.this, NoteActivity.class);
                        noteActivity.putExtra("noteName", number);
                        startActivityForResult(noteActivity, 2);
                    }
                }
            }
        });

        /*
        Listens for long clicks on the main ListView. It is mainly used to activate selectionMode.
         */
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                controller.activateSelectionMode(position); //Activates selection mode
                autosave.setVisibility(View.INVISIBLE);   //Makes some parts of the GUI disappear to let some place for buttons
                autosaveSwitch.setVisibility(View.INVISIBLE);
                delete.setVisibility(View.VISIBLE);  //Make some buttons appear
                export.setVisibility(View.VISIBLE);
                cancel.setVisibility(View.VISIBLE);
                delete.setTextColor(Color.WHITE);
                export.setTextColor(Color.WHITE);
                setListViewAdapters();  //Sets the ListView to show the changes
                return true;
            }
        });

        /*
        Listener for delete button. Builds a dialog to make sure the user wants to delete the notes.
         */
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (controller.getAmountOfNotesSelected() > 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage(R.string.delete_note).setTitle(R.string.confirm);
                    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            controller.deleteNotes(MainActivity.this.getFilesDir().getParent() +"/shared_prefs/");  //Deletes the notes
                            addNotesInList();  //Rebuilds the list of notes from the sharedpreferences directory
                            setListViewAdapters();   //Sets the ListView to reflect the changes
                            exitSelectionMode();  //Exits selection mode
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
        });
        export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (controller.getAmountOfNotesSelected() > 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage(R.string.export_note).setTitle(R.string.confirm);
                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            String[] permission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
                            ActivityCompat.requestPermissions(MainActivity.this, permission, 200);
                            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                                    PackageManager.PERMISSION_GRANTED) {
                                if (controller.exportNotes(MainActivity.this))
                                    Toast.makeText(MainActivity.this, "Notes succesfully exported", Toast.LENGTH_SHORT).show();
                                else
                                    finish();
                            }
                            exitSelectionMode();
                        }
                    });
                    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitSelectionMode();
            }
        });
        dotMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(MainActivity.this, dotMenu);
                popup.getMenuInflater().inflate(R.menu.menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (controller.getSelectionMode())
                            exitSelectionMode();
                        controller.activateImportMode(MainActivity.this, listView);
                        return true;
                    }
                });
                popup.show();
            }
        });
        addNotesInList();
        setListViewAdapters();
    }

    /**
     * Listener called when coming back from NoteActivity.
     * Used to set the listView properly if a note was added or the preview of a note modified during NoteActivity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        addNotesInList();
        setListViewAdapters();
    }

    /**
     * Function to add the notes present in shared preferences to the list of notes.
     * Called at launch of the app or when exiting a note activity.
     */
    public void addNotesInList(){
        controller.resetNoteList();
        for (File file : new File(MainActivity.this.getFilesDir().getParent() + "/shared_prefs").listFiles()) { //Iterates on the files
            if (!file.getName().equals("autosave.xml")) { //Forget the autosave.xml because it is not a note
                String itemNumber = file.getName().substring(0, file.getName().length()-4); //Computes the note number from its filename
                SharedPreferences prefs = getSharedPreferences(itemNumber, MODE_PRIVATE);
                controller.addNoteInList(itemNumber, prefs.getString("preview", ""), prefs.getString("date", ""),
                        prefs.getLong("modified_time", 0)); //Adds the note using controller
            }
        }
    }

    /**
     * Function to set the main ListView adapters according to the actual mode.
     */
    public void setListViewAdapters() {
        controller.setListViewAdapters((ListView) findViewById(R.id.list_view),this);
    }

    /**
     * Function to exit selectionMode properly
     */
    public void exitSelectionMode(){
        controller.exitSelectionMode();
        delete.setTextColor(Color.WHITE);
        export.setTextColor(Color.WHITE);
        autosave.setVisibility(View.VISIBLE);
        autosaveSwitch.setVisibility(View.VISIBLE);
        delete.setVisibility(View.INVISIBLE);
        export.setVisibility(View.INVISIBLE);
        cancel.setVisibility(View.INVISIBLE);
        setListViewAdapters();
    }

    /**
     * Function called when the user presses the phone's back button.
     * The function exits selection or import mode.
     * If none of these mode is active closes the application.
     */
    @Override
    public void onBackPressed(){
        if (controller.getSelectionMode()) {
            exitSelectionMode();
        } else if (controller.getImportMode()){
            controller.exitImportMode();
            setListViewAdapters();
        }else
            finish();
    }
}
