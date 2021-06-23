package systems.omnic.shareboard;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MainActivity extends AppCompatActivity implements Serializable {

    private final String TAG = MainActivity.class.getSimpleName();
    private List<Note> content = new ArrayList<>();
    private CustomRecyclerviewAdapter adapter;
    private RecyclerView recyclerView;
    private ItemTouchHelper.SimpleCallback touch;
    private ArrayList<Note> deleted = new ArrayList<>();
    private static TextView viewUsername;
    private Thread syncThread;

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

        loadNotifChannel();

        if (DataContainer.getInstance().getUserString() == null) startLoginActivity();
        else if (DataContainer.getInstance().isAutoSync()) startAutoSyncTask();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        preferencesChangeListener = this::preferenceChanged;
        prefs.registerOnSharedPreferenceChangeListener(preferencesChangeListener);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("checkbox_autosync", DataContainer.getInstance().isAutoSync());
        editor.commit();

        drawerLayout = (DrawerLayout) findViewById(R.id.mainDrawerLayout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.open_navigation_drawer, R.string.close_navigation_drawer);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        initializeRecyclingView();

        viewUsername = findViewById(R.id.mainNavviewViewUser);

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
                new File(new File(MainActivity.this.getFilesDir(), "data"), "conf.txt").delete();
                DataContainer.getInstance().clear();
                saveRecBin();
                finish();
                startActivity(getIntent());
            }
        });

        Button settingsButton = findViewById(R.id.main_navview_settings_btn);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onCreate: " + settingsButton.getContentDescription() + " selected");
                Intent intent = new Intent(MainActivity.this, CustomPreferenceActivity.class);
                startActivityForResult(intent, 123);
            }
        });

        Button recyclingbinButton = findViewById(R.id.main_navview_recbin_btn);
        recyclingbinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onCreate: " + recyclingbinButton.getContentDescription() + " selected");
                Intent intent = new Intent(MainActivity.this, RecyclingActivity.class);
                List<Note> tmp = DataContainer.getInstance().getRecyclingBin();
                tmp.addAll(deleted);
                DataContainer.getInstance().setRecyclingBin(tmp);
                startActivityForResult(intent, 666);
            }
        });
    }


    public void loadNotifChannel(){
        CharSequence channelName = getString(R.string.channel_name);
        String description = getString(R.string.channel_description);
        NotificationChannel channel = new NotificationChannel(getResources().getString(R.string.channel_id), channelName, NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription(description);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }



    public void init(){
        viewUsername.setText(DataContainer.getInstance().getUsername());
        if (DataContainer.getInstance().isAutoSync()) startAutoSyncTask();
    }



    public void initializeRecyclingView(){
        recyclerView = findViewById(R.id.mainRecView);

        adapter = new CustomRecyclerviewAdapter(content, MainActivity.this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = recyclerView.getChildAdapterPosition(v);
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("text", content.get(pos).getContent());
                clipboardManager.setPrimaryClip(clipData);
                Snackbar.make(recyclerView, "Copied " + content.get(pos).getContent(), Snackbar.LENGTH_SHORT)
                        .show();
            }
        });
        recyclerView.addItemDecoration(new DividerItemDecoration(MainActivity.this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        touch = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int pos = viewHolder.getAdapterPosition();
                switch (direction){
                    case ItemTouchHelper.RIGHT:{
                        Note note = content.remove(pos);
                        note.setSynced(false);
                        note.setDateTime(LocalDateTime.now());
                        deleted.add(note);
                        adapter.notifyItemRemoved(pos);
                        Snackbar.make(recyclerView, note.getContent(), Snackbar.LENGTH_LONG)
                                .setAction("Undo", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        note.setDateTime(null);
                                        deleted.remove(note);
                                        content.add(pos, note);
                                        adapter.notifyItemInserted(pos);
                                    }
                                }).addCallback(new Snackbar.Callback(){
                                    @Override
                                    public void onDismissed(Snackbar snackbar, int event){
                                        DeleteContentTask task = new DeleteContentTask();
                                        task.execute("" + note.getId());
                                    }
                                }).show();
                        break;
                    }
                    case ItemTouchHelper.LEFT:{
                        Note note = content.remove(pos);
                        note.setSynced(true);
                        adapter.notifyItemRemoved(pos);
                        content.add(pos, note);
                        adapter.notifyItemInserted(pos);
                        PostContentTask task = new PostContentTask();
                        task.execute("" + pos);
                        break;
                    }
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                Log.d(TAG, "onChildDraw: Method entered");

                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeRightBackgroundColor(getResources().getColor(R.color.red))
                        .addSwipeLeftBackgroundColor(getResources().getColor(R.color.green))
                        .addSwipeRightActionIcon(R.drawable.ic_delete_bin)
                        .addSwipeLeftActionIcon(R.drawable.ic_upload)
                        .create().decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

            }
        };

        ItemTouchHelper helper = new ItemTouchHelper(touch);
        helper.attachToRecyclerView(recyclerView);
    }


    private void startAutoSyncTask(){
        Log.d(TAG, "startAutoSyncTask: Method entered");

        if (syncThread == null) {
            syncThread = new Thread(() -> {
                while (true) {
                    SyncTask task = new SyncTask();
                    Log.d(TAG, "startAutoSyncTask: started Sync task");
                    task.execute();
                    while (!task.isCancelled()) {
                    }
                    try {
                        Log.d(TAG, "syncThread: wait 500");
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });

            syncThread.start();
        }
    }

    private void loadConf(){
        Log.d(TAG, "loadConf: Method entered");

        File directory = new File(MainActivity.this.getFilesDir(), "data");
        if (!directory.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(new File(directory, "conf.txt")))){
            String line = br.readLine();
            Log.d(TAG, line);
            String[] split = line.split(";");
            DataContainer.getInstance().setUserString(split[0].equals("null") ? null:split[0]);
            DataContainer.getInstance().setStayLoggedIn(Boolean.parseBoolean(split[1]));
            DataContainer.getInstance().setUsername(split[2]);
            DataContainer.getInstance().setAutoSync(Boolean.parseBoolean(split[3]));
        } catch (Exception e){
        }


    }

    private void loadRecBin(){
        Log.d(TAG, "loadRecBin: Method entered");

        File recBin = new File(new File(MainActivity.this.getFilesDir(), "data"), "recbin" + DataContainer.getInstance().getUsername() + ".txt");
        if (!recBin.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(recBin))){
            List<Note> recBinList = new ArrayList<>();
            String line = br.readLine();
            if (line == null) return;
            for (String s:line.split(";")){
                Note note = new Note(s.split("&")[0], false, Integer.parseInt(s.split("&")[1]));
                note.setDateTime(LocalDateTime.from(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss").parse(s.split("&")[1])));
                recBinList.add(note);
            }
            DataContainer.getInstance().setRecyclingBin(recBinList);
        } catch (Exception e){
        }
    }

    private void saveRecBin(){
        Log.d(TAG, "saveRecBin: Method entered");

        File directory = new File(MainActivity.this.getFilesDir(), "data");
        File file = new File(directory, "recbin" + DataContainer.getInstance().getUsername() + ".txt");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))){
            for (Note n:DataContainer.getInstance().getRecyclingBin())
                bw.write(n.getContent() + "&" + n.getDateTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")) + "&" + n.getId());
            bw.flush();
        } catch (Exception e){
        }
    }

    public void startLoginActivity(){
        Log.d(TAG, "startLoginActivity: Method entered");

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("context", MainActivity.this);
        intent.putExtras(bundle);
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
                        content.add(content.size(), new Note(input.getText().toString(), false, -1));
                        adapter.notifyItemInserted(content.size());
                    }
                })
                .setNegativeButton(getResources().getText(R.string.cancel), null).show();
    }


    public void showNotifications(List<String> notifications){
        Log.d(TAG, "showNotifications: Method entered");

        for (int i = 0; i < notifications.size(); i++) {
            String[] split = notifications.get(i).split(":");
            NotificationCompat.Builder builder  = new NotificationCompat.Builder(
                    MainActivity.this, getResources().getString(R.string.channel_id))
                    .setColor(Color.YELLOW)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(split[1])
                    .setSmallIcon(R.drawable.ic_sync)
                    .setWhen(System.currentTimeMillis())
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(MainActivity.this);
            notificationManagerCompat.notify(i, builder.build());
            Log.d(TAG, "onCreateOptionsMenu: Notificatioin sent " + i);
        }
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
            viewUsername.setText(DataContainer.getInstance().getUsername());
            loadRecBin();
            return true;
        }else if (item.getItemId() == R.id.mainSync){
            Log.d(TAG, "onOptionsItemSelected: Load selected");

            GetContentTask task = new GetContentTask();
            task.execute();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: Method entered");

        if (resultCode == Activity.RESULT_OK){
            Bundle bundle = data.getExtras();
            ArrayList<Note> restored = (ArrayList<Note>) bundle.getSerializable("restored");
            content.addAll(content.size(), restored);
            adapter.notifyItemRangeInserted(content.size(), restored.size());
            ItemTouchHelper helper = new ItemTouchHelper(touch);
            helper.attachToRecyclerView(recyclerView);
            deleted.clear();
            saveRecBin();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void preferenceChanged(SharedPreferences sharedPrefs, String key){
        Log.d(TAG, "preferenceChanged: Method entered");

        boolean autoSync = sharedPrefs.getBoolean("checkbox_autosync", false);
        DataContainer.getInstance().setAutoSync(autoSync);
        if (autoSync) startAutoSyncTask();
        else try {
            Log.d(TAG, "preferenceChanged: disabled thread");
            syncThread.interrupt();
        } catch (Exception e){
            e.printStackTrace();
        }
        Log.d(TAG, "preferenceChanged: changed Value:" + autoSync);
    }

    private void loadContent(List<Note> cont){
        Log.d(TAG, "loadContent: Method entered");

        List<String> notifications = new ArrayList<>();
        for (Note n:cont) if (n.getContent().contains("NOTIFICATION:")) notifications.add(n.getContent());
        if (notifications.size() > 0) showNotifications(notifications);

        for (Note n:cont)
            if (n.getContent().contains("NOTIFICATION")){
                String[] split = n.getContent().split(":");
                n.setContent(split[1]);
            }
        content.addAll(content.size(), cont);
        adapter.notifyItemRangeInserted(content.size(), cont.size());
        ItemTouchHelper helper = new ItemTouchHelper(touch);
        helper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onBackPressed(){
        Log.d(TAG, "onBackPressed: Method entered");

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

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: Method entered");

        saveRecBin();
        DataContainer.getInstance().saveConf(MainActivity.this);
        super.onDestroy();
    }

    private class PostContentTask extends AsyncTask<String, Integer, String> {
        private final String TAG = MainActivity.PostContentTask.class.getSimpleName();

        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG, "doInBackground: Method entered");

            String urlString = "http://omnic-systems.com/shareboard/get_request/?user_string=" + DataContainer.getInstance().getUserString() + "&content=" + content.get(Integer.parseInt(strings[0])).getContent();
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

                    return sb.toString() + ";" + strings[0];
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
            }

            String[] split = string.split(";");
            try {
                JSONObject jsonObject = new JSONObject(split[0]);
                int id = jsonObject.getInt("id");
                content.get(Integer.parseInt(split[1])).setId(id);
                Log.d(TAG, "onPostExecute: set id " + id);
            } catch (JSONException e) {
                e.printStackTrace();
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
                    results.add(jsonObject.getString("clipboard") + ";&;" + jsonObject.getString("id"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            List<Note> tmp = new ArrayList<>();
            for (String s:results)
                tmp.add(new Note(s.split(";&;")[0], true, Integer.parseInt(s.split(";&;")[1])));
            loadContent(tmp);
        }
    }

    private class DeleteContentTask extends AsyncTask<String, Integer, String> {
        private final String TAG = MainActivity.DeleteContentTask.class.getSimpleName();

        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG, "doInBackground: Method entered");

            String urlString = "https://omnic-systems.com/shareboard/delete/?id=" + strings[0] + "&user_string=" + DataContainer.getInstance().getUserString();
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

    public class SyncTask extends AsyncTask<String, Integer, String>{
        private final String TAG = MainActivity.SyncTask.class.getSimpleName();

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
            if (string.equals("") || string == null){
                Log.d(TAG, "onPostExecute: " + getResources().getString(R.string.no_response));
            }

            List<String> results = new ArrayList<>();
            JSONArray jsonArray;
            JSONObject jsonObject = null;
            try {
                jsonArray = new JSONArray(string);
                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObject = (JSONObject) jsonArray.get(i);
                    Log.d(TAG, "onPostExecute: result:" + jsonObject.toString());
                    results.add(jsonObject.getString("clipboard") + ";&;" + jsonObject.getString("id"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            List<Note> tmp = new ArrayList<>();
            for (String s:results)
                tmp.add(new Note(s.split(";&;")[0], true, Integer.parseInt(s.split(";&;")[1])));

            List<Note> changes = new ArrayList<>();
            for (Note n:tmp){
                boolean contains = false;
                for (Note note:content){
                    if (n.getId() == note.getId())
                        contains = true;
                }
                if (!contains)
                    changes.add(n);
            }

            if (changes.size() > 0)
                loadContent(changes);

            cancel(true);
        }
    }
}