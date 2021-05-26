package systems.omnic.shareboard;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final String TAG = MainActivity.class.getSimpleName();
    private List<String> content = new ArrayList<>();
    private boolean loggedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Method entered");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!loggedIn) startLoginActivity();

        ListView listView = findViewById(R.id.mainListView);
        listView.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, content));

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
}