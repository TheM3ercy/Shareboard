package systems.omnic.shareboard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.AsyncQueryHandler;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class LoginActivity extends AppCompatActivity {

    private final String TAG = LoginActivity.class.getSimpleName();
    private TextView viewErrorMessage = null;
    private CheckBox rememberMe = null;
    private String username = "";
    private MainActivity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = (MainActivity) getIntent().getExtras().getSerializable("context");
        getSupportActionBar().setTitle("Shareborad: Login");

        rememberMe = findViewById(R.id.loginRemCheckbox);
        rememberMe.setActivated(DataContainer.getInstance().isStayLoggedIn());
        ImageView iconView = findViewById(R.id.loginIconView);
        Button register = findViewById(R.id.login_register_btn);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onCreate: Register Button selected");
                TextView viewErrMsg = findViewById(R.id.loginViewErrMsg);
                viewErrMsg.setText("");
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        Button login = findViewById(R.id.login_login_btn);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onCreate: Login Button selected");
                login();
            }
        });
    }

    public void login(){
        Log.d(TAG, "login: Method entered");

        EditText username = findViewById(R.id.loginEditUser);
        EditText password = findViewById(R.id.loginEditPw);
        viewErrorMessage = findViewById(R.id.loginViewErrMsg);
        DataContainer.getInstance().setStayLoggedIn(rememberMe.isChecked());

        CheckLoginTask task = new CheckLoginTask();
        this.username = username.getText().toString();
        task.execute(this.username, password.getText().toString());
    }



    @Override
    public void onBackPressed(){
        Log.d(TAG, "onBackPressed: Method entered");
    }

    private class CheckLoginTask extends AsyncTask<String, Integer, String>{
        private final String TAG = CheckLoginTask.class.getSimpleName();

        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG, "doInBackground: Method entered");

            String urlString = "http://omnic-systems.com/shareboard/pull_key/?username=" + strings[0] + "&password=" + strings[1];
            URL url = null;
            try {
                url = new URL(urlString);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/json");

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                    Log.d(TAG, "doInBackground: Connection ok");

                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line = br.readLine();
                    while (line != null) {
                        sb.append(line);
                        line = br.readLine();
                    }

                    return sb.toString();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void onPostExecute(String string){
            Log.d(TAG, "onPostExecute: Method entered");
            if (string == null || string.equals("")){
                Log.d(TAG, "onPostExecute: " + getResources().getString(R.string.no_response));
                viewErrorMessage.setText("No server response!");
                return;
            }else if (string.contains("incorrect") && !string.contains("[")){
                Log.d(TAG, "onPostExecute: Wrong login params:" + string);
                viewErrorMessage.setText("Wrong username or password!");
                return;
            }
            Log.d(TAG, "onPostExecute: Recieved user string:" + string);

            String userString = null;
            JSONArray jsonArray;
            JSONObject jsonObject = null;
            try {
                 jsonArray = new JSONArray(string);
                 jsonObject = (JSONObject) jsonArray.get(0);
                 userString = jsonObject.getString("user_string");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (userString == null || userString.equals("")){
                Log.d(TAG, "onPostExecute: No user string returned!");
                viewErrorMessage.setText("Login failed!");
                return;
            }

            DataContainer.getInstance().setUsername(username);
            DataContainer.getInstance().setUserString(userString);
            DataContainer.getInstance().saveConf(LoginActivity.this);
            context.init(LoginActivity.this);
            finish();
        }
    }
}