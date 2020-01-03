package com.github.vincedall.wenote;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Contains the methods for working on the data of the model in the Data class.
 * @author Vincent Dallaire
 * @version 1.0
 * @since 2019-12-29
 */
public class Controller {
    private Data data = new Data();
    private Util util = new Util();

    /**
     * Method to get the selectionMode value from Data
     * @return Returns true if selectionMode is active and vice-versa
     */
    public boolean getSelectionMode(){
        return data.getSelectionMode();
    }

    /**
     * Method to activate selectionMode
     * It adds all the notes of listItems to listItemsSelection to change the view of the ListView items.
     * @param notePosition position of the note to be selected
     */
    public void activateSelectionMode(int notePosition){
        data.setSelectionMode(true);
        data.setListItemsSelection(new ArrayList<ListItemsSelection>());
        for (ListItems item : data.getListItems())  //creating the list of selection items
            data.getListItemsSelection().add(new ListItemsSelection(item.getTitle(), item.getNumber(), false, item.getDate()));
        data.getListItemsSelection().get(notePosition).setSelected(true); //Sets the item under the long click to selected
        data.setAmountOfNotesSelected(1);
    }

    /**
     * The function activates importMode properly. This mode lets the user select a note from Notes directory.
     * The selected note will be imported to the notes of the user.
     * @param context MainActivity context
     * @param listView the main ListView of MainActivity
     */
    public void activateImportMode(Context context, ListView listView){
        if (new File(Environment.getExternalStorageDirectory() + "/Notes/").exists() &
                new File(Environment.getExternalStorageDirectory() + "/Notes/").list() != null &
                new File(Environment.getExternalStorageDirectory() + "/Notes/").list().length != 0) {
            data.setImportMode(true);
            data.setImportModeFiles(new ArrayList<String>());
            data.setImportModeAdapter(new ArrayAdapter<String>(context,
                    android.R.layout.simple_list_item_1, data.getImportModeFiles()));
            listView.setAdapter(data.getImportModeAdapter());
            for (File file : new File(Environment.getExternalStorageDirectory() + "/Notes/").listFiles()) {
                data.getImportModeFiles().add(file.getName());
            }
            data.getImportModeAdapter().notifyDataSetChanged();
        }else
            Toast.makeText(context, "Put notes in Notes folder", Toast.LENGTH_SHORT).show();
    }

    /**
     * The function returns true if importMode is set false otherwise.
     * @return boolean
     */
    public boolean getImportMode(){
        return data.getImportMode();
    }

    /**
     * Symply sets importMode to false to deactivate importMode.
     */
    public void exitImportMode(){
        data.setImportMode(false);
    }

    /**
     * The function is used to import a note once the user selected a note while importMode was active.
     * @param position position in the listView for the importMode adapter to retrieve the filename
     * @param context MainActivity for the Toasts and SharedPreferences
     * @param sharedPreferencesPath the path to call the getUnusedNumber function
     * @return boolean to determine if a file leaked. In some rare occasions, the function will return false.
     * The application will finish to avoid leakage of file. Returns true otherwise.
     */
    public boolean importNote(int position, Context context, String sharedPreferencesPath){
        String filename = data.getImportModeFiles().get(position);
        String noteNumber = getUnusedNumber(sharedPreferencesPath);
        boolean imported = true;
        File file = new File(Environment.getExternalStorageDirectory() + "/Notes/" + filename);
        FileReader in = null;
        char[] chars = new char[(int) file.length()];
        try {
            in = new FileReader(file);
            in.read(chars);
        }catch(IOException e){
            Log.w("Error importing", "The note wasn't imported properly");
            imported = false;
        }finally{
            try {
                if (in != null)
                    in.close();
            }catch(Exception e){
                return false;
            }
        }
        if (imported) {
            String note = String.valueOf(chars);
            SharedPreferences.Editor editor = context.getSharedPreferences(noteNumber, MODE_PRIVATE).edit();
            int length = util.getLength(note);
            if (note.length() > length)
                editor.putString("preview", note.substring(0, length) + " ...").commit();
            else
                editor.putString("preview", note.substring(0, length)).commit();
            editor.putString("date", Util.getCurrentDate()).commit();
            editor.putString("note", note).commit();
        }else
            Toast.makeText(context, "The note wasn't imported properly. Try again.", Toast.LENGTH_SHORT).show();
        return true;
    }

    /**
     * Function to set the main ListView adapters according to the actual mode.
     * @param listView
     * @param context
     */
    public void setListViewAdapters(ListView listView, Context context){
        /*
        Sets indexes of listView to replace it at the same position after adapters are set
         */
        data.setListViewIndex(listView.getFirstVisiblePosition());
        View v = listView.getChildAt(0);
        data.setListViewTop((v == null) ? 0 : (v.getTop() - listView.getPaddingTop()));

        /*
        Sets the adapter of the ListView according to the mode actually in place
         */
        if (data.getSelectionMode()){
            data.setSelectionAdapter(new SelectionAdapter(context, data.getListItemsSelection()));
            listView.setAdapter(data.getSelectionAdapter());
        }else {
            orderList();
            data.setAdapter(new CustomAdapter(context, data.getListItems()));
            listView.setAdapter(data.getAdapter());
        }

        /*
         Sets the listView position according to the index recorded just before
         */
        listView.setSelectionFromTop(data.getListViewIndex(), data.getListViewTop());
    }

    /**
     * Function to order the Notes according to their modification time.
     * The modified_time preference is set when saving the note in NoteActivity.
     * It is then set in the adapter for each note in the funciton addNotesInList in MainActivity.
     * This function is called in setListViewAdapters before setting the adapters.
     * It orders the notes in ListItems before setting it as the ArrayList for the customAdapter.
     */
    public void orderList(){
        if (data.getListItems().size() > 1) {
            ArrayList<ListItems> items = new ArrayList<>();
            items.add(data.getListItems().get(0));
            boolean added = false;
            for (int i = 1; i < data.getListItems().size(); i++) {
                for (int a = 0; a < items.size(); a++) {
                    if (data.getListItems().get(i).getModifiedTime() > items.get(a).getModifiedTime()) {
                        items.add(a, data.getListItems().get(i));
                        added = true;
                        break;
                    }
                }
                if (!added)
                    items.add(data.getListItems().get(i));
                added = false;
            }
            data.setListItems(items);
        }
    }

    /**
     * Function to notify an adapter of changed data. Not available in the GUI since adapters are stored in data.
     * @param mode string to determine what adapter to notify of changed data
     */
    public void notifyDataSetChanged(String mode){
        if (mode.equals("normal"))
            data.getAdapter().notifyDataSetChanged();
        else
            data.getSelectionAdapter().notifyDataSetChanged();
    }

    /**
     * Function to export all the notes selected in the moment
     * @param context MainActivity context
     * @return a boolean to the main GUI to finish the app if a file leak occured.
     */
    public boolean exportNotes(Context context){
        for (ListItemsSelection item : data.getListItemsSelection()){
            if (item.getSelected()){
                FileWriter out = null;
                SharedPreferences prefs = context.getSharedPreferences(item.getNumber(), MODE_PRIVATE);
                String note = prefs.getString("note", "");
                try{
                    File target = new File(Environment.getExternalStorageDirectory()
                            +"/Notes");
                    target.mkdirs();
                    target = new File(Environment.getExternalStorageDirectory()
                            +"/Notes/"+ item.getNumber() +".txt");
                    if (target.exists())
                        target = new File(Environment.getExternalStorageDirectory()
                                +"/Notes/"+ getGeneratedFilename());
                    out = new FileWriter(target);
                    out.write(note);
                }catch(IOException e){
                    Log.w("Problem exporting", "the notes were not exported succesfully");
                }finally{
                    try {
                        if (out != null)
                            out.close();
                    }catch(IOException e){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Function used by exportNotes() to generate a filename for a note if its filename is already taken in Notes directory
     * @return a String consisting of a new filename to give to the exported note
     */
    public String getGeneratedFilename(){
        int num = 0;
        String newName = "note" + num;
        while(new File(Environment.getExternalStorageDirectory() +"/Notes/"+ newName +".txt").exists()) {
            num++;
            newName = "note" + num;
        }
        return newName + ".txt";
    }

    /**
     * Method to exit selectionMode
     */
    public void exitSelectionMode(){
        data.setSelectionMode(false);
    }

    /**
     * Method to return amountOfNotesSelected to the GUI from data
     * @return amountOfNotesSelected
     */
    public int getAmountOfNotesSelected(){
        return data.getAmountOfNotesSelected();
    }

    /**
     * @param notePosition position of the note in the list of notes
     * @return number of the note at position notePosition in the list of notes
     */
    public String getNoteNumber(int notePosition){
        return data.getListItems().get(notePosition).getNumber();
    }

    /**
     * This function receives the filename, the preview and the date of creation of a note
     * and adds this note to the list of notes in data.
     * @param itemNumber number of the note to be added
     * @param preview the preview of the note to be added
     * @param date date of creation of the file/note
     */
    public void addNoteInList(String itemNumber, String preview, String date, long modifiedTime){
        data.getListItems().add(new ListItems(preview, itemNumber, date, modifiedTime)); //Adds the note to the list of notes
    }

    /**
     * Simple function to reset the list of notes
     */
    public void resetNoteList(){
        data.setListItems(new ArrayList<ListItems>());
    }

    /**
     * Function to delete notes that are selected
     * @param path path to shared preferences of the phone
     */
    public void deleteNotes(String path){
        for (ListItemsSelection item : data.getListItemsSelection()){
            if (item.getSelected()) {
                new File(path + item.getNumber() + ".xml").delete();
            }
        }
    }

    /**
     * This function receives the position of a note from the ListView listener in MainActivity.
     * It returns true if the note is selected, false otherwise.
     * @param notePosition position of the note in selectionMode
     * @return value of the selected variable of the note in selectionMode
     */
    public boolean isNoteSelected(int notePosition){
        return data.getListItemsSelection().get(notePosition).getSelected();
    }

    /**
     * This function receives the position of a note from the ListView listener in MainActivity.
     * wasSelected is set to true when a note was just deselected before notifyDataSetChanged() is called on the selectionAdapter.
     * It is used by the adapter to know if the checkmark animation should be played.
     * @param notePosition position of the note in selectionMode
     * @return value of the wasSelected variable
     */
    public boolean noteWasSelected(int notePosition){
        return data.getListItemsSelection().get(notePosition).getWasSelected();
    }

    /**
     * This function receives the position of a note from the ListView listener in MainActivity.
     * animationPlayed is set to false when the animation needs to be played because a note was just set to selected
     * @param notePosition position of the note in selectionMode
     * @return value of animationPlayed variable
     */
    public boolean animationPlayed(int notePosition){
        return data.getListItemsSelection().get(notePosition).getAnimationPlayed();
    }

    /**
     * This function receives the position of a note from the ListView listener in MainActivity.
     * It deselects the note, diminishes amountOfNotesSelected and sets wasSelected to true
     * @param notePosition position of the note in listItemsSelection
     */
    public void deselectNote(int notePosition){
        data.getListItemsSelection().get(notePosition).setSelected(false);
        data.setAmountOfNotesSelected(data.getAmountOfNotesSelected() - 1);
        data.getListItemsSelection().get(notePosition).setWasSelected(true);
    }

    /**
     * This function receives the position of a note from the ListView listener in MainActivity.
     * It sets the note to selected, increases amountOfNotesSelected and sets animationPlayed to false.
     * @param notePosition position of the note in selectionMode
     */
    public void selectNote(int notePosition){
        data.getListItemsSelection().get(notePosition).setSelected(true);
        data.setAmountOfNotesSelected(data.getAmountOfNotesSelected() + 1);
        data.getListItemsSelection().get(notePosition).setAnimationPlayed(false);
    }

    /**
     * Function to get a filename that's not already in use for a new note
     * @param path a String consisting of the path to the shared preferences of the phone
     * @return a String consisting of the new unused filename found
     */
    public String getUnusedNumber(String path){
        int num = 0;
        boolean nameFound = false;
        while (nameFound == false){
            boolean found = true;
            for(File file : new File(path).listFiles()){
                if (file.getName().equals("" + num + ".xml")) {
                    num++;
                    found = false;
                    break;
                }
            }
            if (found == true)
                nameFound = true;
        }
        return "" + num;
    }
}
