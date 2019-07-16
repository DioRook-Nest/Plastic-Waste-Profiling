package com.spearow.deepblue;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.api.Http;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ACCESS_WIFI_STATE;
import static android.graphics.Bitmap.CompressFormat.JPEG;
import static com.google.android.libraries.places.api.Places.createClient;



public class uploadActivity extends AppCompatActivity implements MyVolleyAsyncTask.AsyncResponse {


    long count;
    private static final String TAG = "error";
    Button btnChoose, btnUpload;
    ImageView imageUpload;
    final int CODE_GALLERY_REQUEST = 999;
    String urlUpload = "http://192.168.137.1/DeepBlue/test.php";
    static Bitmap bitmap;
    Uri filePath;
    static String imageData;
    double longitude = 0;
    double latitude = 0;
    LocationManager lm;
    Location location;
    String provider;
    //private PlacesClient placesClient;
    List<Place.Field> placeFields;
    private TextView responseView;
    PlacesClient placesClient;
    //private FieldSelector fieldSelector;
    EditText placeText;
    JSONObject googlePlacesData = null;
    private int PROXIMITY_RADIUS = 5000;
    private String apiKey;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    FirebaseFirestore db;
    String place;
    ArrayList <String> label;
    String item="";
    //ProgressV

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        label=new ArrayList<String>();
        btnChoose = findViewById(R.id.btnChoose);
        btnUpload = findViewById(R.id.btnUpload);
        imageUpload = findViewById(R.id.imageUpload);
        btnUpload.setEnabled(true);
        db = FirebaseFirestore.getInstance();

        apiKey = getString(R.string.browser_key);


        if (apiKey.equals("")) {

            Toast.makeText(this, getString(R.string.error_api_key), Toast.LENGTH_LONG).show();

            return;

        }


        // Setup Places Client

        if (!Places.isInitialized()) {

            Places.initialize(getApplicationContext(), apiKey);

        }



        /*setLaunchActivityClickListener(R.id.autocomplete_button, AutocompleteTestActivity.class);



        setLaunchActivityClickListener(R.id.place_and_photo_button, PlaceAndPhotoTestActivity.class);



        setLaunchActivityClickListener(R.id.current_place_button, CurrentPlaceTestActivity.class);*/

        // Retrieve a PlacesClient (previously initialized - see MainActivity)

        //placesClient = Places.createClient(this);

        placeFields = Arrays.asList(Place.Field.NAME);
        responseView = findViewById(R.id.response);
        placesClient=Places.createClient(this);

        // Set view objects

       /* List<Place.Field> placeFields =

                FieldSelector.getPlaceFields(Place.Field.PHONE_NUMBER, Place.Field.WEBSITE_URI);

        fieldSelector =

                new FieldSelector(

                        findViewById(R.id.use_custom_fields),

                        findViewById(R.id.custom_fields_list),

                        placeFields);

        responseView = findViewById(R.id.response);


*/
        setLoading(false);


        // Set listeners for programmatic Find Current Place

        //findViewById(R.id.imageUpload).setOnClickListener((view) -> findCurrentPlace());


        imageUpload.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {

                //String type = placeText.getText().toString();
                /*StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
                googlePlacesUrl.append("location=" + latitude + "," + longitude);
                googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
                googlePlacesUrl.append("&types=" + type);
                googlePlacesUrl.append("&sensor=true");
                googlePlacesUrl.append("&key=" + GOOGLE_API_KEY);

                GooglePlacesReadTask googlePlacesReadTask = new GooglePlacesReadTask();
                Object[] toPass = new Object[2];
                toPass[0] = googleMap;
                toPass[1] = googlePlacesUrl.toString();
                googlePlacesReadTask.execute(toPass);*/
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMinCropResultSize(640, 640)
                        // .setAspectRatio(1, 1)
                        .start(uploadActivity.this);

                lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);



                if(checkLocationPermission()){
                    location = (Location) lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    if (location == null)
                        location = (Location) lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    longitude = location.getLongitude();
                    latitude = location.getLatitude();
                    findCurrentPlace();
                }




            }
        });

        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(
                        uploadActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        CODE_GALLERY_REQUEST
                );
            }
        });


        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnUpload.setEnabled(false);
                btnUpload.setVisibility(View.INVISIBLE);
                findViewById(R.id.loading2).setVisibility(View.VISIBLE);
                //findViewById(R.id.loading2).setIndeterminate(true);
                Toast.makeText(getApplicationContext(),"longitude"+longitude+"latitude"+latitude,Toast.LENGTH_LONG).show();

                new MyVolleyAsyncTask(uploadActivity.this,uploadActivity.this).execute();


                /*StringRequest stringRequest = new StringRequest(Request.Method.POST, urlUpload, new Response.Listener<String>() {
                    Map<String, Object> user ;
                    String [] l=new String[]{"sprite","cocacola","fanta","lays","kurkure"};
                    @Override
                    public void onResponse(String response) {
                        JSONObject reader = null;

                        try {
                            reader = new JSONObject(response);
                            String msg = reader.getString("Message");
                            Log.d("resp", msg);
                            user = new HashMap<>();
                            label.clear();
                            item=l[(int)(Math.random()*(l.length-1))];
                            label.add(item);
                            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                            count=1;
                            //user.clear();
                            for(int i=0;i<label.size();i++)
                                user.put(label.get(i), count);

                            Log.d(TAG, "initial doc map "+user);

                            final DocumentReference docRef = db.collection("plastic").document(place);
                            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            Map<String, Object> temp = document.getData();
                                            Log.d(TAG, "DocumentSnapshot data: " +temp);
                                            for(int i=0;i<label.size();i++) {
                                                String lab=label.get(i);
                                                if (temp.containsKey(lab)) {

                                                    count = (long) temp.get(lab) + 1;
                                                    user.put(lab,count);
                                                    Log.d(TAG, "count: " +count+user+lab);
                                                }
                                            }

                                            docRef.update(user)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Log.d(TAG, "DocumentSnapshot successfully updated!"+user);
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w(TAG, "Error updating document", e);
                                                        }
                                                    });

                                        } else {
                                            Log.d(TAG, "No such document");
                                            docRef.set(user);

                                        }

                                    } else {
                                        Log.d(TAG, "get failed with ", task.getException());
                                    }
                                }
                            });


                            Intent intent = new Intent(MainActivity.this, showImg.class);
                            //intent.putExtra("filePath", imageData);
                            intent.putExtra("path", filePath.toString());
                            //intent.putExtra("coord", msg);
                            intent.putExtra("serverpath", msg);
                            intent.putExtra("label", label);
                            intent.putExtra("place", place);
                            btnUpload.setEnabled(true);
                            btnUpload.setVisibility(View.VISIBLE);
                            findViewById(R.id.loading2).setVisibility(View.GONE);
                            //findViewById(R.id.loading2).setEnabled(true););
                            startActivity(intent);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "error:" + error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        imageData = imageToString(bitmap);
                        params.put("image", imageData);


                        return params;
                    }
                };
                stringRequest.setRetryPolicy(new RetryPolicy() {
                    @Override
                    public int getCurrentTimeout() {
                        return 500000;
                    }

                    @Override
                    public int getCurrentRetryCount() {
                        return 500000;
                    }

                    @Override
                    public void retry(VolleyError error) throws VolleyError {

                    }
                });



                RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
                requestQueue.add(stringRequest);*/

            }
        });
    }

    @Override
    public void processFinish(String msg, JSONArray brands){
        /*Object r = new MyVolleyAsyncTask(MainActivity.this).execute();
                Log.d("Tag","abc"+r);*/

                /*RequestFuture<String> future=RequestFuture.newFuture();
                StringRequest stringRequest=new StringRequest(Request.Method.POST,urlUpload,future,future){

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        imageData = imageToString(bitmap);
                        params.put("image", imageData);


                        return params;
                    }


                };*/

               /* stringRequest.setRetryPolicy(new DefaultRetryPolicy(600000,15, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
                requestQueue.add(stringRequest);*/

        //JSONObject reader = null;
        //String [] l=new String[]{"sprite","cocacola","fanta","lays","kurkure"};
        //String res;

                    /*res=future.get(5, TimeUnit.MINUTES);
                    Toast.makeText(getApplicationContext(), "error:" + res, Toast.LENGTH_LONG).show();
                    reader = new JSONObject(res);

                    String msg = reader.getString("Message");
                    Log.d("resp", msg);*/

        label.clear();
            /*item=l[(int)Math.round(Math.random()*(l.length-1))];

            label.add(item);*/
        String abc="";
        if (brands!=null){
            for(int i=0;i<brands.length();i++){
                try {
                    abc=brands.getString(i);
                    label.add(abc);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.d("access",abc);
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
        count=1;
        //user.clear();



        final DocumentReference docRef = db.collection("plastic").document(place);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Map<String, Object> user ;

                    user = new HashMap<>();
                    for(int i=0;i<label.size();i++)
                        user.put(label.get(i), count);
                    Log.d(TAG, "initial doc map "+user);

                    if (document.exists()) {
                        Map<String, Object> temp = document.getData();
                        Log.d(TAG, "DocumentSnapshot data: " + temp);
                        for (int i = 0; i < label.size(); i++) {
                            String lab = label.get(i);
                            if (temp.containsKey(lab)) {

                                count = (long) temp.get(lab) + 1;
                                user.put(lab, count);
                                Log.d(TAG, "count: " + count + user + lab);
                            }
                        }
                        Log.d(TAG, "DocumentSnapshot successfully updated!" + user);
                        docRef.update(user)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error updating document", e);
                                    }
                                });

                    } else {
                        Log.d(TAG, "No such document");
                        docRef.set(user);

                    }

                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }

        });

        final DocumentReference pl =db.collection("plastic").document("places");
        pl.update("names", FieldValue.arrayUnion(place));
        /*pl.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                List<String> group = (List<String>) document.get("names");

                Log.d("myTag", group.toString());

                if (!group.contains(place)){
                    group.add(place);
                }

                pl.set(group.toString())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully updated!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error updating document", e);
                            }
                        });



            }
        });*/
        Intent intent = new Intent(uploadActivity.this, showImg.class);
        //intent.putExtra("filePath", imageData);
        intent.putExtra("path", filePath.toString());
        //intent.putExtra("coord", msg);
        intent.putExtra("serverpath", msg);
        intent.putExtra("label", label);
        intent.putExtra("place", place);
        btnUpload.setEnabled(true);
        btnUpload.setVisibility(View.VISIBLE);
        findViewById(R.id.loading2).setVisibility(View.GONE);
        //findViewById(R.id.loading2).setEnabled(true););
        startActivity(intent);



    }


    /* public boolean getMyPlace() {
        // Use fields to define the data types to return.
        List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME);
        PlacesClient placesClient = createClient(Context);
// Use the builder to create a FindCurrentPlaceRequest.
        FindCurrentPlaceRequest request =
                FindCurrentPlaceRequest.builder(placeFields).build();

// Call findCurrentPlace and handle the response (first check that the user has granted permission).
        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);
            placeResponse.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FindCurrentPlaceResponse response = task.getResult();
                    for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                        Log.i("Places", String.format("Place '%s' has likelihood: %f",
                                placeLikelihood.getPlace().getName(),
                                placeLikelihood.getLikelihood()));
                    }
                } else {
                    Exception exception = task.getException();
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                    }
                }
            });
        } else {
            // A local method to request required permissions;
            // See https://developer.android.com/training/permissions/requesting
            checkLocationPermission();
        }

    }*/

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                new AlertDialog.Builder(this)
                        .setTitle(R.string.title_location_permission)
                        .setMessage(R.string.text_location_permission)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(uploadActivity.this,
                                        new String[]{ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    /*@Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        lm.requestLocationUpdates(provider, 400, 1, this);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }
*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {



                filePath = result.getUri();
                // imageUpload.setImageURI(filePath);
                imageUpload.setImageResource(android.R.color.transparent);
                Glide.with(getApplicationContext())
                        .load(filePath)
                        .into(imageUpload);

                try {
                    InputStream inputStream = getContentResolver().openInputStream(filePath);
                    Bitmap original = BitmapFactory.decodeStream(inputStream);

                    ByteArrayOutputStream out1 = new ByteArrayOutputStream();
                    original.compress(Bitmap.CompressFormat.JPEG, 20, out1);
                    bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(out1.toByteArray()));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }


        }

        /*if (requestCode==CODE_GALLERY_REQUEST &&resultCode == RESULT_OK && data != null ){

            filePath= data.getData();

            try {
                InputStream inputStream = getContentResolver().openInputStream(filePath);
                bitmap= BitmapFactory.decodeStream(inputStream);
                imageUpload.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }*/
    }


    private void findCurrentPlace() {
        if (ContextCompat.checkSelfPermission(this, ACCESS_WIFI_STATE)

                != PackageManager.PERMISSION_GRANTED

                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)

                != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(

                    this,

                    "Both ACCESS_WIFI_STATE & ACCESS_FINE_LOCATION permissions are required",

                    Toast.LENGTH_SHORT)

                    .show();
        }
        // Note that it is not possible to request a normal (non-dangerous) permission from

        // ActivityCompat.requestPermissions(), which is why the checkPermission() only checks if

        // ACCESS_FINE_LOCATION is granted. It is still possible to check whether a normal permission

        // is granted or not using ContextCompat.checkSelfPermission().

        if (checkPermission(ACCESS_FINE_LOCATION)) {

            findCurrentPlaceWithPermissions();

        }
    }



    /**

     * Fetches a list of {@link PlaceLikelihood} instances that represent the Places the user is

     * most

     * likely to be at currently.

     */

    @RequiresPermission(allOf = {ACCESS_FINE_LOCATION, ACCESS_WIFI_STATE})

    private void findCurrentPlaceWithPermissions() {

        setLoading(true);


        FindCurrentPlaceRequest currentPlaceRequest =

                FindCurrentPlaceRequest.newInstance(placeFields);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            checkPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;

        }
        //Task<FindCurrentPlaceResponse> currentPlaceTask =

        //placesClient.findCurrentPlace(currentPlaceRequest);



        /*currentPlaceTask.addOnSuccessListener(

                (response) ->

                        responseView.setText(response.toString()));



        currentPlaceTask.addOnFailureListener(

                (exception) -> {

                    exception.printStackTrace();

                    responseView.setText(exception.getMessage());

                });*/

        // Call findCurrentPlace and handle the response (first check that the user has granted permission).
        //String type = placeText.getText().toString();
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "%2C" + longitude);
        googlePlacesUrl.append("&&rankby="+"distance");
        googlePlacesUrl.append("&types=" + "train_station");
        //googlePlacesUrl.append("&fields="+"id,name");
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + apiKey);
        //https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=19.1350056%2C72.93468275&rankby=distance&type=train_station&key=AIzaSyBWjQ2A6CpemKKEcf-3JdwqtJlrNoaExOY



        StringRequest stringRequest = new StringRequest(Request.Method.GET, googlePlacesUrl.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //JSONObject reader = null;
                try {
                    googlePlacesData = new JSONObject(response);
                    if(googlePlacesData!=null){
                        JSONArray results = googlePlacesData.getJSONArray("results");
                        //String s= st[0].
                        String st="";
                        place = results.getJSONObject(0).getString("name");
                        for(int i = 0; i < results.length(); i++)
                        {
                            String place_name = results.getJSONObject(i).getString("name");
                            st=st.concat(place_name+", ");
                            //Iterate through the elements of the array i.
                            //Get thier value.
                            //Get the value for the first element and the value for the last element.
                        }
                        Log.i("Places",st );
                        responseView.setText(item+" "+count+"\n"+place);
                        setLoading(false);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "error:" + error.toString(), Toast.LENGTH_LONG).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(uploadActivity.this);
        requestQueue.add(stringRequest);




        /*Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(currentPlaceRequest);
            placeResponse.addOnCompleteListener(new OnCompleteListener<FindCurrentPlaceResponse>() {
                @Override
                public void onComplete(@NonNull Task<FindCurrentPlaceResponse> task) {

                        if (task.isSuccessful()) {
                            String st="";
                            FindCurrentPlaceResponse response = task.getResult();
                            for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                                st=st.concat(String.format("Place '%s' has likelihood: %f",
                                        placeLikelihood.getPlace().getName(),
                                        placeLikelihood.getLikelihood()));
                                Log.i("Places",String.format("Place '%s' has likelihood: %f",
                                        placeLikelihood.getPlace().getName(),
                                        placeLikelihood.getLikelihood()) );
                                responseView.setText(st);
                            }
                        } else {
                            Exception exception = task.getException();
                            if (exception instanceof ApiException) {
                                ApiException apiException = (ApiException) exception;
                                Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                            }
                        }
                    }


            } );


        placeResponse.addOnCompleteListener(new OnCompleteListener<FindCurrentPlaceResponse>() {
            @Override
            public void onComplete(@NonNull Task<FindCurrentPlaceResponse> task) {
                setLoading(false);
            }
        });
*/

        //currentPlaceTask.addOnCompleteListener(task -> setLoading(false));

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==CODE_GALLERY_REQUEST){
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent= new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent,"Select Image"),CODE_GALLERY_REQUEST);
            }else{
                Toast.makeText(getApplicationContext(),"You Don't Have Permission to Access Gallery",Toast.LENGTH_LONG).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    //AIzaSyAQcPOqTbByHzN1o5yCjDHOPhzUcDsc8NU


    private static String imageToString(Bitmap bitmap){
        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
        bitmap.compress(JPEG,100,outputStream);
        byte[] imageBytes= outputStream.toByteArray();
        String encodedImage= Base64.encodeToString(imageBytes,Base64.DEFAULT);
        return encodedImage;
    }


    private void setLoading(boolean loading) {

        findViewById(R.id.loading).setVisibility(loading ? View.VISIBLE : View.INVISIBLE);

    }

    private boolean checkPermission(String permission) {

        boolean hasPermission =

                ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;

        if (!hasPermission) {

            ActivityCompat.requestPermissions(this, new String[]{permission}, 0);

        }

        return hasPermission;

    }

    //@Override
    protected static Map<String, String> getParams() throws AuthFailureError {
        Map<String, String> params = new HashMap<>();
        imageData = imageToString(bitmap);
        params.put("image", imageData);


        return params;
    }


}
