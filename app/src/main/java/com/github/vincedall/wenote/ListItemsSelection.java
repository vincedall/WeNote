package com.github.vincedall.wenote;

public class ListItemsSelection {
    private String title;
    private String number;
    private boolean selected;
    private int layoutPadding = 0;
    private int textPadding = 50;
    private boolean animationPlayed;
    private boolean wasSelected;
    private String date;

    public ListItemsSelection(String title, String number, boolean selected, String date) {
        this.title = title;
        this.number = number;
        this.selected = selected;
        this.animationPlayed = false;
        this.wasSelected = false;
        this.date = date;
    }

    public boolean getWasSelected() {
        return wasSelected;
    }

    public void setWasSelected(boolean wasSelected) {
        this.wasSelected = wasSelected;
    }

    public boolean getAnimationPlayed() {
        return animationPlayed;
    }

    public void setAnimationPlayed(boolean animationPlayed) {
        this.animationPlayed = animationPlayed;
    }

    public String getNumber() {
        return number;
    }

    public boolean getSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getTitle(){
        return title;
    }

    public int getLayoutPadding() {
        return layoutPadding;
    }

    public void setLayoutPadding(int layoutPadding) {
        this.layoutPadding = layoutPadding;
    }

    public int getTextPadding() {
        return textPadding;
    }

    public void setTextPadding(int textPadding) {
        this.textPadding = textPadding;
    }

    public String getDate() { return date; }
    public void setDate(String date){ this.date = date; }
}
