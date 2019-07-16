package com.spearow.deepblue;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class TestVolley {

    private String TAG = "SO_TEST";
    private String url = "http://192.168.137.1/DeepBlue/test.php";


    public JSONObject fetchModules(Context ctx){
        JSONObject response = null;
        RequestQueue requestQueue = Volley.newRequestQueue(ctx);

        RequestFuture<String> future=RequestFuture.newFuture();
        StringRequest request=new StringRequest(Request.Method.POST,url,future,future){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return uploadActivity.getParams();
            }
        };
        requestQueue.add(request);
        request.setRetryPolicy(new DefaultRetryPolicy(60000000,15, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));



        try {
            //response = future.get(2, TimeUnit.MINUTES); // Blocks for at most 10 seconds.
            String res=future.get(60, TimeUnit.MINUTES);
            //Toast.makeText(ctx, TAG + res, Toast.LENGTH_LONG).show();
            response = new JSONObject(res);


        } catch (InterruptedException e) {
            Log.d(TAG,"interrupted");
        } catch (ExecutionException e) {
            Log.d(TAG,"execution");
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }



        return response;
    }
}