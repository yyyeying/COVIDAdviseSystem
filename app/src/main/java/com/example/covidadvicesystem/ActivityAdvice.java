package com.example.covidadvicesystem;

import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Random;


public class ActivityAdvice extends AppCompatActivity {

    private TextView textCovidCases;
    private TextView textCovidAdvice;
    private TextView textTips;
    private ScrollView scrollWidgets;
    private RelativeLayout layoutWidgets;
    private String covidHost = "https://covid19-api.org/api";

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advice);
        scrollWidgets = findViewById(R.id.scrollView_widgets);
        layoutWidgets = findViewById(R.id.layout_widgets);
        textCovidCases = findViewById(R.id.text_covid_cases);
        textCovidAdvice = findViewById(R.id.text_covid_advice);
        textTips = findViewById((R.id.text_tips));
        //int topMargin = getHeaderTopMargin();
        //Log.d("topmargin", String.valueOf(topMargin));
        refreshAll();
    }

    private int getHeaderTopMargin() {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layoutWidgets.getLayoutParams();
        return params.topMargin;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void refreshAll(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(System.currentTimeMillis());
        String stringDate = simpleDateFormat.format(date);
        Log.d("date", stringDate);
        getCurrentCovidCases("US", stringDate);
        getDiffCovidCases("US", stringDate);
        getTips();
    }

    private void getCurrentCovidCases(String region, String date){
        RequestQueue queue = Volley.newRequestQueue(ActivityAdvice.this);
        String covid_cases_url = covidHost + "/status/" + region + "?date=" + date;
        Log.d("new_cases", covid_cases_url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, covid_cases_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.d("new_cases", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            textCovidCases.setText(jsonObject.optString("cases"));
                        }catch (JSONException e){
                            e.printStackTrace();
                            Toast.makeText(ActivityAdvice.this, "Network Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("cases",error.toString());
            }
        });
        queue.add(stringRequest);
        return;
    }

    private void getDiffCovidCases(String region, String date){
        RequestQueue queue = Volley.newRequestQueue(ActivityAdvice.this);
        String covid_cases_url = covidHost + "/diff/" + region + "?date=" + date;
        Log.d("cases", covid_cases_url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, covid_cases_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.d("cases", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String strNewCases = jsonObject.optString("new_cases");
                            int intNewCases = Integer.parseInt(strNewCases);
                            String covidAdvice = "There are " + strNewCases + " new cases yesterday.";
                            textCovidAdvice.setText(covidAdvice);
                            if (intNewCases>0 && intNewCases<=100){
                                covidAdvice += "\nPlease wear mask when getting out.";
                            } else if (intNewCases > 100) {
                                covidAdvice += "\nYou'd better stay at home!";
                            } else if (intNewCases == 0){
                                covidAdvice += "It is a nice day.";
                            }
                            textCovidAdvice.setText(covidAdvice);
                        }catch (JSONException e){
                            e.printStackTrace();
                            Toast.makeText(ActivityAdvice.this, "Network Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("cases",error.toString());
            }
        });
        queue.add(stringRequest);
        return;
    }

    private void getTips(){
        String[] listTips = {"Washing your hands actively can be effective in preventing viruses.",
        "When you are indoors, please always open the window to ventilate.",
        "Please wear a mask when you go out.",
        "When you cough or sneeze, cover your mouth and nose.",
        "When cooking, the boards that handle raw food should be separated from cooked food.",
        "When you are queuing at the supermarket cashier, keep at least 1 meter away from the customer in front of you.",
        "When you are taking the elevator, please keep your distance from others.",
        "Please do not take off your mask when taking public transportation, as this will increase the risk of infection."};
        int tipIndex = (int)(listTips.length * Math.random());
        textTips.setText(listTips[tipIndex]);
    }

}