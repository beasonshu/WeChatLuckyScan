package xyz.monkeytong.hongbao.fragments;

import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;
import xyz.monkeytong.hongbao.R;

/**
 * Created by Zhongyi on 2/4/16.
 */
public class CommentSettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.comment_preferences);
        setPrefListeners();
    }

    private void setPrefListeners() {
        Preference commentWordsPref = findPreference("pref_comment_words");
        String value = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("pref_comment_words", "10.1.4.71:8002");
        if (value.length() > 0) commentWordsPref.setSummary(value);

        commentWordsPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                if (o != null && o.toString().length() > 0) {
                    preference.setSummary(o.toString());
                } else {
                    preference.setSummary("10.1.4.71:8002");
                }
                return true;
            }
        });
    }
}
