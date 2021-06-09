package systems.omnic.shareboard;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

public class CustomPreferenceFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }
}
