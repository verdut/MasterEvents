package com.example.signapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class RegisterEventPage extends AppCompatActivity {

    private static final String url = "jdbc:mysql://db4free.net:3306/masterevents";
    private static final String user = "masterevents";
    private static final String pass = "Tinicu96";

    final Calendar myCalendar = Calendar.getInstance();

    ImageButton startDate, endDate;
    EditText eventNameET, eventDescriptionET, capacityET;
    EditText startDateET, endDateET;

    Button finishEventReg;

    TextView errorTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_event_page);

        startDate = (ImageButton)findViewById(R.id.imageButton);
        endDate = (ImageButton)findViewById(R.id.imageButton2);

        startDateET = (EditText)findViewById(R.id.eventStartDateET);
        endDateET = (EditText)findViewById(R.id.eventEndDateET);
        eventNameET = (EditText)findViewById(R.id.eventNameET);
        eventDescriptionET = (EditText)findViewById(R.id.eventDescriptionET);
        capacityET = (EditText)findViewById(R.id.eventNoOfPlacesET);

        errorTV = (TextView)findViewById(R.id.eventErrorTV);
        errorTV.setText("");

        finishEventReg = (Button)findViewById(R.id.finishRegEventButton);

        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateLabel(startDateET);
                    }

                };

                new DatePickerDialog(RegisterEventPage.this,R.style.MySpinnerDatePickerStyle, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();


            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateLabel(endDateET);
                    }

                };

                new DatePickerDialog(RegisterEventPage.this,R.style.MySpinnerDatePickerStyle, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        finishEventReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RegisterEventPage.ConnectMySql connectMySql = new RegisterEventPage.ConnectMySql(
                                                                                        eventNameET.getText().toString(),
                                                                                        eventDescriptionET.getText().toString(),
                                                                                        startDateET.getText().toString(),
                                                                                        endDateET.getText().toString(),
                                                                                        Integer.parseInt(capacityET.getText().toString()));

                connectMySql.execute("");
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(RegisterEventPage.this, MainPage.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        errorTV.setText("");
    }

    private void updateLabel(EditText et) {

        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        et.setText(sdf.format(myCalendar.getTime()));
    }

    private class ConnectMySql extends AsyncTask<String, Void, String> {

        String res = "";
        String eventName;
        String eventDescription;
        String eventStartDate;
        String eventEndDate;
        int eventVacantPlaces;


        boolean alreadyExist = false;


        ConnectMySql(String name, String description, String startDate, String endDate, int vacantPlaces){
            this.eventName = name;
            this.eventDescription = description;
            this.eventStartDate = startDate;
            this.eventEndDate = endDate;
            this.eventVacantPlaces = vacantPlaces;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(RegisterEventPage.this, "Please wait...", Toast.LENGTH_SHORT)
                    .show();

            Log.i("RegisterPage","Please wait...");

        }

        @Override
        protected String doInBackground(String... params) {


            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(url, user, pass);
                Log.i("RegisterPage","SuccessfulConnection");

                String result = "SuccessfulConnection,";
                Statement st = con.createStatement();


                //insert new event
                String sqlQuery = "insert into events(name,description, start, end, capacity) values " +
                                  "(\'" + eventName + "\',\'" + eventDescription + "\',\'" + eventStartDate +
                                  "\',\'" + eventEndDate + "\'," + eventVacantPlaces + ")";

                int succesfulUpdate =  st.executeUpdate(sqlQuery);


                Log.i("RegisterEventPage", "Query to insert result: " + succesfulUpdate);

                if(succesfulUpdate!=0)
                    result += "Success" ;
                else result += "Fail";

                res = result;


            } catch (Exception e) {
                e.printStackTrace();
                res = e.toString();
            }

            return res;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i("LoginPage","Resultxx2="+result);



            if(result.contains("Success")) {

                Intent intent = new Intent(RegisterEventPage.this, MainPage.class);
                startActivity(intent);

            }else{
                Log.d("RegisterEventPage","Event already exists");
                errorTV.setText("Event already exists!");

            }

        }
    }
}
