package systems.omnic.shareboard;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

public class CustomPreferenceActivity extends AppCompatActivity {
    private final String TAG = CustomPreferenceActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Method entered");
        super.onCreate(savedInstanceState);
        getSupportFragmentManager()
        .beginTransaction()
        .replace(android.R.id.content, new CustomPreferenceFragment())
        .commit();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Settings");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;
    }
}
