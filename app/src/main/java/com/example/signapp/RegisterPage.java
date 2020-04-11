package com.example.signapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

public class RegisterPage extends AppCompatActivity {

    private static final String url = "jdbc:mysql://db4free.net:3306/masterevents";
    private static final String user = "masterevents";
    private static final String pass = "Tinicu96";
    boolean correctData = false;

    Button finishReg;
    EditText regFirstName, regLastName, regEmail, regPass, regConfPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        Log.i("RegisterPage","Registration started");

        finishReg = (Button)findViewById(R.id.finishRegButton);
        regFirstName = (EditText)findViewById(R.id.firstNameET);
        regLastName = (EditText)findViewById(R.id.lastNameET);
        regEmail = (EditText)findViewById(R.id.regEmailET);
        regPass = (EditText)findViewById(R.id.passET);
        regConfPass = (EditText)findViewById(R.id.confirmPassET);

        finishReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("RegisterPage","Finish Reg Button pressed");

                if(regPass.getText().toString().equals(regConfPass.getText().toString())) {

                    Log.d("RegisterPage","Passwords match");

                    RegisterPage.ConnectMySql connectMySql = new RegisterPage.ConnectMySql(regEmail.getText().toString(), regPass.getText().toString(),
                            regFirstName.getText().toString(), regLastName.getText().toString());

                    connectMySql.execute("");

                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(RegisterPage.this, LoginPage.class);
        startActivity(intent);
    }

    private class ConnectMySql extends AsyncTask<String, Void, String> {

        String res = "";
        String email;
        String password;


        boolean alreadyExist = false;


        String firstName, lastName;



        ConnectMySql(String email, String password, String firstName, String lastName){
            this.email = email;
            this.password = password;
            this.lastName = lastName;
            this.firstName = firstName;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(RegisterPage.this, "Please wait...", Toast.LENGTH_SHORT)
                    .show();

            Log.i("RegisterPage","Please wait...");

        }

        @Override
        protected String doInBackground(String... params) {

            Log.d("RegisterPage","Params = " + email);

            ResultSet rs;

            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(url, user, pass);
                Log.i("RegisterPage","SuccessfulConnection");

                String result = "SuccessfulConnection,";
                Statement st = con.createStatement();


                    //insert new user

                    rs = st.executeQuery("select id from users where email = " + "\'" + email + "\'");
                    ResultSetMetaData rsmd = rs.getMetaData();




                    Log.i("RegisterPage","EmailCheck: " + rs);

                    if(rs.next() == true ) {

                        result = "Email already used";
                        alreadyExist = true;
                        Log.i("RegisterPage", result);

                        res = result;

                    }else {

                        String sqlQuery = "insert into users(last_name,first_name,email,pass,type) " +
                                "values(\'"+ lastName + "\',\'" + firstName + "\',\'" + email + "\',\'" + password +"\',2)";

                        int succesfulUpdate = st.executeUpdate( sqlQuery );



                        Log.i("RegisterPage", "Query to insert result: " + succesfulUpdate);

                        if(succesfulUpdate!=0)
                         result += "Account created" ;
                        else result += "Account couldn't be created";

                        res = result;
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


            if(result.contains("Email")) {
                Toast.makeText(RegisterPage.this, result, Toast.LENGTH_SHORT);
                return;
            }
            else{

                Toast.makeText(RegisterPage.this, result, Toast.LENGTH_SHORT);

            }

            if(alreadyExist == false) {
                //Shared preferences to store app based variable in key-pair format
                SharedPreferences sharedPref = getSharedPreferences("MySharedPref", MODE_PRIVATE);
                SharedPreferences.Editor myEdit = sharedPref.edit();
                myEdit.putString("currentUserEmail", email);
                myEdit.putString("currentUserType", "2");
                myEdit.commit();


                Intent intent = new Intent(RegisterPage.this, MainPage.class);
                intent.putExtra("FIRST_NAME", firstName);
                intent.putExtra("LAST_NAME", lastName);
                startActivity(intent);
            }

        }
    }
}
