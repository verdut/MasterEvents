package com.example.signapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MainPage extends AppCompatActivity {

    private static final String url = "jdbc:mysql://db4free.net:3306/masterevents";
    private static final String user = "masterevents";
    private static final String pass = "Tinicu96";


    MutableLiveData<Boolean> readyToPopulate = new MutableLiveData<>();

    Toolbar myToolbar;
    LinearLayout linearLayout ;
    Button addEventButton;
    ImageButton refreshEventsButton;

    List<Events> currentEvents = new ArrayList<MainPage.Events>();


    SharedPreferences sharedPref;

    String currentUserType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        readyToPopulate.setValue(false);

       sharedPref = getSharedPreferences("MySharedPref", MODE_PRIVATE);
       currentUserType =  sharedPref.getString("currentUserType","");

        //Log.d("MainPage","A"+currentUserType+"B");

        if(currentUserType.equals("1"))
        {
            setContentView(R.layout.activity_main_page_admin);

            linearLayout = findViewById(R.id.linearLayoutAdmin);
            addEventButton = (Button)findViewById(R.id.addEventButton);
            refreshEventsButton = (ImageButton)findViewById(R.id.refreshEventsButton);
            myToolbar = (Toolbar)findViewById(R.id.myToolbar);


            addEventButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("Main Page admin", "Add event button pressed");

                    Intent intent = new Intent(MainPage.this,RegisterEventPage.class);
                    startActivity(intent);
                }
            });
        }

        else
        {
            setContentView(R.layout.activity_main_page_user);

            linearLayout = findViewById(R.id.linearLayoutUser);
            refreshEventsButton = (ImageButton)findViewById(R.id.refreshEventsButtonUser);
        }

        //setSupportActionBar(myToolbar);

        readyToPopulate.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                runOnUiThread(populateEvents);
            }
        });

        refreshEventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               //run database again
                MainPage.ConnectMySql connectMySql = new MainPage.ConnectMySql();
                connectMySql.execute("");


            }
        });

        Log.d("Main Page", "Inainte sa verific baza de date");
        //populate page
        MainPage.ConnectMySql connectMySql = new MainPage.ConnectMySql();
        connectMySql.execute("");


        Log.d("Main Page","Am dat execute la sql");




    }

    private Runnable populateEvents = new Runnable() {
        @Override
        public void run() {

            Log.d("Main Page","Number of events: " + currentEvents.size());

            linearLayout.removeAllViews();

            int i ;

            for( i = 0 ; i< currentEvents.size();i++)
            {
                LinearLayout frameLayout = new LinearLayout(MainPage.this);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                );


                params.setMargins(0,30,0,30);
                frameLayout.setLayoutParams(params);
                frameLayout.setBackgroundColor(getResources().getColor(R.color.secondaryColor));
                frameLayout.setOrientation(LinearLayout.VERTICAL);

                linearLayout.addView(frameLayout);

                params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        Gravity.CENTER
                );

                TextView nameTV = new TextView(MainPage.this);
                params.setMargins(0,10,0,10);
                params.gravity = Gravity.CENTER_HORIZONTAL;
                nameTV.setTextSize(24);
                nameTV.setTypeface(null, Typeface.BOLD);
                nameTV.setText(currentEvents.get(i).getName());
                nameTV.setTextColor(getResources().getColor(R.color.colorAccent));

                frameLayout.addView(nameTV,params);
                nameTV.setGravity(Gravity.CENTER_HORIZONTAL);


                params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );


                TextView descriptionTV = new TextView(MainPage.this);
                params.setMargins(0,10,0,10);
                descriptionTV.setLayoutParams(params);
                descriptionTV.setTextSize(20);
                descriptionTV.setText(currentEvents.get(i).getDescription());
                descriptionTV.setTextColor(getResources().getColor(R.color.colorAccent));

                frameLayout.addView(descriptionTV);

                TextView startDateTV = new TextView(MainPage.this);
                params.setMargins(0,10,0,10);
                startDateTV.setLayoutParams(params);
                startDateTV.setTextSize(20);
                startDateTV.setText("Start date: " + currentEvents.get(i).getStartDate());
                startDateTV.setTextColor(getResources().getColor(R.color.colorAccent));

                frameLayout.addView(startDateTV);

                TextView endDateTV = new TextView(MainPage.this);
                params.setMargins(0,10,0,10);
                endDateTV.setLayoutParams(params);
                endDateTV.setTextSize(20);
                endDateTV.setText("End date: " + currentEvents.get(i).getEndDate());
                endDateTV.setTextColor(getResources().getColor(R.color.colorAccent));

                frameLayout.addView(endDateTV);

                TextView capacityTV = new TextView(MainPage.this);
                params.setMargins(0,10,0,10);
                capacityTV.setLayoutParams(params);
                capacityTV.setTextSize(20);
                capacityTV.setText("Vacant places: " + currentEvents.get(i).getCapacity());
                capacityTV.setTextColor(getResources().getColor(R.color.colorAccent));

                frameLayout.addView(capacityTV);

                frameLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Log.d("Main Page","Am dat click pe evenimentul " + nameTV.getText().toString() );

                    }
                });

            }
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(MainPage.this, LoginPage.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //populate page
        Log.d("Main Page", "On resume called");


    }

    private class ConnectMySql extends AsyncTask<String, Void, String> {

         int totalNumberOfEvents;
         String res = "";


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Log.i("LoginPage","Loading events...");

        }

        @Override
        protected String doInBackground(String... params) {



            ResultSet rs;

            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(url, user, pass);
                Log.i("LoginPage","SuccessfulConnection");

                String result = "SuccessfulConnection,";
                Statement st = con.createStatement();


                rs = st.executeQuery("select count(*) as total from (select * from events) as totalUsers");

                ResultSetMetaData rsmd = rs.getMetaData();
                rs.next();

                totalNumberOfEvents = rs.getInt(1);

                Log.d("Main Page", "Total No of Events " + totalNumberOfEvents);

                if(totalNumberOfEvents!=0)
                {
                    rs = st.executeQuery("select name, description, start, end, capacity from events ");

                    currentEvents.clear();

                    while(rs.next()){
                        res+= " Event ";
                        String eventName = rs.getString(1).toString();
                        String eventDescription = rs.getString(2).toString();
                        String eventStartDate = rs.getString(3).toString();
                        String eventEndDate = rs.getString(4).toString();
                        String eventCapacity = rs.getString(5).toString();

                        currentEvents.add(new Events(eventName,eventDescription,eventStartDate,eventEndDate,eventCapacity));

                    }

                }else{

                    res = "no_events";
                }



            } catch (Exception e) {
                e.printStackTrace();
                res = e.toString();
            }

            return res;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i("LoginPage","Resultxx2="+result);
            readyToPopulate.setValue(!readyToPopulate.getValue());




        }


    }

    private class Events {

       private String name, description, startDate, endDate, capacity;

        Events(String name,String description, String startDate, String endDate, String capacitiy){

            this.name = name;
            this.description = description;
            this.startDate = startDate;
            this.endDate = endDate;
            this.capacity = capacitiy;
        }

        public String getName(){
            return this.name;
        }

        public String getDescription(){
            return this.description;
        }

        public String getStartDate(){
            return this.startDate;
        }
        public String getEndDate(){
            return this.endDate;
        }

        public String getCapacity(){
            return this.capacity;
        }


    }
}
