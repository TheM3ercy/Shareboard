package systems.omnic.shareboard;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final String TAG = MainActivity.class.getSimpleName();
    private List<String> content = new ArrayList<>();
    private boolean loggedIn = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Method entered");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!loggedIn) startLoginActivity();

        ListView listView = findViewById(R.id.mainListView);
        listView.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, content));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PostNoteTask task = new PostNoteTask();
                task.execute(content.get(position));
            }
        });

        FloatingActionButton floatingActionButton = findViewById(R.id.main_add_fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onCreate: " + floatingActionButton.getContentDescription() + " selected");
                showAddAlert();
            }
        });

    }


    public void startLoginActivity(){
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    public void showAddAlert(){
        final EditText input = new EditText(MainActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Add new Entry")
                .setView(input)
                .setPositiveButton(getResources().getText(R.string.add), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        content.add(input.getText().toString());
                    }
                })
                .setNegativeButton(getResources().getText(R.string.cancel), null).show();
    }

    private class PostNoteTask extends AsyncTask<String, Integer, String> {
        private final String TAG = MainActivity.PostNoteTask.class.getSimpleName();
        private String urlString = "http://omnic-systems.com/get_request/?content=";

        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG, "doInBackground: Method entered");
            urlString += strings[0];
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
            if (string == null){
                Log.d(TAG, "onPostExecute: No server response");
                return;
            } else if (string.contains("Post recieved")){
                Log.d(TAG, "onPostExecute: Content sent successfully: " + string);
            }
        }
    }
}