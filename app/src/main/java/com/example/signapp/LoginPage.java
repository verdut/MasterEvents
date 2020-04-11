package com.example.signapp;

import androidx.appcompat.app.AppCompatActivity;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

public class LoginPage extends AppCompatActivity {

    private static final String url = "jdbc:mysql://db4free.net:3306/masterevents";
    private static final String user = "masterevents";
    private static final String pass = "Tinicu96";
    boolean correctData = false;

    Button loginButton;
    Button registerButton;
    EditText emailEditText, passwordEditText;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        Log.i("LoginPage","App started");

        loginButton = (Button)findViewById(R.id.loginButton);
        registerButton = (Button)findViewById(R.id.registerButton);
        emailEditText = (EditText)findViewById(R.id.emailET);
        passwordEditText = (EditText)findViewById(R.id.passwordET);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ConnectMySql connectMySql = new ConnectMySql(emailEditText.getText().toString(), passwordEditText.getText().toString() );
                connectMySql.execute("");
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LoginPage.this,RegisterPage.class);
                startActivity(intent);

            }
        });



    }

    private class ConnectMySql extends AsyncTask<String, Void, String> {

        String res = "";
        String email;
        String password;
        int type;

        String firstName, lastName;

        ConnectMySql(String email, String password){
            this.email = email;
            this.password = password;

        }



        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(LoginPage.this, "Please wait...", Toast.LENGTH_SHORT)
                    .show();

            Log.i("LoginPage","Please wait...");

        }

        @Override
        protected String doInBackground(String... params) {

            Log.d("LoginPage","Params = " + email);

            ResultSet rs;

            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(url, user, pass);
                Log.i("LoginPage","SuccessfulConnection");

                String result = "SuccessfulConnection,";
                Statement st = con.createStatement();


                    rs = st.executeQuery("select pass, first_name, last_name, type from users where email = " + "\'" + email + "\'");
                    ResultSetMetaData rsmd = rs.getMetaData();
                    rs.next();
                    result += rs.getString(1).toString() + "," + rs.getString(2).toString() + "," + rs.getString(3).toString();
                    res = result;

                    type = rs.getInt(4);







            } catch (Exception e) {
                e.printStackTrace();
                res = e.toString();
            }

            return res;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i("LoginPage","Resultxx2="+result);




                String[] separated = result.split(",");

                Log.d("LoginPage", "Separated items are: " + separated[1] + " " + separated[2] + " " + separated[3]);
                Log.d("LoginPage", "Provided password is:" + separated[0]);

                if (separated[1].equals(password)) {

                    Log.d("LoginPage", "comparation is true");

                    correctData = true;

                    firstName = separated[2];
                    lastName = separated[3];


                }



            //Shared preferences to store app based variable in key-pair format
            SharedPreferences sharedPref = getSharedPreferences("MySharedPref", MODE_PRIVATE);
            SharedPreferences.Editor myEdit = sharedPref.edit();
            myEdit.putString("currentUserEmail", email);
            myEdit.putString("currentUserType", type + "");
            myEdit.commit();



            Intent intent = new Intent(LoginPage.this, MainPage.class);
            intent.putExtra("FIRST_NAME", firstName);
            intent.putExtra("LAST_NAME", lastName);
            startActivity(intent);

        }
    }
}


