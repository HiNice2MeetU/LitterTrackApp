package dev.hiworld.littertrackingapp.UI.UIThree.HomeFragments;

import android.os.Bundle;
import dev.hiworld.littertrackingapp.R;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsFrag extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }
}