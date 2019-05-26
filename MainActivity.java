package com.ashishgangaramani.dashboardapp;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.Transition;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.walshfernandes.dashboardapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Spinner spinner, spinner1, spinner2, spinner3, spinner4;
    View container;
    ImageView appLogo;

    Button scatterButton, barButton, pieButton, wordcloudButton, boxPlotButton;
    ImageView scatterImage, barImage, pieImage, wordCloudImage, boxPlotImage;

    ProgressBar scatter_image_progress, bar_image_progress, pie_image_progress, wordcloud_image_progress, box_plot_image_progress;

    ArrayList<String> attributesArray;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeComponents();

        Bundle extras = getIntent().getExtras();
        try {
            String imageTransitionName = extras.getString("image_transition_name");
            appLogo.setTransitionName(imageTransitionName);
        }catch (NullPointerException e){
            e.printStackTrace();;
        }
        supportStartPostponedEnterTransition();

        Transition sharedElementTransition = getWindow().getSharedElementEnterTransition();
        sharedElementTransition.addListener(
                new Transition.TransitionListener() {
                    @Override
                    public void onTransitionStart(Transition transition) {}

                    @Override
                    public void onTransitionEnd(Transition transition) {
                        progressDialog = new ProgressDialog(MainActivity.this);
                        progressDialog.setMessage("Setting up your workspace...");
                        progressDialog.show();
                        inflateSpinners();
                        initializeButtons();
                    }

                    @Override
                    public void onTransitionCancel(Transition transition) {}

                    @Override
                    public void onTransitionPause(Transition transition) {}

                    @Override
                    public void onTransitionResume(Transition transition) {}
                }
        );
    }

    private void initializeButtons() {
        scatterButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getScatterData();
                    }
                }
        );
        barButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getBarData();
                    }
                }
        );
        pieButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getPieData();
                    }
                }
        );
        wordcloudButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getWordcloudData();
                    }
                }
        );
        boxPlotButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getBoxPlotData();
                    }
                }
        );
    }

    private void getBoxPlotData() {
        String url = new Uri.Builder()
                .scheme("http")
                .encodedAuthority(getString(R.string.api_base_domain))
                .appendPath("analysis")
                .appendPath("boxplot/")
                .build().toString();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("asdfghjkl", "inside response");
                        if(response.has("detail")){
                            try {
                                JSONObject detail = response.getJSONObject("detail");
                                String url = detail.getString("image_url");
                                String absoluteUrl = "http://" + getString(R.string.api_base_domain) + url;
                                Log.i("asdfghjkl", absoluteUrl);
                                box_plot_image_progress.setVisibility(View.VISIBLE);
                                Picasso.get()
                                        .load(absoluteUrl)
                                        .fit().into(boxPlotImage,
                                        new Callback() {
                                            @Override
                                            public void onSuccess() {
                                                box_plot_image_progress.setVisibility(ViewGroup.GONE);
                                            }

                                            @Override
                                            public void onError(Exception e) {
                                                e.printStackTrace();
                                                box_plot_image_progress.setVisibility(ViewGroup.GONE);
                                                Toast.makeText(MainActivity.this, "Some Error occured", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if(error instanceof NoConnectionError){
                            // if the internet connection is slow or there is no internet connection
                            Toast.makeText(MainActivity.this, "Kindly check your Internet Connection.", Toast.LENGTH_LONG).show();
                            return;
                        }else if(error instanceof NetworkError){
                            // if there is an error in the network
                            Toast.makeText(MainActivity.this, "There was an error in making the request", Toast.LENGTH_LONG).show();
                            return;
                        }else if(error instanceof TimeoutError){
                            Toast.makeText(MainActivity.this, "The server took too long to respond, Try again later.", Toast.LENGTH_LONG).show();
                            return;
                        }
                        Toast.makeText(getApplicationContext(), "Some error occured.", Toast.LENGTH_LONG).show();
                    }
                }
        );
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private void getWordcloudData() {
        String url = new Uri.Builder()
                .scheme("http")
                .encodedAuthority(getString(R.string.api_base_domain))
                .appendPath("analysis")
                .appendPath("wordcloud/")
                .appendQueryParameter("parameter", attributesArray.get(spinner4.getSelectedItemPosition()))
                .build().toString();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("asdfghjkl", "inside response");
                        if(response.has("detail")){
                            try {
                                JSONObject detail = response.getJSONObject("detail");
                                String url = detail.getString("image_url");
                                String absoluteUrl = "http://" + getString(R.string.api_base_domain) + url;
                                Log.i("asdfghjkl", absoluteUrl);
                                wordcloud_image_progress.setVisibility(View.VISIBLE);
                                Picasso.get()
                                        .load(absoluteUrl)
                                        .fit().into(wordCloudImage,
                                        new Callback() {
                                            @Override
                                            public void onSuccess() {
                                                wordcloud_image_progress.setVisibility(ViewGroup.GONE);
                                            }

                                            @Override
                                            public void onError(Exception e) {
                                                e.printStackTrace();
                                                wordcloud_image_progress.setVisibility(ViewGroup.GONE);
                                                Toast.makeText(MainActivity.this, "Some Error occured", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if(error instanceof NoConnectionError){
                            // if the internet connection is slow or there is no internet connection
                            Toast.makeText(MainActivity.this, "Kindly check your Internet Connection.", Toast.LENGTH_LONG).show();
                            return;
                        }else if(error instanceof NetworkError){
                            // if there is an error in the network
                            Toast.makeText(MainActivity.this, "There was an error in making the request", Toast.LENGTH_LONG).show();
                            return;
                        }else if(error instanceof TimeoutError){
                            Toast.makeText(MainActivity.this, "The server took too long to respond, Try again later.", Toast.LENGTH_LONG).show();
                            return;
                        }
                        Toast.makeText(getApplicationContext(), "Some error occured.", Toast.LENGTH_LONG).show();
                    }
                }
        );
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private void getPieData() {
        String url = new Uri.Builder()
                .scheme("http")
                .encodedAuthority(getString(R.string.api_base_domain))
                .appendPath("analysis")
                .appendPath("pie/")
                .appendQueryParameter("parameter", attributesArray.get(spinner3.getSelectedItemPosition()))
                .build().toString();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("asdfghjkl", "inside response");
                        if(response.has("detail")){
                            try {
                                JSONObject detail = response.getJSONObject("detail");
                                String url = detail.getString("image_url");
                                String absoluteUrl = "http://" + getString(R.string.api_base_domain) + url;
                                Log.i("asdfghjkl", absoluteUrl);
                                pie_image_progress.setVisibility(View.VISIBLE);
                                Picasso.get()
                                        .load(absoluteUrl)
                                        .fit().into(pieImage,
                                        new Callback() {
                                            @Override
                                            public void onSuccess() {
                                                pie_image_progress.setVisibility(ViewGroup.GONE);
                                            }

                                            @Override
                                            public void onError(Exception e) {
                                                e.printStackTrace();
                                                pie_image_progress.setVisibility(ViewGroup.GONE);
                                                Toast.makeText(MainActivity.this, "Some Error occured", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if(error instanceof NoConnectionError){
                            // if the internet connection is slow or there is no internet connection
                            Toast.makeText(MainActivity.this, "Kindly check your Internet Connection.", Toast.LENGTH_LONG).show();
                            return;
                        }else if(error instanceof NetworkError){
                            // if there is an error in the network
                            Toast.makeText(MainActivity.this, "There was an error in making the request", Toast.LENGTH_LONG).show();
                            return;
                        }else if(error instanceof TimeoutError){
                            Toast.makeText(MainActivity.this, "The server took too long to respond, Try again later.", Toast.LENGTH_LONG).show();
                            return;
                        }
                        Toast.makeText(getApplicationContext(), "Some error occured.", Toast.LENGTH_LONG).show();
                    }
                }
        );
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private void getBarData() {
        String url = new Uri.Builder()
                .scheme("http")
                .encodedAuthority(getString(R.string.api_base_domain))
                .appendPath("analysis")
                .appendPath("bar/")
                .appendQueryParameter("parameter", attributesArray.get(spinner2.getSelectedItemPosition()))
                .build().toString();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("asdfghjkl", "inside response");
                        if(response.has("detail")){
                            try {
                                JSONObject detail = response.getJSONObject("detail");
                                String url = detail.getString("image_url");
                                String absoluteUrl = "http://" + getString(R.string.api_base_domain) + url;
                                Log.i("asdfghjkl", absoluteUrl);
                                bar_image_progress.setVisibility(View.VISIBLE);
                                Picasso.get()
                                        .load(absoluteUrl)
                                        .fit().into(barImage,
                                        new Callback() {
                                            @Override
                                            public void onSuccess() {
                                                bar_image_progress.setVisibility(ViewGroup.GONE);
                                            }

                                            @Override
                                            public void onError(Exception e) {
                                                e.printStackTrace();
                                                bar_image_progress.setVisibility(ViewGroup.GONE);
                                                Toast.makeText(MainActivity.this, "Some Error occured", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if(error instanceof NoConnectionError){
                            // if the internet connection is slow or there is no internet connection
                            Toast.makeText(MainActivity.this, "Kindly check your Internet Connection.", Toast.LENGTH_LONG).show();
                            return;
                        }else if(error instanceof NetworkError){
                            // if there is an error in the network
                            Toast.makeText(MainActivity.this, "There was an error in making the request", Toast.LENGTH_LONG).show();
                            return;
                        }else if(error instanceof TimeoutError){
                            Toast.makeText(MainActivity.this, "The server took too long to respond, Try again later.", Toast.LENGTH_LONG).show();
                            return;
                        }
                        Toast.makeText(getApplicationContext(), "Some error occured.", Toast.LENGTH_LONG).show();
                    }
                }
        );
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private void getScatterData() {
        String url = new Uri.Builder()
                .scheme("http")
                .encodedAuthority(getString(R.string.api_base_domain))
                .appendPath("analysis")
                .appendPath("scatter/")
                .appendQueryParameter("parameter1", attributesArray.get(spinner.getSelectedItemPosition()))
                .appendQueryParameter("parameter2", attributesArray.get(spinner1.getSelectedItemPosition()))
                .build().toString();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("asdfghjkl", "inside response");
                        if(response.has("detail")){
                            try {
                                JSONObject detail = response.getJSONObject("detail");
                                String url = detail.getString("image_url");
                                String absoluteUrl = "http://" + getString(R.string.api_base_domain) + url;
                                Log.i("asdfghjkl", absoluteUrl);
                                scatter_image_progress.setVisibility(View.VISIBLE);
                                Picasso.get()
                                        .load(absoluteUrl)
                                        .fit().into(scatterImage,
                                        new Callback() {
                                            @Override
                                            public void onSuccess() {
                                                scatter_image_progress.setVisibility(ViewGroup.GONE);
                                            }

                                            @Override
                                            public void onError(Exception e) {
                                                e.printStackTrace();
                                                scatter_image_progress.setVisibility(ViewGroup.GONE);
                                                Toast.makeText(MainActivity.this, "Some Error occured", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if(error instanceof NoConnectionError){
                            // if the internet connection is slow or there is no internet connection
                            Toast.makeText(MainActivity.this, "Kindly check your Internet Connection.", Toast.LENGTH_LONG).show();
                            return;
                        }else if(error instanceof NetworkError){
                            // if there is an error in the network
                            Toast.makeText(MainActivity.this, "There was an error in making the request", Toast.LENGTH_LONG).show();
                            return;
                        }else if(error instanceof TimeoutError){
                            Toast.makeText(MainActivity.this, "The server took too long to respond, Try again later.", Toast.LENGTH_LONG).show();
                            return;
                        }
                        Toast.makeText(getApplicationContext(), "Some error occured.", Toast.LENGTH_LONG).show();
                    }
                }
        );
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private void inflateSpinners() {
        String url = new Uri.Builder()
                .scheme("http")
                .encodedAuthority(getString(R.string.api_base_domain))
                .appendPath("analysis")
                .appendPath("attributes").build().toString();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
                        Log.i("asdfghjkl", "inside response");
                        if(response.has("detail")){
                            try {
                                JSONObject detail = response.getJSONObject("detail");
                                JSONArray attributes = detail.getJSONArray("attributes");
                                attributesArray = new ArrayList<>();
                                for (int i = 0; i < attributes.length(); i++) {
                                    attributesArray.add(attributes.getString(i));
                                }
                                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                                        MainActivity.this, android.R.layout.simple_spinner_item, attributesArray
                                );
                                spinner.setAdapter(arrayAdapter);
                                spinner1.setAdapter(arrayAdapter);
                                spinner2.setAdapter(arrayAdapter);
                                spinner3.setAdapter(arrayAdapter);
                                spinner4.setAdapter(arrayAdapter);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        if(error instanceof NoConnectionError){
                            // if the internet connection is slow or there is no internet connection
                            Toast.makeText(MainActivity.this, "Kindly check your Internet Connection.", Toast.LENGTH_LONG).show();
                            return;
                        }else if(error instanceof NetworkError){
                            // if there is an error in the network
                            Toast.makeText(MainActivity.this, "There was an error in making the request", Toast.LENGTH_LONG).show();
                            return;
                        }else if(error instanceof TimeoutError){
                            Toast.makeText(MainActivity.this, "The server took too long to respond, Try again later.", Toast.LENGTH_LONG).show();
                            return;
                        }
                        Toast.makeText(getApplicationContext(), "Some error occured.", Toast.LENGTH_LONG).show();
                    }
                }
        );
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private void initializeComponents() {
        appLogo = findViewById(R.id.app_logo);
        container = findViewById(R.id.outermost_container);
        spinner = findViewById(R.id.attribute_selector);
        spinner1 = findViewById(R.id.attribute_selector1);
        spinner2 = findViewById(R.id.attribute_selector2);
        spinner3 = findViewById(R.id.attribute_selector3);
        spinner4 = findViewById(R.id.attribute_selector4);

        scatterButton = findViewById(R.id.scatter_button);
        scatterImage = findViewById(R.id.scatter_image);
        scatter_image_progress = findViewById(R.id.scatter_image_progress);

        barButton = findViewById(R.id.bar_button);
        barImage = findViewById(R.id.bar_image);
        bar_image_progress = findViewById(R.id.bar_image_progress);

        pieButton = findViewById(R.id.pie_button);
        pieImage = findViewById(R.id.pie_image);
        pie_image_progress = findViewById(R.id.pie_image_progress);

        wordcloudButton = findViewById(R.id.wordcloud_button);
        wordCloudImage = findViewById(R.id.wordcloud_image);
        wordcloud_image_progress = findViewById(R.id.wordcloud_image_progress);

        boxPlotButton = findViewById(R.id.box_plot_button);
        boxPlotImage = findViewById(R.id.box_plot_image);
        box_plot_image_progress = findViewById(R.id.box_plot_progress);
    }

}
