package com.spearow.deepblue;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MyVolleyAsyncTask extends AsyncTask<String,String, JSONObject> {

    private Context ctx;

    public interface AsyncResponse {
        void processFinish(String output,JSONArray brands);
    }

    public AsyncResponse delegate=null;



    public MyVolleyAsyncTask(Context hostContext,AsyncResponse delegate)
    {
        ctx = hostContext;
        this.delegate=delegate;
    }


    @Override
    protected JSONObject doInBackground(String... params) {

        // Method runs on a separate thread, make all the network calls you need
        TestVolley tester = new TestVolley();

        return tester.fetchModules(ctx);
    }


    @Override
    protected void onPostExecute(JSONObject result)
    {
        // runs on the UI thread
        // do something with the result
        String msg = null;
        JSONArray brands = null;
        try {
            msg = result.getString("Message");
            brands = result.getJSONArray("Brands");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("resp", msg);
        //Log.d("res", result.toString());
        Log.d("brand",brands.toString());

        delegate.processFinish(msg,brands);
    }
}

