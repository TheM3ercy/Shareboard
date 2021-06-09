package systems.omnic.shareboard;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class CustomPreferenceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getSupportFragmentManager()
            .beginTransaction()
            .replace(android.R.id.content, new CustomPreferenceFragment())
            .commit();

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
