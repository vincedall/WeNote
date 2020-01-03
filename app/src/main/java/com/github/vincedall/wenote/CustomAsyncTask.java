package com.github.vincedall.wenote;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

public class CustomAsyncTask extends AsyncTask<TextView, String, TextView> {
    @Override
    protected void onPreExecute(){   //Execution before launching background task
        super.onPreExecute();          //Called in UI thread
    }

    @Override
    protected TextView doInBackground(TextView... params) {    //Background task (
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Log.w("Fast typing:", "Timer stopped due to fast typing (normal behavior)");
        }
        return params[0];
    }
    @Override
    protected void onProgressUpdate(String... progress) {    //Called when using publishProgress(); method
        super.onProgressUpdate(progress);                       //in doInBackground()
    }

    @Override
    protected void onPostExecute(TextView textView){     //Called in UI thread after execution
        textView.setText(R.string.saved);
    }
}
