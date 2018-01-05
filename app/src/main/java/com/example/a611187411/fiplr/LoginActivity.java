package com.example.a611187411.fiplr;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class LoginActivity extends AppCompatActivity {

    ProgressDialog dialog;
    EditText usrtext, pwdtext;
    private String username, password, answer;
    AsyncTask<String,String,String> resp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        usrtext = (EditText) findViewById(R.id.login_username);
        pwdtext = (EditText) findViewById(R.id.login_password);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    public void AsyncTaskInit(View view) {
        username = usrtext.getText().toString();
        password = pwdtext.getText().toString();
        resp=new Async().execute(username,password);
        dialog = ProgressDialog.show(LoginActivity.this, "",
                "Validating user...", true);
    }

    public void gotoHome(String res) {
        if (res!=null){
            JSONObject obj;
            try {
                obj = new JSONObject(res);
                if (obj.getString("status").equalsIgnoreCase("Failed")) {
                    TextView error = (TextView) findViewById(R.id.login_errormsg);
                    error.setText(obj.getString("message"));
                }
                else {
                    Intent intent = new Intent(this, HomeActivity.class);
                    intent.putExtra("userid", obj.getString("userid"));
                    startActivity(intent);
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        else {
            TextView error = (TextView) findViewById(R.id.login_errormsg);
            error.setText("Please fill in both fields!");
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class Async extends AsyncTask<String,String,String> {

        @SuppressLint("NewApi")
        @Override
        protected String doInBackground(String... params) {
            try{
                URL url = new URL("http://10.32.36.213/rest/login");
                String urlParameters  = "username="+username+"&password="+password;
                byte[] postData       = urlParameters.getBytes( StandardCharsets.UTF_8 );
                int    postDataLength = postData.length;
                HttpURLConnection conn= (HttpURLConnection) url.openConnection();
                conn.setDoOutput( true );
                conn.setInstanceFollowRedirects( false );
                conn.setRequestMethod( "POST" );
                conn.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty( "charset", "utf-8");
                conn.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
                conn.setUseCaches( false );
                try( DataOutputStream wr = new DataOutputStream( conn.getOutputStream())) {
                    wr.write( postData );
                }
                //Execute HTTP Post Request

                int responseCode = conn.getResponseCode();
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                answer=response.toString();
            }
            catch(Exception e){

                System.out.println("Exception : " + e.getMessage());
            }
            return answer;
        }



        protected void onPostExecute(String result){
            dialog.dismiss();
            gotoHome(result);
        }

    }

}
