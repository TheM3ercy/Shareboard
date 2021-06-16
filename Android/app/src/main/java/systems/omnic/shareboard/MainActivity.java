package systems.omnic.shareboard;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final String TAG = MainActivity.class.getSimpleName();
    private List<String> content = new ArrayList<>();

    private SharedPreferences prefs;
    private SharedPreferences.OnSharedPreferenceChangeListener preferencesChangeListener;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Method entered");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadConf();

        if (DataContainer.getInstance().getUserString() == null) startLoginActivity();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        preferencesChangeListener = this::preferenceChanged;
        prefs.registerOnSharedPreferenceChangeListener(preferencesChangeListener);

        drawerLayout = (DrawerLayout) findViewById(R.id.mainDrawerLayout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.open_navigation_drawer, R.string.close_navigation_drawer);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        ListView listView = findViewById(R.id.mainListView);
        listView.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, content));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (DataContainer.getInstance().getUserString() == null)
                    Log.d(TAG, "user string null");
                PostContentTask task = new PostContentTask();
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

        Button logoutButton = findViewById(R.id.main_navview_logout_btn);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onCreate: " + logoutButton.getContentDescription() + " selected");
                File directory = new File(MainActivity.this.getFilesDir(), "data");
                if (directory.exists()) new File(directory, "conf.txt").delete();
                content = new ArrayList<>();
                listView.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, content));
                startLoginActivity();
            }
        });

        TextView viewUsername = findViewById(R.id.mainNavviewViewUser);
        viewUsername.setText(DataContainer.getInstance().getUsername());

        Button settingsButton = findViewById(R.id.main_navview_settings_btn);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onCreate: " + settingsButton.getContentDescription() + " selected");
                Intent intent = new Intent(MainActivity.this, CustomPreferenceActivity.class);
                startActivityForResult(intent, 123);
            }
        });

        Button recyclebinButton = findViewById(R.id.main_navview_recbin_btn);
        recyclebinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onCreate: " + recyclebinButton.getContentDescription() + " selected");

            }
        });
    }

    private void loadConf(){
        Log.d(TAG, "loadConf: Method entered");

        File directory = new File(MainActivity.this.getFilesDir(), "data");
        if (!directory.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(new File(directory, "conf.txt")))){
            String[] split = br.readLine().split(";");
            DataContainer.getInstance().setUserString(split[0].equals("null") ? null:split[0]);
            DataContainer.getInstance().setStayLoggedIn(Boolean.parseBoolean(split[1]));
        } catch (Exception e){
        }
    }

    public void startLoginActivity(){
        Log.d(TAG, "startLoginActivity: Method entered");

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    public void showAddAlert(){
        Log.d(TAG, "showAddAlert: Method entered");

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu: Method entered");

        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Log.d(TAG, "onOptionsItemSelected: Method entered");

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }else if (item.getItemId() == R.id.menuLoad){
            GetContentTask task = new GetContentTask();
            task.execute();
        }

        return super.onOptionsItemSelected(item);
    }


    private void preferenceChanged(SharedPreferences sharedPrefs, String key) {
        Log.d(TAG, "preferenceChanged: Method entered");

        boolean isDarkmode = sharedPrefs.getBoolean(key, false);
        Log.d(TAG, "onCreate: changed Value:" + isDarkmode);
    }

    private void loadContent(List<String> content){
        this.content = content;
        ListView listView = findViewById(R.id.mainListView);
        listView.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, content));

    }

    @Override
    public void onBackPressed(){
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(getResources().getString(R.string.exit))
                .setMessage(getResources().getString(R.string.exit_warning))
                .setPositiveButton("confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel), null)
                .show();
    }

    private class PostContentTask extends AsyncTask<String, Integer, String> {
        private final String TAG = MainActivity.PostContentTask.class.getSimpleName();

        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG, "doInBackground: Method entered");

            String urlString = "http://omnic-systems.com/shareboard/get_request/?user_string=" + DataContainer.getInstance().getUserString() + "&content=" + strings[0];
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
                Log.d(TAG, "onPostExecute: " + getResources().getString(R.string.no_response));
                return;
            } else if (string.contains("Post recieved")){
                Log.d(TAG, "onPostExecute: Content sent successfully: " + string);
            }
        }
    }

    private class GetContentTask extends AsyncTask<String, Integer, String> {
        private final String TAG = MainActivity.GetContentTask.class.getSimpleName();

        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG, "doInBackground: Method entered");

            String urlString = "http://omnic-systems.com/shareboard/pull_request/?user_string=" + DataContainer.getInstance().getUserString();
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
                return;
            }

            List<String> results = new ArrayList<>();
            JSONArray jsonArray;
            JSONObject jsonObject = null;
            try {
                jsonArray = new JSONArray(string);
                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObject = (JSONObject) jsonArray.get(i);
                    Log.d(TAG, "onPostExecute: result:" + jsonObject.toString());
                    results.add(jsonObject.getString("clipboard"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            loadContent(results);
        }
    }

    private class DeleteContentTask extends AsyncTask<String, Integer, String> {
        private final String TAG = MainActivity.DeleteContentTask.class.getSimpleName();

        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG, "doInBackground: Method entered");

            String urlString = "https://omnic-systems.com/shareboard/delete?user_string=" + DataContainer.getInstance().getUserString() + "&content=" + strings[0];
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
            if (string.contains("failed")){
                Log.d(TAG, "onPostExecute: Delete failed");
            } else if (string.equals("") || string == null){
                Log.d(TAG, "onPostExecute: " + getResources().getString(R.string.no_response));
            }
        }
    }
}