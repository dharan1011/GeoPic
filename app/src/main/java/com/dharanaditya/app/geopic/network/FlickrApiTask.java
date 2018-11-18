package com.dharanaditya.app.geopic.network;

import android.os.AsyncTask;

import com.dharanaditya.app.geopic.utils.NetworkUtility;

import java.io.IOException;
import java.net.URL;

public class FlickrApiTask extends AsyncTask<URL, Void, String> {
    private ApiTaskCallbacks apiTaskCallbacks;

    public FlickrApiTask(ApiTaskCallbacks apiTaskCallbacks) {
        this.apiTaskCallbacks = apiTaskCallbacks;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        apiTaskCallbacks.onPreExecute();
    }

    @Override
    protected String doInBackground(URL... urls) {
        try {
            return NetworkUtility.getResponseFromHttpUrl(urls[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        apiTaskCallbacks.onPostExecute(s);
    }

    public interface ApiTaskCallbacks {
        void onPreExecute();

        void onPostExecute(String response);
    }
}
