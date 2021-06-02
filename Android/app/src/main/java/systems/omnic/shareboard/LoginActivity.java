package systems.omnic.shareboard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.AsyncQueryHandler;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class LoginActivity extends AppCompatActivity {

    private final String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
        TextView viewErrMsg = findViewById(R.id.loginViewErrMsg);

        if (username.getText().toString().equals("") || username.getText().toString().contains("[^\\w]") ||
                password.getText().toString().equals("") || password.getText().toString().contains("[^\\w]")){
            viewErrMsg.setText(getResources().getString(R.string.login_error_message));
            return;
        }

        CheckLoginTask task = new CheckLoginTask();
        task.execute(username.getText().toString(), password.getText().toString());
    }

    @Override
    public void onBackPressed(){
        Log.d(TAG, "onBackPressed: Method entered");
    }

    private class CheckLoginTask extends AsyncTask<String, Integer, String>{
        private final String TAG = CheckLoginTask.class.getSimpleName();
        private final String URL = "http://omnic-systems.com/pull_request/";

        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG, "doInBackground: Method entered");

            URL url = null;
            try {
                url = new URL(URL);

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
            if (string == null){
                Log.d(TAG, "onPostExecute: No server response");
                return;
            }

            Log.d(TAG, "onPostExecute: Content sent successfully: " + string);
            JSONArray jsonArray;
            JSONObject jsonObject;
            try {
                jsonArray = new JSONArray(string);
                jsonObject = (JSONObject) jsonArray.get(0);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}