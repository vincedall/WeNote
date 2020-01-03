package com.github.vincedall.wenote;

import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains the data accessed by the GUI to render the application.
 * The data are modified by the methods in the class Controller
 * @author Vincent Dallaire
 * @version 1.0
 * @since 2019-03-31
 */
public class Data {
    private boolean selectionMode = false;
    /*
    Field to determine the amount of notes selected to set the delete/export and
    cancel buttons to transparent if no notes are selected.
     */
    private int amountOfNotesSelected = 0;
    private ArrayList<ListItems> listItems = new ArrayList<>();
    private ArrayList<ListItemsSelection> listItemsSelection = new ArrayList<>();
    private int listViewIndex;
    private int listViewTop;
    private CustomAdapter adapter;
    private SelectionAdapter selectionAdapter;
    private boolean importMode = false;
    private ArrayList<String> importModeFiles;
    private ArrayAdapter<String> importModeAdapter;

    /**
     * Method to return the selectionMode to the controller
     * @return selectionMode
     */
    public boolean getSelectionMode() {
        return selectionMode;
    }

    /**
     * Method to set selectionMode
     * @param selectionMode boolean to set
     */
    public void setSelectionMode(boolean selectionMode) {
        this.selectionMode = selectionMode;
    }

    /**
     * Method to return amountOfNotesSelected to the controller
     * @return amountOfNotesSelected
     */
    public int getAmountOfNotesSelected() {
        return amountOfNotesSelected;
    }

    /**
     * Method to set amountOfNotesSelected
     * @param amountOfNotesSelected integer consisting of the new value
     */
    public void setAmountOfNotesSelected(int amountOfNotesSelected) {
        this.amountOfNotesSelected = amountOfNotesSelected;
    }

    /**
     * @return returns the list of items consisting of the list of notes
     */
    public ArrayList<ListItems> getListItems() {
        return listItems;
    }

    /**
     * @param listItems the list of notes of the user
     */
    public void setListItems(ArrayList<ListItems> listItems) {
        this.listItems = listItems;
    }

    /**
     * @return the list of notes during selection
     */
    public ArrayList<ListItemsSelection> getListItemsSelection() {
        return listItemsSelection;
    }

    /**
     * @param listItemsSelection the list of notes when selectionMode is active
     */
    public void setListItemsSelection(ArrayList<ListItemsSelection> listItemsSelection) {
        this.listItemsSelection = listItemsSelection;
    }

    /**
     * @return the index of the ListView
     */
    public int getListViewIndex() {
        return listViewIndex;
    }

    /**
     * @param listViewIndex the index of the main ListView
     */
    public void setListViewIndex(int listViewIndex) {
        this.listViewIndex = listViewIndex;
    }

    /**
     * @return the top of the ListView
     */
    public int getListViewTop() {
        return listViewTop;
    }

    /**
     * @param listViewTop int representing the top of the main ListView
     */
    public void setListViewTop(int listViewTop) {
        this.listViewTop = listViewTop;
    }

    public CustomAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(CustomAdapter adapter) {
        this.adapter = adapter;
    }

    public SelectionAdapter getSelectionAdapter() {
        return selectionAdapter;
    }

    public void setSelectionAdapter(SelectionAdapter selectionAdapter) {
        this.selectionAdapter = selectionAdapter;
    }

    public boolean getImportMode() {
        return importMode;
    }

    public void setImportMode(boolean importMode) {
        this.importMode = importMode;
    }

    public ArrayList<String> getImportModeFiles() {
        return importModeFiles;
    }

    public void setImportModeFiles(ArrayList<String> importModeFiles) {
        this.importModeFiles = importModeFiles;
    }

    public ArrayAdapter<String> getImportModeAdapter() {
        return importModeAdapter;
    }

    public void setImportModeAdapter(ArrayAdapter<String> importModeAdapter) {
        this.importModeAdapter = importModeAdapter;
    }
}
